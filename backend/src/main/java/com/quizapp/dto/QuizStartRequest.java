package com.quizapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * ============================================================
 * QuizStartRequest - Incoming request body for POST /api/quiz/start
 * ============================================================
 */
public class QuizStartRequest {

    @NotBlank(message = "Player name is required")
    private String playerName;

    @Min(value = 1, message = "Minimum 1 question required")
    @Max(value = 20, message = "Maximum 20 questions allowed")
    private Integer questionCount = 10;

    private String category;

    private String difficulty;

    // ---- Constructors ----

    public QuizStartRequest() {}

    public QuizStartRequest(String playerName, Integer questionCount,
                            String category, String difficulty) {
        this.playerName = playerName;
        this.questionCount = questionCount;
        this.category = category;
        this.difficulty = difficulty;
    }

    // ---- Getters ----

    public String getPlayerName() { return playerName; }
    public Integer getQuestionCount() { return questionCount; }
    public String getCategory() { return category; }
    public String getDifficulty() { return difficulty; }

    // ---- Setters ----

    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
    public void setCategory(String category) { this.category = category; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    @Override
    public String toString() {
        return "QuizStartRequest{playerName='" + playerName + "', questionCount=" + questionCount + "}";
    }
}
