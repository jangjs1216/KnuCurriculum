package com.example.loginregister;

import android.content.Intent;
import android.graphics.Color;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregister.adapters.SubjectAdapter;
import com.example.loginregister.curiList.Curl_List_Fragment;
import com.example.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.otaliastudios.zoom.ZoomLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

public class Fragment2 extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;
    String curData;
    ViewHolder curViewHolder;
    private View v;
    private Toolbar toolbar;
    private final static String TAG ="Frag2";
    TreeNode rootNode;
    int nodeCount = 0;
    private FragmentManager fm;
    private FragmentTransaction ft;

    TreeNode[] treeNodeList;
    ZoomLayout zoomLayout;
    ArrayList<Subject_> subjectList = new ArrayList<>();
    BottomSheetDialog nodeChoiceBottomSheetDialog, subjectChoiceBottomSheetDialog;
    RecyclerView subjectRecyclerView;
    EditText searchET;
    Button searchBtn, addTreeBtn;

    SubjectAdapter subjectAdapter;
    TreeView treeView;
    BaseTreeAdapter adapter;

    //서버에 올리는 Table
    String tableName;
    ArrayList<String> tableNames;
    ArrayList<Table> tables;
    int tableLoc;
    Table userTableInfo;

    //크기 유동적 변화 구현
    private int displaySize = 500;
    private int displayHeightSize = 500;

    //과목 이름 매핑
    HashMap<String, Integer> m;
    ArrayList<Integer> adj[];

    /*
    [20210807] 장준승 Fragment2 시각화 구현
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_2, container, false);
        fm=getActivity().getSupportFragmentManager();
        ft=fm.beginTransaction();
        //툴바 시작
        toolbar = (androidx.appcompat.widget.Toolbar)v.findViewById(R.id.tb_frag3);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        //툴바끝

        fm=getActivity().getSupportFragmentManager();
        ft=fm.beginTransaction();
        /*
        TreeView 선언
         */
        zoomLayout = v.findViewById(R.id.layout_zoom);
        treeView = new TreeView(container.getContext()){
            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent event, float distanceX, float distanceY) {
                return false;
            }
        };
        treeView.setLevelSeparation(50);
        treeView.setLineColor(Color.BLACK);
        treeView.setLineThickness(5);

        /* 테이블 이름 받아오고 해당 테이블 서버에서 받아와서 출력 */
        if (getArguments() != null)
        {
            if(userTableInfo == null){
                Log.e("###", "userTableInfo : null");
            }
            tableName = getArguments().getString("tableName");
            TextView tableNameTV = v.findViewById(R.id.tableNameTV);
            tableNameTV.setText("테이블 이름 : " + tableName);
            getSubjectListFromFB();
        }
        else{
            Toast.makeText(getContext(), "테이블을 선택해주세요", Toast.LENGTH_LONG).show();
        }


        adapter = new BaseTreeAdapter<ViewHolder>(container.getContext(), R.layout.node) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(View view) {
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                /*
                [장준승] treenode에 정보를 업데이트 할 때, 오픈소스의 특성상 textview의
                        값 자체를 변환하기 어려우므로, String 값 자체에 모든 정보를 일괄적으로 넘겨주어 처리합니다.

                        Ex) 논리회로.1학년 1학기.1 (논리회로를 1학년 1학기에 듣고, 선택되었다.)
                 */
                //Log.e("###", "현재 데이터 : "+data.toString());
                String[] nodeData = data.toString().split("\\.");
                //Log.e("###", "변환된 데이터 : ["+nodeData[0]+"] ["+nodeData[1]+"] ["+nodeData[2]+"]");
                viewHolder.mTextView.setText(nodeData[0]);

                if(nodeData[2].equals("1") && nodeData[2] != null)
                {
                    viewHolder.setViewHolderSelected();
                }
                else{
                    viewHolder.setViewHoldernotSelected();
                }

                if(nodeData[1] != null)
                {
                    viewHolder.semesterTv.setText(nodeData[1]);
                }else{
                    viewHolder.semesterTv.setText("인식 오류");
                }
                viewHolder.setSemesterColored();

                /*
                각 노드 선택시 BottomSheetDialog 띄우는 작업 수행
                 */
                viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curViewHolder = viewHolder;

                        Log.e("###", viewHolder.mTextView.getText().toString());
                        curData = viewHolder.mTextView.getText().toString();

                        //노드선택 BottomSheetDialog 띄우기
                        nodeChoiceBottomSheetDialog = new BottomSheetDialog(getActivity());
                        nodeChoiceBottomSheetDialog.setContentView(R.layout.dialog_nodechoicebottomsheet);
                        nodeChoiceBottomSheetDialog.show();

                        LinearLayout LL1 = nodeChoiceBottomSheetDialog.findViewById(R.id.LL1);
                        LinearLayout LL2 = nodeChoiceBottomSheetDialog.findViewById(R.id.LL2);
                        LinearLayout LL3 = nodeChoiceBottomSheetDialog.findViewById(R.id.LL3);
                        LinearLayout LL4 = nodeChoiceBottomSheetDialog.findViewById(R.id.LL4);
                        LL1.setOnClickListener(nodeChoiceBottomSheetOnClickListener);
                        LL2.setOnClickListener(nodeChoiceBottomSheetOnClickListener);
                        LL3.setOnClickListener(nodeChoiceBottomSheetOnClickListener);
                        LL4.setOnClickListener(nodeChoiceBottomSheetOnClickListener);
                    }
                });


            }
        };
        treeView.setAdapter(adapter);

        addTreeBtn = v.findViewById(R.id.addTreeBtn);
        addTreeBtn.setOnClickListener(addTreeBtnOnClickListener);

        Log.e("###", "Treeview's parent is ... " + zoomLayout.getWidth());

        return v;
    }

    /* [최정인] 노드 선택시 나오는 BottomSheetDialog 클릭 리스너 */
    View.OnClickListener nodeChoiceBottomSheetOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.LL1:
                    nodeChoiceBottomSheetDialog.dismiss();
                    // [장준승] BottomSheetDialog 를 사용하기 이전에 Listener를 업데이트 해 줍니다.
                    makeRVBySubjectList();
                    subjectChoiceBottomSheetDialog.show();
                    break;

                case R.id.LL2:
                    TreeNode parent =  treeNodeList[m.get(curData)].getParent();
                    String[] nodeData = parent.getData().toString().split("\\.");
                    userTableInfo.getTable().get(nodeData[0]).put(curData, "0");

                    //UserAccount 정보 업데이트
                    docRef = db.collection("user").document(mAuth.getUid());
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                            userAccount.getTables().set(tableLoc, userTableInfo);
                            db.collection("user").document(mAuth.getUid()).set(userAccount);

                            deleteTreeFromDB(curData);
                            updateDisplaySize();
                            nodeChoiceBottomSheetDialog.dismiss();
                        }
                    });
                    break;

                case R.id.LL3:
                    Intent intent=new Intent(getActivity(), SubjectInfoActivity.class);
                    intent.putExtra("subjectName", curData);
                    startActivity(intent);
                    nodeChoiceBottomSheetDialog.dismiss();
                    break;

                case R.id.LL4:
                    SubjectDetailDialog sDialog = new SubjectDetailDialog(v.getContext(), curData);
                    sDialog.setDialogListener(new SubjectDetailDialog.CustomDialogListener() {
                        @Override
                        public void onReturnClicked(Boolean isTakenClass, String TakenSemester) {
//                            Log.e("###", "수강정보 : " + isTakenClass + ", 수강학기 : " + TakenSemester);
//                            Log.e("###", "현재 Viewholder값 : " + curViewHolder.mTextView.getText());
//                            Log.e("###", "수정 이전 내부 값 : " + curViewHolder.semesterTv.getText());

                            String currSubjectName = (String) curViewHolder.mTextView.getText();

                            for(TreeNode tn : treeNodeList)
                            {
                                // [장준승] rootnode 예외 처리
                                if(tn != null && curData.equals(tn.getData().toString().split("\\.")[0]) && tn.getParent() == null)
                                {
                                    if(isTakenClass)
                                    {
                                        tn.setData(currSubjectName+"."+TakenSemester+".1");
                                        userTableInfo.setRoot(currSubjectName+"."+TakenSemester+".1");
                                    }else{
                                        tn.setData(currSubjectName+"."+TakenSemester+".0");
                                        userTableInfo.setRoot(currSubjectName+"."+TakenSemester+".1");
                                    }
                                    docRef = db.collection("user").document(mAuth.getUid());
                                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                                            userAccount.getTables().set(tableLoc, userTableInfo);
                                            db.collection("user").document(mAuth.getUid()).set(userAccount);
                                        }
                                    });
                                    break;
                                }

                                if(tn != null && curData.equals(tn.getData().toString().split("\\.")[0]))
                                {
//                                    Log.e("###", "선택되었음!!"+curData);
                                    String parentSubjectName = tn.getParent().getData().toString().split("\\.")[0];
                                    if(isTakenClass)
                                    {
                                        tn.setData(currSubjectName+"."+TakenSemester+".1");
                                        userTableInfo.getTable().get(parentSubjectName).put(currSubjectName, "."+TakenSemester+".1");
                                    }else{
                                        tn.setData(currSubjectName+"."+TakenSemester+".0");
                                        userTableInfo.getTable().get(parentSubjectName).put(currSubjectName, "."+TakenSemester+".0");
                                    }
                                    docRef = db.collection("user").document(mAuth.getUid());
                                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                                            userAccount.getTables().set(tableLoc, userTableInfo);
                                            db.collection("user").document(mAuth.getUid()).set(userAccount);
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    });
                    nodeChoiceBottomSheetDialog.dismiss();
                    sDialog.setCancelable(false);
                    sDialog.show();

                    break;
            }
        }
    };

//툴바 레이아웃설정
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_frag2,menu);
    }
//툴바 기능설정
    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_btn_curiList:
                ft.replace(R.id.main_frame, new Curl_List_Fragment());
                ft.addToBackStack(null);
                ft.commit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateDisplaySize()
    {

        //Log.e("###", "Estimate the root node size .. : "+treeView.);

        int displaySize = rootNode.getNodeCount() * 300 + 500;
        treeView.setMinimumWidth(displaySize);
        treeView.setMinimumHeight(displaySize);
        zoomLayout.moveTo((float)1.0, 0, 0, false);
        zoomLayout.zoomBy((float)1.0, false);

        return;
    }

    /* [최정인] 기능 함수화 */

    // 서버에서 Subject 받아오기
    /* SubjectList 완성 -> 노드추가 리사이클러뷰 연결 -> SubjectList 매핑 -> FB로부터 테이블 받기 */ 
    public void getSubjectListFromFB(){
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
                        Log.e("###", "개수 : " + Integer.toString(subjectList.size()));
                        treeNodeList = new TreeNode[subjectList.size()];


                        //adj 초기화
                        adj = new ArrayList[subjectList.size()];
                        for(int i=0; i<subjectList.size(); i++)
                            adj[i] = new ArrayList<Integer>();

                        mappingSubjectList();

                        getTableFromFB();

                        makeRVBySubjectList();
                    }
                });
    }

    //SubjectList 매핑
    public void mappingSubjectList(){
        /* DB에서 받아온 과목들 매핑 */
        m = new HashMap<String, Integer>();
        for(int i=0; i<subjectList.size(); i++){
            m.put(subjectList.get(i).getName(), m.size());
        }
    }

    // 서버에서 Table 받아오기
    /* 서버에서 Table 받아오면 -> 받아온 Table을 adj로 변환 -> adj기반으로 트리 만들기 */
    public void getTableFromFB(){
        docRef = db.collection("user").document(mAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                tableNames = userAccount.getTableNames();
                tables = userAccount.getTables();
                for(int i = 0; i < tableNames.size(); i++){
                    if(tableNames.get(i).equals(tableName)){
                        tableLoc = i;
                        userTableInfo = tables.get(i);
                        changeToAdj(userTableInfo);
                        break;
                    }
                }

                if(userTableInfo == null){
                    Toast.makeText(getContext(), "트리를 추가해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    // 서버에서 받아온 Table을 adj로 변환
    public void changeToAdj(Table table){
        for(String currSubject : table.getTable().keySet()){
            Map<String, String> currRow = table.getTable().get(currSubject);

            for(String nextSubject : currRow.keySet()){
                if(!currRow.get(nextSubject).equals("0")){
                    int currMappingPos = m.get(currSubject);
                    int nextMappingPos = m.get(nextSubject);

                    adj[currMappingPos].add(nextMappingPos);
                }
            }
        }

        //Table의 root 값으로 루트노드 설정 후 adj로 트리 만들기
        rootNode = new TreeNode(table.getRoot());
        treeNodeList[m.get(table.getRoot().split("\\.")[0])] = rootNode;
        makeTreeByAdj(rootNode);
        adapter.setRootNode(rootNode);

        zoomLayout.removeAllViews();
        zoomLayout.addView(treeView);

        updateDisplaySize();
    }

    // adj로 트리 만들기
    public void makeTreeByAdj(TreeNode currNode){
        String currSubjectName = currNode.getData().toString().split("\\.")[0];
        int currMappingPos = m.get(currSubjectName);

        for(int nextMappingPos : adj[currMappingPos])
        {
            String nextSubjectName = subjectList.get(nextMappingPos).getName();
            final TreeNode newChild = new TreeNode(nextSubjectName + userTableInfo.getTable().get(currSubjectName).get(nextSubjectName));
            treeNodeList[nextMappingPos] = newChild;

            Log.e("###", "newchild " + newChild.getData());
            currNode.addChild(newChild);

            Log.e("###", currNode.toString());

            makeTreeByAdj(newChild);
        }
    }

    // SubjectList로 리사이클러뷰 만들기
    public void makeRVBySubjectList(){
        //과목 리스트 볼 수 있는 BottomSheetDialog
        subjectChoiceBottomSheetDialog = new BottomSheetDialog(getActivity());
        subjectChoiceBottomSheetDialog.setContentView(R.layout.dialog_subjectchoicebottomsheet);

        subjectAdapter = new SubjectAdapter(subjectList);
        subjectRecyclerView = subjectChoiceBottomSheetDialog.findViewById(R.id.subjectChoiceRecyclerView);
        searchET = subjectChoiceBottomSheetDialog.findViewById(R.id.searchET);
        searchBtn = subjectChoiceBottomSheetDialog.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(searchBtnOnClickListener);

        subjectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        subjectRecyclerView.setAdapter(subjectAdapter);

        //RecyclerView에서 선택된 아이템에 접근
        subjectAdapter.setOnItemListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                String choosedSubjectName = subjectList.get(pos).getName();
                Log.e("###", choosedSubjectName + " 선택 됨");

                Toast.makeText(v.getContext(), choosedSubjectName, Toast.LENGTH_LONG).show();
                for(TreeNode tn : treeNodeList)
                {
                    if(tn != null && curData.equals(tn.getData().toString().split("\\.")[0]))
                    {
                        //DBG
                        //UserAccount 정보 업데이트
                        userTableInfo.getTable().get(curData).put(choosedSubjectName, ".1학년 1학기.0");
                        docRef = db.collection("user").document(mAuth.getUid());
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                                userAccount.getTables().set(tableLoc, userTableInfo);
                                db.collection("user").document(mAuth.getUid()).set(userAccount);
                            }
                        });
                        int mappingPos = m.get(choosedSubjectName);

                        //[장준승] 위의 규칙에 맞게 SubjectName을 변환합니다.
                        String convertedSubjectName = choosedSubjectName + ".1학년 1학기.0";
                        final TreeNode newChild = new TreeNode(convertedSubjectName);

                        //[장준승] 화면 사이즈 node 개수에 비례하여 변화
                        updateDisplaySize();
                        Log.e("###", "Current displaySize : "+displaySize);

                        adj[m.get(curData)].add(mappingPos);
                        treeNodeList[mappingPos] = newChild;
                        tn.addChild(newChild);
                        break;
                    }
                }
                //subjectAdapter = new SubjectAdapter(subjectList);
                subjectChoiceBottomSheetDialog.dismiss();
            }
        });
    }
    
    //노드추가에서 검색 버튼 클릭 리스너
    View.OnClickListener searchBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<Subject_> searchSubjectList = new ArrayList<>();
            for(Subject_ subject_ : subjectList){
                if(subject_.getName().contains(searchET.getText().toString())) searchSubjectList.add(subject_);
            }
            subjectAdapter = new SubjectAdapter(searchSubjectList);
            subjectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            subjectRecyclerView.setAdapter(subjectAdapter);

            //RecyclerView에서 선택된 아이템에 접근
            subjectAdapter.setOnItemListener(new SubjectAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    if(userTableInfo != null){
                        String choosedSubjectName = searchSubjectList.get(pos).getName();
                        Log.e("###", choosedSubjectName + " 선택 됨");

                        Toast.makeText(v.getContext(), choosedSubjectName, Toast.LENGTH_LONG).show();
                        for(TreeNode tn : treeNodeList)
                        {
                            if(tn != null && curData.equals(tn.getData().toString().split("\\.")[0]))
                            {
                                //UserAccount 정보 업데이트
                                userTableInfo.getTable().get(curData).put(choosedSubjectName, ".1학년 1학기.0");
                                docRef = db.collection("user").document(mAuth.getUid());
                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                                        userAccount.getTables().set(tableLoc, userTableInfo);
                                        db.collection("user").document(mAuth.getUid()).set(userAccount);
                                    }
                                });
                                int mappingPos = m.get(choosedSubjectName);

                                //[장준승] 위의 규칙에 맞게 SubjectName을 변환합니다.
                                String convertedSubjectName = choosedSubjectName + ".1학년 1학기.0";
                                final TreeNode newChild = new TreeNode(convertedSubjectName);

                                //[장준승] 화면 사이즈 node 개수에 비례하여 변화
                                updateDisplaySize();
                                Log.e("###", "Current displaySize : "+displaySize);

                                adj[m.get(curData)].add(mappingPos);
                                treeNodeList[mappingPos] = newChild;
                                tn.addChild(newChild);
                                break;
                            }
                        }
                    }
                    else {
                        String choosedSubjectName = searchSubjectList.get(pos).getName();
                        Log.e("###", choosedSubjectName + " 선택 됨");

                        Toast.makeText(v.getContext(), choosedSubjectName, Toast.LENGTH_LONG).show();

                        Map<String, Map<String, String>> tb = new HashMap<>();
                        for (Subject_ subject_ : subjectList) {
                            Map<String, String> line = new HashMap<>();
                            tb.put(subject_.getName(), line);
                        }
                        Table table = new Table(tb, choosedSubjectName + ".1학년 1학기.0");

                        docRef = db.collection("user").document(mAuth.getUid());
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                                userAccount.getTableNames().add(tableName);
                                userAccount.getTables().add(table);
                                db.collection("user").document(mAuth.getUid()).set(userAccount);

                                //테이블 만들어서 넣어줬으니까 여기서부터 다시 시작
                                getTableFromFB();
                            }
                        });
                    }

                    subjectChoiceBottomSheetDialog.dismiss();
                }
            });
        }
    };

    // 트리추가 버튼 클릭 리스너
    View.OnClickListener addTreeBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(userTableInfo != null){
                Toast.makeText(getContext(), "이미 트리가 있습니다.", Toast.LENGTH_LONG).show();
                return;
            }

            subjectChoiceBottomSheetDialog.show();
            ArrayList<Subject_> searchSubjectList = new ArrayList<>();
            for(Subject_ subject_ : subjectList){
                if(subject_.getName().contains(searchET.getText().toString())) searchSubjectList.add(subject_);
            }
            subjectAdapter = new SubjectAdapter(searchSubjectList);
            subjectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            subjectRecyclerView.setAdapter(subjectAdapter);

            //DBG
            //RecyclerView에서 선택된 아이템에 접근
            subjectAdapter.setOnItemListener(new SubjectAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    String choosedSubjectName = searchSubjectList.get(pos).getName();
                    Log.e("###", choosedSubjectName + " 선택 됨");

                    Toast.makeText(v.getContext(), choosedSubjectName, Toast.LENGTH_LONG).show();

                    Map<String, Map<String, String>> tb = new HashMap<>();
                    for(Subject_ subject_ : subjectList){
                        Map<String, String> line = new HashMap<>();
                        tb.put(subject_.getName(), line);
                    }
                    Table table = new Table(tb, choosedSubjectName + ".1학년 1학기.0");

                    docRef = db.collection("user").document(mAuth.getUid());
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                            userAccount.getTableNames().add(tableName);
                            userAccount.getTables().add(table);

                            Log.e("###", "트리 추가 " + tableName);
                            db.collection("user").document(mAuth.getUid()).set(userAccount);

                            //테이블 만들어서 넣어줬으니까 여기서부터 다시 시작

                            getTableFromFB();
                        }
                    });

                    subjectChoiceBottomSheetDialog.dismiss();
                }

            });
        }
    };

    // DB 바탕으로 트리 노드 삭제
    public void deleteTreeFromDB(String currNode){
        int currNodeValue = m.get(currNode);

        for(int nextNodeValue : adj[currNodeValue])
        {
            String nextNode = subjectList.get(nextNodeValue).getName();
            deleteTreeFromDB(nextNode);

            adj[currNodeValue].remove(Integer.valueOf(nextNodeValue));

            //UserAccount 정보 업데이트
            userTableInfo.getTable().get(currNode).remove(nextNode);
            docRef = db.collection("user").document(mAuth.getUid());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                    userAccount.getTables().set(tableLoc, userTableInfo);
                    db.collection("user").document(mAuth.getUid()).set(userAccount);
                }
            });
        }

        treeNodeList[currNodeValue].getParent().removeChild(treeNodeList[currNodeValue]);
    }
}