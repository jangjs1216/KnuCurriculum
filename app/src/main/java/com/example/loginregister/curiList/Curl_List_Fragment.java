package com.example.loginregister.curiList;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.loginregister.MainActivity;
import com.example.loginregister.R;
import com.example.loginregister.Setting_Container_Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class Curl_List_Fragment extends Fragment {
    private View view;
    private LinearLayoutManager linearLayoutManager;
    private  RecyclerView recyclerView;
    private ArrayList<Recycler_Data> arrayList;
    private Recycler_Adapter recycler_adapter;
    private Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_curl__list_, container, false);
        //////////툴바///////////
        //상단 제목바꾸기 프래그먼트별로 설정 및 커스텀 및 안보이게 가능- 안승재
        toolbar = (Toolbar)view.findViewById(R.id.tb_curi_list);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        // actionBar.setLogo(getResources().getDrawable(R.drawable.knucurricular_app_icon));//앱아이콘
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로가기 기능생성
        /////////툴바끝///////////////


        //      리싸이클러뷰
        recyclerView = (RecyclerView)view.findViewById(R.id.Recycler_View_Curi_List);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = ((MainActivity)getActivity()).getArrayList_curiList();
        recycler_adapter = new Recycler_Adapter(arrayList);
        recyclerView.setAdapter(recycler_adapter);
        //      리싸이클러뷰 끝

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_curi_list,menu);
        // Log.e(TAG,"sex");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_add:
                Recycler_Data recycler_data = new Recycler_Data("헤헤");
                arrayList.add(recycler_data);
                ((MainActivity)getActivity()).setArrayList_curiList(arrayList);
                recycler_adapter.notifyDataSetChanged();
                Log.e("###", String.valueOf(recycler_adapter.getItemCount()));
                break;

            case android.R.id.home:
                getActivity().getSupportFragmentManager().beginTransaction().remove(Curl_List_Fragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}