package com.example.loginregister.UserInfo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
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

public class Fragment_User_Info extends Fragment implements MainActivity.IOnBackPressed {
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
    private TextView tv_userName;
    private TextView tv_userUniv;
    private TextView tv_userMajor;
    private TextView btn_logout;
    private UserAccount userAccount;
    private TextView tv_major;
    private TextView tv_taked;
    private ArrayList<User_Info_Data> specs;
    private ArrayList<String> str_specs;
    private ItemTouchHelper itemTouchHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment__user__info, container, false);
        fm=getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        fm2 = getChildFragmentManager();
        ft2=fm.beginTransaction();
        tv_userName = view.findViewById(R.id.tv_userName);
        //뒤로가기
        ((MainActivity) getActivity()).setBackPressedlistener(this);


        userAccount = ((MainActivity)getActivity()).getUserAccount();
        tv_userName.setText(userAccount.getNickname());
        tv_userUniv = view.findViewById(R.id.tv_userUniv);
        tv_userMajor = view.findViewById(R.id.tv_userMajor);
        tv_major=view.findViewById(R.id.tv_major_GPA);
        tv_taked=view.findViewById(R.id.tv_taked_GPA);
        tv_major.setText(userAccount.getMajor());
        tv_taked.setText(userAccount.getTaked());
        ft2.add(R.id.fragment_setting_container,new SettingsFragment()).commit();
        //상단 제목바꾸기 프래그먼트별로 설정 및 커스텀 및 안보이게 가능- 안승재
        toolbar = (Toolbar)view.findViewById(R.id.tb_user_info);
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
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_user_info);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        specs = str_specs_to_specs(userAccount.getSpecs());
        adapter_user_info = new Adapter_User_Info(specs);
        Log.e("spec","what?");

        recyclerView.setAdapter(adapter_user_info);


        //      리싸이클러뷰 끝


        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_user_info,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_edit:
                    ft.replace(R.id.main_frame,new Fragment_Edit_User_Info());
                    ft.addToBackStack(null);
                    ft.commit();
                break;

            case android.R.id.home:
                ft.remove(Fragment_User_Info.this).commit();
                fm.popBackStack();
                ((MainActivity)MainActivity.maincontext).setvisibleNavi(false);
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<String> specs_to_str_specs(ArrayList<User_Info_Data> specs){
        ArrayList<String> list_ret=new ArrayList<>();
        for(User_Info_Data spec : specs){
            String ret ="";
            ret+=spec.getUser_info_title();
            ret+=',';
            ret+=spec.getUser_info_content();
            list_ret.add(ret);
        }
        return list_ret;
    }
    public ArrayList<User_Info_Data> str_specs_to_specs(ArrayList<String> str_specs){
        ArrayList<User_Info_Data> user_info_data = new ArrayList<>();
        for(String spec : str_specs){
            String [] temp = spec.split(",");
            User_Info_Data uid = new User_Info_Data(temp[0],temp[1]);
            user_info_data.add(uid);
        }
        return user_info_data;
    }


    @Override
    public void onBackPressed() {
        ft.remove(Fragment_User_Info.this).commit();
        fm.popBackStack();
        ((MainActivity)MainActivity.maincontext).setvisibleNavi(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).setBackPressedlistener(null);
    }
}


