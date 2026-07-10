package org.example.quiz.dao;

import org.example.quiz.db.Database;
import org.example.quiz.model.Question;
import org.example.quiz.model.QuestionAnswer;
import org.example.quiz.model.QuestionOption;
import org.example.quiz.model.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only access to quizzes and questions for the taking-flow.
 * Options and answers are fetched separately via getOptions()/getAnswers().
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

    /** Load a quiz with all its questions (options/answers are fetched separately, see below). */
    public Quiz getQuizWithQuestions(long quizId) throws SQLException {
        Quiz quiz = getQuiz(quizId);
        if (quiz == null) return null;

        String qSql = "SELECT * FROM questions WHERE quiz_id=? ORDER BY position, id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(qSql)) {
            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) quiz.getQuestions().add(mapQuestion(rs));
            }
        }
        return quiz;
    }

    /** Options for a MULTIPLE_CHOICE question, in display order. */
    public List<QuestionOption> getOptions(long questionId) throws SQLException {
        String sql = "SELECT * FROM question_options WHERE question_id=? ORDER BY position, id";
        List<QuestionOption> result = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuestionOption o = new QuestionOption(rs.getString("option_text"));
                    o.setId(rs.getLong("id"));
                    o.setQuestionId(rs.getLong("question_id"));
                    o.setCorrect(rs.getBoolean("is_correct"));
                    o.setPosition(rs.getInt("position"));
                    result.add(o);
                }
            }
        }
        return result;
    }

    /** Accepted text answers for a QUESTION_RESPONSE/FILL_BLANK/PICTURE_RESPONSE question. */
    public List<QuestionAnswer> getAnswers(long questionId) throws SQLException {
        String sql = "SELECT * FROM question_answers WHERE question_id=? ORDER BY id";
        List<QuestionAnswer> result = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuestionAnswer a = new QuestionAnswer(rs.getString("answer_text"));
                    a.setId(rs.getLong("id"));
                    a.setQuestionId(rs.getLong("question_id"));
                    result.add(a);
                }
            }
        }
        return result;
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
        Question q = new Question(rs.getString("prompt"));
        q.setId(rs.getLong("id"));
        q.setQuizId(rs.getLong("quiz_id"));
        q.setQuestionType(rs.getString("question_type"));
        q.setNum(rs.getInt("position"));
        q.setImageUrl(rs.getString("image_url"));
        return q;
    }
}


