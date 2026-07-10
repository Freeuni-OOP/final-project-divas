package org.example.quiz.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordHasherTest {

    @Test
    public void sameInputProducesSameHash() {
        String salt = "abc123";
        String h1 = PasswordHasher.hash("mypassword", salt);
        String h2 = PasswordHasher.hash("mypassword", salt);
        assertEquals(h1, h2);
    }

    @Test
    public void differentSaltProducesDifferentHash() {
        String h1 = PasswordHasher.hash("mypassword", "salt1");
        String h2 = PasswordHasher.hash("mypassword", "salt2");
        assertNotEquals(h1, h2);
    }

    @Test
    public void differentPasswordProducesDifferentHash() {
        String salt = "abc123";
        String correct = PasswordHasher.hash("mypassword", salt);
        String wrong = PasswordHasher.hash("wrongpassword", salt);
        assertNotEquals(correct, wrong);
    }

    @Test
    public void hashDoesNotContainRawPassword() {
        String hash = PasswordHasher.hash("secret", "salt");
        assertFalse(hash.contains("secret"));
    }

    @Test
    public void generatedSaltsAreDifferent() {
        String s1 = PasswordHasher.generateSalt();
        String s2 = PasswordHasher.generateSalt();
        assertNotEquals(s1, s2);
    }

    @Test
    public void generatedSaltIsNotEmpty() {
        assertFalse(PasswordHasher.generateSalt().isEmpty());
    }
}
