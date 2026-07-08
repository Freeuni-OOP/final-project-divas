package org.example.quiz.util;

import org.example.quiz.model.Question;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scoring logic for a single question. (Part C core)
 *
 * Rules from the spec:
 *  - Question-Response / Fill-Blank / Picture-Response may have several legal
 *    answers; any one match scores the (single) slot.
 *  - Multiple-Choice: correct if the chosen option is a correct option.
 *  - Multi-answer questions: each blank is a separate answer worth 1 point.
 *      * ordered   -> answer i must match the accepted answer(s) for slot i.
 *      * unordered -> each user answer matches any not-yet-used accepted answer,
 *                     and each accepted answer may only satisfy one slot.
 *
 * Comparison is case-insensitive and trims surrounding whitespace.
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
     * @param q         the question (with its answers/options populated)
     * @param responses user text responses; for multiple choice, responses[0]
     *                  is the chosen option's text; for multi-answer, one entry
     *                  per slot in slot order.
     */
    public static Result grade(Question q, List<String> responses) {
        if (responses == null) responses = new ArrayList<>();
        switch (q.getType()) {
            case MULTIPLE_CHOICE:
                return gradeMultipleChoice(q, responses);
            case QUESTION_RESPONSE:
            case FILL_BLANK:
            case PICTURE_RESPONSE:
            default:
                return gradeTextual(q, responses);
        }
    }

    private static Result gradeMultipleChoice(Question q, List<String> responses) {
        String chosen = responses.isEmpty() ? "" : norm(responses.get(0));
        boolean right = false;
        for (Question.Option o : q.getOptions()) {
            if (o.isCorrect() && norm(o.getText()).equals(chosen)) { right = true; break; }
        }
        return new Result(right ? 1 : 0, 1);
    }

    /**
     * Handles single-slot textual questions AND multi-answer questions.
     * Slot count comes from the accepted answers' slot indices.
     */
    private static Result gradeTextual(Question q, List<String> responses) {
        int slots = q.getSlotCount();

        if (slots <= 1) {
            // single-answer: any accepted answer matches
            String given = responses.isEmpty() ? "" : norm(responses.get(0));
            for (Question.Answer a : q.getAnswers()) {
                if (norm(a.getText()).equals(given)) return new Result(1, 1);
            }
            return new Result(0, 1);
        }

        if (q.isOrdered()) {
            return gradeOrdered(q, responses, slots);
        }
        return gradeUnordered(q, responses, slots);
    }

    /** Ordered multi-answer: response[i] must match an accepted answer for slot i. */
    private static Result gradeOrdered(Question q, List<String> responses, int slots) {
        int correct = 0;
        for (int slot = 0; slot < slots; slot++) {
            String given = slot < responses.size() ? norm(responses.get(slot)) : "";
            if (given.isEmpty()) continue;
            for (Question.Answer a : q.getAnswers()) {
                if (a.getSlot() == slot && norm(a.getText()).equals(given)) { correct++; break; }
            }
        }
        return new Result(correct, slots);
    }

    /**
     * Unordered multi-answer: each user response may match any accepted answer.
     * An accepted answer can satisfy only one slot, and a single user response
     * cannot score twice. Note: there may be MORE accepted answers than slots
     * (e.g. "name five Best-Picture winners"), which is fine.
     */
    private static Result gradeUnordered(Question q, List<String> responses, int slots) {
        // Build the set of accepted answers (normalized).
        Set<String> accepted = new HashSet<>();
        for (Question.Answer a : q.getAnswers()) accepted.add(norm(a.getText()));

        Set<String> usedResponses = new HashSet<>();  // avoid double-counting same answer twice
        Set<String> consumed = new HashSet<>();        // accepted answers already matched
        int correct = 0;

        for (String r : responses) {
            String given = norm(r);
            if (given.isEmpty() || usedResponses.contains(given)) continue;
            if (accepted.contains(given) && !consumed.contains(given)) {
                correct++;
                consumed.add(given);
                usedResponses.add(given);
                if (correct >= slots) break; // can't score more than the slots available
            }
        }
        return new Result(correct, slots);
    }
}
