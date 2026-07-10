package org.example.quiz.model;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class FriendshipTest {

    @Test
    public void testGettersAndSetters() {
        Friendship friendship = new Friendship();

        Timestamp created = new Timestamp(System.currentTimeMillis());
        Timestamp responded = new Timestamp(System.currentTimeMillis() + 1000);

        friendship.setId(1);
        friendship.setRequesterId(10);
        friendship.setAddresseeId(20);
        friendship.setStatus(Friendship.Status.ACCEPTED);
        friendship.setCreatedAt(created);
        friendship.setRespondedAt(responded);

        assertEquals(1, friendship.getId());
        assertEquals(10, friendship.getRequesterId());
        assertEquals(20, friendship.getAddresseeId());
        assertEquals(Friendship.Status.ACCEPTED, friendship.getStatus());
        assertEquals(created, friendship.getCreatedAt());
        assertEquals(responded, friendship.getRespondedAt());
    }

    @Test
    public void testStatusEnumValues() {
        assertEquals(Friendship.Status.PENDING,
                Friendship.Status.valueOf("PENDING"));

        assertEquals(Friendship.Status.ACCEPTED,
                Friendship.Status.valueOf("ACCEPTED"));

        assertEquals(Friendship.Status.REJECTED,
                Friendship.Status.valueOf("REJECTED"));
    }
}