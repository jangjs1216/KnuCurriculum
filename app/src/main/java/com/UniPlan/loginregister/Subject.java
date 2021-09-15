package com.UniPlan.loginregister;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Subject {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    private String name;//과목명

    @ColumnInfo(name = "code")
    private String code;//과목코드

    @ColumnInfo(name = "grade")
    private String grade;//학년

    @ColumnInfo(name = "semester")
    private String semester;//학기

    @ColumnInfo(name = "major")
    private String major;//전공여부

    @ColumnInfo(name = "open")
    private Boolean open;// 해당학기 개설여부

    @ColumnInfo(name = "pre_sub")
    private String pre_sub;//선수 과목

    @ColumnInfo(name = "next_sub")
    private String next_sub;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public String getPre_sub() {
        return pre_sub;
    }

    public void setPre_sub(String pre_sub) {
        this.pre_sub = pre_sub;
    }

    public String getNext_sub() {
        return next_sub;
    }

    public void setNext_sub(String next_sub) {
        this.next_sub = next_sub;
    }

    @Ignore
    public Subject(String name, String code, String grade, String semester, String major, Boolean open, String pre_sub, String next_sub) {
        this.name = name;
        this.code = code;
        this.grade = grade;
        this.semester = semester;
        this.major = major;
        this.open = open;
        this.pre_sub = pre_sub;
        this.next_sub = next_sub;
    }

    public Subject(int uid, String name, String code, String grade, String semester, String major, Boolean open, String pre_sub, String next_sub) {
        this.uid = uid;
        this.name = name;
        this.code = code;
        this.grade = grade;
        this.semester = semester;
        this.major = major;
        this.open = open;
        this.pre_sub = pre_sub;
        this.next_sub = next_sub;
    }
}


