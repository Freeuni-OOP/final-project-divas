package org.example.quiz.dao;

import org.example.quiz.db.Database;
import org.example.quiz.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the messages table. (Part C)
 *
 * Three message types: FRIEND_REQUEST, CHALLENGE, NOTE.
 * CHALLENGE messages carry a quiz_id.
 */
public class MessageDAO {

    public long send(Message m) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, recipient_id, msg_type, body, quiz_id, is_read) "
                   + "VALUES (?, ?, ?, ?, ?, FALSE)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, m.getSenderId());
            ps.setLong(2, m.getRecipientId());
            ps.setString(3, m.getType().name());
            ps.setString(4, m.getBody());
            if (m.getQuizId() != null) ps.setLong(5, m.getQuizId());
            else ps.setNull(5, Types.BIGINT);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return -1;
    }

    /** Convenience: send a NOTE. */
    public long sendNote(long from, long to, String body) throws SQLException {
        Message m = new Message();
        m.setSenderId(from); m.setRecipientId(to);
        m.setType(Message.Type.NOTE); m.setBody(body);
        return send(m);
    }

    /** Convenience: send a FRIEND_REQUEST message. */
    public long sendFriendRequest(long from, long to) throws SQLException {
        Message m = new Message();
        m.setSenderId(from); m.setRecipientId(to);
        m.setType(Message.Type.FRIEND_REQUEST);
        m.setBody("You have a new friend request.");
        return send(m);
    }

    /** Convenience: send a CHALLENGE message with challenger's best score. */
    public long sendChallenge(long from, long to, long quizId, int bestCorrect, int bestTotal)
            throws SQLException {
        Message m = new Message();
        m.setSenderId(from); m.setRecipientId(to);
        m.setType(Message.Type.CHALLENGE);
        m.setQuizId(quizId);
        m.setBody("I challenge you to beat my score of " + bestCorrect + "/" + bestTotal + "!");
        return send(m);
    }

    /** Inbox for a user, newest first, with sender username + quiz title joined in. */
    public List<Message> getInbox(long userId) throws SQLException {
        String sql =
            "SELECT m.*, u.username AS sender_username, q.title AS quiz_title "
          + "FROM messages m "
          + "JOIN users u ON u.id = m.sender_id "
          + "LEFT JOIN quizzes q ON q.id = m.quiz_id "
          + "WHERE m.recipient_id=? ORDER BY m.created_at DESC";
        List<Message> out = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    public int countUnread(long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM messages WHERE recipient_id=? AND is_read=FALSE";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public Message getById(long id) throws SQLException {
        String sql =
            "SELECT m.*, u.username AS sender_username, q.title AS quiz_title "
          + "FROM messages m JOIN users u ON u.id=m.sender_id "
          + "LEFT JOIN quizzes q ON q.id=m.quiz_id WHERE m.id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean markRead(long messageId, long recipientId) throws SQLException {
        String sql = "UPDATE messages SET is_read=TRUE WHERE id=? AND recipient_id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, messageId);
            ps.setLong(2, recipientId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long messageId, long recipientId) throws SQLException {
        String sql = "DELETE FROM messages WHERE id=? AND recipient_id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, messageId);
            ps.setLong(2, recipientId);
            return ps.executeUpdate() > 0;
        }
    }

    private Message map(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(rs.getLong("id"));
        m.setSenderId(rs.getLong("sender_id"));
        m.setRecipientId(rs.getLong("recipient_id"));
        m.setType(Message.Type.valueOf(rs.getString("msg_type")));
        m.setBody(rs.getString("body"));
        long q = rs.getLong("quiz_id");
        m.setQuizId(rs.wasNull() ? null : q);
        m.setRead(rs.getBoolean("is_read"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        m.setSenderUsername(rs.getString("sender_username"));
        m.setQuizTitle(rs.getString("quiz_title"));
        return m;
    }
}
