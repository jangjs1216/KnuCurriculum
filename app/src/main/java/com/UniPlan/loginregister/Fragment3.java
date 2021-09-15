package com.UniPlan.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.UniPlan.loginregister.Notice_B.NoticeBoard;
import com.UniPlan.loginregister.R;
import com.google.firebase.database.annotations.NotNull;


public class Fragment3 extends Fragment {
    private View view;
    private Toolbar toolbar;
    private final static String TAG ="Frag3";
    private LinearLayout layout_like, layout_ranking, layout_mypost;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_3, container, false);
        fm = getActivity().getSupportFragmentManager();
        ft= fm.beginTransaction();
        toolbar = (androidx.appcompat.widget.Toolbar)view.findViewById(R.id.tb_frag3);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);

        //내가 누른 좋아요글과 베스트 글들 보기 박경무
        layout_like = view.findViewById(R.id.layout_like);
        layout_ranking = view.findViewById(R.id.layout_best);
        layout_mypost = view.findViewById(R.id.layout_mypost);

        layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),NoticeBoard.class);
                intent.putExtra("게시판", 8);
                intent.putExtra("이름","좋아요 누른 글");
                startActivity(intent);
            }
        });

        layout_mypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),NoticeBoard.class);
                intent.putExtra("게시판",9);
                intent.putExtra("이름","내가 쓴글");
                startActivity(intent);
            }
        });

        layout_ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),NoticeBoard.class);
                intent.putExtra("게시판", 10);
                intent.putExtra("이름","Best 게시글");
                startActivity(intent);
            }
        });



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
                Log.e(TAG,"게시판클릭");
                switch(v.getId()){
                    case R.id.layout_forum1:
                        intent.putExtra("게시판",1);
                        intent.putExtra("이름","반도체 및 디스플레이 공학");
                        break;
                    case R.id.layout_forum2:
                        intent.putExtra("게시판",2);
                        intent.putExtra("이름","회로 및 임베디드 시스템 공학");
                        break;
                    case R.id.layout_forum3:
                        intent.putExtra("게시판",3);
                        intent.putExtra("이름","전자파 및 광전자 공학");
                        break;
                    case R.id.layout_forum4:
                        intent.putExtra("게시판",4);
                        intent.putExtra("이름","인공지능 및 신호처리");
                        break;
                    case R.id.layout_forum5:
                        intent.putExtra("게시판",5);
                        intent.putExtra("이름","정보 통신 공학");
                        break;
                    case R.id.layout_forum6:
                        intent.putExtra("게시판",6);
                        intent.putExtra("이름","임베디드 시스템 및 제어공학");
                        break;
                    case R.id.layout_forum7:
                        intent.putExtra("게시판",7);
                        intent.putExtra("이름","자유게시판");
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

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){

        }
        return super.onOptionsItemSelected(item);
    }
}