package org.example.quiz.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class QuestionOptionTest {

    @Test
    public void constructorSetsOptionText() {
        QuestionOption o = new QuestionOption("Blue");
        assertEquals("Blue", o.getOptionText());
    }

    @Test
    public void gettersReturnSetValues() {
        QuestionOption o = new QuestionOption("Blue");
        o.setId(11);
        o.setQuestionId(4);
        o.setCorrect(true);
        o.setPosition(1);

        assertEquals(11, o.getId());
        assertEquals(4, o.getQuestionId());
        assertTrue(o.isCorrect());
        assertEquals(1, o.getPosition());
    }

    @Test
    public void correctDefaultsToFalse() {
        QuestionOption o = new QuestionOption("Red");
        assertFalse(o.isCorrect());
    }
}
