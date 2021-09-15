package com.UniPlan.loginregister.push_alram;

import java.util.ArrayList;

public class Alarms {
    private ArrayList<Alarm> alarms;

    public Alarms() {
    }

    public ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }

    public Alarms(ArrayList<Alarm> alarms) {
        this.alarms = alarms;
    }
}

