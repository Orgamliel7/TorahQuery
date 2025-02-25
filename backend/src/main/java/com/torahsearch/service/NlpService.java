package com.torahsearch.service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class NlpService {

    private final StanfordCoreNLP pipeline;
    
    public NlpService() {
        // כרגע נשתמש בלימוד באנגלית - בהמשך נחליף לעברית אם תהיה תמיכה
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        props.setProperty("tokenize.language", "en");
        
        pipeline = new StanfordCoreNLP(props);
    }
    
    public List<String> extractLemmas(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        
        List<String> lemmas = new ArrayList<>();
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                if (lemma.length() > 2) {  // נסנן מילים קצרות
                    lemmas.add(lemma.toLowerCase());
                }
            }
        }
        
        return lemmas;
    }
    
    // בעתיד נוסיף פונקציות כמו ניתוח ישויות, זיהוי קטגוריות וכדומה
}