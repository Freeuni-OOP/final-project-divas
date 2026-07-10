package org.example.quiz.model;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class MessageTest {
    @Test
    public void testGettersAndSetters() {
        Message m = new Message();

        Timestamp ts = new Timestamp(System.currentTimeMillis());

        m.setId(1);
        m.setSenderId(10);
        m.setRecipientId(20);
        m.setType(Message.Type.CHALLENGE);
        m.setBody("new quiz!");
        m.setQuizId(5L);
        m.setRead(true);
        m.setCreatedAt(ts);
        m.setSenderUsername("tatia");
        m.setQuizTitle("Quiz1");

        assertEquals(1, m.getId());
        assertEquals(10, m.getSenderId());
        assertEquals(20, m.getRecipientId());
        assertEquals(Message.Type.CHALLENGE, m.getType());
        assertEquals("new quiz!", m.getBody());
        assertEquals(Long.valueOf(5), m.getQuizId());
        assertTrue(m.isRead());
        assertEquals(ts, m.getCreatedAt());
        assertEquals("tatia", m.getSenderUsername());
        assertEquals("Quiz1", m.getQuizTitle());
    }

}
