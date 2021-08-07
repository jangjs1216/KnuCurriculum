package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment1 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_1, container, false);

        //상단 제목바꾸기 프래그먼트별로 설정 및 커스텀 및 안보이게 가능- 안승재
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("main화면입니다.");
        actionBar.setDisplayHomeAsUpEnabled(true);

        return view;
    }

    public void setProfile(View view){
        Intent intent =getActivity().getIntent();
        //로그인액티비티에서 메인으로 넘어올때 계정정보 인텐트에 실어보내면 받아와서 사용가능
        //>>최정인씨 부탁드립니다.
        //앱바 사용해서 상단에 검색창이랑 설정창 띄워서 프래그먼트 연결
    }
}