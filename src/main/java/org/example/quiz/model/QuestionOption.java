package org.example.quiz.model;

public class QuestionOption {
    private long id;          // this option's own id
    private long questionId;  // which question this option belongs to
    private String optionText;
    private boolean correct;
    private int position;     // display order among the other options

    public QuestionOption(String optionText) {
        this.optionText = optionText;
    }

    //setter and getter for option ID
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //setter and getter for question id
    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    //getter for the option text
    public String getOptionText() {
        return optionText;
    }

    //is the option correct one
    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    //setter and getter for display position
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
