package com.example.loginregister;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.loginregister.UserInfo.Fragment_Edit_User_Info;
import com.example.loginregister.curiList.Recycler_Adapter;
import com.example.loginregister.curiList.Recycler_Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Fragment1 extends Fragment {
    private static  final String TAG = "Frag1";
    private Toolbar toolbar;
    private FragmentManager fm;
    private Button btn_add;
    private FragmentTransaction ft;
    private ArrayList<Recycler_Data> curi_List;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Recycler_Adapter curi_adapter;
    private TextView tv_username;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_1, container, false);
        //리싸이클러뷰
        recyclerView = (RecyclerView)view.findViewById(R.id.layout_frag1_curi_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        curi_List = ((MainActivity)getActivity()).getArrayList_curiList();
        curi_adapter = new Recycler_Adapter(curi_List);
        recyclerView.setAdapter(curi_adapter);




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
        // 프로필 설정
        tv_username = view.findViewById(R.id.tv_userName);
        setProfile(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_frag1,menu);
       // Log.e(TAG,"sex");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_setting:
                ft.replace(R.id.main_frame, new Fragment_Edit_User_Info());
                ft.addToBackStack(null);
                ft.commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setProfile(View view){
       tv_username.setText(((MainActivity)getActivity()).getUserAccount().getNickname());
    }
}