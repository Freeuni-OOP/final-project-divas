package org.example.quiz.util;

import org.example.quiz.model.AchievementType;

public class AchievementDisplay {

    public static String getLabel(AchievementType type) {
        switch (type) {
            case AMATEUR_AUTHOR: return "Amateur Author";
            case PROLIFIC_AUTHOR: return "Prolific Author";
            case PRODIGIOUS_AUTHOR: return "Prodigious Author";
            case QUIZ_MACHINE: return "Quiz Machine";
            case I_AM_THE_GREATEST: return "I am the Greatest";
            case PRACTICE_MAKES_PERFECT: return "Practice Makes Perfect";
            default: return type.name();
        }
    }

    public static String getDescription(AchievementType type) {
        switch (type) {
            case AMATEUR_AUTHOR: return "Created your first quiz.";
            case PROLIFIC_AUTHOR: return "Created five quizzes.";
            case PRODIGIOUS_AUTHOR: return "Created ten quizzes.";
            case QUIZ_MACHINE: return "Took ten quizzes.";
            case I_AM_THE_GREATEST: return "Had the highest score on a quiz.";
            case PRACTICE_MAKES_PERFECT: return "Took a quiz in practice mode.";
            default: return "";
        }
    }

    public static String getIconUrl(AchievementType type) {
        return "/img/badges/" + type.name().toLowerCase() + ".png";
    }
}