package com.torahsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // הוספת אפשרות תזמון משימות
public class TorahSearchEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TorahSearchEngineApplication.class, args);
    }
}