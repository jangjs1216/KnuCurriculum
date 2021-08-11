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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregister.login.KeepLoginActivity;
import com.example.loginregister.login.SavedSharedPreferences;
import com.example.loginregister.login.SetNicknameActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Fragment_SetNickName extends Fragment {
    private View view;
    private Toolbar toolbar;
    private final static String TAG = "setNIckName_Activity";
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private String user_nick;
    private EditText et_nickname;
    private TextView tv_confirm;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment__set_nick_name, container, false);

        et_nickname =view.findViewById(R.id.et_setNickName);
        tv_confirm = view.findViewById(R.id.tv_confirm);

        toolbar = (Toolbar)view.findViewById(R.id.tb_setNickname);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        // actionBar.setLogo(getResources().getDrawable(R.drawable.knucurricular_app_icon));//앱아이콘
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        //  툴바 끝

        //닉네임설정시작
        fm=getActivity().getSupportFragmentManager();
        ft=fm.beginTransaction();
        if(mAuth!=null) {//UserInfo에 등록되어있는 닉네임을 가져오기 위해서}{
            Log.e("frag1", String.valueOf(mAuth));
            mStore.collection("user").document(mAuth.getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                user_nick = task.getResult().getString("nickname");
                                if(user_nick!=null&&user_nick.length()!=0) {
                                    Log.e(TAG, "닉네임오기성공 - " + user_nick.length());
                                    ft.replace(R.id.main_frame,new Fragment1()).commit();
                                }
                                else{
                                    tv_confirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String curNick=et_nickname.getText().toString();

                                            if(curNick==null||curNick.length()==0){
                                                Log.e(TAG, "닉네임없음");
                                                Map<String,String> map = new HashMap<String,String>();
                                                map.put("nickname",curNick);
                                                Log.e("Setnickname", String.valueOf(map));
                                                mStore.collection("user").document(mAuth.getUid()).update("nickname", curNick);
                                                ft.replace(R.id.main_frame,new Fragment1()).commit();
                                            }
                                            else{
                                                Toast.makeText(getActivity(),"닉네임을 입력해주세요.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
        else {
            Log.e(TAG,"계정정보없음 " );
            SavedSharedPreferences.setUserName(getContext(),null);
            Intent intent = new Intent(getActivity(), KeepLoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        return view;
    }
}
