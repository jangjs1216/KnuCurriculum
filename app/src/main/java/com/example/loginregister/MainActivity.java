package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    //과목코드 해시함수로 배열화 과목코드넣으면 과목명이랑 학점나옴
    HashMap<String, Object> subjectCode =  new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth=FirebaseAuth.getInstance();

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그아웃 하기
                mFirebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        //과목코드 받아오기 함수시작
        try {
            readFromAssets("subjectCode.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //과목코드 받아오기 함수
    private void  readFromAssets(String filename) throws Exception {


        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));

        String line = reader.readLine();
        int counti=0;

        while(line != null) {

            String[] Sarray = line.split("\t");
            String Scode = Sarray[0];
            String[] Sname = {Sarray[1],Sarray[2]};
            subjectCode.put(Sarray[0],Sname);

            ++counti;
            line = reader.readLine();
        }
        reader.close();
    }
}