package com.UniPlan.loginregister;

import java.util.ArrayList;

public class Request {
    String title;
    String content;
    String Id;
    ArrayList<String> urllist;

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

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public ArrayList<String> getUrllist() {
        return urllist;
    }

    public void setUrllist(ArrayList<String> urllist) {
        this.urllist = urllist;
    }

    public Request(String title, String content, String id, ArrayList<String> urllist) {
        this.title = title;
        this.content = content;
        Id = id;
        this.urllist = urllist;
    }
}
