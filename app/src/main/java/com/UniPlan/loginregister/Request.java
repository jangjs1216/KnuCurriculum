package com.UniPlan.loginregister;

public class Request {
    String title;
    String content;

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Request(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
