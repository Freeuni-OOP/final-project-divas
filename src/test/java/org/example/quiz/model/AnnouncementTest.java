package org.example.quiz.model;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class AnnouncementTest {

    @Test
    public void constructorSetsFields() {
        Announcement a = new Announcement("Notice", "Site down Friday", 1, true);
        assertEquals("Notice", a.getTitle());
        assertEquals("Site down Friday", a.getMessage());
        assertEquals(1, a.getAuthorID());
        assertTrue(a.isActive());
    }

    @Test
    public void settersUpdateFields() {
        Announcement a = new Announcement("Old title", "Old body", 1, true);
        a.setId(3);
        a.setTitle("New title");
        a.setMessage("New body");
        a.setAuthorID(2);
        a.setActive(false);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        a.setCreatedAt(now);

        assertEquals(3, a.getId());
        assertEquals("New title", a.getTitle());
        assertEquals("New body", a.getMessage());
        assertEquals(2, a.getAuthorID());
        assertFalse(a.isActive());
        assertEquals(now, a.getCreatedAt());
    }
}
