package com.alphaCoachingAdmin.activity;


import com.google.firebase.Timestamp;

public class QuizData {
    String quizName;
    int questionNumber,quizTime;
    Timestamp quizDate;
    private boolean activeStatus;

    public QuizData() {
    }

    public Timestamp getQuizDate() {
        return quizDate;
    }

    public void setQuizDate(Timestamp quizDate) {
        this.quizDate = quizDate;
    }

    public QuizData(String quizName, int questionNumber, int quizTime, Timestamp quizDate) {
        this.quizName = quizName;
        this.questionNumber = questionNumber;
        this.quizTime = quizTime;
        this.quizDate=quizDate;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public int getQuizTime() {
        return quizTime;
    }

    public void setQuizTime(int quizTime) {
        this.quizTime = quizTime;
    }

    public boolean getActiveStatus() {
        return activeStatus;
    }
}
