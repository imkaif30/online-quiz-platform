package com.quizapp.controller;

import com.quizapp.dto.QuestionDto;
import com.quizapp.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * QuestionController - REST endpoints for Questions
 * ============================================================
 * Base URL: /api/questions
 *
 *   GET  /api/questions              → Get all questions
 *   GET  /api/questions/{id}         → Get question by ID
 *   GET  /api/questions/categories   → Get all category names
 *   GET  /api/questions/count        → Get total active question count
 * ============================================================
 */
@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    private final QuestionService questionService;

    // Explicit constructor injection (replaces @RequiredArgsConstructor)
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * GET /api/questions/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        log.info("GET /api/questions/categories");
        List<String> categories = questionService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * GET /api/questions/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getQuestionCount() {
        log.info("GET /api/questions/count");
        Map<String, Long> response = new HashMap<>();
        response.put("count", questionService.getActiveQuestionCount());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/questions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        log.info("GET /api/questions/{}", id);
        try {
            QuestionDto question = questionService.getQuestionById(id);
            return ResponseEntity.ok(question);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
