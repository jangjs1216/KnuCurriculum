package com.example.loginregister.Notice_B;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Content {
    private String c_nickname;//댓글 단사람 닉네임
    private String c_photo; //댓글 단사람 사진
    private String comment;//댓글
    private String documentId;//댓글 단사람 고유식별번호
    private String post_position;
    private String post_title;
    @ServerTimestamp
    private Date comment_date;
    private String comment_post;



    public Content(String doucumentId, String c_nickname, String comment, String post_position, String post_title, String c_photo, String comment_post) {
        this.c_nickname = c_nickname;
        this.c_photo = c_photo;
        this.comment = comment;
        this.documentId=doucumentId;
        this.post_position=post_position;
        this.post_title=post_title;
        this.comment_post="comment_post";
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_position() {
        return post_position;
    }

    public void setPost_position(String post_position) {
        this.post_position = post_position;
    }



    public Date getComment_date() {
        return comment_date;
    }

    public void setComment_date(Date comment_date) {
        this.comment_date = comment_date;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getC_nickname() {
        return c_nickname;
    }

    public void setC_nickname(String c_nickname) {
        this.c_nickname = c_nickname;
    }

    public String getC_photo() {
        return c_photo;
    }

    public void setC_photo(String c_photo) {
        this.c_photo = c_photo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_post() {
        return comment_post;
    }

    public void setComment_post(String comment_post) {
        this.comment_post = comment_post;
    }

    public Content() {//빈생성자

    }


    @Override
    public String toString() {
        return "Content{" +
                "c_nickname='" + c_nickname + '\'' +
                ", c_photo='" + c_photo + '\'' +
                ", comment='" + comment + '\'' +
                ", documentId='" + documentId + '\'' +
                ", post_position='" + post_position + '\'' +
                ", post_title='" + post_title + '\'' +
                ", comment_date=" + comment_date +
                ", comment_post='" + comment_post + '\'' +
                '}';
    }
}