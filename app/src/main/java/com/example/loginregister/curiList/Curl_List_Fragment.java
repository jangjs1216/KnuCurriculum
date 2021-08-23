package com.example.loginregister.curiList;


import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.loginregister.MainActivity;
import com.example.loginregister.R;
import com.example.loginregister.Setting_Container_Fragment;
import com.example.loginregister.SubjectComment;
import com.example.loginregister.SubjectInfoActivity;
import com.example.loginregister.Subject_;
import com.example.loginregister.adapters.SubjectCommentAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class Curl_List_Fragment extends Fragment {
    private View view;
    private LinearLayoutManager linearLayoutManager;
    private  RecyclerView recyclerView;
    private ArrayList<Recycler_Data> arrayList;
    private Recycler_Adapter recycler_adapter;
    private Toolbar toolbar;
    Dialog addTreeDialog;


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

        //트리 추가 버튼 다이얼로그
        addTreeDialog = new Dialog(getContext());
        addTreeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addTreeDialog.setContentView(R.layout.dialog_addtree);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_curi_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_add:
                showDialog();
                break;

            case android.R.id.home:
                getActivity().getSupportFragmentManager().beginTransaction().remove(Curl_List_Fragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialog() {
        addTreeDialog.show();

        Button noBtn = addTreeDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTreeDialog.dismiss();
            }
        });
        addTreeDialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText treeNameET = addTreeDialog.findViewById(R.id.treeNameET);
                String treeName = treeNameET.getText().toString();
                treeNameET.setText("");

                Recycler_Data recycler_data = new Recycler_Data(treeName);
                arrayList.add(recycler_data);
                ((MainActivity)getActivity()).setArrayList_curiList(arrayList);
                recycler_adapter.notifyDataSetChanged();
                addTreeDialog.dismiss();
            }
        });
    }
}