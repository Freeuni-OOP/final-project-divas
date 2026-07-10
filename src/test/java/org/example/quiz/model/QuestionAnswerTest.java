package org.example.quiz.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class QuestionAnswerTest {

    @Test
    public void constructorSetsAnswerText() {
        QuestionAnswer a = new QuestionAnswer("Tbilisi");
        assertEquals("Tbilisi", a.getAnswer());
    }

    @Test
    public void gettersReturnSetValues() {
        QuestionAnswer a = new QuestionAnswer("Tbilisi");
        a.setId(9);
        a.setQuestionId(6);

        assertEquals(9, a.getId());
        assertEquals(6, a.getQuestionId());
    }
}
