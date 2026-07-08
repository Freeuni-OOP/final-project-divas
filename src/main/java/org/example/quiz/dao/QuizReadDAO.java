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

    private Quiz mapQuiz(ResultSet rs) throws SQLException {
        Quiz q = new Quiz();
        q.setId(rs.getLong("id"));
        q.setTitle(rs.getString("title"));
        q.setDescription(rs.getString("description"));
        q.setCreatorId(rs.getLong("creator_id"));
        q.setRandomize(rs.getBoolean("randomize"));
        q.setMultiPage(rs.getBoolean("multi_page"));
        q.setImmediateCorrect(rs.getBoolean("immediate_correct"));
        q.setPracticeAllowed(rs.getBoolean("practice_allowed"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        return q;
    }

    private Question mapQuestion(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setId(rs.getLong("id"));
        q.setQuizId(rs.getLong("quiz_id"));
        q.setType(Question.Type.valueOf(rs.getString("question_type")));
        q.setPrompt(rs.getString("prompt"));
        q.setImageUrl(rs.getString("image_url"));
        q.setOrdered(rs.getBoolean("ordered"));
        q.setPosition(rs.getInt("position"));
        return q;
    }
}
