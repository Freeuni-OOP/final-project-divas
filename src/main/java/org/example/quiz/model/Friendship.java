package org.example.quiz.model;

import java.sql.Timestamp;

/**
 * A friendship / friend-request row. (Part C)
 *
 * requester -> addressee, with a status that moves
 * PENDING -> ACCEPTED or PENDING -> REJECTED.
 */
public class Friendship {

    public enum Status { PENDING, ACCEPTED, REJECTED }

    private long id;
    private long requesterId;
    private long addresseeId;
    private Status status;
    private Timestamp createdAt;
    private Timestamp respondedAt;

    public Friendship() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getRequesterId() { return requesterId; }
    public void setRequesterId(long requesterId) { this.requesterId = requesterId; }

    public long getAddresseeId() { return addresseeId; }
    public void setAddresseeId(long addresseeId) { this.addresseeId = addresseeId; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getRespondedAt() { return respondedAt; }
    public void setRespondedAt(Timestamp respondedAt) { this.respondedAt = respondedAt; }
}
