package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.loginregister.Notice_B.NoticeBoard;
import com.google.firebase.database.annotations.NotNull;


public class Fragment3 extends Fragment {
    private View view;
    private Toolbar toolbar;
    private final static String TAG ="Frag3";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_3, container, false);
        toolbar = (androidx.appcompat.widget.Toolbar)view.findViewById(R.id.tb_frag3);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);

        View forum1=view.findViewById(R.id.layout_forum1);
        View forum2=view.findViewById(R.id.layout_forum2);
        View forum3=view.findViewById(R.id.layout_forum3);
        View forum4=view.findViewById(R.id.layout_forum4);
        View forum5=view.findViewById(R.id.layout_forum5);
        View forum6=view.findViewById(R.id.layout_forum6);
        View forum7=view.findViewById(R.id.layout_forum7);

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),NoticeBoard.class);
                switch(v.getId()){
                    case R.id.forum1:
                        intent.putExtra("게시판",1);
                        break;
                    case R.id.forum2:
                        intent.putExtra("게시판",2);
                        break;
                    case R.id.forum3:
                        intent.putExtra("게시판",3);
                        break;
                    case R.id.forum4:
                        intent.putExtra("게시판",4);
                        break;
                    case R.id.forum5:
                        intent.putExtra("게시판",5);
                        break;
                    case R.id.forum6:
                        intent.putExtra("게시판",6);
                        break;
                    case R.id.forum7:
                        intent.putExtra("게시판",7);
                        break;
                }
                startActivity(intent);
            }
        };
        forum1.setOnClickListener(onClickListener);
        forum2.setOnClickListener(onClickListener);
        forum3.setOnClickListener(onClickListener);
        forum4.setOnClickListener(onClickListener);
        forum5.setOnClickListener(onClickListener);
        forum6.setOnClickListener(onClickListener);
        forum7.setOnClickListener(onClickListener);

        return view;
    }
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_frag3,menu);
        Log.e(TAG,"이건뭐노?");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_search:
                //검색프래그먼트 오픈
                break;
            case R.id.action_btn_create:
                //글쓰기 프래그먼트로 이동;
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}