package com.torahsearch.controller;

import com.torahsearch.model.SearchResult;
import com.torahsearch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    private final SearchService searchService;
    
    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }
    
    @GetMapping
    public ResponseEntity<SearchResult> search(@RequestParam String query) {
        SearchResult results = searchService.searchWithLucene(query);
        return ResponseEntity.ok(results);
    }
}