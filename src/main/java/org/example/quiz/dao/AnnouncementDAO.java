package org.example.quiz.dao;

import org.example.quiz.db.Database;
import org.example.quiz.model.Announcement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {

    //saves a new announcement to the database and returns its generated id
    public long createAnnouncement(Announcement announcement) throws SQLException {
        String sql = "INSERT INTO announcements (title, body, created_by, active) VALUES (?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, announcement.getTitle());
            ps.setString(2, announcement.getMessage());
            ps.setLong(3, announcement.getAuthorID());
            ps.setBoolean(4, announcement.isActive());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                long id = keys.getLong(1);
                announcement.setId(id);
                return id;
            }
        }
    }
    //updates an existing announcement's title and body text
    public void editAnnouncement(long id, String title, String body) throws SQLException {
        String sql = "UPDATE announcements SET title = ?, body = ? WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, body);
            ps.setLong(3, id);
            ps.executeUpdate();
        }
    }

    public void deactivateAnnouncement(long id) throws SQLException {
        String sql = "UPDATE announcements SET active = false WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    //gets all announcements that are currently active. newest first for display on the homepage
    public List<Announcement> getActiveAnnouncements() throws SQLException {
        String sql = "SELECT * FROM announcements WHERE active = true ORDER BY created_at DESC";
        List<Announcement> result = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapAnnouncement(rs));
            }
        }
        return result;
    }

    private Announcement mapAnnouncement(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement(
                rs.getString("title"),
                rs.getString("body"),
                rs.getLong("created_by"),
                rs.getBoolean("active"));
        announcement.setId(rs.getLong("id"));
        announcement.setCreatedAt(rs.getTimestamp("created_at"));
        return announcement;
    }
}
