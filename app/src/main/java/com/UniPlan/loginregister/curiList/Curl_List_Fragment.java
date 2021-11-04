package com.UniPlan.loginregister.curiList;


import android.app.Dialog;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.UniPlan.loginregister.Fragment1;
import com.UniPlan.loginregister.Fragment2;
import com.UniPlan.loginregister.MainActivity;
import com.UniPlan.loginregister.Subject_;
import com.UniPlan.loginregister.Table;
import com.UniPlan.loginregister.UserInfo.Fragment_User_Info;
import com.UniPlan.loginregister.login.UserAccount;
import com.UniPlan.loginregister.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;


public class Curl_List_Fragment extends Fragment implements MainActivity.IOnBackPressed {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;
    UserAccount userAccount;
    private View view;
    private LinearLayoutManager linearLayoutManager;
    private  RecyclerView recyclerView;
    private ArrayList<Recycler_Data> arrayList;
    private Recycler_Adapter recycler_adapter;
    private Toolbar toolbar;
    Dialog addTreeDialog;
    Dialog deleteTreeDialog;
    private FragmentManager fm;
    private FragmentTransaction ft;
    Table UsersTableInfo;
    ArrayList<Subject_> subjectList = new ArrayList<>();
    ArrayList<Table> recommendationList = new ArrayList<>();
    Map<String, String> takenSubject;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_curl__list_, container, false);
        fm = getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        //////////툴바///////////
        //상단 제목바꾸기 프래그먼트별로 설정 및 커스텀 및 안보이게 가능- 안승재
        toolbar = (Toolbar)view.findViewById(R.id.tb_curi_list);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        // actionBar.setLogo(getResources().getDrawable(R.drawable.knucurricular_app_icon));//앱아이콘
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로가기 기능생성
        /////////툴바끝///////////////

        //뒤로가기
        ((MainActivity) getActivity()).setBackPressedlistener(this);

        //SubjectList 받아오기(테이블 삭제시 학점 정보 얻어오려고)
        db.collection("Subject")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                subjectList.add(document.toObject(Subject_.class));
                            }
                        } else {
                        }
                    }
                });

        db.collection("Recommendation")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                recommendationList.add(document.toObject(Table.class));
                            }
                        }else{
                        }
                    }
                });

        //UsersTableInfo 처음에만 받아오고 후에 변화할땐 저장만 해주면 됨.
        docRef = db.collection("UsersTableInfo").document("Matrix");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UsersTableInfo = documentSnapshot.toObject(Table.class);
            }
        });

        docRef = db.collection("user").document(mAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userAccount = documentSnapshot.toObject(UserAccount.class);
                takenSubject = userAccount.getTakenSubject();

                //      리싸이클러뷰
                recyclerView = (RecyclerView)view.findViewById(R.id.Recycler_View_Curi_List);
                linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                arrayList = new ArrayList<>();
                for(String tableName : userAccount.getTableNames()){
                    Recycler_Data recycler_data = new Recycler_Data(tableName);
                    arrayList.add(recycler_data);
                    Log.e("###", "item : " + tableName);
                }
                recycler_adapter = new Recycler_Adapter(arrayList);
                recyclerView.setAdapter(recycler_adapter);

                //리싸이클러뷰 클릭 리스너
                recycler_adapter.setOnItemListener(new Recycler_Adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos, String option) {
                        if(option.equals("choice")){
                            String tableName = arrayList.get(pos).getTv_title().toString();

                            Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                            bundle.putString("tableName", tableName);//번들에 넘길 값 저장
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            Fragment2 fragment2 = new Fragment2();//프래그먼트2 선언
                            fragment2.setArguments(bundle);//번들을 프래그먼트2로 보낼 준비
                            transaction.replace(R.id.main_frame, fragment2);
                            transaction.commit();
                        }
                        if(option.equals("delete")){
                            showDeleteTreeDialog(pos);
                        }
                    }
                });
            }
        });

        //트리 추가 버튼 다이얼로그
        addTreeDialog = new Dialog(getContext(), R.style.AddTreeDialog);
        addTreeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addTreeDialog.setContentView(R.layout.dialog_addtree);
        addTreeDialog.setCanceledOnTouchOutside(true);
        addTreeDialog.getWindow().setGravity(Gravity.CENTER);

        //트리 삭제 다이얼로그
        deleteTreeDialog = new Dialog(getContext(), R.style.DeleteTreeDialog);
        deleteTreeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteTreeDialog.setContentView(R.layout.dialog_deletetree);
        deleteTreeDialog.setCanceledOnTouchOutside(true);
        deleteTreeDialog.getWindow().setGravity(Gravity.CENTER);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_curi_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_add:
                showAddTreeDialog();
                break;

            case android.R.id.home:
                onBackPressed();
                //ft.replace(R.id.main_frame,new Fragment2()).commit();
               // ((MainActivity)MainActivity.maincontext).setvisibleNavi(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAddTreeDialog() {
        addTreeDialog.show();

        TextView noTV = addTreeDialog.findViewById(R.id.noTV);
        TextView yesTV = addTreeDialog.findViewById(R.id.yesTV);
        TextView TV1 = addTreeDialog.findViewById(R.id.TV1);
        TextView TV2 = addTreeDialog.findViewById(R.id.TV2);
        TextView TV3 = addTreeDialog.findViewById(R.id.TV3);
        noTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTreeDialog.dismiss();
            }
        });
        yesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText treeNameET = addTreeDialog.findViewById(R.id.treeNameET);
                String treeName = treeNameET.getText().toString();
                treeNameET.setText("");
                
                //과정 이름 중복 체크
                boolean checkName = false;
                for(int i = 0; i < userAccount.getTableNames().size(); i++){
                    if(treeName.equals(userAccount.getTableNames().get(i))) checkName = true;
                }
                if(checkName == false){
                    Recycler_Data recycler_data = new Recycler_Data(treeName);
                    arrayList.add(recycler_data);
                    ((MainActivity)getActivity()).setArrayList_curiList(arrayList);
                    recycler_adapter.notifyDataSetChanged();
                    addTreeDialog.dismiss();

                    Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                    bundle.putString("tableName", treeName);//번들에 넘길 값 저장
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment2 fragment2 = new Fragment2();//프래그먼트2 선언
                    fragment2.setArguments(bundle);//번들을 프래그먼트2로 보낼 준비
                    transaction.replace(R.id.main_frame, fragment2);
                    transaction.commit();
                }
                else{
                    Toast.makeText(getContext(), "이미 존재하는 과정입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TV1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String treeName = "반도체";
                //과정 이름 중복 체크
                boolean checkName = false;
                for(int i = 0; i < userAccount.getTableNames().size(); i++){
                    if(treeName.equals(userAccount.getTableNames().get(i))) checkName = true;
                }
                if(checkName == false){
                    userAccount.getTableNames().add(treeName);
                    userAccount.getTables().add(recommendationList.get(0));
                    db.collection("user").document(mAuth.getUid()).set(userAccount);

                    ((MainActivity)getActivity()).setArrayList_curiList(arrayList);
                    recycler_adapter.notifyDataSetChanged();
                    addTreeDialog.dismiss();

                    Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                    bundle.putString("tableName", treeName);//번들에 넘길 값 저장
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment2 fragment2 = new Fragment2();//프래그먼트2 선언
                    fragment2.setArguments(bundle);//번들을 프래그먼트2로 보낼 준비
                    transaction.replace(R.id.main_frame, fragment2);
                    transaction.commit();
                }
                else{
                    Toast.makeText(getContext(), "이미 존재하는 과정입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String treeName = "통신";
                //과정 이름 중복 체크
                boolean checkName = false;
                for(int i = 0; i < userAccount.getTableNames().size(); i++){
                    if(treeName.equals(userAccount.getTableNames().get(i))) checkName = true;
                }
                if(checkName == false){
                    userAccount.getTableNames().add(treeName);
                    userAccount.getTables().add(recommendationList.get(1));
                    db.collection("user").document(mAuth.getUid()).set(userAccount);

                    ((MainActivity)getActivity()).setArrayList_curiList(arrayList);
                    recycler_adapter.notifyDataSetChanged();
                    addTreeDialog.dismiss();

                    Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                    bundle.putString("tableName", treeName);//번들에 넘길 값 저장
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment2 fragment2 = new Fragment2();//프래그먼트2 선언
                    fragment2.setArguments(bundle);//번들을 프래그먼트2로 보낼 준비
                    transaction.replace(R.id.main_frame, fragment2);
                    transaction.commit();
                }
                else{
                    Toast.makeText(getContext(), "이미 존재하는 과정입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TV3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String treeName = "회로";
                //과정 이름 중복 체크
                boolean checkName = false;
                for(int i = 0; i < userAccount.getTableNames().size(); i++){
                    if(treeName.equals(userAccount.getTableNames().get(i))) checkName = true;
                }
                if(checkName == false){
                    userAccount.getTableNames().add(treeName);
                    userAccount.getTables().add(recommendationList.get(2));
                    db.collection("user").document(mAuth.getUid()).set(userAccount);

                    ((MainActivity)getActivity()).setArrayList_curiList(arrayList);
                    recycler_adapter.notifyDataSetChanged();
                    addTreeDialog.dismiss();

                    Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                    bundle.putString("tableName", treeName);//번들에 넘길 값 저장
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment2 fragment2 = new Fragment2();//프래그먼트2 선언
                    fragment2.setArguments(bundle);//번들을 프래그먼트2로 보낼 준비
                    transaction.replace(R.id.main_frame, fragment2);
                    transaction.commit();
                }
                else{
                    Toast.makeText(getContext(), "이미 존재하는 과정입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showDeleteTreeDialog(int pos) {
        deleteTreeDialog.show();

        TextView noTV = deleteTreeDialog.findViewById(R.id.noTV);
        TextView yesTV = deleteTreeDialog.findViewById(R.id.yesTV);
        noTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTreeDialog.dismiss();
            }
        });
        yesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deleteTreeName = userAccount.getTableNames().get(pos);

                deleteTreeFromServer(userAccount.getTables().get(pos));
                userAccount.getTableNames().remove(pos);
                userAccount.getTables().remove(pos);
                db.collection("user").document(userAccount.getIdToken()).set(userAccount);
                Toast.makeText(getContext(), deleteTreeName + "을 삭제했습니다.", Toast.LENGTH_SHORT).show();

                arrayList.remove(pos);
                ((MainActivity)getActivity()).setArrayList_curiList(arrayList);
                recycler_adapter.notifyDataSetChanged();
                deleteTreeDialog.dismiss();
            }
        });
    }

    public void deleteTreeFromServer(Table table){
        setSubjectInfo(table.getRoot().split("\\.")[0], ".1학년 1학기.0");

        for(String subjectName : table.getTable().keySet()){
            for(String lineSubjectName : table.getTable().get(subjectName).keySet()){
                setSubjectInfo(lineSubjectName, ".1학년 1학기.0");
            }
        }
    }

    public void setSubjectInfo(String subjectName, String info){
        takenSubject.put(subjectName, info);
        userAccount.setTakenSubject(takenSubject);
        db.collection("user").document(mAuth.getUid()).set(userAccount);
    }

    //뒤로가기
    @Override
    public void onBackPressed() {
        int sum = 0;
        for(String subjectName : takenSubject.keySet()){
            if(takenSubject.get(subjectName).split("\\.")[2].equals("0")) continue;
            for(int i = 0; i < subjectList.size(); i++){
                if(subjectList.get(i).getName().equals(subjectName)){
                    int score = Integer.parseInt(subjectList.get(i).getScore());
                    sum += score;
                    break;
                }
            }
        }
        userAccount.setTaked(Integer.toString(sum));
        db.collection("user").document(mAuth.getUid()).set(userAccount);

        ft.replace(R.id.main_frame, new Fragment1()).commit();
        //ft.remove(Curl_List_Fragment.this).commit();
        fm.popBackStack();

        ((MainActivity)MainActivity.maincontext).setvisibleNavi(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).setBackPressedlistener(null);
    }

}