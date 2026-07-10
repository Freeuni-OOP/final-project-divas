package org.example.quiz.model;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class AchievementTest {

    @Test
    public void testDefaultConstructorAndSetters() {
        Achievement a = new Achievement();
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        a.setId(1);
        a.setUserId(10);
        a.setAchievementCode("quiz");
        a.setEarnedAt(ts);
        a.setLabel("project");
        a.setDescription("completed");

        assertEquals(1, a.getId());
        assertEquals(10, a.getUserId());
        assertEquals("quiz", a.getAchievementCode());
        assertEquals(ts, a.getEarnedAt());
        assertEquals("project", a.getLabel());
        assertEquals("completed", a.getDescription());
    }

    @Test
    public void testParameterizedConstructor() {
        Achievement a = new Achievement(25, "AMATEUR_AUTHOR");

        assertEquals(25, a.getUserId());
        assertEquals("AMATEUR_AUTHOR", a.getAchievementCode());

        assertEquals(0, a.getId());
        assertNull(a.getEarnedAt());
        assertNull(a.getLabel());
        assertNull(a.getDescription());
    }
}