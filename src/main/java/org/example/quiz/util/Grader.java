package org.example.quiz.util;


import org.example.quiz.model.Question;
import org.example.quiz.model.QuestionAnswer;
import org.example.quiz.model.QuestionOption;

import java.util.List;

/**
 * Scoring logic for a single question.
 *
 * Question-Response, Fill-Blank and Picture-Response may have several legal
 * answers; any one match scores the slot. Multiple-Choice is correct if the
 * chosen option is a correct option. Comparison is case-insensitive and
 * trims surrounding whitespace.
 */
public final class Grader {

    private Grader() { }

    /** Result of grading one question. */
    public static class Result {
        public final int correct;   // slots the user got right
        public final int total;     // total slots for this question
        public Result(int correct, int total) { this.correct = correct; this.total = total; }
    }

    private static String norm(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    /**
     * Grade a question given the user's response(s).
     *
     * @param q         the question being graded
     * @param options   this question's options (only used for MULTIPLE_CHOICE)
     * @param answers   this question's accepted text answers (used for the other 3 types)
     * @param responses user text responses; for multiple choice, responses[0]
     *                  is the chosen option's text.
     */
    public static Result grade(Question q, List<QuestionOption> options, List<QuestionAnswer> answers, List<String> responses) {
        if (responses == null) responses = new java.util.ArrayList<>();
        if ("MULTIPLE_CHOICE".equals(q.getQuestionType())) {
            return gradeMultipleChoice(options, responses);
        }
        return gradeTextual(answers, responses);
    }

    private static Result gradeMultipleChoice(List<QuestionOption> options, List<String> responses) {
        String chosen = responses.isEmpty() ? "" : norm(responses.get(0));
        boolean right = false;
        for (QuestionOption o : options) {
            if (o.isCorrect() && norm(o.getOptionText()).equals(chosen)) { right = true; break; }
        }
        return new Result(right ? 1 : 0, 1);
    }

    /** Single-slot textual question: any accepted answer matches. */
    private static Result gradeTextual(List<QuestionAnswer> answers, List<String> responses) {
        String given = responses.isEmpty() ? "" : norm(responses.get(0));
        for (QuestionAnswer a : answers) {
            if (norm(a.getAnswer()).equals(given)) return new Result(1, 1);
        }
        return new Result(0, 1);
    }
}

