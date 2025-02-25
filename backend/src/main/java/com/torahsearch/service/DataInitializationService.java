package com.torahsearch.service;

import com.torahsearch.model.Answer;
import com.torahsearch.model.Question;
import com.torahsearch.repository.AnswerRepository;
import com.torahsearch.repository.QuestionRepository;
import com.torahsearch.search.LuceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializationService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final LuceneService luceneService;
    
    @Autowired
    public DataInitializationService(QuestionRepository questionRepository, 
                                      AnswerRepository answerRepository,
                                      LuceneService luceneService) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.luceneService = luceneService;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        try {
            // בדיקה אם כבר יש נתונים במערכת
            if (questionRepository.count() == 0) {
                System.out.println("Initializing sample data...");
                createSampleData();
                
                // אינדוקס כל הנתונים ב-Lucene
                System.out.println("Indexing data in Lucene...");
                indexAllData();
            } else {
                System.out.println("Data already exists, skipping initialization");
            }
        } catch (Exception e) {
            System.err.println("Error during data initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createSampleData() {
        // יצירת נתוני דוגמה
        
        // תשובה 1
        Answer answer1 = new Answer();
        answer1.setText("על פי ההלכה, מוקצה בשבת הוא חפץ שאסור לטלטל בשבת. " +
                "ישנם מספר סוגי מוקצה, כמו מוקצה מחמת גופו (כלים שמלאכתם לאיסור), " +
                "מוקצה מחמת חסרון כיס (חפצים יקרי ערך שמקפידים עליהם שלא יינזקו), " +
                "ומוקצה מחמת איסור (חפצים שאסורים בשימוש בשבת). ההלכה אוסרת טלטול " +
                "של מוקצה, אלא אם כן יש צורך בגוף החפץ או במקומו, או כדי להצילו מנזק.");
        answer1.setSource("הרב משה פיינשטיין");
        answer1.setUrl("");
        answer1.setBookReference("שו\"ת אגרות משה, אורח חיים חלק ה");
        answer1.setCreatedAt(LocalDateTime.now());
        answer1.setRelevanceScore(1.0);
        Answer savedAnswer1 = answerRepository.save(answer1);
        
        // שאלה 1
        Question question1 = new Question();
        question1.setText("מה ההלכה לגבי מוקצה בשבת?");
        question1.setCategory("הלכות שבת");
        question1.setCreatedAt(LocalDateTime.now());
        question1.setKeywords(Arrays.asList("מוקצה", "שבת", "הלכה", "טלטול"));
        question1.setAnswerIds(List.of(savedAnswer1.getId()));
        questionRepository.save(question1);
        
        // תשובה 2
        Answer answer2 = new Answer();
        answer2.setText("השימוש בפלטה חשמלית (פלטת שבת) בשבת מותר כל עוד היא הודלקה לפני כניסת " +
                "השבת ונשארת דלוקה במשך כל השבת. מותר להניח עליה אוכל שכבר התבשל כל צורכו, " +
                "לצורך חימום. יש להיזהר שלא לכסות את כל הפלטה בנייר כסף, ויש שמחמירים " +
                "שלא להניח אוכל קר ישירות על הפלטה אלא להניחו על גבי סיר שכבר נמצא על הפלטה.");
        answer2.setSource("הרב עובדיה יוסף");
        answer2.setUrl("");
        answer2.setBookReference("חזון עובדיה - שבת");
        answer2.setCreatedAt(LocalDateTime.now());
        answer2.setRelevanceScore(1.0);
        Answer savedAnswer2 = answerRepository.save(answer2);
        
        // שאלה 2
        Question question2 = new Question();
        question2.setText("האם מותר להשתמש בפלטת שבת לחימום אוכל?");
        question2.setCategory("הלכות שבת");
        question2.setCreatedAt(LocalDateTime.now());
        question2.setKeywords(Arrays.asList("פלטה", "שבת", "חימום", "בישול"));
        question2.setAnswerIds(List.of(savedAnswer2.getId()));
        questionRepository.save(question2);
        
        // עוד תשובות ושאלות...
    }
    
    private void indexAllData() {
        try {
            // מחיקת האינדקס הקיים
            luceneService.clearIndex();
            
            // אינדוקס כל השאלות והתשובות
            List<Question> allQuestions = questionRepository.findAll();
            
            for (Question question : allQuestions) {
                List<Answer> answers = answerRepository.findByIdIn(question.getAnswerIds());
                luceneService.indexQuestion(question, answers);
            }
            
            System.out.println("Indexed " + allQuestions.size() + " questions in Lucene");
        } catch (Exception e) {
            System.err.println("Error during Lucene indexing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}