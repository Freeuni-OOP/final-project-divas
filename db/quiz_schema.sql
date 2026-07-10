
--  Shared team schema.
--    B: users, quizzes, questions, question_options, question_answers, announcements
--    C: friendships, messages   (+ reads/writes quiz_attempts)
--    D: achievements, user_achievements
--  This file is auto-run by docker-compose on first MySQL start.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

--Part B: Users / Auth
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(64)  NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    salt          VARCHAR(64)  NOT NULL,
    is_admin      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--Part B: Quizzes / Questions
CREATE TABLE IF NOT EXISTS quizzes (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    title             VARCHAR(255) NOT NULL,
    description       TEXT,
    creator_id        BIGINT       NOT NULL,
    randomize         BOOLEAN      NOT NULL DEFAULT FALSE,
    multi_page        BOOLEAN      NOT NULL DEFAULT FALSE,
    immediate_correct BOOLEAN      NOT NULL DEFAULT FALSE,
    practice_allowed  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS questions (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id       BIGINT       NOT NULL,
    -- QUESTION_RESPONSE, FILL_BLANK, MULTIPLE_CHOICE, PICTURE_RESPONSE
    question_type VARCHAR(32)  NOT NULL,
    prompt        TEXT         NOT NULL,
    image_url     VARCHAR(512),          -- used by PICTURE_RESPONSE
    ordered       BOOLEAN      NOT NULL DEFAULT FALSE, -- multi-answer ordering
    position      INT          NOT NULL DEFAULT 0,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- options shown for MULTIPLE_CHOICE questions
CREATE TABLE IF NOT EXISTS question_options (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT       NOT NULL,
    option_text VARCHAR(512) NOT NULL,
    is_correct  BOOLEAN      NOT NULL DEFAULT FALSE,
    position    INT          NOT NULL DEFAULT 0,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- accepted textual answers (a question may accept several)
CREATE TABLE IF NOT EXISTS question_answers (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT       NOT NULL,
    answer_text VARCHAR(512) NOT NULL,
    slot        INT          NOT NULL DEFAULT 0,  -- multi-answer slot index
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Part C: Quiz attempts (shared results table)
-- Written by C's quiz-taking flow, read by B/D for stats & leaderboards.
CREATE TABLE IF NOT EXISTS quiz_attempts (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id       BIGINT    NOT NULL,
    user_id       BIGINT    NOT NULL,
    score_correct INT       NOT NULL,   -- correct answer-slots
    score_total   INT       NOT NULL,   -- total answer-slots possible
    time_seconds  INT       NOT NULL,   -- time taken
    practice      BOOLEAN   NOT NULL DEFAULT FALSE,
    taken_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)   ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--Part C: Friendships
CREATE TABLE IF NOT EXISTS friendships (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id  BIGINT      NOT NULL,   -- user who sent the request
    addressee_id  BIGINT      NOT NULL,   -- user who receives the request
    -- PENDING, ACCEPTED, REJECTED
    status        VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at  TIMESTAMP   NULL,
    UNIQUE KEY uq_pair (requester_id, addressee_id),
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (addressee_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--Part C: Mail messages
CREATE TABLE IF NOT EXISTS messages (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id    BIGINT      NOT NULL,
    recipient_id BIGINT      NOT NULL,
    -- FRIEND_REQUEST, CHALLENGE, NOTE
    msg_type     VARCHAR(16) NOT NULL,
    body         TEXT,
    quiz_id      BIGINT      NULL,      -- set for CHALLENGE messages
    is_read      BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id)    REFERENCES users(id)   ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES users(id)   ON DELETE CASCADE,
    FOREIGN KEY (quiz_id)      REFERENCES quizzes(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Part D: Achievements
CREATE TABLE IF NOT EXISTS user_achievements (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    achievement_code VARCHAR(32) NOT NULL,
    earned_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_ach (user_id, achievement_code),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--Part B: Admin (Announcements)
CREATE TABLE IF NOT EXISTS announcements (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    body       TEXT         NOT NULL,
    created_by BIGINT       NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--  Views used by C & D
-- Leaderboard: best attempt per user per quiz, ranked by correctness then time.
CREATE OR REPLACE VIEW v_quiz_leaderboard AS
SELECT a.quiz_id,
       a.user_id,
       u.username,
       a.score_correct,
       a.score_total,
       a.time_seconds,
       a.taken_at
FROM quiz_attempts a
JOIN users u ON u.id = a.user_id
WHERE a.practice = FALSE;

-- Per-quiz summary statistics.
CREATE OR REPLACE VIEW v_quiz_stats AS
SELECT quiz_id,
       COUNT(*)                                            AS attempts_count,
       AVG(score_correct / NULLIF(score_total,0)) * 100    AS avg_percent,
       AVG(time_seconds)                                   AS avg_time_seconds
FROM quiz_attempts
WHERE practice = FALSE
GROUP BY quiz_id;

SET FOREIGN_KEY_CHECKS = 1;
