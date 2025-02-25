package com.torahsearch.scraper;

import com.torahsearch.model.Answer;
import com.torahsearch.model.Question;
import com.torahsearch.repository.AnswerRepository;
import com.torahsearch.repository.QuestionRepository;
import com.torahsearch.service.NlpService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class WebScraper {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final NlpService nlpService;

    @Autowired
    public WebScraper(QuestionRepository questionRepository, AnswerRepository answerRepository, NlpService nlpService) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.nlpService = nlpService;
    }

    // ריצה לפי קרון
    @Scheduled(cron = "0 0 1 * * ?") // כל יום בשעה 1:00 בלילה
    public void scheduledScrape() {
        try {
            // ניתן להוסיף רשימת אתרים לסריקה
            scrapeKipaQA();
        } catch (Exception e) {
            System.err.println("Error during scheduled scraping: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // פונקציה לסריקת שאלות ותשובות מאתר כיפה
    public void scrapeKipaQA() {
        try {
            // נשנה את מספר הדף לפי הצורך בלולאה
            String url = "https://www.kipa.co.il/%D7%A9%D7%90%D7%9C-%D7%90%D7%AA-%D7%94%D7%A8%D7%91/";
            Document doc = Jsoup.connect(url).get();

            // נמצא את רשימת השאלות בדף
            Elements questionElements = doc.select(".question-item");

            for (Element questionElement : questionElements) {
                try {
                    // חילוץ פרטי השאלה
                    String questionTitle = questionElement.select("h2").text();
                    String questionLink = questionElement.select("a").attr("href");
                    
                    // ביקור בדף השאלה המלא
                    Document questionPage = Jsoup.connect(questionLink).get();
                    
                    // חילוץ תוכן השאלה והתשובה
                    String fullQuestion = questionPage.select(".question-content").text();
                    String answerText = questionPage.select(".answer-content").text();
                    String rabbiName = questionPage.select(".rabbi-name").text();
                    
                    // יצירת אובייקט תשובה
                    Answer answer = new Answer();
                    answer.setText(answerText);
                    answer.setSource("כיפה - שאל את הרב");
                    answer.setUrl(questionLink);
                    answer.setBookReference("");
                    answer.setCreatedAt(LocalDateTime.now());
                    answer.setRelevanceScore(1.0); // ציון ברירת מחדל
                    
                    // שמירת התשובה
                    Answer savedAnswer = answerRepository.save(answer);
                    
                    // חילוץ מילות מפתח
                    List<String> keywords = new ArrayList<>(nlpService.extractLemmas(fullQuestion));
                    keywords.addAll(Arrays.asList(questionTitle.split("\\s+")));
                    
                    // יצירת אובייקט שאלה
                    Question question = new Question();
                    question.setText(fullQuestion);
                    question.setCategory("כללי"); // אפשר לחלץ קטגוריה בהמשך
                    question.setCreatedAt(LocalDateTime.now());
                    question.setKeywords(keywords);
                    question.setAnswerIds(List.of(savedAnswer.getId()));
                    
                    // שמירת השאלה
                    questionRepository.save(question);
                    
                    System.out.println("Scraped question: " + questionTitle);
                } catch (Exception e) {
                    System.err.println("Error processing question: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error scraping Kipa Q&A: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // אפשר להוסיף פונקציות לסריקת אתרים נוספים
}