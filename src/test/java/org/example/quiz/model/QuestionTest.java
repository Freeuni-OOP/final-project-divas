package org.example.quiz.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class QuestionTest {

    @Test
    public void constructorSetsQuestionText() {
        Question q = new Question("What is 2+2?");
        assertEquals("What is 2+2?", q.getQuestion());
    }

    @Test
    public void gettersReturnSetValues() {
        Question q = new Question("prompt");
        q.setId(7);
        q.setQuizId(3);
        q.setQuestionType("MULTIPLE_CHOICE");
        q.setNum(2);
        q.setImageUrl("http://example.com/img.png");

        assertEquals(7, q.getId());
        assertEquals(3, q.getQuizId());
        assertEquals("MULTIPLE_CHOICE", q.getQuestionType());
        assertEquals(2, q.getNum());
        assertEquals("http://example.com/img.png", q.getImageUrl());
    }

    @Test
    public void imageUrlIsNullByDefault() {
        Question q = new Question("prompt");
        assertNull(q.getImageUrl());
    }
}
