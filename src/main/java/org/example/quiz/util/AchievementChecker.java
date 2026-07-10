package org.example.quiz.util;

//fixed by kesaria: called quizReadDAO.countCreatedQuizzes(...), but that method
//doesn't exist on QuizReadDAO - it was only ever built on QuizDao; switched to QuizDao

import org.example.quiz.dao.AchievementDAO;
import org.example.quiz.dao.QuizDao;
import org.example.quiz.dao.QuizAttemptDAO;
import org.example.quiz.model.AchievementType;
import org.example.quiz.model.QuizAttempt;

import java.sql.SQLException;

/**
 * NOTE: originally called quizReadDAO.countCreatedQuizzes(...), but that
 * method does not exist on QuizReadDAO - it was only ever built on QuizDao
 * (Part B). Corrected to use QuizDao here instead.
 */
public class AchievementChecker {

    private final AchievementDAO achievementDAO = new AchievementDAO();
    private final QuizDao quizDao = new QuizDao();
    private final QuizAttemptDAO attemptDAO = new QuizAttemptDAO();

    public void onQuizCreated(long userId) throws SQLException {
        int count = quizDao.countCreatedQuizzes(userId);
        if (count >= 1)  achievementDAO.grant(userId, AchievementType.AMATEUR_AUTHOR);
        if (count >= 5)  achievementDAO.grant(userId, AchievementType.PROLIFIC_AUTHOR);
        if (count >= 10) achievementDAO.grant(userId, AchievementType.PRODIGIOUS_AUTHOR);
    }

    public void onQuizSubmitted(QuizAttempt attempt) throws SQLException {
        long userId = attempt.getUserId();
        if (attemptDAO.countAttempts(userId) >= 10) {
            achievementDAO.grant(userId, AchievementType.QUIZ_MACHINE);
        }
        if (attempt.isPractice()) {
            achievementDAO.grant(userId, AchievementType.PRACTICE_MAKES_PERFECT);
        } else if (attemptDAO.isTopScore(attempt)) {
            achievementDAO.grant(userId, AchievementType.I_AM_THE_GREATEST);
        }
    }
}