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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.loginregister.Notice_B.Post_Comment;
import com.example.loginregister.UserInfo.Adapter_User_Info;
import com.example.loginregister.UserInfo.Fragment_Edit_User_Info;
import com.example.loginregister.UserInfo.User_Info_Data;
import com.example.loginregister.curiList.Curl_List_Fragment;
import com.example.loginregister.curiList.Recycler_Adapter;
import com.example.loginregister.curiList.Recycler_Data;
import com.example.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Fragment1 extends Fragment {
    private static  final String TAG = "Frag1";
    private Toolbar toolbar;
    private FragmentManager fm;
    private Button btn_add;
    private FragmentTransaction ft;
    private ArrayList<Recycler_Data> curi_List;
    private RecyclerView curi_recyclerView;
    private RecyclerView specs_recyclerView;
    private LinearLayoutManager curi_linearLayoutManager;
    private LinearLayoutManager specs_linearLayoutManager;
    private Recycler_Adapter curi_adapter;
    private Adapter_User_Info spec_adapter;
    private TextView tv_username, tv_taked, tv_major, specMore, treeMore;
    private UserAccount userAccount;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DocumentReference docRef;
    private ArrayList<User_Info_Data> specs;
    BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_1, container, false);
        tv_taked = view.findViewById(R.id.tv_taked);
        tv_major = view.findViewById(R.id.tv_major);
        userAccount = ((MainActivity)getActivity()).getUserAccount();
        specMore=view.findViewById(R.id.specMore);
        treeMore=view.findViewById(R.id.treeMore);

        //리싸이클러뷰
        specs_recyclerView=(RecyclerView)view.findViewById(R.id.layout_frag1_specs_recyclerview);
        specs_linearLayoutManager = new LinearLayoutManager(getContext());
        specs_recyclerView.setLayoutManager(specs_linearLayoutManager);
        specs = str_specs_to_specs(userAccount.getSpecs());
        spec_adapter = new Adapter_User_Info(specs);
        specs_recyclerView.setAdapter(spec_adapter);
        bottomNavigationView = view.findViewById(R.id.bottomNavi);

        docRef = mStore.collection("user").document(mAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);

                curi_recyclerView = (RecyclerView)view.findViewById(R.id.layout_frag1_curi_recyclerview);
                curi_linearLayoutManager = new LinearLayoutManager(getContext());
                curi_recyclerView.setLayoutManager(curi_linearLayoutManager);

                //      리싸이클러뷰
                curi_List = new ArrayList<>();
                for(String tableName : userAccount.getTableNames()){
                    Recycler_Data recycler_data = new Recycler_Data(tableName);
                    curi_List.add(recycler_data);

                    if(curi_List.size()>4){
                        break;
                    }
                    Log.e("###", "item : " + tableName);
                }
                curi_adapter = new Recycler_Adapter(curi_List);
                curi_recyclerView.setAdapter(curi_adapter);

                //리싸이클러뷰 클릭 리스너
                curi_adapter.setOnItemListener(new Recycler_Adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos, String option) {
                        String tableName = curi_List.get(pos).getTv_title().toString();
                        Toast.makeText(getContext(), tableName + " 선택됨", Toast.LENGTH_LONG).show();

                        Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                        bundle.putString("tableName", tableName);//번들에 넘길 값 저장
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        Fragment2 fragment2 = new Fragment2();//프래그먼트2 선언
                        fragment2.setArguments(bundle);//번들을 프래그먼트2로 보낼 준비
                        transaction.replace(R.id.main_frame, fragment2);
                        transaction.commit();
                    }
                });
            }
        });

        specMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right,R.anim.enter_to_right, R.anim.exit_to_right);
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, new Fragment_Edit_User_Info());
                ft.commit();

                ((MainActivity)MainActivity.maincontext).setvisibleNavi(true);
            }
        });

        treeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right,R.anim.enter_to_right, R.anim.exit_to_right);
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, new Curl_List_Fragment());
                ft.commit();
                ((MainActivity)MainActivity.maincontext).setvisibleNavi(true);
            }
        });


        fm=getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        //상단 제목바꾸기 프래그먼트별로 설정 및 커스텀 및 안보이게 가능- 안승재
        toolbar = (Toolbar)view.findViewById(R.id.tb_frag1);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        // actionBar.setLogo(getResources().getDrawable(R.drawable.knucurricular_app_icon));//앱아이콘
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        // actionBar.setDisplayHomeAsUpEnabled(true); //뒤로가기 기능생성
        // 프로필 설정
        tv_username = view.findViewById(R.id.tv_userName);
        setProfile(view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_frag1,menu);
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

    public void setProfile(View view){
        tv_major.setText(userAccount.getMajor());
        tv_taked.setText(userAccount.getTaked());
        tv_username.setText(userAccount.getNickname());
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

            if(user_info_data.size()>4){
                break;
            }
        }
        return user_info_data;
    }
}