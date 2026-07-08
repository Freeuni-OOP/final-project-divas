package org.example.quiz.model;

public class QuestionAnswer {
    private long id;          //answer has its own id for easy changes in database
    private long questionId;
    private String answer;

    //constructor and getter for the actual question string
    public QuestionAnswer(String answer) {
        this.answer = answer;
    }
    public String getAnswer() {
        return answer;
    }

    //setter and getter for answer ID
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
}
