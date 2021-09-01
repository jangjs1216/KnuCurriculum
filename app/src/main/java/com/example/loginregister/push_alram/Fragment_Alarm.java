package com.example.loginregister.push_alram;

import android.content.Intent;
import android.os.Build;
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
import android.widget.Toast;

import com.example.loginregister.MainActivity;
import com.example.loginregister.Notice_B.Post_Comment;
import com.example.loginregister.R;
import com.example.loginregister.UserInfo.Fragment_Edit_User_Info;
import com.example.loginregister.curiList.Curl_List_Fragment;
import com.example.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Fragment_Alarm extends Fragment {
    private FragmentTransaction ft;
    private FragmentManager fm;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private View view;
    private Toolbar toolbar;
    private ArrayList<Alarm> alarms;
    private Alarms data;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Adater_Alarm adater_alarm;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment__alarm, container, false);
        fm = getActivity().getSupportFragmentManager();
        ft= fm.beginTransaction();
        //////////툴바///////////
        //상단 제목바꾸기 프래그먼트별로 설정 및 커스텀 및 안보이게 가능- 안승재
        toolbar = (Toolbar)view.findViewById(R.id.tb_alarm);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        // actionBar.setLogo(getResources().getDrawable(R.drawable.knucurricular_app_icon));//앱아이콘
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        /////////툴바끝///////////////


        mStore.collection("Alarm").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                data = documentSnapshot.toObject(Alarms.class);
                alarms = data.getAlarms();
                //알람목록 받아오기
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    alarms.sort(new Comparator<Alarm>() {
                        @Override
                        public int compare(Alarm o1, Alarm o2) {
                            return o2.getTimestamp().compareTo(o1.getTimestamp());
                        }
                    });
                    Log.e("frag5", String.valueOf(alarms));

                }

                adater_alarm = new Adater_Alarm(alarms);
                recyclerView = (RecyclerView)view.findViewById(R.id.Recycler_View_Alarm);
                linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);


                recyclerView.setAdapter(adater_alarm);

                adater_alarm.setOnAlarmClickListener(new Adater_Alarm.IOnAlarmClickListener() {
                    @Override
                    public void onAlarmCLick(View v, int pos) {
                        String forum_sort;
                        String post_id;
                        forum_sort=alarms.get(pos).getForum_sort();
                        post_id=alarms.get(pos).getPost_id();
                        alarms.get(pos).setChecked(true);
                        v.setBackgroundResource(R.color.white);
                        Intent intent = new Intent(getContext(), Post_Comment.class);
                        intent.putExtra("forum_sort",forum_sort);
                        intent.putExtra("post_id",post_id);
                        startActivity(intent);
                    }
                });
            }
        });





        Log.e("alarm","done");
        return view;
    }


    public void setAlarmsChecked() {
        if (alarms != null) {
            for (Alarm alarm : alarms) {
                alarm.setChecked(true);
            }
            data.setAlarms(alarms);
            mStore.collection("Alarm").document(mAuth.getUid()).set(data);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_alarm,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_btn_setting:
                ft.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right,R.anim.enter_to_right, R.anim.exit_to_right);
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, new Fragment_Edit_User_Info());
                ft.commit();
                ((MainActivity)MainActivity.maincontext).setvisibleNavi(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setAlarmsChecked();
        ((MainActivity)getActivity()).setBottom_alarm();
    }
}