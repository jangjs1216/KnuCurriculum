package com.UniPlan.loginregister;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Subject.class},version = 2)
public abstract class AppDataBase extends RoomDatabase {
    public abstract SubjectRepository subjectRepository();
}
