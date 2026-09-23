package com.quizapp.service;

import com.quizapp.dto.*;
import com.quizapp.entity.Question;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.QuizSession;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.repository.QuizSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * QuizService - Core business logic for Quiz operations
 * ============================================================
 * Handles:
 *   1. Starting a new quiz session
 *   2. Submitting answers and calculating score
 *   3. Retrieving detailed quiz results
 * ============================================================
 */
@Service
public class QuizService {

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    private final QuizSessionRepository  sessionRepository;
    private final QuizAttemptRepository  attemptRepository;
    private final QuestionRepository     questionRepository;
    private final QuestionService        questionService;

    private static final int TIME_PER_QUESTION_SECONDS = 30;

    // Explicit constructor injection (replaces @RequiredArgsConstructor)
    public QuizService(QuizSessionRepository sessionRepository,
                       QuizAttemptRepository attemptRepository,
                       QuestionRepository questionRepository,
                       QuestionService questionService) {
        this.sessionRepository = sessionRepository;
        this.attemptRepository = attemptRepository;
        this.questionRepository = questionRepository;
        this.questionService = questionService;
    }

    // ============================================================
    // 1. START QUIZ
    // ============================================================

    @Transactional
    public QuizStartResponse startQuiz(QuizStartRequest request) {
        log.info("Starting quiz for player: {}", request.getPlayerName());

        List<QuestionDto> questions = questionService.getRandomQuestions(
                request.getQuestionCount(),
                request.getCategory(),
                request.getDifficulty()
        );

        if (questions.isEmpty()) {
            throw new RuntimeException("No questions available for the selected filters. Please try different options.");
        }

        QuizSession session = new QuizSession();
        session.setPlayerName(request.getPlayerName().trim());
        session.setTotalQuestions(questions.size());
        session.setScore(0);
        session.setPercentage(0.0);
        session.setStatus("STARTED");

        QuizSession savedSession = sessionRepository.save(session);
        log.info("Created QuizSession with ID: {}", savedSession.getId());

        QuizStartResponse response = new QuizStartResponse();
        response.setSessionId(savedSession.getId());
        response.setPlayerName(savedSession.getPlayerName());
        response.setTotalQuestions(savedSession.getTotalQuestions());
        response.setTimePerQuestionSeconds(TIME_PER_QUESTION_SECONDS);
        response.setQuestions(questions);

        return response;
    }

    // ============================================================
    // 2. SUBMIT QUIZ
    // ============================================================

    @Transactional
    public QuizResultDto submitQuiz(QuizSubmitRequest request) {
        log.info("Submitting quiz for session ID: {}", request.getSessionId());

        QuizSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException(
                        "Quiz session not found with ID: " + request.getSessionId()));

        if ("COMPLETED".equals(session.getStatus())) {
            throw new RuntimeException("This quiz session has already been submitted.");
        }

        int correctCount = 0;
        List<QuizAttempt> attempts = new ArrayList<>();

        for (QuizSubmitRequest.AnswerDto answerDto : request.getAnswers()) {

            Question question = questionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException(
                            "Question not found with ID: " + answerDto.getQuestionId()));

            boolean isCorrect = false;
            if (answerDto.getSelectedOption() != null && !answerDto.getSelectedOption().isBlank()) {
                isCorrect = answerDto.getSelectedOption()
                        .trim()
                        .equalsIgnoreCase(question.getCorrectOption());
            }

            if (isCorrect) correctCount++;

            QuizAttempt attempt = new QuizAttempt();
            attempt.setSession(session);
            attempt.setQuestion(question);
            attempt.setSelectedOption(answerDto.getSelectedOption());
            attempt.setIsCorrect(isCorrect);
            attempts.add(attempt);
        }

        attemptRepository.saveAll(attempts);

        double percentage = session.getTotalQuestions() > 0
                ? ((double) correctCount / session.getTotalQuestions()) * 100
                : 0.0;

        session.setScore(correctCount);
        session.setPercentage(Math.round(percentage * 10.0) / 10.0);
        session.setTimeTakenSeconds(request.getTimeTakenSeconds());
        session.setStatus("COMPLETED");
        session.setCompletedAt(LocalDateTime.now());
        sessionRepository.save(session);

        log.info("Quiz completed: player={}, score={}/{}, percentage={}%",
                session.getPlayerName(), correctCount, session.getTotalQuestions(), percentage);

        return buildResultDto(session, attempts);
    }

    // ============================================================
    // 3. GET RESULT
    // ============================================================

    @Transactional(readOnly = true)
    public QuizResultDto getResult(Long sessionId) {
        log.info("Fetching result for session ID: {}", sessionId);

        QuizSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Quiz result not found for session ID: " + sessionId));

        List<QuizAttempt> attempts = attemptRepository.findBySessionOrderByQuestionOrder(session);

        return buildResultDto(session, attempts);
    }

    // ============================================================
    // PRIVATE HELPERS
    // ============================================================

    private QuizResultDto buildResultDto(QuizSession session, List<QuizAttempt> attempts) {

        List<QuizResultDto.AnswerReviewDto> reviewList = new ArrayList<>();
        int order = 1;

        for (QuizAttempt attempt : attempts) {
            Question q = attempt.getQuestion();
            QuizResultDto.AnswerReviewDto review = QuizResultDto.AnswerReviewDto.builder()
                    .questionId(q.getId())
                    .questionText(q.getQuestionText())
                    .optionA(q.getOptionA())
                    .optionB(q.getOptionB())
                    .optionC(q.getOptionC())
                    .optionD(q.getOptionD())
                    .selectedOption(attempt.getSelectedOption())
                    .correctOption(q.getCorrectOption())
                    .isCorrect(attempt.getIsCorrect())
                    .questionOrder(order++)
                    .build();
            reviewList.add(review);
        }

        double pct = session.getPercentage() != null ? session.getPercentage() : 0.0;
        String grade   = calculateGrade(pct);
        String message = getMotivationalMessage(grade);

        return QuizResultDto.builder()
                .sessionId(session.getId())
                .playerName(session.getPlayerName())
                .score(session.getScore())
                .totalQuestions(session.getTotalQuestions())
                .percentage(pct)
                .timeTakenSeconds(session.getTimeTakenSeconds())
                .grade(grade)
                .message(message)
                .completedAt(session.getCompletedAt())
                .answers(reviewList)
                .build();
    }

    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A";
        if (percentage >= 75) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }

    private String getMotivationalMessage(String grade) {
        return switch (grade) {
            case "A" -> "🏆 Outstanding! You're a Quiz Champion!";
            case "B" -> "🌟 Great Job! You really know your stuff!";
            case "C" -> "👍 Good effort! A bit more practice and you'll ace it!";
            case "D" -> "💪 Keep Going! Every expert was once a beginner!";
            default  -> "📚 Don't give up! Learning takes time and effort!";
        };
    }
}
