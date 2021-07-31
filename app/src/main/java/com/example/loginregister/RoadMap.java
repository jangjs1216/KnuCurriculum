package com.example.loginregister;

import android.util.Log;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoadMap {
    boolean adj[][];
    HashMap<String, Integer> m;
    ArrayList<Subject> subjects;
    int size;

    public RoadMap(int input){
        size = input;
        adj = new boolean[size][size];
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                adj[i][j] = false;
            }
        }
        m = new HashMap<String, Integer>();
        subjects = new ArrayList<>();
    }

    public void add(Subject s){
        // 매핑
        m.put(s.getName(), m.size());

        // 정보 추가
        subjects.add(s);
    }

    public void makeadj(){
        // 인접리스트(후수과목)
        for(Subject s : subjects){
            String temp = s.getNext_sub();
            if(temp == null) continue;
            String [] tempsplit = temp.split(",");
            for(String t : tempsplit) {
                int u = m.get(s.getName());
                int v = m.get(t);
                adj[u][v] = true;

                Log.e("###", s.getName() + " to " + t + ", u : " + u + ", v : " + v);
            }
        }
    }
}

//    public void DataBaseInit(){
//        Log.e("###", "느검마0");
//
//        AppDatabase db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,"db-cos")
//                .fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
//                .build();
//
//        subjectRepository = db.subjectRepository();
//
//        Log.e("###", "느검마1");
//
//        Subject subject0 = new Subject("논리회로", null,"마이크로프로세서");
//        Subject subject1 = new Subject("마이크로프로세서", "논리회로","운영체제,임베디드시스템");
//        Subject subject2 = new Subject("신호및시스템", null, "임베디드시스템");
//        Subject subject3 = new Subject("운영체제", "마이크로프로세서",  null);
//        Subject subject4 = new Subject("임베디드시스템", "마이크로프로세서", "디지털신호처리");
//        Subject subject5 = new Subject("디지털신호처리", "임베디드시스템", null);
//
//        subjectRepository.nukeTable();
//        List<Subject> subjectList = subjectRepository.findAll();
//        Log.e(TAG, String.valueOf(subjectList.isEmpty()));
//
//        subjectRepository.insert(subject0);
//        subjectRepository.insert(subject1);
//        subjectRepository.insert(subject2);
//        subjectRepository.insert(subject3);
//        subjectRepository.insert(subject4);
//        subjectRepository.insert(subject5);
//
//        Log.e("###", "느검마2");
//
//        makeTree();
//    }
//
//    public void makeTree(){
//        List<Subject> subjectlist = subjectRepository.findAll();
//        SubjectTree subjectTree = new SubjectTree(subjectlist.size());
//
//        for(Subject sub : subjectlist){
//            subjectTree.add(sub);
//        }
//
//        subjectTree.makeadj();
//    }
//
//    public void printTree(Subject subject){
//        List<String> list = gson.fromJson(subject.getPre_sub(),type);
//        String [] strarray = list.toArray(new String[0]);
//        Log.e(TAG,subject.getName()+" "+subject.getPre_sub()+" "+subject.getNext_sub());
//        for(String string : strarray ){
//            Log.e(TAG,"후수과목 : "+string);
//        }
//
//    }

