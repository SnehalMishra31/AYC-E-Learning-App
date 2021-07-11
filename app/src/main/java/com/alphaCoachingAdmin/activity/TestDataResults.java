package com.alphaCoachingAdmin.activity;

public class TestDataResults {

    String userName,userId;
    int score,TotalScore;

    public TestDataResults() {
    }

    public TestDataResults(String userName, String userId, int score, int totalScore) {
        this.userName = userName;
        this.userId = userId;
        this.score = score;
        TotalScore = totalScore;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalScore() {
        return TotalScore;
    }

    public void setTotalScore(int totalScore) {
        TotalScore = totalScore;
    }
}
