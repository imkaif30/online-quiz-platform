-- ============================================================
-- Online Quiz App - PostgreSQL Database Schema
-- ============================================================
-- HOW TO USE:
--   1. Open pgAdmin OR psql terminal
--   2. Create the database first (see step below)
--   3. Run this entire script
--
-- Step 1: Create Database (run in psql as superuser)
--   CREATE DATABASE quizdb;
--   \c quizdb
--
-- Step 2: Run this file
--   psql -U postgres -d quizdb -f schema.sql
-- ============================================================


-- ============================================================
-- TABLE 1: questions
-- Stores all quiz questions with options and correct answer
-- ============================================================
CREATE TABLE IF NOT EXISTS questions (
    id              BIGSERIAL PRIMARY KEY,           -- Auto-increment PK
    question_text   TEXT        NOT NULL,            -- The question
    option_a        VARCHAR(500) NOT NULL,           -- Choice A
    option_b        VARCHAR(500) NOT NULL,           -- Choice B
    option_c        VARCHAR(500) NOT NULL,           -- Choice C
    option_d        VARCHAR(500) NOT NULL,           -- Choice D
    correct_option  CHAR(1)     NOT NULL             -- 'A', 'B', 'C', or 'D'
                    CHECK (correct_option IN ('A','B','C','D')),
    category        VARCHAR(100),                   -- e.g. 'Java', 'Spring Boot'
    difficulty      VARCHAR(20)                      -- 'EASY', 'MEDIUM', 'HARD'
                    CHECK (difficulty IN ('EASY','MEDIUM','HARD')),
    is_active       BOOLEAN     DEFAULT TRUE,        -- Soft delete / disable flag
    created_at      TIMESTAMP   DEFAULT NOW()        -- Auto timestamp
);


-- ============================================================
-- TABLE 2: quiz_sessions
-- Tracks each quiz taken by a player
-- ============================================================
CREATE TABLE IF NOT EXISTS quiz_sessions (
    id                  BIGSERIAL PRIMARY KEY,
    player_name         VARCHAR(100) NOT NULL,
    total_questions     INT          NOT NULL,
    score               INT          DEFAULT 0,
    percentage          DECIMAL(5,2) DEFAULT 0.0,
    time_taken_seconds  BIGINT,
    status              VARCHAR(20)  DEFAULT 'STARTED'
                        CHECK (status IN ('STARTED','COMPLETED','ABANDONED')),
    started_at          TIMESTAMP    DEFAULT NOW(),
    completed_at        TIMESTAMP
);


-- ============================================================
-- TABLE 3: quiz_attempts
-- Records each individual answer within a session
-- ============================================================
CREATE TABLE IF NOT EXISTS quiz_attempts (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT      NOT NULL REFERENCES quiz_sessions(id) ON DELETE CASCADE,
    question_id     BIGINT      NOT NULL REFERENCES questions(id)     ON DELETE CASCADE,
    selected_option CHAR(1)     CHECK (selected_option IN ('A','B','C','D')),
    is_correct      BOOLEAN     DEFAULT FALSE,
    question_order  INT         NOT NULL
);


-- ============================================================
-- INDEXES - Improve query performance
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_questions_category   ON questions(category);
CREATE INDEX IF NOT EXISTS idx_questions_difficulty  ON questions(difficulty);
CREATE INDEX IF NOT EXISTS idx_questions_is_active   ON questions(is_active);
CREATE INDEX IF NOT EXISTS idx_sessions_player_name  ON quiz_sessions(player_name);
CREATE INDEX IF NOT EXISTS idx_sessions_status       ON quiz_sessions(status);
CREATE INDEX IF NOT EXISTS idx_attempts_session_id   ON quiz_attempts(session_id);
CREATE INDEX IF NOT EXISTS idx_attempts_question_id  ON quiz_attempts(question_id);


-- ============================================================
-- SAMPLE DATA: Java Questions (10 Questions)
-- ============================================================
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, category, difficulty) VALUES
(
    'What does JVM stand for?',
    'Java Virtual Machine',
    'Java Variable Method',
    'Java Visual Manager',
    'Java Verified Module',
    'A', 'Java', 'EASY'
),
(
    'Which keyword is used to inherit a class in Java?',
    'implements',
    'inherits',
    'extends',
    'super',
    'C', 'Java', 'EASY'
),
(
    'Which of the following is NOT a Java primitive data type?',
    'int',
    'boolean',
    'String',
    'double',
    'C', 'Java', 'EASY'
),
(
    'What is the default value of a boolean variable in Java?',
    'true',
    'false',
    'null',
    '0',
    'B', 'Java', 'EASY'
),
(
    'Which method is the entry point of a Java program?',
    'start()',
    'run()',
    'init()',
    'main()',
    'D', 'Java', 'EASY'
),
(
    'What is the output of: System.out.println(10 / 3); in Java?',
    '3.33',
    '3',
    '3.0',
    'Error',
    'B', 'Java', 'MEDIUM'
),
(
    'Which interface must be implemented to create a thread in Java?',
    'Runnable',
    'Callable',
    'Thread',
    'Executor',
    'A', 'Java', 'MEDIUM'
),
(
    'What does the ''final'' keyword mean when applied to a variable?',
    'The variable is private',
    'The variable cannot be changed once assigned',
    'The variable is static',
    'The variable is thread-safe',
    'B', 'Java', 'MEDIUM'
),
(
    'Which collection class allows duplicate elements and maintains insertion order?',
    'HashSet',
    'TreeSet',
    'ArrayList',
    'HashMap',
    'C', 'Java', 'MEDIUM'
),
(
    'What is autoboxing in Java?',
    'Converting int to double automatically',
    'Automatic conversion between primitive types and their wrapper classes',
    'Converting String to int automatically',
    'Creating objects automatically',
    'B', 'Java', 'HARD'
);


-- ============================================================
-- SAMPLE DATA: Spring Boot Questions (10 Questions)
-- ============================================================
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, category, difficulty) VALUES
(
    'What annotation is used to mark a class as a Spring Bean component?',
    '@Bean',
    '@Component',
    '@Service',
    'Both B and C',
    'D', 'Spring Boot', 'EASY'
),
(
    'Which annotation creates a REST API controller in Spring Boot?',
    '@Controller',
    '@RestController',
    '@RequestMapping',
    '@Service',
    'B', 'Spring Boot', 'EASY'
),
(
    'What is the default port Spring Boot starts on?',
    '3000',
    '80',
    '8080',
    '9090',
    'C', 'Spring Boot', 'EASY'
),
(
    'Which annotation maps a method to handle GET HTTP requests?',
    '@PostMapping',
    '@RequestMapping(method=GET)',
    '@GetMapping',
    'Both B and C',
    'D', 'Spring Boot', 'EASY'
),
(
    'What does @Autowired do in Spring?',
    'Creates a new object',
    'Automatically injects a dependency',
    'Maps a URL to a method',
    'Marks a class as a configuration',
    'B', 'Spring Boot', 'EASY'
),
(
    'Which file configures Spring Boot application settings?',
    'settings.xml',
    'config.properties',
    'application.properties',
    'spring.config',
    'C', 'Spring Boot', 'MEDIUM'
),
(
    'What does JPA stand for?',
    'Java Persistence API',
    'Java Program Architecture',
    'Java Parameter Annotation',
    'Java Performance Analyzer',
    'A', 'Spring Boot', 'MEDIUM'
),
(
    'Which annotation marks a class as a JPA entity (database table)?',
    '@Table',
    '@Column',
    '@Entity',
    '@Repository',
    'C', 'Spring Boot', 'MEDIUM'
),
(
    'What does @Transactional annotation do?',
    'Encrypts the data before saving',
    'Ensures all DB operations succeed or all fail together',
    'Maps URLs to methods',
    'Validates request body',
    'B', 'Spring Boot', 'MEDIUM'
),
(
    'Which HTTP status code should be returned when a resource is successfully CREATED?',
    '200 OK',
    '201 Created',
    '204 No Content',
    '400 Bad Request',
    'B', 'Spring Boot', 'HARD'
);


-- ============================================================
-- SAMPLE DATA: SQL / Database Questions (5 Questions)
-- ============================================================
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, category, difficulty) VALUES
(
    'Which SQL keyword retrieves data from a database table?',
    'FETCH',
    'GET',
    'SELECT',
    'READ',
    'C', 'SQL', 'EASY'
),
(
    'What does PRIMARY KEY mean in a database?',
    'First column in the table',
    'A column that uniquely identifies each row',
    'A column that allows NULL values',
    'A column with the most data',
    'B', 'SQL', 'EASY'
),
(
    'Which SQL command removes all rows from a table but keeps the structure?',
    'DROP TABLE',
    'DELETE FROM table',
    'TRUNCATE TABLE',
    'REMOVE FROM table',
    'C', 'SQL', 'MEDIUM'
),
(
    'What does a FOREIGN KEY do?',
    'Creates a new table',
    'Links a column in one table to the PRIMARY KEY of another table',
    'Speeds up queries',
    'Prevents NULL values',
    'B', 'SQL', 'MEDIUM'
),
(
    'Which PostgreSQL function returns a random integer between 1 and 10?',
    'RAND(1,10)',
    'RANDOM(10)',
    'FLOOR(RANDOM() * 10) + 1',
    'INT(RANDOM() * 10)',
    'C', 'SQL', 'HARD'
);


-- ============================================================
-- SAMPLE DATA: General Knowledge Questions (5 Questions)
-- ============================================================
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, category, difficulty) VALUES
(
    'What does API stand for?',
    'Application Programming Interface',
    'Automated Programming Input',
    'Application Protocol Integration',
    'Advanced Program Interaction',
    'A', 'General', 'EASY'
),
(
    'What does HTTP stand for?',
    'Hyper Text Transfer Protocol',
    'High Tech Transfer Protocol',
    'Hyper Transfer Text Protocol',
    'Highly Typed Text Protocol',
    'A', 'General', 'EASY'
),
(
    'Which data format is most commonly used in REST APIs?',
    'XML',
    'CSV',
    'JSON',
    'HTML',
    'C', 'General', 'EASY'
),
(
    'What is the difference between GET and POST HTTP methods?',
    'GET is faster than POST',
    'GET retrieves data, POST sends data to the server',
    'POST is only for file uploads',
    'There is no difference',
    'B', 'General', 'MEDIUM'
),
(
    'What does CRUD stand for in software development?',
    'Create, Read, Update, Delete',
    'Connect, Retrieve, Upload, Delete',
    'Compile, Run, Update, Deploy',
    'Create, Retrieve, Undo, Delete',
    'A', 'General', 'EASY'
);


-- ============================================================
-- VERIFY: Check that data was inserted correctly
-- ============================================================
SELECT category, difficulty, COUNT(*) AS question_count
FROM questions
GROUP BY category, difficulty
ORDER BY category, difficulty;
