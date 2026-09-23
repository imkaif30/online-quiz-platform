package com.quizapp.dto;

import java.util.List;

/**
 * ============================================================
 * QuizStartResponse - Response for POST /api/quiz/start
 * ============================================================
 * Returned to the frontend when a new quiz session begins.
 */
public class QuizStartResponse {

    private Long sessionId;
    private String playerName;
    private Integer totalQuestions;
    private Integer timePerQuestionSeconds = 30;
    private List<QuestionDto> questions;

    // ---- Constructors ----

    public QuizStartResponse() {}

    public QuizStartResponse(Long sessionId, String playerName, Integer totalQuestions,
                             Integer timePerQuestionSeconds, List<QuestionDto> questions) {
        this.sessionId = sessionId;
        this.playerName = playerName;
        this.totalQuestions = totalQuestions;
        this.timePerQuestionSeconds = timePerQuestionSeconds;
        this.questions = questions;
    }

    // ---- Getters ----

    public Long getSessionId() { return sessionId; }
    public String getPlayerName() { return playerName; }
    public Integer getTotalQuestions() { return totalQuestions; }
    public Integer getTimePerQuestionSeconds() { return timePerQuestionSeconds; }
    public List<QuestionDto> getQuestions() { return questions; }

    // ---- Setters ----

    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
    public void setTimePerQuestionSeconds(Integer timePerQuestionSeconds) { this.timePerQuestionSeconds = timePerQuestionSeconds; }
    public void setQuestions(List<QuestionDto> questions) { this.questions = questions; }

    @Override
    public String toString() {
        return "QuizStartResponse{sessionId=" + sessionId + ", playerName='" + playerName + "'}";
    }
}
