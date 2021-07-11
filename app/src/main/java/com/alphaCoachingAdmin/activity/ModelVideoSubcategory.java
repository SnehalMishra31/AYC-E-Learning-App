package com.alphaCoachingAdmin.activity;

public class ModelVideoSubcategory {
    String name,url,category;

    public ModelVideoSubcategory() {
    }

    public ModelVideoSubcategory(String name, String url, String category) {
        this.name = name;
        this.url = url;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
