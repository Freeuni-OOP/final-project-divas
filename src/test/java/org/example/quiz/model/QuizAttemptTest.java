package org.example.quiz.model;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class QuizAttemptTest {
    @Test
    public void testZeroPercent() {
        QuizAttempt qa = new QuizAttempt();
        qa.setScoreCorrect(5);
        qa.setScoreTotal(0);
        assertEquals(0, qa.getPercent());
    }
    @Test
    public void testGetPercent() {
        QuizAttempt qa = new QuizAttempt();
        qa.setScoreCorrect(8);
        qa.setScoreTotal(10);
        assertEquals(80, qa.getPercent());
    }
    @Test
    public void testRounded() {
        QuizAttempt qa = new QuizAttempt();
        qa.setScoreCorrect(1);
        qa.setScoreTotal(6);
        assertEquals(17, qa.getPercent());
    }
    @Test
    public void testGettersAndSetters() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        QuizAttempt qa = new QuizAttempt();

        qa.setId(1);
        qa.setQuizId(2);
        qa.setUserId(3);
        qa.setScoreCorrect(8);
        qa.setScoreTotal(10);
        qa.setTimeSeconds(120);
        qa.setPractice(true);
        qa.setTakenAt(ts);
        qa.setUsername("tatia");
        qa.setQuizTitle("NewQuiz");

        assertEquals(1, qa.getId());
        assertEquals(2, qa.getQuizId());
        assertEquals(3, qa.getUserId());
        assertEquals(8, qa.getScoreCorrect());
        assertEquals(10, qa.getScoreTotal());
        assertEquals(120, qa.getTimeSeconds());
        assertTrue(qa.isPractice());
        assertEquals(ts, qa.getTakenAt());
        assertEquals("tatia", qa.getUsername());
        assertEquals("NewQuiz", qa.getQuizTitle());
    }
}
