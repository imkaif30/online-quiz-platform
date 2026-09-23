package com.quizapp.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================
 * QuizResultDto - Response for GET /api/quiz/result/{sessionId}
 * ============================================================
 * Sent to the frontend after quiz submission.
 * Contains full result breakdown including correct answers.
 * ============================================================
 */
public class QuizResultDto {

    private Long sessionId;
    private String playerName;
    private Integer score;
    private Integer totalQuestions;
    private Double percentage;
    private Long timeTakenSeconds;
    private String grade;
    private String message;
    private LocalDateTime completedAt;
    private List<AnswerReviewDto> answers;

    // ---- No-args constructor ----

    public QuizResultDto() {}

    // ---- All-args constructor ----

    public QuizResultDto(Long sessionId, String playerName, Integer score, Integer totalQuestions,
                         Double percentage, Long timeTakenSeconds, String grade, String message,
                         LocalDateTime completedAt, List<AnswerReviewDto> answers) {
        this.sessionId = sessionId;
        this.playerName = playerName;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.timeTakenSeconds = timeTakenSeconds;
        this.grade = grade;
        this.message = message;
        this.completedAt = completedAt;
        this.answers = answers;
    }

    // ---- Getters ----

    public Long getSessionId() { return sessionId; }
    public String getPlayerName() { return playerName; }
    public Integer getScore() { return score; }
    public Integer getTotalQuestions() { return totalQuestions; }
    public Double getPercentage() { return percentage; }
    public Long getTimeTakenSeconds() { return timeTakenSeconds; }
    public String getGrade() { return grade; }
    public String getMessage() { return message; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public List<AnswerReviewDto> getAnswers() { return answers; }

    // ---- Setters ----

    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setScore(Integer score) { this.score = score; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    public void setTimeTakenSeconds(Long timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setMessage(String message) { this.message = message; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setAnswers(List<AnswerReviewDto> answers) { this.answers = answers; }

    // ============================================================
    // Builder pattern (replaces Lombok @Builder)
    // ============================================================
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long sessionId;
        private String playerName;
        private Integer score;
        private Integer totalQuestions;
        private Double percentage;
        private Long timeTakenSeconds;
        private String grade;
        private String message;
        private LocalDateTime completedAt;
        private List<AnswerReviewDto> answers;

        public Builder sessionId(Long sessionId) { this.sessionId = sessionId; return this; }
        public Builder playerName(String playerName) { this.playerName = playerName; return this; }
        public Builder score(Integer score) { this.score = score; return this; }
        public Builder totalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; return this; }
        public Builder percentage(Double percentage) { this.percentage = percentage; return this; }
        public Builder timeTakenSeconds(Long timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; return this; }
        public Builder grade(String grade) { this.grade = grade; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public Builder answers(List<AnswerReviewDto> answers) { this.answers = answers; return this; }

        public QuizResultDto build() {
            return new QuizResultDto(sessionId, playerName, score, totalQuestions,
                    percentage, timeTakenSeconds, grade, message, completedAt, answers);
        }
    }

    // ============================================================
    // Inner DTO - Per-question answer review
    // ============================================================
    public static class AnswerReviewDto {

        private Long questionId;
        private String questionText;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String selectedOption;
        private String correctOption;
        private Boolean isCorrect;
        private Integer questionOrder;

        public AnswerReviewDto() {}

        public AnswerReviewDto(Long questionId, String questionText, String optionA, String optionB,
                               String optionC, String optionD, String selectedOption,
                               String correctOption, Boolean isCorrect, Integer questionOrder) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.selectedOption = selectedOption;
            this.correctOption = correctOption;
            this.isCorrect = isCorrect;
            this.questionOrder = questionOrder;
        }

        // Getters
        public Long getQuestionId() { return questionId; }
        public String getQuestionText() { return questionText; }
        public String getOptionA() { return optionA; }
        public String getOptionB() { return optionB; }
        public String getOptionC() { return optionC; }
        public String getOptionD() { return optionD; }
        public String getSelectedOption() { return selectedOption; }
        public String getCorrectOption() { return correctOption; }
        public Boolean getIsCorrect() { return isCorrect; }
        public Integer getQuestionOrder() { return questionOrder; }

        // Setters
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public void setOptionA(String optionA) { this.optionA = optionA; }
        public void setOptionB(String optionB) { this.optionB = optionB; }
        public void setOptionC(String optionC) { this.optionC = optionC; }
        public void setOptionD(String optionD) { this.optionD = optionD; }
        public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
        public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }
        public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
        public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }

        // Builder
        public static AnswerReviewDtoBuilder builder() { return new AnswerReviewDtoBuilder(); }

        public static class AnswerReviewDtoBuilder {
            private Long questionId;
            private String questionText;
            private String optionA, optionB, optionC, optionD;
            private String selectedOption, correctOption;
            private Boolean isCorrect;
            private Integer questionOrder;

            public AnswerReviewDtoBuilder questionId(Long v) { this.questionId = v; return this; }
            public AnswerReviewDtoBuilder questionText(String v) { this.questionText = v; return this; }
            public AnswerReviewDtoBuilder optionA(String v) { this.optionA = v; return this; }
            public AnswerReviewDtoBuilder optionB(String v) { this.optionB = v; return this; }
            public AnswerReviewDtoBuilder optionC(String v) { this.optionC = v; return this; }
            public AnswerReviewDtoBuilder optionD(String v) { this.optionD = v; return this; }
            public AnswerReviewDtoBuilder selectedOption(String v) { this.selectedOption = v; return this; }
            public AnswerReviewDtoBuilder correctOption(String v) { this.correctOption = v; return this; }
            public AnswerReviewDtoBuilder isCorrect(Boolean v) { this.isCorrect = v; return this; }
            public AnswerReviewDtoBuilder questionOrder(Integer v) { this.questionOrder = v; return this; }

            public AnswerReviewDto build() {
                return new AnswerReviewDto(questionId, questionText, optionA, optionB,
                        optionC, optionD, selectedOption, correctOption, isCorrect, questionOrder);
            }
        }
    }
}
