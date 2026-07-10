package org.example.quiz.dao;

import org.example.quiz.db.Database;
import org.example.quiz.model.Question;
import org.example.quiz.model.QuestionAnswer;
import org.example.quiz.model.QuestionOption;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    //saves a new question to the database and returns its generated id
    public long createQuestion(Question question) throws SQLException {
        String sql = "INSERT INTO questions " +
                "(quiz_id, question_type, prompt, position) VALUES (?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, question.getQuizId());
            ps.setString(2, question.getQuestionType());
            ps.setString(3, question.getQuestion());
            ps.setInt(4, question.getNum());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                long id = keys.getLong(1);
                question.setId(id);
                return id;
            }
        }
    }

    //saves the multiple choice options for a question
    public void addOptions(long questionId, List<QuestionOption> options) throws SQLException {
        String sql = "INSERT INTO question_options " +
                "(question_id, option_text, is_correct, position) VALUES (?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (QuestionOption option : options) {
                ps.setLong(1, questionId);
                ps.setString(2, option.getOptionText());
                ps.setBoolean(3, option.isCorrect());
                ps.setInt(4, option.getPosition());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    //saves the accepted text answers for a question
    public void addAcceptedAnswers(long questionId, List<QuestionAnswer> answers) throws SQLException {
        String sql = "INSERT INTO question_answers " +
                "(question_id, answer_text) VALUES (?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (QuestionAnswer answer : answers) {
                ps.setLong(1, questionId);
                ps.setString(2, answer.getAnswer());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    //fetches all questions belonging to a quiz, in display order
    public List<Question> getQuestionsForQuiz(long quizId) throws SQLException {
        String sql = "SELECT * FROM questions WHERE quiz_id=? ORDER BY position, id";
        List<Question> result = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapQuestion(rs));
            }
        }
        return result;
    }

    private Question mapQuestion(ResultSet rs) throws SQLException {
        Question question = new Question(rs.getString("prompt"));
        question.setId(rs.getLong("id"));
        question.setQuizId(rs.getLong("quiz_id"));
        question.setQuestionType(rs.getString("question_type"));
        question.setNum(rs.getInt("position"));
        return question;
    }
}
