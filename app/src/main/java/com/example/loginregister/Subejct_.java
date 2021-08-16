package com.example.loginregister;

import java.util.ArrayList;

public class Subejct_ {
    String name;
    String code;
    String grade;
    String semester;
    Boolean open;
    ArrayList<SubjectComment> comments;

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

    public Subejct_(){
        this.name = "";
        this.code = "";
        this.grade = "";
        this.semester = "";
        this.open = true;
        this.comments = new ArrayList<>();
    }

    public Subejct_(String name, String code, String grade, String semester, Boolean open, ArrayList<SubjectComment> comments) {
        this.name = name;
        this.code = code;
        this.grade = grade;
        this.semester = semester;
        this.open = open;
        this.comments = comments;
    }
}
