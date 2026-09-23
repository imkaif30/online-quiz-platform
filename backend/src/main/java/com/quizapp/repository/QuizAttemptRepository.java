package com.quizapp.repository;

import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================
 * QuizAttemptRepository - Database access for individual answers
 * ============================================================
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    /**
     * Find all attempts for a specific quiz session.
     * Used to build the detailed result/review page.
     * Orders by question position for consistent display.
     */
    List<QuizAttempt> findBySessionOrderByQuestionOrder(QuizSession session);

    /**
     * Count how many correct answers exist in a session.
     * Used as a verification/cross-check for the score.
     */
    @Query("SELECT COUNT(a) FROM QuizAttempt a WHERE a.session.id = :sessionId AND a.isCorrect = true")
    long countCorrectAnswersBySessionId(@Param("sessionId") Long sessionId);

    /**
     * Delete all attempts belonging to a session.
     * Useful for "restart quiz" functionality in the future.
     */
    void deleteBySession(QuizSession session);
}
