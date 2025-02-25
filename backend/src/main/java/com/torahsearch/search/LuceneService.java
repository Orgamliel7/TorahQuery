package com.torahsearch.search;

import com.torahsearch.model.Answer;
import com.torahsearch.model.Question;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LuceneService {

    private static final String INDEX_DIR = "lucene-index";
    private Directory directory;
    private Analyzer analyzer;
    private IndexWriter indexWriter;

    @PostConstruct
    public void init() throws IOException {
        Path indexPath = Paths.get(INDEX_DIR);
        if (!Files.exists(indexPath)) {
            Files.createDirectories(indexPath);
        }
        
        directory = FSDirectory.open(indexPath);
        analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        indexWriter = new IndexWriter(directory, config);
    }

    @PreDestroy
    public void cleanup() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
        if (directory != null) {
            directory.close();
        }
    }

    public void indexQuestion(Question question, List<Answer> answers) throws IOException {
        Document doc = new Document();
        
        // מזהה השאלה
        doc.add(new StoredField("id", question.getId()));
        
        // טקסט השאלה - נשמור אותו לחיפוש
        doc.add(new TextField("question", question.getText(), Field.Store.YES));
        
        // קטגוריה
        doc.add(new TextField("category", question.getCategory(), Field.Store.YES));
        
        // מילות מפתח
        if (question.getKeywords() != null && !question.getKeywords().isEmpty()) {
            String keywordsStr = String.join(" ", question.getKeywords());
            doc.add(new TextField("keywords", keywordsStr, Field.Store.YES));
        }
        
        // טקסט התשובות המקושרות
        StringBuilder answersText = new StringBuilder();
        for (Answer answer : answers) {
            answersText.append(answer.getText()).append(" ");
            
            // נוסיף גם את מזהה התשובה כדי שנוכל למצוא אותה אחר כך
            doc.add(new StoredField("answer_id", answer.getId()));
        }
        
        doc.add(new TextField("answers", answersText.toString(), Field.Store.NO));
        
        // נוסיף את המסמך למאגר
        indexWriter.addDocument(doc);
        indexWriter.commit();
    }

    public void clearIndex() throws IOException {
        indexWriter.deleteAll();
        indexWriter.commit();
    }

    public List<SearchResult> search(String queryStr, int maxResults) throws IOException, ParseException {
        // הגדרת השדות לחיפוש ומשקלות
        String[] fields = {"question", "answers", "keywords", "category"};
        Map<String, Float> boosts = new HashMap<>();
        boosts.put("question", 2.0f);
        boosts.put("answers", 1.0f);
        boosts.put("keywords", 1.5f);
        boosts.put("category", 0.5f);
        
        // יצירת Parser לחיפוש במספר שדות
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);
        Query query = parser.parse(queryStr);
        
        // פתיחת הקורא והחיפוש
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(query, maxResults);
        
        List<SearchResult> results = new ArrayList<>();
        
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            
            SearchResult result = new SearchResult();
            result.setQuestionId(doc.get("id"));
            result.setQuestionText(doc.get("question"));
            result.setCategory(doc.get("category"));
            result.setScore(scoreDoc.score);
            
            // איסוף מזהי התשובות
            String[] answerIds = doc.getValues("answer_id");
            result.setAnswerIds(List.of(answerIds));
            
            results.add(result);
        }
        
        reader.close();
        return results;
    }

    // מחלקה פנימית לתוצאות חיפוש
    public static class SearchResult {
        private String questionId;
        private String questionText;
        private String category;
        private List<String> answerIds;
        private float score;

        // גטרים וסטרים
        public String getQuestionId() { return questionId; }
        public void setQuestionId(String questionId) { this.questionId = questionId; }
        
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public List<String> getAnswerIds() { return answerIds; }
        public void setAnswerIds(List<String> answerIds) { this.answerIds = answerIds; }
        
        public float getScore() { return score; }
        public void setScore(float score) { this.score = score; }
    }
}