package com.quizapp.controller;

import com.quizapp.dto.*;
import com.quizapp.service.QuizService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 * QuizController - REST endpoints for Quiz operations
 * ============================================================
 * Base URL: /api/quiz
 *
 *   POST /api/quiz/start           → Start a new quiz session
 *   POST /api/quiz/submit          → Submit answers, get score
 *   GET  /api/quiz/result/{id}     → Get result for a session
 *   GET  /api/quiz/health          → Health check endpoint
 * ============================================================
 */
@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizController {

    private static final Logger log = LoggerFactory.getLogger(QuizController.class);

    private final QuizService quizService;

    // Explicit constructor injection (replaces @RequiredArgsConstructor)
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * GET /api/quiz/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Quiz API is running!");
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/quiz/start
     */
    @PostMapping("/start")
    public ResponseEntity<?> startQuiz(@Valid @RequestBody QuizStartRequest request) {
        log.info("POST /api/quiz/start | player={} | count={}",
                request.getPlayerName(), request.getQuestionCount());

        try {
            QuizStartResponse response = quizService.startQuiz(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("Error starting quiz: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * POST /api/quiz/submit
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@Valid @RequestBody QuizSubmitRequest request) {
        log.info("POST /api/quiz/submit | sessionId={}", request.getSessionId());

        try {
            QuizResultDto result = quizService.submitQuiz(request);
            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            log.error("Error submitting quiz: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/quiz/result/{sessionId}
     */
    @GetMapping("/result/{sessionId}")
    public ResponseEntity<?> getResult(@PathVariable Long sessionId) {
        log.info("GET /api/quiz/result/{}", sessionId);

        try {
            QuizResultDto result = quizService.getResult(sessionId);
            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            log.error("Error fetching result: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
