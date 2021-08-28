package com.example.loginregister.Notice_B;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.loginregister.R;

public class Activity_Container_Board extends AppCompatActivity {
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft =fm.beginTransaction();
    private int forum_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_board);
        Intent intent = getIntent();
        forum_num = intent.getExtras().getInt("게시판");
        ft.add(R.id.main_board,new Fragment_Notice_Board(forum_num)).commit();
    }

    public int getForum_num() {
        return forum_num;
    }


}