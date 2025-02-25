package com.torahsearch.repository;

import com.torahsearch.model.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnswerRepository extends MongoRepository<Answer, String> {
    List<Answer> findByIdIn(List<String> ids);
    List<Answer> findBySource(String source);
}