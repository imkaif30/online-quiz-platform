/* ============================================================
   script.js — Online Quiz App | Full JavaScript Logic
   ============================================================
   Architecture:
   - State management via a central `quizState` object
   - All API calls use the native fetch() API with async/await
   - Timer managed via setInterval with circular SVG animation
   - SPA-style page navigation (no page reloads)

   API Base URL:
     Change BASE_URL below if your backend runs on a different port.
   ============================================================ */

'use strict';

// ============================================================
// CONFIGURATION — Change this to match your backend URL
// ============================================================
const BASE_URL = 'http://localhost:8080/api';

// ============================================================
// GLOBAL QUIZ STATE
// Keeps track of everything during a live quiz session
// ============================================================
let quizState = {
    sessionId: null,     // ID returned from /api/quiz/start
    playerName: '',       // Name entered on the start screen
    questions: [],       // Array of QuestionDto objects from API
    answers: {},       // { questionId: selectedOption } map
    currentIndex: 0,        // Index of currently displayed question
    timer: null,     // setInterval reference for countdown
    timePerQuestion: 30,       // Seconds allowed per question
    timeLeft: 30,       // Current countdown value
    totalStartTime: null,     // Date.now() when quiz started
    totalTimeTaken: 0,        // Seconds taken for entire quiz
    result: null,     // QuizResultDto from API after submission
};

// ============================================================
// DOM ELEMENT REFERENCES
// Cache all frequently accessed elements for performance
// ============================================================
const dom = {
    // Pages
    homePage: document.getElementById('home-page'),
    quizPage: document.getElementById('quiz-page'),
    resultPage: document.getElementById('result-page'),

    // Loading & Toast
    loadingOverlay: document.getElementById('loadingOverlay'),
    loadingText: document.getElementById('loadingText'),
    toast: document.getElementById('toast'),

    // Home Page
    startForm: document.getElementById('startForm'),
    playerNameInput: document.getElementById('playerName'),
    questionCount: document.getElementById('questionCount'),
    categorySelect: document.getElementById('category'),
    difficultySelect: document.getElementById('difficulty'),
    statCount: document.getElementById('statCount'),

    // Quiz Page
    quizPlayerName: document.getElementById('quizPlayerName'),
    currentQNum: document.getElementById('currentQNum'),
    totalQNum: document.getElementById('totalQNum'),
    progressBar: document.getElementById('progressBar'),
    dotNavigator: document.getElementById('dotNavigator'),
    questionCard: document.getElementById('questionCard'),
    questionCategory: document.getElementById('questionCategory'),
    questionDifficulty: document.getElementById('questionDifficulty'),
    questionNumBadge: document.getElementById('questionNumBadge'),
    questionText: document.getElementById('questionText'),
    optionsGrid: document.getElementById('optionsGrid'),
    prevBtn: document.getElementById('prevBtn'),
    nextBtn: document.getElementById('nextBtn'),
    submitBtn: document.getElementById('submitBtn'),
    timerText: document.getElementById('timerText'),
    timerArc: document.getElementById('timerArc'),

    // Result Page
    resultEmoji: document.getElementById('resultEmoji'),
    resultGradeText: document.getElementById('resultGradeText'),
    resultMessage: document.getElementById('resultMessage'),
    scoreArc: document.getElementById('scoreArc'),
    scoreNumber: document.getElementById('scoreNumber'),
    scoreTotal: document.getElementById('scoreTotal'),
    resultPercentage: document.getElementById('resultPercentage'),
    resultGrade: document.getElementById('resultGrade'),
    resultTime: document.getElementById('resultTime'),
    reviewList: document.getElementById('reviewList'),
};

// ============================================================
// PAGE NAVIGATION
// SPA-style: hide all pages, show the target page
// ============================================================
function showPage(pageId) {
    document.querySelectorAll('.page').forEach(page => {
        page.classList.remove('active');
    });
    const target = document.getElementById(pageId);
    if (target) {
        target.classList.add('active');
        // Scroll to top on page change
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
}

// ============================================================
// LOADING OVERLAY
// ============================================================
function showLoading(message = 'Loading...') {
    dom.loadingText.textContent = message;
    dom.loadingOverlay.classList.add('visible');
}

function hideLoading() {
    dom.loadingOverlay.classList.remove('visible');
}

// ============================================================
// TOAST NOTIFICATIONS
// ============================================================
let toastTimeout = null;

function showToast(message, type = 'error', duration = 3500) {
    dom.toast.textContent = message;
    dom.toast.className = `toast ${type} show`;

    // Clear any existing toast timer
    if (toastTimeout) clearTimeout(toastTimeout);

    toastTimeout = setTimeout(() => {
        dom.toast.classList.remove('show');
    }, duration);
}

// ============================================================
// API HELPER — Generic fetch wrapper
// ============================================================

/**
 * Makes an HTTP request to the backend API.
 * Handles JSON parsing and error responses centrally.
 *
 * @param {string} endpoint  - API path e.g. '/quiz/start'
 * @param {string} method    - 'GET' | 'POST'
 * @param {object} body      - Request body (for POST requests)
 * @returns {Promise<object>} - Parsed JSON response
 */
async function apiCall(endpoint, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
    };

    // Only attach body for non-GET requests
    if (body && method !== 'GET') {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(`${BASE_URL}${endpoint}`, options);

    // Parse the JSON response body
    const data = await response.json().catch(() => ({}));

    // If HTTP status is not 2xx, treat as error
    if (!response.ok) {
        const errorMsg = data.error || `Request failed with status ${response.status}`;
        throw new Error(errorMsg);
    }

    return data;
}

// ============================================================
// INITIALIZATION — Runs when the page first loads
// ============================================================
async function init() {
    // Load categories from backend for the dropdown
    await loadCategories();

    // Attach form submit listener
    dom.startForm.addEventListener('submit', handleStartQuiz);

    // Load total question count
    loadQuestionCount();
}

// ============================================================
// LOAD CATEGORIES — Populates the category dropdown
// ============================================================
async function loadCategories() {
    try {
        const categories = await apiCall('/questions/categories');
        categories.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat;
            option.textContent = cat;
            dom.categorySelect.appendChild(option);
        });
    } catch (err) {
        // Categories are optional - don't block the app if this fails
        console.warn('Could not load categories:', err.message);
    }
}

// ============================================================
// LOAD QUESTION COUNT — Updates the stat badge
// ============================================================
async function loadQuestionCount() {
    try {
        const data = await apiCall('/questions/count');
        if (dom.statCount && data.count !== undefined) {
            dom.statCount.textContent = data.count;
        }
    } catch (err) {
        console.warn('Could not load question count:', err.message);
    }
}

// ============================================================
// START QUIZ
// Called when the user submits the start form
// ============================================================
async function handleStartQuiz(event) {
    event.preventDefault(); // Prevent default form submission (page reload)

    // ---- Validate player name ----
    const playerName = dom.playerNameInput.value.trim();
    if (!playerName) {
        showToast('⚠️ Please enter your name to start!');
        dom.playerNameInput.focus();
        return;
    }
    if (playerName.length > 50) {
        showToast('⚠️ Name cannot exceed 50 characters!');
        dom.playerNameInput.focus();
        return;
    }

    // ---- Validate question count ----
    const questionCount = parseInt(dom.questionCount.value);
    if (isNaN(questionCount) || questionCount < 1 || questionCount > 20) {
        showToast('⚠️ Question count must be between 1 and 20!');
        return;
    }

    // ---- Build request payload ----
    const requestBody = {
        playerName: playerName,
        questionCount: Number(questionCount),

        category:
            !dom.categorySelect.value ||
                dom.categorySelect.value === 'All Categories'
                ? null
                : dom.categorySelect.value,

        difficulty:
            !dom.difficultySelect.value ||
                dom.difficultySelect.value === 'all'
                ? null
                : dom.difficultySelect.value.toUpperCase()
    };

    console.log("Sending Request:", requestBody);

    showLoading('Setting up your quiz... ⚡');

    try {
        // POST /api/quiz/start
        const response = await apiCall('/quiz/start', 'POST', requestBody);

        // Store session data in state
        quizState.sessionId = response.sessionId;
        quizState.playerName = response.playerName;
        quizState.questions = response.questions;
        quizState.timePerQuestion = response.timePerQuestionSeconds || 30;
        quizState.answers = {};
        quizState.currentIndex = 0;
        quizState.totalStartTime = Date.now();

        hideLoading();

        // Navigate to quiz page
        showPage('quiz-page');
        renderQuiz();

    } catch (err) {
        hideLoading();
        showToast(`❌ ${err.message}`);
        console.error('Start quiz error:', err);
    }
}

// ============================================================
// RENDER QUIZ
// Initializes the quiz UI with question data
// ============================================================
function renderQuiz() {
    const total = quizState.questions.length;

    // Set player name label
    dom.quizPlayerName.textContent = `👤 ${quizState.playerName}`;
    dom.totalQNum.textContent = total;

    // Build navigation dots
    buildDotNavigator(total);

    // Show the first question
    renderQuestion(0);
}

// ============================================================
// BUILD DOT NAVIGATOR
// Creates numbered dots for each question
// ============================================================
function buildDotNavigator(total) {
    dom.dotNavigator.innerHTML = '';
    for (let i = 0; i < total; i++) {
        const dot = document.createElement('button');
        dot.className = 'dot';
        dot.textContent = i + 1;
        dot.setAttribute('aria-label', `Go to question ${i + 1}`);
        dot.addEventListener('click', () => navigateToDot(i));
        dom.dotNavigator.appendChild(dot);
    }
}

// ============================================================
// RENDER QUESTION
// Displays a single question with its options
// ============================================================
function renderQuestion(index, direction = 'right') {
    const q = quizState.questions[index];
    if (!q) return;

    quizState.currentIndex = index;

    // ---- Update header info ----
    dom.currentQNum.textContent = index + 1;
    dom.totalQNum.textContent = quizState.questions.length;

    // ---- Update progress bar ----
    const progressPct = ((index + 1) / quizState.questions.length) * 100;
    dom.progressBar.style.width = `${progressPct}%`;

    // ---- Update question meta ----
    dom.questionCategory.textContent = q.category || 'General';
    dom.questionNumBadge.textContent = `Q${index + 1}`;

    // Set difficulty badge class
    const diff = (q.difficulty || 'easy').toLowerCase();
    dom.questionDifficulty.className = `badge badge-${diff}`;
    dom.questionDifficulty.textContent = q.difficulty || 'Easy';

    // ---- Update question text ----
    dom.questionText.textContent = q.questionText;

    // ---- Add slide animation to question card ----
    dom.questionCard.classList.remove('slide-in-right', 'slide-in-left');
    void dom.questionCard.offsetWidth; // Trigger reflow to restart animation
    dom.questionCard.classList.add(direction === 'right' ? 'slide-in-right' : 'slide-in-left');

    // ---- Render answer options ----
    renderOptions(q, index);

    // ---- Update dot navigator ----
    updateDots(index);

    // ---- Update navigation buttons ----
    const isFirst = index === 0;
    const isLast = index === quizState.questions.length - 1;
    dom.prevBtn.disabled = isFirst;
    dom.nextBtn.style.display = isLast ? 'none' : 'inline-flex';
    dom.submitBtn.style.display = isLast ? 'inline-flex' : 'none';

    // ---- Reset and start timer ----
    resetTimer();
    startTimer();
}

// ============================================================
// RENDER OPTIONS
// Creates the 4 answer option buttons
// ============================================================
function renderOptions(question, questionIndex) {
    dom.optionsGrid.innerHTML = '';

    const options = [
        { letter: 'A', text: question.optionA },
        { letter: 'B', text: question.optionB },
        { letter: 'C', text: question.optionC },
        { letter: 'D', text: question.optionD },
    ];

    // The previously selected answer for this question (if any)
    const savedAnswer = quizState.answers[question.id];

    options.forEach(opt => {
        const btn = document.createElement('button');
        btn.className = 'option-btn';
        btn.setAttribute('data-option', opt.letter);
        btn.setAttribute('aria-label', `Option ${opt.letter}: ${opt.text}`);
        btn.id = `option-${opt.letter}`;

        // Mark as selected if user previously answered this question
        if (savedAnswer === opt.letter) {
            btn.classList.add('selected');
        }

        btn.innerHTML = `
            <span class="option-letter">${opt.letter}</span>
            <span class="option-text">${opt.text}</span>
        `;

        btn.addEventListener('click', () => selectOption(question.id, opt.letter, btn));
        dom.optionsGrid.appendChild(btn);
    });
}

// ============================================================
// SELECT OPTION
// Called when user clicks an answer option
// ============================================================
function selectOption(questionId, letter, clickedBtn) {
    // Remove 'selected' class from all options
    document.querySelectorAll('.option-btn').forEach(btn => {
        btn.classList.remove('selected');
    });

    // Highlight the clicked option
    clickedBtn.classList.add('selected');

    // Save the answer in state
    quizState.answers[questionId] = letter;

    // Update the dot for this question to show it's answered
    updateDots(quizState.currentIndex);
}

// ============================================================
// UPDATE DOTS
// Updates the visual state of all navigation dots
// ============================================================
function updateDots(currentIndex) {
    const dots = dom.dotNavigator.querySelectorAll('.dot');
    dots.forEach((dot, i) => {
        const question = quizState.questions[i];
        const isAnswered = question && quizState.answers[question.id];
        const isCurrent = i === currentIndex;

        dot.className = 'dot';
        if (isCurrent) dot.classList.add('current');
        else if (isAnswered) dot.classList.add('answered');
    });
}

// ============================================================
// NAVIGATION — Previous / Next / Dot click
// ============================================================
function goToNext() {
    const next = quizState.currentIndex + 1;
    if (next < quizState.questions.length) {
        renderQuestion(next, 'right');
    }
}

function goToPrev() {
    const prev = quizState.currentIndex - 1;
    if (prev >= 0) {
        renderQuestion(prev, 'left');
    }
}

function navigateToDot(index) {
    const direction = index > quizState.currentIndex ? 'right' : 'left';
    renderQuestion(index, direction);
}

// ============================================================
// TIMER — Circular Countdown
// ============================================================

// SVG circle circumference: 2 * π * r = 2 * 3.14159 * 26 ≈ 163.4
const CIRCUMFERENCE = 2 * Math.PI * 26;

function resetTimer() {
    // Stop any existing timer
    if (quizState.timer) {
        clearInterval(quizState.timer);
        quizState.timer = null;
    }

    quizState.timeLeft = quizState.timePerQuestion;

    // Reset SVG arc
    dom.timerArc.style.strokeDashoffset = 0;
    dom.timerArc.style.stroke = 'var(--color-secondary)';
    dom.timerText.textContent = quizState.timeLeft;
    dom.timerText.classList.remove('urgent');
}

function startTimer() {
    quizState.timer = setInterval(() => {
        quizState.timeLeft--;

        // Update the displayed number
        dom.timerText.textContent = quizState.timeLeft;

        // Update the SVG circle arc
        const elapsed = quizState.timePerQuestion - quizState.timeLeft;
        const progress = elapsed / quizState.timePerQuestion;
        const offset = CIRCUMFERENCE * progress;
        dom.timerArc.style.strokeDashoffset = offset;

        // Turn red and pulse when ≤ 5 seconds remain
        if (quizState.timeLeft <= 5) {
            dom.timerArc.style.stroke = 'var(--color-danger)';
            dom.timerText.classList.add('urgent');
        }

        // Time's up! Auto-advance to next question
        if (quizState.timeLeft <= 0) {
            clearInterval(quizState.timer);
            quizState.timer = null;

            const isLast = quizState.currentIndex === quizState.questions.length - 1;
            if (isLast) {
                // Auto-submit on last question timeout
                submitQuiz();
            } else {
                // Skip to next question
                showToast("⏰ Time's up! Moving to next question.", 'error', 2000);
                goToNext();
            }
        }
    }, 1000); // Fires every 1 second
}

// ============================================================
// SUBMIT QUIZ
// Called when user clicks "Submit Quiz" or last timer runs out
// ============================================================
async function submitQuiz() {
    // Stop the timer
    if (quizState.timer) {
        clearInterval(quizState.timer);
        quizState.timer = null;
    }

    // Calculate total time taken
    const totalMs = Date.now() - quizState.totalStartTime;
    quizState.totalTimeTaken = Math.round(totalMs / 1000);

    // ---- Check if any questions are unanswered ----
    const answeredCount = Object.keys(quizState.answers).length;
    const totalCount = quizState.questions.length;

    if (answeredCount < totalCount) {
        const unanswered = totalCount - answeredCount;
        // Ask user to confirm skip
        const confirmed = confirm(
            `You have ${unanswered} unanswered question(s).\n` +
            'Unanswered questions will be marked as wrong.\n\n' +
            'Are you sure you want to submit?'
        );
        if (!confirmed) {
            // Resume timer for current question
            startTimer();
            return;
        }
    }

    // ---- Build answers array for API ----
    const answers = quizState.questions.map(q => ({
        questionId: q.id,
        selectedOption: quizState.answers[q.id] || null, // null = skipped
    }));

    // ---- Build request body ----
    const requestBody = {
        sessionId: quizState.sessionId,
        timeTakenSeconds: quizState.totalTimeTaken,
        answers: answers,
    };

    showLoading('Calculating your score... 🧮');

    try {
        // POST /api/quiz/submit
        const result = await apiCall('/quiz/submit', 'POST', requestBody);
        quizState.result = result;

        hideLoading();

        // Navigate to result page
        showPage('result-page');
        renderResult(result);

    } catch (err) {
        hideLoading();
        showToast(`❌ Submission failed: ${err.message}`);
        console.error('Submit error:', err);
    }
}

// ============================================================
// RENDER RESULT
// Populates the result page with the score and review
// ============================================================
function renderResult(result) {
    // ---- Emoji + Grade heading ----
    const gradeConfig = {
        'A': { emoji: '🏆', text: 'Outstanding!', color: 'var(--color-accent)' },
        'B': { emoji: '🌟', text: 'Excellent!', color: 'var(--color-primary-light)' },
        'C': { emoji: '👍', text: 'Good Job!', color: 'var(--color-secondary)' },
        'D': { emoji: '💪', text: 'Keep Going!', color: 'var(--color-warning)' },
        'F': { emoji: '📚', text: "Don't Give Up!", color: 'var(--color-danger)' },
    };

    const grade = result.grade || 'C';
    const config = gradeConfig[grade] || gradeConfig['C'];

    dom.resultEmoji.textContent = config.emoji;
    dom.resultGradeText.textContent = config.text;
    dom.resultMessage.textContent = result.message || '';

    // ---- Score Circle Animation ----
    const score = result.score || 0;
    const total = result.totalQuestions || 10;
    const percentage = result.percentage || 0;

    dom.scoreNumber.textContent = score;
    dom.scoreTotal.textContent = `/ ${total}`;

    // Animate the SVG circle arc
    // Circumference of score circle: 2 * π * 75 ≈ 471
    const scoreCircumference = 2 * Math.PI * 75;
    dom.scoreArc.style.strokeDasharray = scoreCircumference;
    dom.scoreArc.style.strokeDashoffset = scoreCircumference; // Start at 0%

    // Set arc color based on grade
    dom.scoreArc.style.stroke = config.color;

    // Animate to actual percentage (with a short delay for visual effect)
    setTimeout(() => {
        const offset = scoreCircumference - (percentage / 100) * scoreCircumference;
        dom.scoreArc.style.strokeDashoffset = offset;
    }, 300);

    // ---- Stats ----
    dom.resultPercentage.textContent = `${percentage}%`;
    dom.resultGrade.textContent = grade;
    dom.resultGrade.style.color = config.color;

    // Format time (seconds → mm:ss)
    const seconds = result.timeTakenSeconds || quizState.totalTimeTaken || 0;
    dom.resultTime.textContent = formatTime(seconds);

    // ---- Answer Review List ----
    renderReviewList(result.answers || []);
}

// ============================================================
// RENDER REVIEW LIST
// Shows per-question correct/wrong breakdown
// ============================================================
function renderReviewList(answers) {
    dom.reviewList.innerHTML = '';

    if (!answers || answers.length === 0) {
        dom.reviewList.innerHTML = '<p style="color: var(--color-text-muted);">No answer data available.</p>';
        return;
    }

    answers.forEach((answer, idx) => {
        const isCorrect = answer.isCorrect;
        const isSkipped = !answer.selectedOption;

        let statusClass = isSkipped ? 'skipped' : (isCorrect ? 'correct' : 'wrong');
        let statusIcon = isSkipped ? '⏭️' : (isCorrect ? '✅' : '❌');

        // Map option letter to actual text
        const correctText = getOptionText(answer, answer.correctOption);
        const selectedText = answer.selectedOption
            ? getOptionText(answer, answer.selectedOption)
            : 'Skipped';

        const item = document.createElement('div');
        item.className = `review-item ${statusClass}`;
        item.innerHTML = `
            <div class="review-q-header">
                <span class="review-status-icon">${statusIcon}</span>
                <p class="review-q-text">
                    <strong>Q${idx + 1}.</strong> ${answer.questionText || ''}
                </p>
            </div>
            <div class="review-answers">
                <div class="review-answer-tag your-answer ${isCorrect ? 'is-correct' : ''}">
                    <strong>Your Answer:</strong>&nbsp;
                    ${answer.selectedOption ? `(${answer.selectedOption}) ` : ''}${selectedText}
                </div>
                <div class="review-answer-tag correct-answer">
                    <strong>Correct:</strong>&nbsp;
                    (${answer.correctOption}) ${correctText}
                </div>
            </div>
        `;

        dom.reviewList.appendChild(item);
    });
}

// Helper: Get the text for a given option letter from an answer object
function getOptionText(answer, letter) {
    if (!letter) return 'N/A';
    const map = {
        'A': answer.optionA || '',
        'B': answer.optionB || '',
        'C': answer.optionC || '',
        'D': answer.optionD || '',
    };
    return map[letter.toUpperCase()] || '';
}

// ============================================================
// PLAY AGAIN — Reset state and go back to home
// ============================================================
function playAgain() {
    // Stop any running timer
    if (quizState.timer) {
        clearInterval(quizState.timer);
    }

    // Reset state
    quizState = {
        sessionId: null,
        playerName: '',
        questions: [],
        answers: {},
        currentIndex: 0,
        timer: null,
        timePerQuestion: 30,
        timeLeft: 30,
        totalStartTime: null,
        totalTimeTaken: 0,
        result: null,
    };

    // Clear the start form
    dom.startForm.reset();

    // Navigate back to home
    showPage('home-page');
}

// ============================================================
// SHARE RESULT — Web Share API / clipboard fallback
// ============================================================
function shareResult() {
    if (!quizState.result) return;

    const r = quizState.result;
    const text = `🧠 QuizMaster Result\n` +
        `Player: ${r.playerName}\n` +
        `Score: ${r.score}/${r.totalQuestions} (${r.percentage}%)\n` +
        `Grade: ${r.grade} ${r.message}\n` +
        `Time: ${formatTime(r.timeTakenSeconds)}`;

    // Use native Web Share API if available (mobile/modern browsers)
    if (navigator.share) {
        navigator.share({ title: 'My Quiz Result', text })
            .catch(err => console.log('Share cancelled:', err));
    } else {
        // Fallback: copy to clipboard
        navigator.clipboard.writeText(text)
            .then(() => showToast('📋 Result copied to clipboard!', 'success'))
            .catch(() => showToast('Could not copy to clipboard.'));
    }
}

// ============================================================
// UTILITY FUNCTIONS
// ============================================================

/**
 * Formats seconds into MM:SS string.
 * Example: formatTime(125) → "2:05"
 */
function formatTime(seconds) {
    if (!seconds || seconds < 0) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${String(secs).padStart(2, '0')}`;
}

// ============================================================
// ERROR HANDLER — Global uncaught error handling
// ============================================================
window.addEventListener('unhandledrejection', (event) => {
    console.error('Unhandled promise rejection:', event.reason);
    showToast('An unexpected error occurred. Please try again.');
});

// ============================================================
// BOOTSTRAP — Initialize when DOM is ready
// ============================================================
document.addEventListener('DOMContentLoaded', init);
