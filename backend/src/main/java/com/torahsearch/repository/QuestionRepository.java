package com.torahsearch.repository;

import com.torahsearch.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findByCategory(String category);
    
    @Query("{'keywords': {$in: ?0}}")
    List<Question> findByKeywords(List<String> keywords);
    
    @Query("{'text': {$regex: ?0, $options: 'i'}}")
    List<Question> findByTextContaining(String text);
}