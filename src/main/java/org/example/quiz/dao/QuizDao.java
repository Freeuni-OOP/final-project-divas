package org.example.quiz.dao;

import org.example.quiz.db.Database;
import org.example.quiz.model.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDao {

    //saves a new quiz to the database and returns its generated id
    public long createQuiz(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (title, description, creator_id, randomize, multi_page, immediate_correct, practice_allowed) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, quiz.getTitle());
            ps.setString(2, quiz.getDescription());
            ps.setLong(3, quiz.getCreatorId());
            ps.setBoolean(4, quiz.isRandomize());
            ps.setBoolean(5, quiz.isMultiPage());
            ps.setBoolean(6, quiz.isImmediateCorrect());
            ps.setBoolean(7, quiz.isPracticeAllowed());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                long id = keys.getLong(1);
                quiz.setId(id);
                return id;
            }
        }
    }

    //fetches one quiz by its id or null if it does not exist
    public Quiz getQuizById(long id) throws SQLException {
        String sql = "SELECT * FROM quizzes WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapQuiz(rs) : null;
            }
        }
    }

    //counts how many quizzes a given user has created
    public int countCreatedQuizzes(long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM quizzes WHERE creator_id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    //returns the most attempted quizzes for the homepage's popular quizzes list
    public List<Quiz> getPopular(int limit) throws SQLException {
        String sql = "SELECT q.* FROM quizzes q "
                + "LEFT JOIN quiz_attempts a ON a.quiz_id = q.id "
                + "GROUP BY q.id ORDER BY COUNT(a.id) DESC LIMIT ?";
        List<Quiz> result = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapQuiz(rs));
            }
        }
        return result;
    }

    private Quiz mapQuiz(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setId(rs.getLong("id"));
        quiz.setTitle(rs.getString("title"));
        quiz.setDescription(rs.getString("description"));
        quiz.setCreatorId(rs.getLong("creator_id"));
        quiz.setRandomize(rs.getBoolean("randomize"));
        quiz.setMultiPage(rs.getBoolean("multi_page"));
        quiz.setImmediateCorrect(rs.getBoolean("immediate_correct"));
        quiz.setPracticeAllowed(rs.getBoolean("practice_allowed"));
        quiz.setCreatedAt(rs.getTimestamp("created_at"));
        return quiz;
    }
}
