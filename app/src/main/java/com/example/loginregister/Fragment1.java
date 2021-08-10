package com.example.loginregister;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


import org.apache.log4j.chainsaw.Main;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Fragment1 extends Fragment {
    private static  final String TAG = "Frag1";
    private Toolbar toolbar;
    private FragmentManager fm;
    private Button btn_add;
    private FragmentTransaction ft;
    private ArrayList<Recycler_Data> arrayList;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Recycler_Adapter recycler_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_1, container, false);
        //리싸이클러뷰
        recyclerView = (RecyclerView)view.findViewById(R.id.layout_frag1_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        arrayList = new ArrayList<>();
        recycler_adapter = new Recycler_Adapter(arrayList);
        recyclerView.setAdapter(recycler_adapter);

        btn_add = view.findViewById(R.id.btn_recycler_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recycler_Data recycler_data = new Recycler_Data("헤헤","된당");
                arrayList.add(recycler_data);
                recycler_adapter.notifyDataSetChanged();
            }
        });
        //
        fm=getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        //상단 제목바꾸기 프래그먼트별로 설정 및 커스텀 및 안보이게 가능- 안승재
        toolbar = (Toolbar)view.findViewById(R.id.tb_frag1);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
       // actionBar.setLogo(getResources().getDrawable(R.drawable.knucurricular_app_icon));//앱아이콘
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
                ft.replace(R.id.main_frame, new Setting_Container_Fragment());
                ft.addToBackStack(null);
                ft.commit();
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