package com.example.loginregister.UserInfo;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.loginregister.MainActivity;
import com.example.loginregister.R;
import com.example.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class Fragment_UserSpec extends Fragment {

    private String type;
    private View view;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private UserAccount userAccount;
    private ArrayList<String> datas;
    private ArrayList<User_Info_Data> temp_specs;
    private ArrayList<User_Info_Data> specs;
    private RecyclerView recyclerView;
    private Adapter_User_Info adapter_user_info;
    private LinearLayoutManager linearLayoutManager;
    public Fragment_UserSpec(){}
    public Fragment_UserSpec(String type) {
        this.type = type;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment__user_spec, container, false);
        tabLayout = view.findViewById(R.id.tabBar);
        TabLayout.Tab tab = tabLayout.getTabAt(Integer.parseInt(type));
        tab.select();
        toolbar = view.findViewById(R.id.tb_spec);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.Recycler_View_Spec);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        mStore.collection("user").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userAccount = documentSnapshot.toObject(UserAccount.class);
                datas=userAccount.getSpecs();
                temp_specs =str_specs_to_specs(datas);
            }
        });
        for(User_Info_Data spec : temp_specs){
            if(spec.getType()==type){
                specs.add(spec);
            }
        }
        adapter_user_info = new Adapter_User_Info(specs);
        recyclerView.setAdapter(adapter_user_info);

        return view;
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
}