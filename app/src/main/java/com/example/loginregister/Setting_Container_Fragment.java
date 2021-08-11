package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.loginregister.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;


public class Setting_Container_Fragment extends Fragment {
    private static final String TAG  = "setting_container";
    private View view;
    private Toolbar toolbar;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FirebaseAuth mFirebaseAuth;
    private TextView tv_logout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_setting__container_, container, false);
        //툴바
        toolbar = (androidx.appcompat.widget.Toolbar)view.findViewById(R.id.tb_setting);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayHomeAsUpEnabled(true);//뒤로가기버튼
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        //프래그먼트설정
        fm=getChildFragmentManager();
        ft=fm.beginTransaction();
        ft.add(R.id.container_setting,new SettingsFragment()).commit();
        //로그아웃
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"펑");
                mFirebaseAuth= FirebaseAuth.getInstance();
                mFirebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_settings,menu);
      //  Log.e(TAG,"sex");
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().getSupportFragmentManager().beginTransaction().remove(Setting_Container_Fragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}