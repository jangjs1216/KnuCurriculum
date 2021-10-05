package com.UniPlan.loginregister;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.UniPlan.loginregister.R;
import com.UniPlan.loginregister.UserInfo.Fragment_UserSpec;
import com.UniPlan.loginregister.UserInfo.Fragment_User_Info;
import com.UniPlan.loginregister.UserInfo.User_Info_Data;
import com.UniPlan.loginregister.adapters.CurriculumAdapter;
import com.UniPlan.loginregister.curiList.Curl_List_Fragment;
import com.UniPlan.loginregister.curiList.Recycler_Data;
import com.UniPlan.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.blox.treeview.TreeNode;

public class Fragment1 extends Fragment {
    private static  final String TAG = "Frag1";
    private FragmentManager fm;
    private FragmentTransaction ft;
    private ArrayList<Recycler_Data> curi_List;
    private RecyclerView curi_recyclerView;
    private LinearLayoutManager curi_linearLayoutManager;
    private CurriculumAdapter curriculumAdapter;
    private TextView tv_username, tv_taked, tv_major, treeMore,specMore;
    private UserAccount userAccount;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DocumentReference docRef;
    private ArrayList<User_Info_Data> specs;
    private View btn_lang,btn_cert,btn_award,btn_extra,view;
    private ImageView setting;
    BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_1, container, false);
        tv_taked = view.findViewById(R.id.tv_taked);
        tv_major = view.findViewById(R.id.tv_major);
        userAccount = ((MainActivity)getActivity()).getUserAccount();
        treeMore=view.findViewById(R.id.treeMore);
        specMore = view.findViewById(R.id.tv_specmore);
        btn_lang = view.findViewById(R.id.btn_frag1_lang);
        btn_cert = view.findViewById(R.id.btn_frag1_cert);
        btn_award = view.findViewById(R.id.btn_frag1_award);
        btn_extra = view.findViewById(R.id.btn_frag1_extra);
        setting = view.findViewById(R.id.frag1_setting);
        curi_recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        curi_linearLayoutManager = new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically(){
                return false;
            }
        };
        curi_recyclerView.setLayoutManager(curi_linearLayoutManager);

        //리싸이클러뷰
//        specs_linearLayoutManager = new LinearLayoutManager(getContext());
//        specs_recyclerView.setLayoutManager(specs_linearLayoutManager);
//        specs = str_specs_to_specs(userAccount.getSpecs());
//        spec_adapter = new Adapter_User_Info(specs);
//        specs_recyclerView.setAdapter(spec_adapter);
        bottomNavigationView = view.findViewById(R.id.bottomNavi);

        docRef = mStore.collection("user").document(mAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                //      리싸이클러뷰
                curi_List = new ArrayList<>();
                for(String tableName : userAccount.getTableNames()){
                    Recycler_Data recycler_data = new Recycler_Data(tableName);
                    curi_List.add(recycler_data);

                    if(curi_List.size()>2){
                        break;
                    }
                    Log.e("###", "item : " + tableName);
                }
                curriculumAdapter = new CurriculumAdapter(curi_List);
                curi_recyclerView.setAdapter(curriculumAdapter);

                //리싸이클러뷰 클릭 리스너
                curriculumAdapter.setOnItemListener(new CurriculumAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos, String option) {
                        if(option.equals("choice")){
                            String tableName = curi_List.get(pos).getTv_title().toString();
                            Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                            bundle.putString("tableName", tableName);//번들에 넘길 값 저장
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            Fragment2 fragment2 = new Fragment2();//프래그먼트2 선언
                            fragment2.setArguments(bundle);//번들을 프래그먼트2로 보낼 준비
                            transaction.replace(R.id.main_frame, fragment2);
                            transaction.commit();
                        }
                    }
                });
            }
        });

        ///아직 덜함
        btn_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ft.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right,R.anim.enter_to_right, R.anim.exit_to_right);
//                ft.addToBackStack(null);
//                ft.replace(R.id.main_frame, new Fragment_Edit_User_Info());
//                ft.commit();

                ((MainActivity)MainActivity.maincontext).setvisibleNavi(true);
            }
        });

        treeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right);
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, new Curl_List_Fragment());
                ft.commit();
                ((MainActivity)MainActivity.maincontext).setvisibleNavi(true);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right,R.anim.enter_to_right, R.anim.exit_to_right);
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, new Fragment_User_Info());
                ft.commit();
                ((MainActivity)MainActivity.maincontext).setvisibleNavi(true);
            }
        });
        specMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right,R.anim.enter_to_right, R.anim.exit_to_right);
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, new Fragment_UserSpec("0"));
                ft.commit();
                ((MainActivity)MainActivity.maincontext).setvisibleNavi(true);
            }
        });

        btn_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right,R.anim.enter_to_right, R.anim.exit_to_right);
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, new Fragment_UserSpec("1"));
                ft.commit();
                ((MainActivity)MainActivity.maincontext).setvisibleNavi(true);
            }
        });
        btn_cert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right,R.anim.enter_to_right, R.anim.exit_to_right);
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, new Fragment_UserSpec("2"));
                ft.commit();
                ((MainActivity)MainActivity.maincontext).setvisibleNavi(true);
            }
        });
        btn_award.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right,R.anim.enter_to_right, R.anim.exit_to_right);
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, new Fragment_UserSpec("3"));
                ft.commit();
                ((MainActivity)MainActivity.maincontext).setvisibleNavi(true);
            }
        });
        btn_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right,R.anim.enter_to_right, R.anim.exit_to_right);
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, new Fragment_UserSpec("4"));
                ft.commit();
                ((MainActivity)MainActivity.maincontext).setvisibleNavi(true);
            }
        });





        fm=getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        //상단 제목바꾸기 프래그먼트별로 설정 및 커스텀 및 안보이게 가능- 안승재
        // 프로필 설정
        tv_username = view.findViewById(R.id.tv_userName);


        return view;
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
            User_Info_Data uid = new User_Info_Data(temp[0],temp[1],temp[2]);
            user_info_data.add(uid);

            if(user_info_data.size()>4){
                break;
            }
        }
        return user_info_data;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("frag1","데이터 재설정");
        mStore.collection("user").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userAccount = documentSnapshot.toObject(UserAccount.class);
                setProfile(view);
            }
        });

    }
}