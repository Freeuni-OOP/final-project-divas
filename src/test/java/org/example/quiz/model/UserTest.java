package org.example.quiz.model;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void constructorSetsIdAndUsername() {
        User u = new User(1, "kesaria");
        assertEquals(1, u.getId());
        assertEquals("kesaria", u.getUsername());
    }

    @Test
    public void settersUpdateFields() {
        User u = new User();
        u.setId(2);
        u.setUsername("admin");
        u.setAdmin(true);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        u.setCreatedAt(now);

        assertEquals(2, u.getId());
        assertEquals("admin", u.getUsername());
        assertTrue(u.isAdmin());
        assertEquals(now, u.getCreatedAt());
    }

    @Test
    public void adminDefaultsToFalse() {
        User u = new User();
        assertFalse(u.isAdmin());
    }
}
