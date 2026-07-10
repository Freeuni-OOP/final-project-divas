package org.example.quiz.util;

import org.example.quiz.model.Question;
import org.example.quiz.model.QuestionAnswer;
import org.example.quiz.model.QuestionOption;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * NOTE: originally written against a Question with a nested Type enum and
 * embedded options/answers lists, and a 2-argument Grader.grade(q, responses).
 * Question was not built that way, so this test was rewritten to match the
 * actual model (questionType as a String, options/answers passed separately).
 * The multi-answer ordered/unordered tests were removed since QuestionAnswer
 * has no slot field - only the 4 required single-slot question types exist.
 */
public class GraderTest {

    @Test
    public void questionResponse_anyAcceptedAnswerMatches() {
        Question q = new Question("Who was the first US president?");
        q.setQuestionType("QUESTION_RESPONSE");
        List<QuestionAnswer> answers = new ArrayList<>();
        answers.add(new QuestionAnswer("Washington"));
        answers.add(new QuestionAnswer("George Washington"));

        assertEquals(1, Grader.grade(q, Collections.emptyList(), answers,
                Collections.singletonList("george washington")).correct);
        assertEquals(0, Grader.grade(q, Collections.emptyList(), answers,
                Collections.singletonList("Lincoln")).correct);
    }

    @Test
    public void fillBlank_caseAndSpaceInsensitive() {
        Question q = new Question("The ____ Address was given by Lincoln.");
        q.setQuestionType("FILL_BLANK");
        List<QuestionAnswer> answers = Collections.singletonList(new QuestionAnswer("Gettysburg"));

        assertEquals(1, Grader.grade(q, Collections.emptyList(), answers,
                Collections.singletonList("  gettysburg ")).correct);
    }

    @Test
    public void multipleChoice_correctOptionScores() {
        Question q = new Question("What is the capital of France?");
        q.setQuestionType("MULTIPLE_CHOICE");
        QuestionOption paris = new QuestionOption("Paris");
        paris.setCorrect(true);
        QuestionOption london = new QuestionOption("London");
        london.setCorrect(false);
        List<QuestionOption> options = new ArrayList<>();
        options.add(paris);
        options.add(london);

        assertEquals(1, Grader.grade(q, options, Collections.emptyList(),
                Collections.singletonList("Paris")).correct);
        assertEquals(0, Grader.grade(q, options, Collections.emptyList(),
                Collections.singletonList("London")).correct);
    }
}

