package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.curiList.Recycler_Adapter;
import com.example.loginregister.curiList.Recycler_Data;
import com.example.loginregister.curiList.User_Info_Data;
import com.example.loginregister.login.KeepLoginActivity;
import com.example.loginregister.login.SavedSharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "main";
    /*
    [ 2021-08-06 장준승 Fragment 추가 ]
     */
    BottomNavigationView bottomNavigationView;

    //과목코드 해시함수로 배열화 과목코드넣으면 과목명이랑 학점나옴
    HashMap<String, Object> subjectCode =  new HashMap<>();

    //닉네임 검사
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String user_nick;

    //리싸이클러뷰
    private ArrayList<Recycler_Data> arrayList_curiList;
    private ArrayList<User_Info_Data> arrayList_userInfoData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavi);

        bottomNavigationView.setVisibility(View.INVISIBLE);

        check_nickname();

        arrayList_curiList = new ArrayList<>();
        arrayList_userInfoData = new ArrayList<>();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.item_fragment1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment1()).commit();
                        break;
                    case R.id.item_fragment2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment2()).commit();
                        break;
                    case R.id.item_fragment3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment3()).commit();
                        break;
                    case R.id.item_fragment4:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment4()).commit();
                        break;
                }
                return true;
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
    public void check_nickname(){
        if(mAuth.getCurrentUser()!=null){
            Log.e(TAG, "계정정보있음");
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                user_nick = task.getResult().getString("nickname");
                                if(user_nick!=null&&user_nick.length()!=0) {
                                    Log.e(TAG, "닉네임받아오기성공 - "+user_nick);
                                    bottomNavigationView.setVisibility(View.VISIBLE);
                                    getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new Fragment1()).commit();
                                }
                                else{
                                    Log.e(TAG,"닉네임없음");
                                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,new Fragment_SetNickName()).commit();
                                }
                            }
                            else Log.e(TAG,"계정정보받아오기좆버그");
                        }
                    });
        }
        else {
            Log.e(TAG,"계정정보없음 " );
            SavedSharedPreferences.setUserName(getApplicationContext(),null);//폰에있는 자동로그인정보 초기화
            Intent intent = new Intent(MainActivity.this, KeepLoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public ArrayList<User_Info_Data> getArrayList_userInfoData() {
        return arrayList_userInfoData;
    }

    public void setArrayList_userInfoData(ArrayList<User_Info_Data> arrayList_userInfoData) {
        this.arrayList_userInfoData = arrayList_userInfoData;
    }
    public ArrayList<Recycler_Data> getArrayList_curiList() {
        return arrayList_curiList;
    }

    public void setArrayList_curiList(ArrayList<Recycler_Data> arrayList_curiList) {
        this.arrayList_curiList = arrayList_curiList;
    }


}