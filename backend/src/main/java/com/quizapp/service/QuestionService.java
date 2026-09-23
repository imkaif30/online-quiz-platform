package com.quizapp.service;

import com.quizapp.dto.QuestionDto;
import com.quizapp.entity.Question;
import com.quizapp.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ============================================================
 * QuestionService - Business logic for Questions
 * ============================================================
 */
@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionRepository questionRepository;

    // Explicit constructor injection (replaces @RequiredArgsConstructor)
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    /**
     * Fetch a random set of questions for a quiz session.
     */
    public List<QuestionDto> getRandomQuestions(int count, String category, String difficulty) {
        log.info("Fetching {} random questions | category={} | difficulty={}", count, category, difficulty);

        List<Question> questions;

        boolean hasCategory   = category   != null && !category.isBlank();
        boolean hasDifficulty = difficulty != null && !difficulty.isBlank();

        if (hasCategory && hasDifficulty) {
            questions = questionRepository.findRandomByCategAndDifficulty(category, difficulty, count);
        } else if (hasCategory) {
            questions = questionRepository.findRandomByCategory(category, count);
        } else if (hasDifficulty) {
            questions = questionRepository.findRandomByDifficulty(difficulty, count);
        } else {
            questions = questionRepository.findRandomActiveQuestions(count);
        }

        log.info("Found {} questions from database", questions.size());

        return IntStream.range(0, questions.size())
                .mapToObj(index -> convertToDto(questions.get(index), index + 1))
                .collect(Collectors.toList());
    }

    /**
     * Get a single question by its ID.
     */
    public QuestionDto getQuestionById(Long id) {
        log.debug("Fetching question with id={}", id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        return convertToDto(question, 1);
    }

    /**
     * Fetch all available categories for the dropdown.
     */
    public List<String> getAllCategories() {
        return questionRepository.findAllActiveCategories();
    }

    /**
     * Count how many active questions are in the database.
     */
    public long getActiveQuestionCount() {
        return questionRepository.countActiveQuestions();
    }

    // ============================================================
    // Private helper: Convert Question entity → QuestionDto
    // ============================================================
    private QuestionDto convertToDto(Question question, int order) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setCategory(question.getCategory());
        dto.setDifficulty(question.getDifficulty());
        dto.setQuestionOrder(order);
        // NOTE: correctOption is NOT set here - that's intentional!
        return dto;
    }
}
