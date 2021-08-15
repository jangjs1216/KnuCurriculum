package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class SubjectInfoActivity extends AppCompatActivity {
    ArrayList<Subject> subjectList;
    HashMap<String, Integer> m;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjectinfo);

        /* 서버에서 받아 올 과목 정보 */
        subjectList = new ArrayList<>();
        Subject subject1 = new Subject("논리회로", "ELEC000000", "0", "0", "0", false, "0", "0");
        Subject subject2 = new Subject("회로이론", "ELEC111111", "0", "0", "0", false, "0", "0");
        Subject subject3 = new Subject("확률과정", "ELEC222222", "0", "0", "0", false, "0", "0");
        Subject subject4 = new Subject("머신러닝", "ELEC333333", "0", "0", "0", false, "0", "0");
        Subject subject5 = new Subject("A", "1", "0", "0", "0", false, "0", "0");
        Subject subject6 = new Subject("B", "2", "0", "0", "0", false, "0", "0");
        Subject subject7 = new Subject("C", "3", "0", "0", "0", false, "0", "0");
        Subject subject8 = new Subject("D", "4", "0", "0", "0", false, "0", "0");
        subjectList.add(subject1);
        subjectList.add(subject2);
        subjectList.add(subject3);
        subjectList.add(subject4);
        subjectList.add(subject5);
        subjectList.add(subject6);
        subjectList.add(subject7);
        subjectList.add(subject8);

        /* DB에서 받아온 과목들 매핑 */
        m = new HashMap<String, Integer>();
        for(int i=0; i<subjectList.size(); i++){
            m.put(subjectList.get(i).getName(), m.size());
        }

        Intent intent = getIntent();
        String subjectName = intent.getStringExtra("subjectName");

        TextView tv1 = (TextView) findViewById(R.id.tv1);
        TextView tv2 = (TextView) findViewById(R.id.tv2);

        Subject curSubject = subjectList.get(m.get(subjectName));

        tv1.setText("과목명 : " + curSubject.getName());
        tv2.setText("과목 코드 : " + curSubject.getCode());
    }
}
