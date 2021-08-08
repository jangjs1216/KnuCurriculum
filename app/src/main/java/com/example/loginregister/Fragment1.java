package com.example.loginregister;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import org.apache.log4j.chainsaw.Main;
import org.jetbrains.annotations.NotNull;

public class Fragment1 extends Fragment {
    private static  final String TAG = "Frag1";
    private Toolbar toolbar;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_1, container, false);

        //상단 제목바꾸기 프래그먼트별로 설정 및 커스텀 및 안보이게 가능- 안승재
        toolbar = (Toolbar)view.findViewById(R.id.tb_frag1);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
       // actionBar.setDisplayHomeAsUpEnabled(true); //뒤로가기 기능생성


        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_frag1,menu);
        Log.e(TAG,"sex");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_setting:
                //설정프래그먼트 오픈
                break;
            case android.R.id.home:
                //뒷프래그먼트로 이동;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setProfile(View view){
        Intent intent =getActivity().getIntent();
        //로그인액티비티에서 메인으로 넘어올때 계정정보 인텐트에 실어보내면 받아와서 사용가능
        //>>최정인씨 부탁드립니다.
        //앱바 사용해서 상단에 검색창이랑 설정창 띄워서 프래그먼트 연결
    }
}