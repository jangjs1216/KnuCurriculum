package com.example.loginregister.Notice_B;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post implements Comparable<Post> {
    private String writer_id;
    private String title;//게시글 제목
    private String contents;//게시글 내용
    private String p_nickname;//게시글 작성자 닉네임
    private String like; //게시글 좋아요 개수
    @ServerTimestamp
    private Timestamp timestamp;
    private String post_id;

    public String getWriter_id() {
        return writer_id;
    }

    public void setWriter_id(String writer_id) {
        this.writer_id = writer_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getP_nickname() {
        return p_nickname;
    }

    public void setP_nickname(String p_nickname) {
        this.p_nickname = p_nickname;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setDate(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public Post() {//빈생성자 생성
        this.writer_id = "";
        this.title = "";
        this.contents = "";
        this.p_nickname = "";
        this.like = "";
        this.timestamp = null;
        this.post_id = "";
    }

    public Post(String writer_id, String title, String contents, String p_nickname, String like, Timestamp timestamp, String post_id) {
        this.writer_id = writer_id;
        this.title = title;
        this.contents = contents;
        this.p_nickname = p_nickname;
        this.like = like;
        this.timestamp = timestamp;
        this.post_id = post_id;
    }

    @Override
    public int compareTo(Post o) {
        return Integer.parseInt(o.getLike()) - Integer.parseInt(getLike());
    }
}