package com.UniPlan.loginregister;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.UniPlan.loginregister.R;
import com.UniPlan.loginregister.login.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class Fragment_SetNickName extends Fragment {
    private View view;
    private Toolbar toolbar;
    private final static String TAG = "setNIckName_Activity";
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private EditText et_nickname;
    private TextView tv_confirm;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private UserAccount userAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment__set_nick_name, container, false);

        et_nickname =view.findViewById(R.id.et_setNickName);
        tv_confirm = view.findViewById(R.id.tv_confirm);

        userAccount = new UserAccount();
        toolbar = (Toolbar)view.findViewById(R.id.tb_setNickname);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        // actionBar.setLogo(getResources().getDrawable(R.drawable.knucurricular_app_icon));//앱아이콘
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        //  툴바 끝
        fm=getActivity().getSupportFragmentManager();
        ft=fm.beginTransaction();

        //닉네임설정시작
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curNick=et_nickname.getText().toString();
                Log.e("###",curNick);
                if(curNick!=null&&curNick.length()!=0){
                    Log.e(TAG, "닉네임입력완료");
                    ((MainActivity) getActivity()).bottomNavigationView.setVisibility(View.VISIBLE);
                    userAccount = ((MainActivity)getActivity()).getUserAccount();
                    userAccount.setNickname(curNick);
                    Log.e("Setnickname", String.valueOf(curNick));
                    mStore.collection("user").document(mAuth.getUid()).set(userAccount);

                    ft.replace(R.id.main_frame,new Fragment1()).commit();
                }
                else{
                    Toast.makeText(getActivity(),"닉네임을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
