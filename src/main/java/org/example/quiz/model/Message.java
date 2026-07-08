package org.example.quiz.model;

import java.sql.Timestamp;

/**
 * An internal-mail message. (Part C)
 *
 * Three types are required by the spec:
 *   FRIEND_REQUEST — someone asked to be friends
 *   CHALLENGE      — a friend challenged the user to take a quiz (quizId set)
 *   NOTE           — free-text message
 */
public class Message {

    public enum Type { FRIEND_REQUEST, CHALLENGE, NOTE }

    private long id;
    private long senderId;
    private long recipientId;
    private Type type;
    private String body;
    private Long quizId;      // nullable; set for CHALLENGE
    private boolean read;
    private Timestamp createdAt;

    // convenience fields populated by joins for display
    private String senderUsername;
    private String quizTitle;

    public Message() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getSenderId() { return senderId; }
    public void setSenderId(long senderId) { this.senderId = senderId; }

    public long getRecipientId() { return recipientId; }
    public void setRecipientId(long recipientId) { this.recipientId = recipientId; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }
}
