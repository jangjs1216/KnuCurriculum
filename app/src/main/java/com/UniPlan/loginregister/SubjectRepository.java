package com.UniPlan.loginregister;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SubjectRepository {
    @Query("SELECT * FROM subject")
    List<Subject> findAll();

    @Query("SELECT next_Sub FROM subject WHERE code = :codeInput")
    public String getNext_Sub(String codeInput);

    @Query("SELECT * FROM subject WHERE code = :codeInput")
    public List<Subject> getSubjectInfo(String codeInput);

    @Query("DELETE FROM subject")
    public void nukeTable();

    @Query("SELECT * FROM subject")
    public List<Subject> loadAllUser();

    @Insert
    void insert(Subject subject);

}
