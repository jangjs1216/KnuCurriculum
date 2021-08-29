package com.example.loginregister;

public class Picksubject {

    String curSub;
    String nextSub;
    String n_nextSub;
    float first;
    float second;

    Picksubject(){}

    Picksubject(String curSub, String nextSub, String n_nextSub, float first, float second){

        this.curSub =curSub;
        this.nextSub =nextSub;
        this.n_nextSub=n_nextSub;
        this.first=first;
        this.second=second;
    }

    public String getCurSub() {
        return curSub;
    }

    public void setCurSub(String curSub) {
        this.curSub = curSub;
    }

    public String getNextSub() {
        return nextSub;
    }

    public void setNextSub(String nextSub) {
        this.nextSub = nextSub;
    }

    public String getN_nextSub() {
        return n_nextSub;
    }

    public void setN_nextSub(String n_nextSub) {
        this.n_nextSub = n_nextSub;
    }

    public float getFirst() {
        return first;
    }

    public void setFirst(float first) {
        this.first = first;
    }

    public float getSecond() {
        return second;
    }

    public void setSecond(float second) {
        this.second = second;
    }
}
