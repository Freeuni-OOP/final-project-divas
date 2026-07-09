package org.example.quiz.dao;

import org.example.quiz.db.Database;
import org.example.quiz.model.Question;
import org.example.quiz.model.Quiz;

import java.sql.*;

/**
 * Read-only access to quizzes/questions for the taking-flow. (Part C)
 *
 * The quizzes/questions tables are OWNED by Part B (creation side). C only
 * reads them here to render and grade a quiz. If B changes column names, only
 * this file needs adjustment on C's side.
 */
public class QuizReadDAO {

    /** Load a quiz header only (no questions). */
    public Quiz getQuiz(long quizId) throws SQLException {
        String sql = "SELECT * FROM quizzes WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapQuiz(rs) : null;
            }
        }
    }

    /** Load a quiz WITH all its questions, options and answers populated. */
    public Quiz getQuizWithQuestions(long quizId) throws SQLException {
        Quiz quiz = getQuiz(quizId);
        if (quiz == null) return null;

        try (Connection c = Database.getConnection()) {
            // questions
            String qSql = "SELECT * FROM questions WHERE quiz_id=? ORDER BY position, id";
            try (PreparedStatement ps = c.prepareStatement(qSql)) {
                ps.setLong(1, quizId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) quiz.getQuestions().add(mapQuestion(rs));
                }
            }
            // options + answers per question
            String oSql = "SELECT * FROM question_options WHERE question_id=? ORDER BY position, id";
            String aSql = "SELECT * FROM question_answers WHERE question_id=? ORDER BY slot, id";
            try (PreparedStatement po = c.prepareStatement(oSql);
                 PreparedStatement pa = c.prepareStatement(aSql)) {
                for (Question q : quiz.getQuestions()) {
                    po.setLong(1, q.getId());
                    try (ResultSet rs = po.executeQuery()) {
                        while (rs.next()) {
                            q.getOptions().add(new Question.Option(
                                    rs.getLong("id"),
                                    rs.getString("option_text"),
                                    rs.getBoolean("is_correct"),
                                    rs.getInt("position")));
                        }
                    }
                    pa.setLong(1, q.getId());
                    try (ResultSet rs = pa.executeQuery()) {
                        while (rs.next()) {
                            q.getAnswers().add(new Question.Answer(
                                    rs.getLong("id"),
                                    rs.getString("answer_text"),
                                    rs.getInt("slot")));
                        }
                    }
                }
            }
        }
        return quiz;
    }
    /*
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

    public java.util.List<Quiz> getPopular(int limit) throws SQLException {
        String sql = "SELECT q.* FROM quizzes q "
                + "LEFT JOIN quiz_attempts a ON a.quiz_id = q.id "
                + "GROUP BY q.id ORDER BY COUNT(a.id) DESC LIMIT ?";
        java.util.List<Quiz> out = new java.util.ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapQuiz(rs));
            }
        }
        return out;
    }
     */


}

