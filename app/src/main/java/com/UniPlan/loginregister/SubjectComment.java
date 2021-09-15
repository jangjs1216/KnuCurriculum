package com.UniPlan.loginregister;

import java.util.Date;

public class SubjectComment {
    String content;
    String user_id;
    String rating;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public SubjectComment(){
        this.content = "";
        this.user_id = "";
        this.rating = "";
    }

    public SubjectComment(String content, String user_id, String rating) {
        this.content = content;
        this.user_id = user_id;
        this.rating = rating;
    }
}
