package org.example.quiz.dao;

import org.example.quiz.db.Database;
import org.example.quiz.model.Achievement;
import org.example.quiz.model.AchievementType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchievementDAO {

    public boolean grant(long userId, AchievementType type) throws SQLException {
        String sql = "INSERT INTO user_achievements (user_id, achievement_code) VALUES (?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, type.name());
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException dup) {
            return false;
        }
    }

    public boolean hasAchievement(long userId, AchievementType type) throws SQLException {
        String sql = "SELECT 1 FROM user_achievements WHERE user_id=? AND achievement_code=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Achievement> getForUser(long userId) throws SQLException {
        String sql = "SELECT * FROM user_achievements WHERE user_id=? ORDER BY earned_at DESC";
        List<Achievement> result = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Achievement a = new Achievement();
                    a.setId(rs.getLong("id"));
                    a.setUserId(rs.getLong("user_id"));
                    a.setAchievementCode(rs.getString("achievement_code"));
                    a.setEarnedAt(rs.getTimestamp("earned_at"));
                    result.add(a);
                }
            }
        }
        return result;
    }
}