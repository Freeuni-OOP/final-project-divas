package org.example.quiz.dao;

import org.example.quiz.db.Database;
import org.example.quiz.model.QuizAttempt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for quiz_attempts. (Part C)
 *
 * Writes each completed (non-practice) attempt and reads leaderboards, per-user
 * history, and recent activity. Practice attempts are recorded but excluded
 * from leaderboards/stats via the views.
 */
public class QuizAttemptDAO {

    public long record(QuizAttempt a) throws SQLException {
        String sql = "INSERT INTO quiz_attempts "
                + "(quiz_id, user_id, score_correct, score_total, time_seconds, practice) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, a.getQuizId());
            ps.setLong(2, a.getUserId());
            ps.setInt(3, a.getScoreCorrect());
            ps.setInt(4, a.getScoreTotal());
            ps.setInt(5, a.getTimeSeconds());
            ps.setBoolean(6, a.isPractice());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return -1;
    }

    public QuizAttempt getById(long id) throws SQLException {
        String sql = "SELECT a.*, u.username, q.title AS quiz_title "
                + "FROM quiz_attempts a JOIN users u ON u.id=a.user_id "
                + "JOIN quizzes q ON q.id=a.quiz_id WHERE a.id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /**
     * Top scorers for a quiz. Ranked by correct answers desc, then time asc.
     * One (best) row per user.
     */
    public List<QuizAttempt> getTopScorers(long quizId, int limit) throws SQLException {
        String sql =
                "SELECT a.*, u.username FROM quiz_attempts a "
                        + "JOIN users u ON u.id=a.user_id "
                        + "JOIN ( "
                        + "   SELECT user_id, MAX(score_correct) AS best "
                        + "   FROM quiz_attempts WHERE quiz_id=? AND practice=FALSE GROUP BY user_id "
                        + ") b ON b.user_id=a.user_id AND b.best=a.score_correct "
                        + "WHERE a.quiz_id=? AND a.practice=FALSE "
                        + "GROUP BY a.user_id "
                        + "ORDER BY a.score_correct DESC, MIN(a.time_seconds) ASC "
                        + "LIMIT ?";
        return query(sql, quizId, quizId, limit);
    }

    /** Top performers within the last N minutes (spec's "top in last day"). */
    public List<QuizAttempt> getTopScorersRecent(long quizId, int minutes, int limit)
            throws SQLException {
        String sql =
                "SELECT a.*, u.username FROM quiz_attempts a "
                        + "JOIN users u ON u.id=a.user_id "
                        + "WHERE a.quiz_id=? AND a.practice=FALSE "
                        + "  AND a.taken_at >= (NOW() - INTERVAL ? MINUTE) "
                        + "ORDER BY a.score_correct DESC, a.time_seconds ASC LIMIT ?";
        return query(sql, quizId, minutes, limit);
    }

    /** Most recent attempts on a quiz (good and bad). */
    public List<QuizAttempt> getRecentAttempts(long quizId, int limit) throws SQLException {
        String sql =
                "SELECT a.*, u.username FROM quiz_attempts a "
                        + "JOIN users u ON u.id=a.user_id "
                        + "WHERE a.quiz_id=? AND a.practice=FALSE "
                        + "ORDER BY a.taken_at DESC LIMIT ?";
        return query(sql, quizId, limit);
    }

    /** A single user's past performance on a specific quiz. */
    public List<QuizAttempt> getUserHistoryForQuiz(long userId, long quizId) throws SQLException {
        String sql =
                "SELECT a.*, u.username FROM quiz_attempts a "
                        + "JOIN users u ON u.id=a.user_id "
                        + "WHERE a.user_id=? AND a.quiz_id=? AND a.practice=FALSE "
                        + "ORDER BY a.taken_at DESC";
        return query(sql, userId, quizId);
    }

    /** A user's full history across all quizzes (for the history page). */
    public List<QuizAttempt> getUserHistory(long userId, int limit) throws SQLException {
        String sql =
                "SELECT a.*, u.username, q.title AS quiz_title FROM quiz_attempts a "
                        + "JOIN users u ON u.id=a.user_id "
                        + "JOIN quizzes q ON q.id=a.quiz_id "
                        + "WHERE a.user_id=? AND a.practice=FALSE "
                        + "ORDER BY a.taken_at DESC LIMIT ?";
        return query(sql, userId, limit);
    }

    /** Summary stats for a quiz from the v_quiz_stats view. */
    public double[] getQuizStats(long quizId) throws SQLException {
        String sql = "SELECT attempts_count, avg_percent, avg_time_seconds "
                + "FROM v_quiz_stats WHERE quiz_id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new double[]{
                            rs.getDouble("attempts_count"),
                            rs.getDouble("avg_percent"),
                            rs.getDouble("avg_time_seconds")
                    };
                }
            }
        }
        return new double[]{0, 0, 0};
    }

    /** Challenger's best score on a quiz — used to build CHALLENGE messages. */
    public int[] getBestScore(long userId, long quizId) throws SQLException {
        String sql = "SELECT score_correct, score_total FROM quiz_attempts "
                + "WHERE user_id=? AND quiz_id=? AND practice=FALSE "
                + "ORDER BY score_correct DESC, time_seconds ASC LIMIT 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new int[]{rs.getInt(1), rs.getInt(2)};
            }
        }
        return new int[]{0, 0};
    }

    // ---- helpers ----

    private List<QuizAttempt> query(String sql, Object... params) throws SQLException {
        List<QuizAttempt> out = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    private QuizAttempt map(ResultSet rs) throws SQLException {
        QuizAttempt a = new QuizAttempt();
        a.setId(rs.getLong("id"));
        a.setQuizId(rs.getLong("quiz_id"));
        a.setUserId(rs.getLong("user_id"));
        a.setScoreCorrect(rs.getInt("score_correct"));
        a.setScoreTotal(rs.getInt("score_total"));
        a.setTimeSeconds(rs.getInt("time_seconds"));
        a.setPractice(rs.getBoolean("practice"));
        a.setTakenAt(rs.getTimestamp("taken_at"));
        try { a.setUsername(rs.getString("username")); } catch (SQLException ignore) { }
        try { a.setQuizTitle(rs.getString("quiz_title")); } catch (SQLException ignore) { }
        return a;
    }
    public int countAttempts(long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE user_id=? AND practice=FALSE";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public boolean isTopScore(QuizAttempt attempt) throws SQLException {
        String sql = "SELECT score_correct, time_seconds FROM v_quiz_leaderboard "
                + "WHERE quiz_id=? ORDER BY score_correct DESC, time_seconds ASC LIMIT 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, attempt.getQuizId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return true; // first attempt ever on this quiz
                int topScore = rs.getInt("score_correct");
                int topTime = rs.getInt("time_seconds");
                return attempt.getScoreCorrect() >= topScore
                        && attempt.getTimeSeconds() <= topTime;
            }
        }
    }
}

