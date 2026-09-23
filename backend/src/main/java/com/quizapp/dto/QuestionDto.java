package com.quizapp.dto;

/**
 * ============================================================
 * QuestionDto - Data Transfer Object for Question
 * ============================================================
 * This DTO is what the API sends to the frontend.
 * NOTE: 'correctOption' is intentionally NOT included here.
 * It is only included in AnswerReviewDto (sent after submission).
 * ============================================================
 */
public class QuestionDto {

    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String category;
    private String difficulty;
    private Integer questionOrder;

    // ---- Constructors ----

    public QuestionDto() {}

    public QuestionDto(Long id, String questionText, String optionA, String optionB,
                       String optionC, String optionD, String category,
                       String difficulty, Integer questionOrder) {
        this.id = id;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.category = category;
        this.difficulty = difficulty;
        this.questionOrder = questionOrder;
    }

    // ---- Getters ----

    public Long getId() { return id; }
    public String getQuestionText() { return questionText; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCategory() { return category; }
    public String getDifficulty() { return difficulty; }
    public Integer getQuestionOrder() { return questionOrder; }

    // ---- Setters ----

    public void setId(Long id) { this.id = id; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public void setCategory(String category) { this.category = category; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }

    @Override
    public String toString() {
        return "QuestionDto{id=" + id + ", questionText='" + questionText + "'}";
    }
}
