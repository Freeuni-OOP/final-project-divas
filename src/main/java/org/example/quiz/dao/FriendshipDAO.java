package org.example.quiz.dao;

import org.example.quiz.db.Database;
import org.example.quiz.model.Friendship;
import org.example.quiz.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the friendships table. (Part C)
 *
 * Lifecycle: sendRequest -> PENDING -> accept()/reject().
 * Friendship is symmetric once ACCEPTED: (a,b) accepted means both are friends.
 */
public class FriendshipDAO {

    /**
     * Send a friend request from requester to addressee.
     * Returns false if a row already exists for this ordered pair.
     */
    public boolean sendRequest(long requesterId, long addresseeId) throws SQLException {
        if (requesterId == addresseeId) return false;
        // If the reverse request already exists and is pending, accepting is the
        // caller's job; here we just refuse to create a duplicate.
        String sql = "INSERT INTO friendships (requester_id, addressee_id, status) "
                   + "VALUES (?, ?, 'PENDING')";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, requesterId);
            ps.setLong(2, addresseeId);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException dup) {
            return false; // pair already exists
        }
    }

    /** Accept a pending request addressed to addresseeId. */
    public boolean accept(long requesterId, long addresseeId) throws SQLException {
        String sql = "UPDATE friendships SET status='ACCEPTED', responded_at=NOW() "
                   + "WHERE requester_id=? AND addressee_id=? AND status='PENDING'";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, requesterId);
            ps.setLong(2, addresseeId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Reject a pending request addressed to addresseeId. */
    public boolean reject(long requesterId, long addresseeId) throws SQLException {
        String sql = "UPDATE friendships SET status='REJECTED', responded_at=NOW() "
                   + "WHERE requester_id=? AND addressee_id=? AND status='PENDING'";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, requesterId);
            ps.setLong(2, addresseeId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Remove a friendship (in either direction). */
    public boolean removeFriend(long userA, long userB) throws SQLException {
        String sql = "DELETE FROM friendships WHERE status='ACCEPTED' AND "
                   + "((requester_id=? AND addressee_id=?) OR (requester_id=? AND addressee_id=?))";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userA); ps.setLong(2, userB);
            ps.setLong(3, userB); ps.setLong(4, userA);
            return ps.executeUpdate() > 0;
        }
    }

    /** True if the two users are accepted friends. */
    public boolean areFriends(long userA, long userB) throws SQLException {
        String sql = "SELECT 1 FROM friendships WHERE status='ACCEPTED' AND "
                   + "((requester_id=? AND addressee_id=?) OR (requester_id=? AND addressee_id=?)) LIMIT 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userA); ps.setLong(2, userB);
            ps.setLong(3, userB); ps.setLong(4, userA);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /** Status of the relationship between viewer and other, or null if none. */
    public Friendship getRelationship(long userA, long userB) throws SQLException {
        String sql = "SELECT * FROM friendships WHERE "
                   + "(requester_id=? AND addressee_id=?) OR (requester_id=? AND addressee_id=?) LIMIT 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userA); ps.setLong(2, userB);
            ps.setLong(3, userB); ps.setLong(4, userA);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** All accepted friends of a user, as User objects. */
    public List<User> getFriends(long userId) throws SQLException {
        String sql =
            "SELECT u.id, u.username FROM friendships f "
          + "JOIN users u ON u.id = CASE WHEN f.requester_id=? THEN f.addressee_id ELSE f.requester_id END "
          + "WHERE f.status='ACCEPTED' AND (f.requester_id=? OR f.addressee_id=?) "
          + "ORDER BY u.username";
        List<User> out = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId); ps.setLong(2, userId); ps.setLong(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(new User(rs.getLong("id"), rs.getString("username")));
            }
        }
        return out;
    }

    /** Pending requests addressed TO this user (incoming). */
    public List<Friendship> getIncomingRequests(long userId) throws SQLException {
        String sql = "SELECT * FROM friendships WHERE addressee_id=? AND status='PENDING' "
                   + "ORDER BY created_at DESC";
        List<Friendship> out = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    private Friendship map(ResultSet rs) throws SQLException {
        Friendship f = new Friendship();
        f.setId(rs.getLong("id"));
        f.setRequesterId(rs.getLong("requester_id"));
        f.setAddresseeId(rs.getLong("addressee_id"));
        f.setStatus(Friendship.Status.valueOf(rs.getString("status")));
        f.setCreatedAt(rs.getTimestamp("created_at"));
        f.setRespondedAt(rs.getTimestamp("responded_at"));
        return f;
    }
}
