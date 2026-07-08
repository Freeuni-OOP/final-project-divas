package org.example.quiz.util;

import org.example.quiz.model.Question;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class GraderTest {

    private Question textual(Question.Type type, String... answers) {
        Question q = new Question();
        q.setType(type);
        long id = 1;
        for (String a : answers) q.getAnswers().add(new Question.Answer(id++, a, 0));
        return q;
    }

    @Test
    public void questionResponse_anyAcceptedAnswerMatches() {
        Question q = textual(Question.Type.QUESTION_RESPONSE, "Washington", "George Washington");
        assertEquals(1, Grader.grade(q, Collections.singletonList("george washington")).correct);
        assertEquals(0, Grader.grade(q, Collections.singletonList("Lincoln")).correct);
    }

    @Test
    public void fillBlank_caseAndSpaceInsensitive() {
        Question q = textual(Question.Type.FILL_BLANK, "Gettysburg");
        assertEquals(1, Grader.grade(q, Collections.singletonList("  gettysburg ")).correct);
    }

    @Test
    public void multipleChoice_correctOptionScores() {
        Question q = new Question();
        q.setType(Question.Type.MULTIPLE_CHOICE);
        q.getOptions().add(new Question.Option(1, "Paris", true, 0));
        q.getOptions().add(new Question.Option(2, "London", false, 1));
        assertEquals(1, Grader.grade(q, Collections.singletonList("Paris")).correct);
        assertEquals(0, Grader.grade(q, Collections.singletonList("London")).correct);
    }

    @Test
    public void multiAnswerUnordered_countsMatchesUpToSlots() {
        Question q = new Question();
        q.setType(Question.Type.QUESTION_RESPONSE);
        q.setOrdered(false);
        // 3 slots, 3 accepted answers
        q.getAnswers().add(new Question.Answer(1, "California", 0));
        q.getAnswers().add(new Question.Answer(2, "Texas", 1));
        q.getAnswers().add(new Question.Answer(3, "Florida", 2));
        Grader.Result r = Grader.grade(q, Arrays.asList("florida", "texas", "nevada"));
        assertEquals(2, r.correct);
        assertEquals(3, r.total);
    }

    @Test
    public void multiAnswerUnordered_duplicateResponseNotDoubleCounted() {
        Question q = new Question();
        q.setType(Question.Type.QUESTION_RESPONSE);
        q.setOrdered(false);
        q.getAnswers().add(new Question.Answer(1, "Texas", 0));
        q.getAnswers().add(new Question.Answer(2, "Ohio", 1));
        Grader.Result r = Grader.grade(q, Arrays.asList("texas", "texas"));
        assertEquals(1, r.correct);
        assertEquals(2, r.total);
    }

    @Test
    public void multiAnswerOrdered_positionMatters() {
        Question q = new Question();
        q.setType(Question.Type.QUESTION_RESPONSE);
        q.setOrdered(true);
        q.getAnswers().add(new Question.Answer(1, "New York", 0));
        q.getAnswers().add(new Question.Answer(2, "Los Angeles", 1));
        // right values, wrong order -> only slots that happen to line up count
        Grader.Result swapped = Grader.grade(q, Arrays.asList("Los Angeles", "New York"));
        assertEquals(0, swapped.correct);
        Grader.Result correct = Grader.grade(q, Arrays.asList("New York", "Los Angeles"));
        assertEquals(2, correct.correct);
    }
}
