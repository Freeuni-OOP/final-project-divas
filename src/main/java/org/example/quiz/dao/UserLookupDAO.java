package org.example.quiz.dao;

import org.example.quiz.db.Database;
import org.example.quiz.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only user lookups. (used by Part C for friend search)
 *
 * The users table is owned by Part A. This DAO only reads it. If A's full
 * UserDAO exists, C can switch to it; this keeps C self-contained.
 */
public class UserLookupDAO {

    public User getById(long id) throws SQLException {
        String sql = "SELECT id, username, is_admin FROM users WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User getByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, is_admin FROM users WHERE username=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Search by partial username, excluding the searcher. */
    public List<User> search(String term, long excludeUserId, int limit) throws SQLException {
        String sql = "SELECT id, username, is_admin FROM users "
                   + "WHERE username LIKE ? AND id<>? ORDER BY username LIMIT ?";
        List<User> out = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + term + "%");
            ps.setLong(2, excludeUserId);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User(rs.getLong("id"), rs.getString("username"));
        u.setAdmin(rs.getBoolean("is_admin"));
        return u;
    }
}
