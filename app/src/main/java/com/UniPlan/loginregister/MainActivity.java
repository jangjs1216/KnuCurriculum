package com.UniPlan.loginregister;

import static com.UniPlan.loginregister.R.drawable.ic_baseline_notifications_24;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.UniPlan.loginregister.R;
import com.UniPlan.loginregister.curiList.Recycler_Data;
import com.UniPlan.loginregister.login.UserAccount;
import com.UniPlan.loginregister.push_alram.Fragment_Alarm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

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
    MenuItem bottom_alarm;
    //과목코드 해시함수로 배열화 과목코드넣으면 과목명이랑 학점나옴
    HashMap<String, Object> subjectCode =  new HashMap<>();
    public static Context maincontext;

    public void setBackPressedlistener(IOnBackPressed backPressedlistener) {
        this.backPressedlistener = backPressedlistener;
    }

    IOnBackPressed backPressedlistener;
    //닉네임 검사
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String user_nick;

    //리싸이클러뷰
    private ArrayList<Recycler_Data> arrayList_curiList;
    private UserAccount userAccount;

    private long time=0;
    private Toast toast;

    public interface IOnBackPressed{
        void onBackPressed();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(backPressedlistener!=null){
            Log.e("backpress","null아님");
           backPressedlistener.onBackPressed();
        }
        else {
            Log.e("backpress","null");
            if (System.currentTimeMillis() - time >= 2000) {
                time = System.currentTimeMillis();
                toast = Toast.makeText(getApplicationContext(), "뒤로 가기 버튼 한번 더 누르면 앱 종료", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast.cancel();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maincontext = this;
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                           // Log.e(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        //Log.e(TAG, msg);
                       // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        bottomNavigationView = findViewById(R.id.bottomNavi);

        bottomNavigationView.setVisibility(View.INVISIBLE);


        check_nickname();

        //알람생기면 바텀아이콘 변경
        bottom_alarm = bottomNavigationView.getMenu().findItem(R.id.item_fragment5);

//        mStore.collection("Alarm").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                Log.e("alarm", "감지");
//                bottom_alarm.setIcon(ic_baseline_notifications_active_24);
//            }
//        });


        arrayList_curiList = new ArrayList<>();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.item_fragment1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment1()).commit();
                        break;
//                    case R.id.item_fragment2:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment2()).commit();
//                        break;
                    case R.id.item_fragment3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment3()).commit();
                        break;
                    case R.id.item_fragment4:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment4()).commit();
                        break;
                    case R.id.item_fragment5:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,new Fragment_Alarm()).commit();
                }
                return true;
            }
        });








        //과목코드 받아오기 함수시작
//        try {
//            readFromAssets("subjectCode.txt");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    public void setvisibleNavi(boolean bottom){
        if(bottom){bottomNavigationView.setVisibility(View.INVISIBLE);}
        else{bottomNavigationView.setVisibility(View.VISIBLE);}
    }


    //과목코드 받아오기 함수
    private void  readFromAssets(String filename) throws Exception {


        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));

        String line = reader.readLine();
        int counti=0;

        while(line != null) {
            Subject_ subject_;
            String[] Sarray = line.split("\t");
            String Scode = Sarray[0];
            String Sname = Sarray[1];
            String score = Sarray[2].substring(0, 1);


            //String name, String code, String score, String grade, String semester, Boolean open, ArrayList<SubjectComment> comments
            subject_ = new Subject_(Sname, Scode, score, "1", "1", "없음", "없음", false, new ArrayList<>(),0,0);

            mStore.collection("Subject").document(subject_.getName()).set(subject_);



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
                                userAccount = task.getResult().toObject(UserAccount.class);
                                user_nick = userAccount.getNickname();
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
        }
    }


    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
    public ArrayList<Recycler_Data> getArrayList_curiList() {
        return arrayList_curiList;
    }

    public void setArrayList_curiList(ArrayList<Recycler_Data> arrayList_curiList) {
        this.arrayList_curiList = arrayList_curiList;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }

    public void setBottom_alarm(){
        bottom_alarm.setIcon(ic_baseline_notifications_24);
    }



}