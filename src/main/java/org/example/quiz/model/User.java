package org.example.quiz.model;

import java.sql.Timestamp;

/** Full auth fields are owned by Part A. */
public class User {
    private long id;
    private String username;
    private boolean admin;
    private Timestamp createdAt;

    public User() { }

    public User(long id, String username) {
        this.id = id;
        this.username = username;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
