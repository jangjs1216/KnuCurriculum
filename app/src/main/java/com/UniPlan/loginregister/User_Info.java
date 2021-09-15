package com.UniPlan.loginregister;

public class User_Info {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User_Info(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
