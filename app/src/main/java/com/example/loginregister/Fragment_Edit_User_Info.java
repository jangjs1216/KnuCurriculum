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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregister.curiList.Adapter_User_Info;
import com.example.loginregister.curiList.Recycler_Adapter;
import com.example.loginregister.curiList.User_Info_Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import org.apache.log4j.chainsaw.Main;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fragment_Edit_User_Info extends Fragment {
    private View view;
    private Toolbar toolbar;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView recyclerView;
    private ArrayList<User_Info_Data> arrayList;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_User_Info adapter_user_info;
    private TextView btn_add_user_info;
    private FragmentManager fm2;
    private FragmentTransaction ft2;
    private EditText et_userName;
    private EditText et_userUniv;
    private EditText et_userMajor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment__edit__user__info, container, false);
        fm=getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        fm2 = getChildFragmentManager();
        ft2=fm.beginTransaction();
        et_userName = view.findViewById(R.id.et_userName);
        et_userUniv = view.findViewById(R.id.et_userUniv);
        et_userMajor = view.findViewById(R.id.et_userMajor);

        ft2.add(R.id.fragment_setting_container,new SettingsFragment()).commit();
         //상단 제목바꾸기 프래그먼트별로 설정 및 커스텀 및 안보이게 가능- 안승재
        toolbar = (Toolbar)view.findViewById(R.id.tb_edit_user_info);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        // actionBar.setLogo(getResources().getDrawable(R.drawable.knucurricular_app_icon));//앱아이콘
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로가기 기능생성
        //툴바 끝


        //      리싸이클러뷰
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_edit_user_info);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        arrayList = ((MainActivity)getActivity()).getArrayList_userInfoData();
        adapter_user_info = new Adapter_User_Info(arrayList);
        recyclerView.setAdapter(adapter_user_info);
        btn_add_user_info = (TextView) view.findViewById(R.id.btn_add_user_info);
        btn_add_user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User_Info_Data user_info_data = new User_Info_Data("스펙이름","내용");
                arrayList.add(user_info_data);
                ((MainActivity)getActivity()).setArrayList_userInfoData(arrayList);
                adapter_user_info.notifyDataSetChanged();
            }
        });

        //      리싸이클러뷰 끝
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_edit_user_info,menu);
        // Log.e(TAG,"sex");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_save:
                String curName = et_userName.getText().toString();
                String curUniv = et_userUniv.getText().toString();
                String curMajor = et_userMajor.getText().toString();
                if(curName==null||curName.length()==0
                ||curUniv==null||curUniv.length()==0
                ||curMajor==null||curMajor.length()==0){
                    Toast.makeText(getContext(),"회원정보를 다시 확인해주세요(공백x).",Toast.LENGTH_LONG).show();
                }
                else{
                    Map<String,String> map = new HashMap<String,String>();
                    map.put(FirebaseID.nickname,curName);
                    map.put(FirebaseID.university,curUniv);
                    map.put(FirebaseID.major,curMajor);
                    Log.e("Edit_User_Info", String.valueOf(map));
                    mStore.collection("user").document(mAuth.getUid()).set(map, SetOptions.merge());
                    Toast.makeText(getContext(),"회원정보가 저장되었습니다.",Toast.LENGTH_LONG).show();

                }
                break;
            case android.R.id.home:
                //데베에 올려야함
                ft.remove(Fragment_Edit_User_Info.this).commit();
                fm.popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}


