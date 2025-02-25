package com.torahsearch.controller;

import com.torahsearch.model.Answer;
import com.torahsearch.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/answers")
@CrossOrigin(origins = "*")
public class AnswerController {

    private final AnswerRepository answerRepository;
    
    @Autowired
    public AnswerController(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }
    
    @GetMapping
    public ResponseEntity<List<Answer>> getAllAnswers() {
        return ResponseEntity.ok(answerRepository.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Answer> getAnswerById(@PathVariable String id) {
        return answerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Answer> createAnswer(@RequestBody Answer answer) {
        answer.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(answerRepository.save(answer));
    }
}