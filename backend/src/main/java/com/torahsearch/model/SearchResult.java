package com.torahsearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private String query;
    private List<AnswerResult> results;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerResult {
        private Answer answer;
        private double matchScore;
        private String questionText;
    }
}