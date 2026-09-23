package com.quizapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================
 * QuizApplication - Main Entry Point
 * ============================================================
 * This is the starting point of the Spring Boot application.
 *
 * @SpringBootApplication combines:
 *   - @Configuration       : Marks this as a config class
 *   - @EnableAutoConfiguration : Auto-configures Spring beans
 *   - @ComponentScan       : Scans all classes in this package
 *
 * HOW TO RUN:
 *   mvn spring-boot:run
 *   OR
 *   java -jar target/online-quiz-app-1.0.0.jar
 * ============================================================
 */
@SpringBootApplication
public class QuizApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizApplication.class, args);
        System.out.println("================================================");
        System.out.println("  Online Quiz App is RUNNING!");
        System.out.println("  Backend API: http://localhost:8080");
        System.out.println("  Health Check: http://localhost:8080/api/questions");
        System.out.println("================================================");
    }
}
