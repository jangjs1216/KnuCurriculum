package com.example.loginregister.Notice_B;


//내가 누른 좋아요나 쓴글보기위해 만든 클래스 박경무

public class Where_who_post {
    String forumNum;
    String postId;

    public String getForumNum() {
        return forumNum;
    }

    public void setForumNum(String forumNum) {
        this.forumNum = forumNum;
    }

    public String getPostid() {
        return postId;
    }

    public void setPostid(String postid) {
        this.postId = postid;
    }

    public Where_who_post(String forumNum, String postId){
        this.postId=postId;
        this.forumNum=forumNum;
    }

    public Where_who_post()
    {

    }
}
