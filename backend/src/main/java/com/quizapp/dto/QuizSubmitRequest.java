package com.quizapp.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * ============================================================
 * QuizSubmitRequest - Incoming request for POST /api/quiz/submit
 * ============================================================
 */
public class QuizSubmitRequest {

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    private Long timeTakenSeconds;

    private List<AnswerDto> answers;

    // ---- Constructors ----

    public QuizSubmitRequest() {}

    public QuizSubmitRequest(Long sessionId, Long timeTakenSeconds, List<AnswerDto> answers) {
        this.sessionId = sessionId;
        this.timeTakenSeconds = timeTakenSeconds;
        this.answers = answers;
    }

    // ---- Getters ----

    public Long getSessionId() { return sessionId; }
    public Long getTimeTakenSeconds() { return timeTakenSeconds; }
    public List<AnswerDto> getAnswers() { return answers; }

    // ---- Setters ----

    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public void setTimeTakenSeconds(Long timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }
    public void setAnswers(List<AnswerDto> answers) { this.answers = answers; }

    @Override
    public String toString() {
        return "QuizSubmitRequest{sessionId=" + sessionId + "}";
    }

    // ============================================================
    // Inner class - AnswerDto
    // ============================================================
    public static class AnswerDto {

        @NotNull(message = "Question ID is required")
        private Long questionId;

        private String selectedOption;

        public AnswerDto() {}

        public AnswerDto(Long questionId, String selectedOption) {
            this.questionId = questionId;
            this.selectedOption = selectedOption;
        }

        public Long getQuestionId() { return questionId; }
        public String getSelectedOption() { return selectedOption; }

        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
    }
}
