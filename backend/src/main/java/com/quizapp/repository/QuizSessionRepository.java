package com.quizapp.repository;

import com.quizapp.entity.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================
 * QuizSessionRepository - Database access for quiz sessions
 * ============================================================
 * JpaRepository<QuizSession, Long>:
 *   - QuizSession = the entity type
 *   - Long        = the type of the primary key
 * ============================================================
 */
@Repository
public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {

    /**
     * Find all sessions for a specific player name.
     * Used to show quiz history for a player.
     * Spring Data JPA auto-generates the SQL from the method name!
     */
    List<QuizSession> findByPlayerNameIgnoreCase(String playerName);

    /**
     * Get top 10 scores for leaderboard.
     * Orders by percentage DESC then by time taken ASC (faster is better).
     */
    @Query("SELECT s FROM QuizSession s WHERE s.status = 'COMPLETED' " +
           "ORDER BY s.percentage DESC, s.timeTakenSeconds ASC")
    List<QuizSession> findTop10ByOrderByPercentageDesc(
            org.springframework.data.domain.Pageable pageable);

    /**
     * Find all completed sessions (status = 'COMPLETED')
     */
    List<QuizSession> findByStatus(String status);
}
