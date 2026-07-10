package org.example.quiz.util;

import org.example.quiz.model.AchievementType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AchievementDisplayTest {

    @Test
    public void testGetLabelAmateurAuthor() {
        assertEquals("Amateur Author",
                AchievementDisplay.getLabel(AchievementType.AMATEUR_AUTHOR));
    }

    @Test
    public void testGetLabelProlificAuthor() {
        assertEquals("Prolific Author",
                AchievementDisplay.getLabel(AchievementType.PROLIFIC_AUTHOR));
    }

    @Test
    public void testGetLabelProdigiousAuthor() {
        assertEquals("Prodigious Author",
                AchievementDisplay.getLabel(AchievementType.PRODIGIOUS_AUTHOR));
    }

    @Test
    public void testGetLabelQuizMachine() {
        assertEquals("Quiz Machine",
                AchievementDisplay.getLabel(AchievementType.QUIZ_MACHINE));
    }

    @Test
    public void testGetLabelIAmTheGreatest() {
        assertEquals("I am the Greatest",
                AchievementDisplay.getLabel(AchievementType.I_AM_THE_GREATEST));
    }

    @Test
    public void testGetLabelPracticeMakesPerfect() {
        assertEquals("Practice Makes Perfect",
                AchievementDisplay.getLabel(AchievementType.PRACTICE_MAKES_PERFECT));
    }

    @Test
    public void testGetDescriptionAmateurAuthor() {
        assertEquals("Created your first quiz.",
                AchievementDisplay.getDescription(AchievementType.AMATEUR_AUTHOR));
    }

    @Test
    public void testGetDescriptionProlificAuthor() {
        assertEquals("Created five quizzes.",
                AchievementDisplay.getDescription(AchievementType.PROLIFIC_AUTHOR));
    }

    @Test
    public void testGetDescriptionProdigiousAuthor() {
        assertEquals("Created ten quizzes.",
                AchievementDisplay.getDescription(AchievementType.PRODIGIOUS_AUTHOR));
    }

    @Test
    public void testGetDescriptionQuizMachine() {
        assertEquals("Took ten quizzes.",
                AchievementDisplay.getDescription(AchievementType.QUIZ_MACHINE));
    }

    @Test
    public void testGetDescriptionIAmTheGreatest() {
        assertEquals("Had the highest score on a quiz.",
                AchievementDisplay.getDescription(AchievementType.I_AM_THE_GREATEST));
    }

    @Test
    public void testGetDescriptionPracticeMakesPerfect() {
        assertEquals("Took a quiz in practice mode.",
                AchievementDisplay.getDescription(AchievementType.PRACTICE_MAKES_PERFECT));
    }

    @Test
    public void testGetIconUrlAmateurAuthor() {
        assertEquals("/img/badges/amateur_author.png",
                AchievementDisplay.getIconUrl(AchievementType.AMATEUR_AUTHOR));
    }

    @Test
    public void testGetIconUrlQuizMachine() {
        assertEquals("/img/badges/quiz_machine.png",
                AchievementDisplay.getIconUrl(AchievementType.QUIZ_MACHINE));
    }
}