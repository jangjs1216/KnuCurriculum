package com.example.loginregister.Notice_B;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post implements Comparable<Post> {
    private String documentId;//게시글 올린 사람
    private String title;//게시글 제목
    private String contents;//게시글 내용
    private String p_nickname;//게시글 작성자 닉네임
    private String p_photo;//게시글 작성자 사진
    private String post_photo; //게시글에 등록할 사진
    private String like; //게시글 좋아요 개수
    @ServerTimestamp
    private Date date;
    private String post_num;
    private String post_id;
    private String writer_id;
    public Post() {//빈생성자 생성

    }



    public Post(String documentId, String title, String contents, String p_nickname, String p_photo, String post_num, String post_photo, String post_id, String writer_id, String like) {//String p_nickname 잠시 보류

        this.documentId = documentId;
        this.title = title;
        this.contents = contents;
        this.p_nickname=p_nickname;
        this.p_photo=p_photo;
        this.post_num=post_num;
        this.post_photo=post_photo;
        this.like = like;
        this.post_id=post_id;
        this.writer_id=writer_id;

    }

    public String getPost_id() {
        return post_id;

    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }



    public String getPost_num() {
        return post_num;
    }

    public void setPost_num(String post_num) {
        this.post_num = post_num;
    }

    //alt+insert키르 누르면 클래스에 필요한 메소드 자동생성 가능
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String DocumentId) {
        this.documentId = documentId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getP_nickname() {
        return p_nickname;
    }

    public void setP_nickname(String p_nickname) {
        this.p_nickname = p_nickname;
    }



    public String getP_photo() {
        return p_photo;
    }

    public void setP_photo(String p_photo) {
        this.p_photo = p_photo;
    }

    public String getPost_photo() {
        return post_photo;
    }

    public void setPost_photo(String post_photo) {
        this.post_photo = post_photo;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getWriter_id() {
        return writer_id;
    }
    public void setWriter_id(String writer_id) {
        this.writer_id = writer_id;
    }

    @Override
    public String toString() {
        return "Post{" +
                "documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", p_nickname='" + p_nickname + '\'' +
                ", p_photo='" + p_photo + '\'' +
                ", post_photo='" + post_photo + '\'' +
                ", like='" + like + '\'' +
                ", date=" + date +
                ", post_num='" + post_num + '\'' +
                ", post_id='" + post_id + '\'' +
                ", writer_id='" + writer_id + '\'' +
                '}';
    }

    @Override
    public int compareTo(Post o) {
        return Integer.parseInt(o.getLike()) - Integer.parseInt(getLike());
    }
}