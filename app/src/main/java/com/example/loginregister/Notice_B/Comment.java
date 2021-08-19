package com.example.loginregister.Notice_B;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment implements Comparable<Comment> {
    private String c_nickname;//댓글 단사람 닉네임
    private String comment;//댓글내용
    private String documentId;//댓글 단사람 고유식별번호
    private String comment_id;//댓글 고유값 정렬할 숫자
    private int Ccoment_Num; //댓글이 가진 대댓글 갯수


    public Comment(String doucumentId, String c_nickname, String comment,   String comment_id, int Ccmment_Num) {
        this.c_nickname = c_nickname;
        this.comment = comment;
        this.documentId=doucumentId;
        this.comment_id=comment_id;
        this.Ccoment_Num=Ccmment_Num;
    }

    public void setCcoment_Num(int ccoment_num){this.Ccoment_Num=ccoment_num;}

    public int getCcoment_Num(){return Ccoment_Num;}

    public void setComment_id(String comment_id) {this.comment_id=comment_id; }

    public String getComment_id() {
        return comment_id;
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


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public Comment() {//빈생성자
        this.c_nickname = "";
        this.comment = "";
        this.documentId="";
        this.comment_id="";
        this.Ccoment_Num=0;
    }


    @Override
    public int compareTo(Comment comment) {

        Integer c1=Integer.parseInt(this.comment_id);
        Integer c2 = Integer.parseInt(comment.comment_id);
        return c1.compareTo(c2);
    }
}