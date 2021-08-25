package com.example.loginregister;

import android.app.Dialog;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loginregister.UserInfo.Fragment_Edit_User_Info;
import com.example.loginregister.adapters.SubjectAdapter;
import com.example.loginregister.adapters.SubjectCommentAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Fragment4 extends Fragment {
    private Toolbar toolbar;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private final static  String TAG = "Frag4";
    EditText searchText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Subject_> subjectList = new ArrayList<>();
    ArrayList<Subject_> search_subjectList = new ArrayList<>();
    SubjectAdapter subjectAdapter;
    RecyclerView recyclerView;
    ImageView delete_btt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_4, container, false);
        fm = getActivity().getSupportFragmentManager();
        ft= fm.beginTransaction();
        toolbar = (Toolbar)view.findViewById(R.id.tb_frag4);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.F4_frag);
        delete_btt=view.findViewById(R.id.delete_btt);

        //검색완료시 함수 박경무
        searchText=view.findViewById(R.id.searcf_into);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                   Searchinto();
                    return true;
                }
                return false;
            }
        });

       listSub();

       delete_btt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               listSub();
               searchText.setText(null);
           }
       });


        return view;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_frag1,menu);
        Log.e(TAG,"sex");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_setting:
                ft.replace(R.id.main_frame, new Fragment_Edit_User_Info());
                ft.addToBackStack(null);
                ft.commit();
                break;
            case android.R.id.home:
                //뒷프래그먼트로 이동;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //과목리스트 출력
    public void listSub()
    {
        db.collection("Subject")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                subjectList.add(document.toObject(Subject_.class));
                            }
                            subjectAdapter = new SubjectAdapter(subjectList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(subjectAdapter);

                            subjectAdapter.setOnItemListener(new SubjectAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, int pos) {
                                    String choosedSubjectName = subjectList.get(pos).getName();
                                    Intent intent = new Intent(getContext(),SubjectInfoActivity.class);
                                    intent.putExtra("subjectName",choosedSubjectName);
                                    startActivity(intent);
                                }
                            });

                        }
                    }
                });


    }

    //검색학기
    public void Searchinto(){
        String schtxt = searchText.getText().toString();

        for(Subject_ ss: subjectList){
            if(ss.getName().contains(schtxt) ){
                search_subjectList.add(ss);
            }
        }
        subjectAdapter = new SubjectAdapter(search_subjectList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(subjectAdapter);

    }

}