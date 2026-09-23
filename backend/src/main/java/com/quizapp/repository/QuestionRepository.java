package com.quizapp.repository;

import com.quizapp.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================
 * QuestionRepository - Database access layer for Questions
 * ============================================================
 * Extends JpaRepository which gives us free CRUD methods:
 *   save(), findById(), findAll(), deleteById(), count(), etc.
 *
 * We only add CUSTOM query methods here.
 * Spring Data JPA auto-implements them at runtime!
 * ============================================================
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Find all active questions shuffled randomly.
     * Uses JPQL (Java Persistence Query Language) with RANDOM() function.
     *
     * NOTE: RANDOM() works in PostgreSQL. For MySQL use RAND().
     *
     * @param limit  - how many questions to fetch
     * @return       - randomized list of active questions
     */
    @Query(value = "SELECT * FROM questions WHERE is_active = true ORDER BY RANDOM() LIMIT :limit",
           nativeQuery = true) // nativeQuery=true = raw SQL, not JPQL
    List<Question> findRandomActiveQuestions(@Param("limit") int limit);

    /**
     * Fetch random questions filtered by BOTH category AND difficulty.
     * Used when user selects both filters on the start screen.
     */
    @Query(value = "SELECT * FROM questions WHERE is_active = true " +
                   "AND LOWER(category) = LOWER(:category) " +
                   "AND LOWER(difficulty) = LOWER(:difficulty) " +
                   "ORDER BY RANDOM() LIMIT :limit",
           nativeQuery = true)
    List<Question> findRandomByCategAndDifficulty(
            @Param("category") String category,
            @Param("difficulty") String difficulty,
            @Param("limit") int limit);

    /**
     * Fetch random questions filtered by category only.
     */
    @Query(value = "SELECT * FROM questions WHERE is_active = true " +
                   "AND LOWER(category) = LOWER(:category) " +
                   "ORDER BY RANDOM() LIMIT :limit",
           nativeQuery = true)
    List<Question> findRandomByCategory(
            @Param("category") String category,
            @Param("limit") int limit);

    /**
     * Fetch random questions filtered by difficulty only.
     */
    @Query(value = "SELECT * FROM questions WHERE is_active = true " +
                   "AND LOWER(difficulty) = LOWER(:difficulty) " +
                   "ORDER BY RANDOM() LIMIT :limit",
           nativeQuery = true)
    List<Question> findRandomByDifficulty(
            @Param("difficulty") String difficulty,
            @Param("limit") int limit);

    /**
     * Get all distinct categories (for dropdown in frontend)
     * e.g., ["Java", "Spring Boot", "SQL", "General"]
     */
    @Query("SELECT DISTINCT q.category FROM Question q WHERE q.isActive = true AND q.category IS NOT NULL")
    List<String> findAllActiveCategories();

    /**
     * Count how many active questions are available.
     * Used to validate if requested count is achievable.
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.isActive = true")
    long countActiveQuestions();
}
