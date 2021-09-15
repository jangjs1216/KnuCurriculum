package com.UniPlan.loginregister.Notice_B;

import java.util.Map;

public class FireStoreTable {
    Map<String, Map<String, String>> table;

    public FireStoreTable() {
    }

    public FireStoreTable(Map<String, Map<String, String>> table) {
        this.table = table;
    }

    public Map<String, Map<String, String>> getTable() {
        return table;
    }

    public void setTable(Map<String, Map<String, String>> table) {
        this.table = table;
    }
}
