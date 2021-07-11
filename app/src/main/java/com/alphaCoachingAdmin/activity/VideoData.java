package com.alphaCoachingAdmin.activity;

public class VideoData {
    String name,uid;


    public VideoData() {
    }

    public VideoData(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
