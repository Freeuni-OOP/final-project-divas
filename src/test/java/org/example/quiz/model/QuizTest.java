package org.example.quiz.model;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class QuizTest {

    @Test
    public void gettersReturnSetValues() {
        Quiz q = new Quiz();
        q.setId(5);
        q.setTitle("Geography");
        q.setDescription("A quiz about the world");
        q.setCreatorId(42);
        q.setRandomize(true);
        q.setMultiPage(true);
        q.setImmediateCorrect(true);
        q.setPracticeAllowed(true);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        q.setCreatedAt(now);

        assertEquals(5, q.getId());
        assertEquals("Geography", q.getTitle());
        assertEquals("A quiz about the world", q.getDescription());
        assertEquals(42, q.getCreatorId());
        assertTrue(q.isRandomize());
        assertTrue(q.isMultiPage());
        assertTrue(q.isImmediateCorrect());
        assertTrue(q.isPracticeAllowed());
        assertEquals(now, q.getCreatedAt());
    }

    @Test
    public void booleanFlagsDefaultToFalse() {
        Quiz q = new Quiz();
        assertFalse(q.isRandomize());
        assertFalse(q.isMultiPage());
        assertFalse(q.isImmediateCorrect());
        assertFalse(q.isPracticeAllowed());
    }

    @Test
    public void questionsListStartsEmptyAndIsSettable() {
        Quiz q = new Quiz();
        assertNotNull(q.getQuestions());
        assertTrue(q.getQuestions().isEmpty());

        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Q1"));
        q.setQuestions(questions);
        assertEquals(1, q.getQuestions().size());
    }
}
