package com.quizapp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * ============================================================
 * QuizSession Entity - Maps to 'quiz_sessions' table
 * ============================================================
 * Tracks every quiz attempt by a user.
 */
@Entity
@Table(name = "quiz_sessions")
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_name", nullable = false, length = 100)
    private String playerName;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "score")
    private Integer score = 0;

    @Column(name = "percentage")
    private Double percentage = 0.0;

    @Column(name = "time_taken_seconds")
    private Long timeTakenSeconds;

    @Column(name = "status", length = 20)
    private String status = "STARTED";

    @CreationTimestamp
    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // ---- Constructors ----

    public QuizSession() {}

    public QuizSession(Long id, String playerName, Integer totalQuestions, Integer score,
                       Double percentage, Long timeTakenSeconds, String status,
                       LocalDateTime startedAt, LocalDateTime completedAt) {
        this.id = id;
        this.playerName = playerName;
        this.totalQuestions = totalQuestions;
        this.score = score;
        this.percentage = percentage;
        this.timeTakenSeconds = timeTakenSeconds;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    // ---- Getters ----

    public Long getId() { return id; }
    public String getPlayerName() { return playerName; }
    public Integer getTotalQuestions() { return totalQuestions; }
    public Integer getScore() { return score; }
    public Double getPercentage() { return percentage; }
    public Long getTimeTakenSeconds() { return timeTakenSeconds; }
    public String getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    // ---- Setters ----

    public void setId(Long id) { this.id = id; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
    public void setScore(Integer score) { this.score = score; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    public void setTimeTakenSeconds(Long timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }
    public void setStatus(String status) { this.status = status; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    @Override
    public String toString() {
        return "QuizSession{id=" + id + ", playerName='" + playerName + "', status='" + status + "'}";
    }
}
