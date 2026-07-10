package org.example.quiz.dao;

import org.example.quiz.db.Database;

import java.sql.*;

public class AdminDAO {

    //deletes a user account
    public void removeUser(long userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }

    //deletes a quiz
    public void removeQuiz(long quizId) throws SQLException {
        String sql = "DELETE FROM quizzes WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, quizId);
            ps.executeUpdate();
        }
    }

    //promotes a user account to admin
    public void promoteToAdmin(long userId) throws SQLException {
        String sql = "UPDATE users SET is_admin = true WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }

    //clears all history (attempts) for a particular quiz, without deleting the quiz itself
    public void clearQuizHistory(long quizId) throws SQLException {
        String sql = "DELETE FROM quiz_attempts WHERE quiz_id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, quizId);
            ps.executeUpdate();
        }
    }

    //for site statistics

    //counts the number of users
    public int countUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    //counts the number of quizzes taken
    public int countQuizzesTaken() throws SQLException {
        String sql = "SELECT COUNT(*) FROM quiz_attempts";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
