package com.example.loginregister;

import java.util.ArrayList;

public class Subject_ {
    String name;
    String code;
    String score;
    //전체 별점 평균내기 위한 변수
    int voteNum;
    float TotalScore;
    String grade;
    String semester;
    String recoPre;
    String recoPost;
    Boolean open;
    ArrayList<SubjectComment> comments;

    public int getVoteNum(){return voteNum;}

    public void setVoteNum(int voteNum){this.voteNum=voteNum;}

    public float getTotalScore() {
        return TotalScore;
    }

    public void setTotalScore(float totalScore) {
        TotalScore = totalScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getRecoPre() {
        return recoPre;
    }

    public void setRecoPre(String recoPre) {
        this.recoPre = recoPre;
    }

    public String getRecoPost() {
        return recoPost;
    }

    public void setRecoPost(String recoPost) {
        this.recoPost = recoPost;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public ArrayList<SubjectComment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<SubjectComment> comments) {
        this.comments = comments;
    }

    public Subject_() {
        this.voteNum=0;
        this.name = "";
        this.code = "";
        this.score = "";
        this.grade = "";
        this.semester = "";
        this.recoPre = "";
        this.recoPost = "";
        this.open = false;
        this.comments = new ArrayList<>();
        this.TotalScore=0;
    }

    public Subject_(String name, String code, String score, String grade, String semester, String recoPre, String recoPost, Boolean open, ArrayList<SubjectComment> comments, int voteNum, float totalScore) {
        this.name = name;
        this.code = code;
        this.score = score;
        this.grade = grade;
        this.semester = semester;
        this.recoPre = recoPre;
        this.recoPost = recoPost;
        this.open = open;
        this.comments = comments;
        this.voteNum=voteNum;
        this.TotalScore=totalScore;
    }

}
