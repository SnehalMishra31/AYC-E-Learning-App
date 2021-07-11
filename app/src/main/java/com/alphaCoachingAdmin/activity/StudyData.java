package com.alphaCoachingAdmin.activity;

public class StudyData {

    String PDFName,url;
    public StudyData() {

    }

    public StudyData(String PDFName, String url) {
        this.PDFName = PDFName;
        this.url = url;
    }

    public String getPDFName() {
        return PDFName;
    }

    public void setPDFName(String PDFName) {
        this.PDFName = PDFName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
