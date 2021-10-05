package com.UniPlan.loginregister;

import java.util.Map;

public class Line {
    Map<String, String> line;
    String root;

    public Line() {
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public Line(Map<String, String> line, String root) {
        this.line = line;
        this.root = root;
    }

    public Map<String, String> getLine() {
        return line;
    }

    public void setLine(Map<String, String> line) {
        this.line = line;
    }
}
