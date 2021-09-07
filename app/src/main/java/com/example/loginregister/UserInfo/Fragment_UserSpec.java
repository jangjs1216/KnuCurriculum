package com.example.loginregister.UserInfo;

import android.graphics.Point;
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
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.loginregister.MainActivity;
import com.example.loginregister.R;
import com.example.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class Fragment_UserSpec extends Fragment implements MainActivity.IOnBackPressed {

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
    private FragmentManager fm;
    private FragmentTransaction ft;
    private ItemTouchHelperListener itemTouchHelperListener;

    public Fragment_UserSpec(){}
    public Fragment_UserSpec(String type) {
        this.type = type;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment__user_spec, container, false);

        fm = getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();

        tabLayout = (TabLayout)view.findViewById(R.id.tabBar);
        TabLayout.Tab tab = tabLayout.getTabAt(Integer.parseInt(type));
        tab.select();
        toolbar = view.findViewById(R.id.tb_spec);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.Recycler_View_Spec);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnTouchListener(new OnSwipeTouchListener(getContext()){
            @Override
            public void onSwipeLeft() {
                if(!type.equals("0")){
                    int it = Integer.parseInt(type);
                    it--;
                    type = Integer.toString(it);
                    TabLayout.Tab tabAt = tabLayout.getTabAt(it);
                    tabLayout.selectTab(tabAt);
                }
            }

            @Override
            public void onSwipeRight() {
                if(!type.equals("4")){
                    int it = Integer.parseInt(type);
                    it++;
                    type = Integer.toString(it);
                    TabLayout.Tab tabAt = tabLayout.getTabAt(it);
                    tabLayout.selectTab(tabAt);
                }
            }
        });


        //뒤로가기
        ((MainActivity)getActivity()).setBackPressedlistener(this);


        mStore.collection("user").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userAccount = documentSnapshot.toObject(UserAccount.class);
                datas=userAccount.getSpecs();
                specs = new ArrayList<>();
                temp_specs =str_specs_to_specs(datas);
                if(type.equals("0")){
                    for(User_Info_Data spec : temp_specs){
                        specs.add(spec);
                    }
                }
                else {
                    for (User_Info_Data spec : temp_specs) {
                        if (spec.getType().equals(type)) {
                            specs.add(spec);
                        }
                    }
                }
                adapter_user_info = new Adapter_User_Info(specs);
                recyclerView.setAdapter(adapter_user_info);
                adapter_user_info.setOnItemListener(new Adapter_User_Info.OnItemClickListner() {
                    @Override
                    public void onItemClick(View v, int pos) {
                    }

                    @Override
                    public void onItemLongClick(View v, int pos) {
                        SpecDiaLog specDiaLog = new SpecDiaLog(getContext(),specs.get(pos));
                        specDiaLog.setSpecDialogListener(new SpecDiaLog.SpecDiaLogListener() {
                            @Override
                            public void onPositiveClicked(User_Info_Data user_info_data) {
                                temp_specs.remove(specs.get(pos));
                                temp_specs.add(user_info_data);
                                datas = specs_to_str_specs(temp_specs);
                                userAccount.setSpecs(datas);
                                mStore.collection("user").document(mAuth.getUid()).set(userAccount).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        datachanged(type);
                                    }
                                });
                                adapter_user_info.notifyDataSetChanged();
                            }

                            @Override
                            public void onNegativeClicked(User_Info_Data user_info_data) {
                                temp_specs.remove(specs.get(pos));
                                datas  = specs_to_str_specs(temp_specs);
                                userAccount.setSpecs(datas);
                                specs.remove(pos);
                                mStore.collection("user").document(mAuth.getUid()).set(userAccount).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        datachanged(type);
                                    }
                                });
                                adapter_user_info.notifyDataSetChanged();
                            }
                        });
                        specDiaLog.show();
                    }
                });
            }

        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                type = Integer.toString(tab.getPosition());
                Log.e("spec",type);
                specs.clear();
                datachanged(type);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }
    public ArrayList<String> specs_to_str_specs(ArrayList<User_Info_Data> specs){
        ArrayList<String> list_ret=new ArrayList<>();
        for(User_Info_Data spec : specs){
            String ret ="";
            ret+=spec.getType();
            ret+=',';
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
            Log.e("spec",spec);
            String [] temp = spec.split(",");
            User_Info_Data uid = new User_Info_Data(temp[0],temp[1],temp[2]);
            user_info_data.add(uid);

            if(user_info_data.size()>4){
                break;
            }
        }
        return user_info_data;
    }
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_edit_user_info,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_save:
                showDialog();
                break;
            case android.R.id.home:
                ft.remove(Fragment_UserSpec.this).commit();
                fm.popBackStack();
                ((MainActivity)MainActivity.maincontext).setvisibleNavi(false);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    public void showDialog(){
        SpecDiaLog specDiaLog = new SpecDiaLog(getContext());
        specDiaLog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams params = specDiaLog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height= WindowManager.LayoutParams.MATCH_PARENT;
        specDiaLog.getWindow().setAttributes(params);
        specDiaLog.setSpecDialogListener(new SpecDiaLog.SpecDiaLogListener() {
            @Override
            public void onPositiveClicked(User_Info_Data user_info_data) {
                temp_specs.add(user_info_data);
                datas = specs_to_str_specs(temp_specs);
                userAccount.setSpecs(datas);
                mStore.collection("user").document(mAuth.getUid()).set(userAccount);
                datachanged(type);
            }

            @Override
            public void onNegativeClicked(User_Info_Data user_info_data) {
                temp_specs.remove(user_info_data);
            }
        });
        specDiaLog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).setBackPressedlistener(this);
    }

    @Override
    public void onBackPressed() {
        ft.remove(Fragment_UserSpec.this).commit();
        fm.popBackStack();
        ((MainActivity)MainActivity.maincontext).setvisibleNavi(false);

    }
    public void datachanged(String type){
        specs.clear();
        for(User_Info_Data temp : temp_specs){
            if(type.equals("0")){
                specs.add(temp);
            }
            else{
                if(type.equals(temp.getType())){
                    specs.add(temp);
                }
            }
        }
        adapter_user_info.notifyDataSetChanged();
    }


}