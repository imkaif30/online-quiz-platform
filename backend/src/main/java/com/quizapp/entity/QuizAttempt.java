package com.quizapp.entity;

import jakarta.persistence.*;

/**
 * ============================================================
 * QuizAttempt Entity - Maps to 'quiz_attempts' table
 * ============================================================
 * Records each individual answer a user gives during a session.
 */
@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private QuizSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "selected_option", length = 1)
    private String selectedOption;

    @Column(name = "is_correct")
    private Boolean isCorrect = false;

    @Column(name = "question_order")
    private Integer questionOrder;

    // ---- Constructors ----

    public QuizAttempt() {}

    public QuizAttempt(Long id, QuizSession session, Question question,
                       String selectedOption, Boolean isCorrect, Integer questionOrder) {
        this.id = id;
        this.session = session;
        this.question = question;
        this.selectedOption = selectedOption;
        this.isCorrect = isCorrect;
        this.questionOrder = questionOrder;
    }

    // ---- Getters ----

    public Long getId() { return id; }
    public QuizSession getSession() { return session; }
    public Question getQuestion() { return question; }
    public String getSelectedOption() { return selectedOption; }
    public Boolean getIsCorrect() { return isCorrect; }
    public Integer getQuestionOrder() { return questionOrder; }

    // ---- Setters ----

    public void setId(Long id) { this.id = id; }
    public void setSession(QuizSession session) { this.session = session; }
    public void setQuestion(Question question) { this.question = question; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }

    @Override
    public String toString() {
        return "QuizAttempt{id=" + id + ", isCorrect=" + isCorrect + "}";
    }
}
