package com.UniPlan.loginregister;

import java.util.Map;

public class Table {
    Map<String, Map<String, String>> table;
    String root;

    public Table() {
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public Table(Map<String, Map<String, String>> table, String root) {
        this.table = table;
        this.root = root;
    }

    public Map<String, Map<String, String>> getTable() {
        return table;
    }

    public void setTable(Map<String, Map<String, String>> table) {
        this.table = table;
    }
}
