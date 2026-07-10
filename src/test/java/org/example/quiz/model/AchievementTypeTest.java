package org.example.quiz.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class AchievementTypeTest {

    @Test
    public void testEnumValuesCount() {
        assertEquals(6, AchievementType.values().length);
    }

    @Test
    public void testValueOf() {
        assertEquals(
                AchievementType.AMATEUR_AUTHOR,
                AchievementType.valueOf("AMATEUR_AUTHOR")
        );
    }

    @Test
    public void testQuizMachineExists() {
        assertEquals(
                "QUIZ_MACHINE",
                AchievementType.QUIZ_MACHINE.name()
        );
    }
}
