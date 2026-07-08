package org.example.quiz.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A single question. Table owned by Part B; C reads it for grading.
 *
 * Four required types:
 *   QUESTION_RESPONSE, FILL_BLANK, MULTIPLE_CHOICE, PICTURE_RESPONSE
 */
public class Question {

    public enum Type { QUESTION_RESPONSE, FILL_BLANK, MULTIPLE_CHOICE, PICTURE_RESPONSE }

    private long id;
    private long quizId;
    private Type type;
    private String prompt;
    private String imageUrl;   // PICTURE_RESPONSE
    private boolean ordered;   // multi-answer ordering
    private int position;

    /** For MULTIPLE_CHOICE. */
    private List<Option> options = new ArrayList<>();
    /** Accepted text answers, grouped by slot for multi-answer questions. */
    private List<Answer> answers = new ArrayList<>();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getQuizId() { return quizId; }
    public void setQuizId(long quizId) { this.quizId = quizId; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isOrdered() { return ordered; }
    public void setOrdered(boolean ordered) { this.ordered = ordered; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public List<Option> getOptions() { return options; }
    public void setOptions(List<Option> options) { this.options = options; }

    public List<Answer> getAnswers() { return answers; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }

    /** Number of answer slots (1 for normal questions, N for multi-answer). */
    public int getSlotCount() {
        int max = 0;
        for (Answer a : answers) max = Math.max(max, a.getSlot());
        return max + 1; // slots are 0-indexed
    }

    /** A multiple-choice option. */
    public static class Option {
        private long id;
        private String text;
        private boolean correct;
        private int position;

        public Option() { }
        public Option(long id, String text, boolean correct, int position) {
            this.id = id; this.text = text; this.correct = correct; this.position = position;
        }
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public boolean isCorrect() { return correct; }
        public void setCorrect(boolean correct) { this.correct = correct; }
        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }
    }

    /** An accepted text answer for a given slot. */
    public static class Answer {
        private long id;
        private String text;
        private int slot;

        public Answer() { }
        public Answer(long id, String text, int slot) {
            this.id = id; this.text = text; this.slot = slot;
        }
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public int getSlot() { return slot; }
        public void setSlot(int slot) { this.slot = slot; }
    }
}
