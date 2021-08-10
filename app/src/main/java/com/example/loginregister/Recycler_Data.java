package com.example.loginregister;

public class Recycler_Data {
    private String tv_title;
    private String tv_content;

    public Recycler_Data(String tv_title, String tv_content) {
        this.tv_title = tv_title;
        this.tv_content = tv_content;
    }

    public String getTv_title() {
        return tv_title;
    }

    public void setTv_title(String tv_title) {
        this.tv_title = tv_title;
    }

    public String getTv_content() {
        return tv_content;
    }

    public void setTv_content(String tv_content) {
        this.tv_content = tv_content;
    }
}
