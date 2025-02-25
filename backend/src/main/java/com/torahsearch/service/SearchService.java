package com.torahsearch.service;

import com.torahsearch.model.Answer;
import com.torahsearch.model.Question;
import com.torahsearch.model.SearchResult;
import com.torahsearch.repository.AnswerRepository;
import com.torahsearch.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    
    @Autowired
    public SearchService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }
    
    public SearchResult search(String query) {
        // בשלב ראשוני, פשוט נחפש לפי מילות מפתח בסיסיות
        List<String> keywords = extractKeywords(query);
        
        // נמצא שאלות שמכילות את מילות המפתח
        List<Question> matchingQuestions = questionRepository.findByKeywords(keywords);
        
        // נמצא שאלות שהטקסט שלהן מכיל את הביטוי
        matchingQuestions.addAll(questionRepository.findByTextContaining(query));
        
        // נאסוף מזהים של תשובות רלוונטיות
        List<String> answerIds = matchingQuestions.stream()
                .flatMap(q -> q.getAnswerIds().stream())
                .distinct()
                .collect(Collectors.toList());
        
        // נמצא את התשובות
        List<Answer> answers = answerRepository.findByIdIn(answerIds);
        
        // הכנת תוצאת החיפוש
        List<SearchResult.AnswerResult> results = new ArrayList<>();
        for (Answer answer : answers) {
            // כרגע, נחשב ציון התאמה פשוט:
            double score = calculateSimpleMatchScore(query, answer);
            
            // נמצא את הטקסט של השאלה המקורית 
            String questionText = findQuestionText(matchingQuestions, answer);
            
            results.add(new SearchResult.AnswerResult(answer, score, questionText));
        }
        
        // מיון לפי ציון ההתאמה (מהגבוה לנמוך)
        results.sort((r1, r2) -> Double.compare(r2.getMatchScore(), r1.getMatchScore()));
        
        return new SearchResult(query, results);
    }
    
    private List<String> extractKeywords(String query) {
        // בסיס פשוט - פירוק לפי רווחים והסרת מילות קישור נפוצות
        String[] words = query.toLowerCase().split("\\s+");
        
        List<String> stopWords = Arrays.asList("מה", "איך", "למה", "האם", "מי", "מתי", "את", "של", "על", "אם");
        
        return Arrays.stream(words)
                .filter(word -> word.length() > 1 && !stopWords.contains(word))
                .collect(Collectors.toList());
    }
    
    private double calculateSimpleMatchScore(String query, Answer answer) {
        // בשלב זה, נשתמש בחישוב פשוט מאוד:
        // נספור כמה מילות מפתח מהשאלה מופיעות בתשובה
        List<String> keywords = extractKeywords(query);
        int matches = 0;
        
        for (String keyword : keywords) {
            if (answer.getText().toLowerCase().contains(keyword)) {
                matches++;
            }
        }
        
        // מנרמל לציון 0-1
        return (double) matches / keywords.size();
    }
    
    private String findQuestionText(List<Question> questions, Answer answer) {
        // מציאת טקסט השאלה שמקושרת לתשובה
        for (Question q : questions) {
            if (q.getAnswerIds().contains(answer.getId())) {
                return q.getText();
            }
        }
        return "";
    }
}