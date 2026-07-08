package org.example.quiz.model;

import java.sql.Timestamp;

/** One record of a user taking a quiz. Written by Part C. */
public class QuizAttempt {
    private long id;
    private long quizId;
    private long userId;
    private int scoreCorrect;   // correct answer-slots
    private int scoreTotal;     // total answer-slots
    private int timeSeconds;
    private boolean practice;
    private Timestamp takenAt;

    // convenience fields for display
    private String username;
    private String quizTitle;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getQuizId() { return quizId; }
    public void setQuizId(long quizId) { this.quizId = quizId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public int getScoreCorrect() { return scoreCorrect; }
    public void setScoreCorrect(int scoreCorrect) { this.scoreCorrect = scoreCorrect; }

    public int getScoreTotal() { return scoreTotal; }
    public void setScoreTotal(int scoreTotal) { this.scoreTotal = scoreTotal; }

    public int getTimeSeconds() { return timeSeconds; }
    public void setTimeSeconds(int timeSeconds) { this.timeSeconds = timeSeconds; }

    public boolean isPractice() { return practice; }
    public void setPractice(boolean practice) { this.practice = practice; }

    public Timestamp getTakenAt() { return takenAt; }
    public void setTakenAt(Timestamp takenAt) { this.takenAt = takenAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public int getPercent() {
        return scoreTotal == 0 ? 0 : (int) Math.round(100.0 * scoreCorrect / scoreTotal);
    }
}
