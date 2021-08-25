package com.example.loginregister.UserInfo;

import android.content.Intent;
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

import com.example.loginregister.MainActivity;
import com.example.loginregister.R;
import com.example.loginregister.SettingsFragment;
import com.example.loginregister.login.LogoutActivity;
import com.example.loginregister.login.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Fragment_Edit_User_Info extends Fragment {
    private View view;
    private Toolbar toolbar;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Adapter_User_Info adapter_user_info;
    private TextView btn_add_user_info;
    private FragmentManager fm2;
    private FragmentTransaction ft2;
    private EditText et_userName;
    private TextView tv_userUniv;
    private TextView tv_userMajor;
    private TextView btn_logout;
    private UserAccount userAccount;
    private ArrayList<User_Info_Data> specs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment__edit__user__info, container, false);
        fm=getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        fm2 = getChildFragmentManager();
        ft2=fm.beginTransaction();
        et_userName = view.findViewById(R.id.et_userName);

        userAccount = ((MainActivity)getActivity()).getUserAccount();

        et_userName.setText(userAccount.getNickname());
        tv_userUniv = view.findViewById(R.id.tv_userUniv);
        tv_userMajor = view.findViewById(R.id.tv_userMajor);


        ft2.add(R.id.fragment_setting_container,new SettingsFragment()).commit();
         //상단 제목바꾸기 프래그먼트별로 설정 및 커스텀 및 안보이게 가능- 안승재
        toolbar = (Toolbar)view.findViewById(R.id.tb_edit_user_info);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로가기 기능생성
        //툴바 끝

        //로그아웃
        btn_logout=view.findViewById(R.id.btn_logout);
//        SharedPreferences account_info= getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=account_info.edit();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), LogoutActivity.class);
                startActivity(intent);
            }
        });

        //      리싸이클러뷰
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_edit_user_info);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter_user_info = new Adapter_User_Info(specs);
        recyclerView.setAdapter(adapter_user_info);
        btn_add_user_info = (TextView) view.findViewById(R.id.btn_add_user_info);
        btn_add_user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User_Info_Data user_info_data = new User_Info_Data("","");
                Log.e("edit", "b"+String.valueOf(userAccount.getSpecs()));
                specs.add(user_info_data);
                Log.e("edit", "e"+String.valueOf(userAccount.getSpecs()));
                Log.e("edit", String.valueOf(((MainActivity)getActivity()).getUserAccount().getSpecs()));
                //adapter_user_info.notifyDataSetChanged();
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
                if(curName==null||curName.length()==0){
                    Toast.makeText(getContext(),"회원정보를 다시 확인해주세요(공백x).",Toast.LENGTH_LONG).show();
                }
                else{
                    Log.e("userinfo", String.valueOf(specs));
                    userAccount.setNickname(curName);

                    userAccount.setSpecs(specs);
                    //에딧그냥들어가는지 봐야함

                    mStore.collection("user").document(mAuth.getUid()).set(userAccount);
                    ((MainActivity)getActivity()).setUserAccount(userAccount);
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


