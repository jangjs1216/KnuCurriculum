package com.UniPlan.loginregister.push_alram;


import com.google.firebase.Timestamp;

public class Alarm {
    private String title;
    private Timestamp timestamp;
    private String forum_sort;
    private String post_id;
    private boolean checked;

    public Alarm() {
        this.title="";
        this.timestamp=null;
        this.forum_sort="";
        this.post_id="";
        this.checked=false;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getForum_sort() {
        return forum_sort;
    }

    public void setForum_sort(String forum_sort) {
        this.forum_sort = forum_sort;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Alarm(String title, Timestamp timestamp, String forum_sort, String post_id, boolean checked) {
        this.title = title;
        this.timestamp = timestamp;
        this.forum_sort = forum_sort;
        this.post_id = post_id;
        this.checked = checked;
    }
}
