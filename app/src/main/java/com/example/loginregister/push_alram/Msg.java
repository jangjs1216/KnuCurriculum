package com.example.loginregister.push_alram;


import com.google.firebase.Timestamp;

public class Msg {
    private String post_id;
    private String forum;

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public Msg(String forum, String post_id, String who, Timestamp timestamp) {
        this.post_id = post_id;
        this.who = who;
        this.timestamp = timestamp;
        this.forum = forum;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    private String who;
    private Timestamp timestamp;
}
