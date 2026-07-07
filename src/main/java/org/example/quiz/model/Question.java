package org.example.quiz.model;

public class Question {

    private long id;          // question id in database
    private long quizId;      // which quiz this question belongs to
    private String questionType;
    private String question;
    private int num;     // numerical position of question

    //setter and getter for question text
    public Question(String question) {
        this.question = question;
    }
    public String getQuestion() {
        return question;
    }

    //setter and getters for question and quizz ID-s
    public void setId(long id) {
        this.id = id;
    }
    public long getId(){
        return id;
    }
    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }
    public long getQuizId(){
        return quizId;
    }

    //setter and getter for a quesiton type
    public String getQuestionType() {
        return questionType;
    }
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    //setter and getter for the numerical position of the question in a quizz
    public void setNum(int num) {
        this.num = num;
    }
    public int getNum() {
        return num;
    }
}
