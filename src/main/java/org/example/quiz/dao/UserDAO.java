package org.example.quiz.dao;

import org.example.quiz.db.Database;
import org.example.quiz.model.PasswordHasher;
import org.example.quiz.model.User;

import java.sql.*;

public class UserDAO {

    //registers a new user with a hashed+salted password, returns the generated id
    public long createUser(String username, String password) throws SQLException {
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash(password, salt);

        String sql = "INSERT INTO users (username, password_hash, salt, is_admin) VALUES (?, ?, ?, false)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, salt);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    //returns the matching User if the password is correct, otherwise null
    public User verifyLogin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String storedHash = rs.getString("password_hash");
                String salt = rs.getString("salt");
                String attemptHash = PasswordHasher.hash(password, salt);

                if (!attemptHash.equals(storedHash)) return null;

                User user = new User(rs.getLong("id"), rs.getString("username"));
                user.setAdmin(rs.getBoolean("is_admin"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;
            }
        }
    }

    //true if a username is already taken (used to validate registration)
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    //fetches one user by id, or null if it does not exist
    public User getById(long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User user = new User(rs.getLong("id"), rs.getString("username"));
                user.setAdmin(rs.getBoolean("is_admin"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;
            }
        }
    }
}
