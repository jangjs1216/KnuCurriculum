package com.UniPlan.loginregister;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.UniPlan.loginregister.R;
import com.UniPlan.loginregister.adapters.SubjectAdapter;
import com.UniPlan.loginregister.curiList.Curl_List_Fragment;
import com.UniPlan.loginregister.login.UserAccount;
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

public class Fragment2 extends Fragment implements MainActivity.IOnBackPressed{
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;
    UserAccount userAccount;
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
    ImageView cancelIV;
    InputMethodManager inputMethodManager;

    SubjectAdapter subjectAdapter;
    TreeView treeView;
    BaseTreeAdapter adapter;

    //서버에 올리는 Table
    String tableName;
    ArrayList<String> tableNames;
    ArrayList<Table> tables;
    int tableLoc;
    Table currTable;
    Boolean treeResisted = false;
    Map<String, String> takenSubject;

    //UsersTableInfo
    //Table UsersTableInfo;

    //크기 유동적 변화 구현
    ViewHolder[] viewHolderList;
    private int displaySize = 500;
    private float displayHeight = 0;
    private float displayWidth = 0;
    private int displayWidthMargin = 1200;
    private int displayHeightMargin = 600;

    //과목 이름 매핑
    HashMap<String, Integer> m;
    ArrayList<Integer> adj[];

    //로딩
    private AppCompatDialog progressDialog;

    //프래그먼트 새로고침용
    Fragment refreshFragment;

    /*
    [20210807] 장준승 Fragment2 시각화 구현
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_2, container, false);
        refreshFragment = this;
        fm=getActivity().getSupportFragmentManager();
        ft=fm.beginTransaction();
        //툴바 시작
        toolbar = (androidx.appcompat.widget.Toolbar)v.findViewById(R.id.tb_frag3);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //툴바끝

        //BottomNavigation 띄우기
        ((MainActivity)MainActivity.maincontext).setvisibleNavi(false);

        //과목 갯수
        int subjectNumber = 200;
        viewHolderList = new ViewHolder[subjectNumber];

        fm=getActivity().getSupportFragmentManager();
        ft=fm.beginTransaction();

        //뒤로가기
        ((MainActivity) getActivity()).setBackPressedlistener(this);
        inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);

        /*
        TreeView 선언
         */
        zoomLayout = v.findViewById(R.id.layout_zoom);
        treeView = new TreeView(container.getContext()){
            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent event, float distanceX, float distanceY) {
                return false;
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        treeView.setLevelSeparation(50);
        treeView.setLineColor(Color.BLACK);
        treeView.setLineThickness(5);

        progressDialog = new AppCompatDialog(getContext());
        Onprogress(getActivity(),"로딩중...");
        docRef = db.collection("user").document(mAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userAccount = documentSnapshot.toObject(UserAccount.class);
                takenSubject = userAccount.getTakenSubject();
                init();
            }
        });

        /* TreeAdapter 선언 */
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

                viewHolderList[m.get(nodeData[0])] = viewHolder;

                if(nodeData[2] != null && nodeData[2].equals("1"))
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
                viewHolder.tn_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curViewHolder = viewHolder;

                        Log.e("###", "ViewHolder Position : "+viewHolder.tn_layout.getX());

                        Log.e("###", viewHolder.mTextView.getText().toString());
                        curData = viewHolder.mTextView.getText().toString();

                        //노드선택 BottomSheetDialog 띄우기
                        nodeChoiceBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.NewDialog);
                        nodeChoiceBottomSheetDialog.setContentView(R.layout.dialog_nodechoicebottomsheet);
                        nodeChoiceBottomSheetDialog.setCanceledOnTouchOutside(true);
                        nodeChoiceBottomSheetDialog.show();

                        TextView btn_isTaken = nodeChoiceBottomSheetDialog.findViewById(R.id.btn_isTaken);

                        String[] spinnerItem =
                                {"1학년 1학기", "1학년 2학기", "2학년 1학기", "2학년 2학기", "3학년 1학기",
                                        "3학년 2학기", "4학년 1학기", "4학년 2학기", "5학년 1학기"};
                        Spinner spinner = nodeChoiceBottomSheetDialog.findViewById(R.id.bottomSheetSpinner);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                getContext(), R.layout.spinner_item, spinnerItem
                        );

                        adapter.setDropDownViewResource(R.layout.spinner_item);
                        spinner.setAdapter(adapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Log.e("###", "onItemSelected ... : "+spinnerItem[position]);

                                TreeNode tn = treeNodeList[m.get(curData)];
                                String[] nodeData = tn.getData().toString().split("\\.");

                                tn.setData(nodeData[0]+"."+spinnerItem[position]+"."+nodeData[2]);
                                if(tn != null && tn.getParent() == null) {
                                    //루트노드인 경우
                                    currTable.setRoot(nodeData[0]+"."+spinnerItem[position]+"."+nodeData[2]);
                                    setSubjectInfo(nodeData[0], "." + spinnerItem[position] + "." + nodeData[2]);
                                }else{
                                    String parentSubjectName = treeNodeList[m.get(curData)].getParent().getData().toString().split("\\.")[0];
                                    currTable.getTable().get(parentSubjectName).put(nodeData[0], "."+spinnerItem[position]+"."+nodeData[2]);
                                    setSubjectInfo(nodeData[0], "." + spinnerItem[position] + "." + nodeData[2]);
                                }
                                userAccount.getTables().set(tableLoc, currTable);
                                db.collection("user").document(mAuth.getUid()).set(userAccount);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        // 현재 선택된 노드의 값을 가져옵니다.
                        String curNodeSemester = viewHolder.semesterTv.getText().toString();
                        for(int position = 0; position < spinnerItem.length; position++)
                        {
                            if(spinnerItem[position].equals(curNodeSemester))
                            {
                                spinner.setSelection(position);
                                break;
                            }
                        }

                        Boolean curNodeTaken = viewHolder.isTaken;

                        if(curNodeTaken)
                        {
                            //선택된 노드의 경우
                            btn_isTaken.setBackgroundResource(R.drawable.button_shape);
                            btn_isTaken.setText("수강 완료");
                        }else{
                            btn_isTaken.setBackgroundResource(R.drawable.ic_not_taken_class);
                            btn_isTaken.setText("");
                        }

                        //과목 이름 설정해주기
                        String className = viewHolder.mTextView.getText().toString();
                        TextView classNameOnBottomSheet = nodeChoiceBottomSheetDialog.findViewById(R.id.tv_classNameOnBottomSheet);
                        classNameOnBottomSheet.setText(className);
                        classNameOnBottomSheet.setSelected(true);

                        //수강 버튼 눌렀을 경우
                        btn_isTaken.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TreeNode tn = treeNodeList[m.get(curData)];
                                String[] nodeData = tn.getData().toString().split("\\.");
                                String currentSemester = nodeData[1];
                                String currSubjectName = curData;

                                Log.e("###", "Viewholder semester : "+currentSemester);

                                if(tn != null && tn.getParent() == null)
                                {
                                    //루트노드인 경우
                                    if(viewHolder.isTaken)
                                    {
                                        //듣 -> 안듣
                                        btn_isTaken.setBackgroundResource(R.drawable.ic_not_taken_class);
                                        btn_isTaken.setText("");
                                        viewHolder.setViewHoldernotSelected();

                                        tn.setData(currSubjectName+"."+currentSemester+".0");
                                        currTable.setRoot(currSubjectName+"."+currentSemester+".0");

                                        String[] currInfo = getSubjectInfo(currSubjectName).split("\\.");
                                        currInfo[2] = "0";
                                        String inputString = currInfo[0] + "." + currInfo[1] + "." + currInfo[2];
                                        setSubjectInfo(currSubjectName, inputString);
                                    }else{
                                        //안듣 -> 듣
                                        btn_isTaken.setBackgroundResource(R.drawable.button_shape);
                                        btn_isTaken.setText("수강 완료");
                                        viewHolder.setViewHolderSelected();

                                        tn.setData(currSubjectName+"."+currentSemester+".1");
                                        currTable.setRoot(currSubjectName+"."+currentSemester+".1");

                                        String[] currInfo = getSubjectInfo(currSubjectName).split("\\.");
                                        currInfo[2] = "1";
                                        String inputString = currInfo[0] + "." + currInfo[1] + "." + currInfo[2];
                                        setSubjectInfo(currSubjectName, inputString);
                                    }
                                }else{
                                    String parentSubjectName = treeNodeList[m.get(curData)].getParent().getData().toString().split("\\.")[0];

                                    if(viewHolder.isTaken)
                                    {
                                        //듣 -> 안듣
                                        btn_isTaken.setBackgroundResource(R.drawable.ic_not_taken_class);
                                        btn_isTaken.setText("");
                                        viewHolder.setViewHoldernotSelected();

                                        ChangeMatrixToServer(parentSubjectName, currSubjectName, true);
                                        tn.setData(currSubjectName+"."+currentSemester+".0");
                                        currTable.getTable().get(parentSubjectName).put(currSubjectName, "."+currentSemester+".0");

                                        String[] currInfo = getSubjectInfo(currSubjectName).split("\\.");
                                        currInfo[2] = "0";
                                        String inputString = currInfo[0] + "." + currInfo[1] + "." + currInfo[2];
                                        setSubjectInfo(currSubjectName, inputString);
                                    }else{
                                        //안듣 -> 듣
                                        btn_isTaken.setBackgroundResource(R.drawable.button_shape);
                                        btn_isTaken.setText("수강 완료");
                                        viewHolder.setViewHolderSelected();

                                        ChangeMatrixToServer(parentSubjectName, currSubjectName, false);
                                        tn.setData(currSubjectName+"."+currentSemester+".1");
                                        currTable.getTable().get(parentSubjectName).put(currSubjectName, "."+currentSemester+".1");

                                        String[] currInfo = getSubjectInfo(currSubjectName).split("\\.");
                                        currInfo[2] = "1";
                                        String inputString = currInfo[0] + "." + currInfo[1] + "." + currInfo[2];
                                        setSubjectInfo(currSubjectName, inputString);
                                    }
                                }
                                userAccount.getTables().set(tableLoc, currTable);
                                db.collection("user").document(mAuth.getUid()).set(userAccount);
                            }
                        });


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
                    TreeNode parent = treeNodeList[m.get(curData)].getParent();

                    if(parent == null)
                    {
                        Toast.makeText(getContext(),"루트 노드는 삭제할 수 없습니다.", Toast.LENGTH_LONG).show();
                    }else{
                        String[] nodeData = parent.getData().toString().split("\\.");
                        currTable.getTable().get(nodeData[0]).remove(curData);
                        setSubjectInfo(curData, ".1학년 1학기.0");

                        userAccount.getTables().set(tableLoc, currTable);
                        db.collection("user").document(mAuth.getUid()).set(userAccount);

                        deleteTreeFromDB(curData);

                        // 자신 노드 삭제
                        int currNodeValue = m.get(curData);
                        String[] curNodeData = treeNodeList[currNodeValue].getData().toString().split("\\.");
                        Log.e("###", "노드 자신의 수강결과 : "+curNodeData[2].equals("1"));
                        if(curNodeData[2].equals("1"))
                        {
                            ChangeMatrixToServer(nodeData[0], curData, true);
                        }
                        treeNodeList[currNodeValue].getParent().removeChild(treeNodeList[currNodeValue]);
                        treeNodeList[currNodeValue] = null;

                        updateDisplaySize();
                        nodeChoiceBottomSheetDialog.dismiss();
                    }
                    break;
                case R.id.LL3:
                    Intent intent=new Intent(getActivity(), SubjectInfoActivity.class);
                    intent.putExtra("subjectName", curData);
                    startActivity(intent);
                    nodeChoiceBottomSheetDialog.dismiss();
                    break;
                case R.id.LL4: //과목 변경 기능
                    nodeChoiceBottomSheetDialog.dismiss();
                    // [장준승] BottomSheetDialog 를 사용하기 이전에 Listener를 업데이트 해 줍니다.
                    makeRVBySubjectList_LL4();
                    subjectChoiceBottomSheetDialog.show();
/*
                    parent = treeNodeList[m.get(curData)].getParent();
                    if(parent == null){ //루트 노드인 경우

                    }
                    else{ //루트 노드가 아닌 경우

                    }*/
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
                ft.commit();
                break;
            case android.R.id.home:
                ft.replace(R.id.main_frame, new Fragment1());
                ft.commit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void updateDisplaySize()
    {
        treeView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                for(ViewHolder viewHolder : viewHolderList)
                {
                    if(viewHolder != null) {
                        if (displayHeight < viewHolder.tn_layout.getY()) {
                            displayHeight = viewHolder.tn_layout.getY();
                        }
                        if (displayWidth < viewHolder.tn_layout.getX()) {
                            displayWidth = viewHolder.tn_layout.getX();
                        }
                    }
                }
                treeView.setMinimumWidth((int) displayWidth + displayWidthMargin);
                treeView.setMinimumHeight((int) displayHeight + displayHeightMargin);

                treeView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });

        zoomLayout.moveTo((float)1.0, 0, 0, false);
        zoomLayout.zoomBy((float)1.0, false);
        zoomLayout.zoomOut();

        return;
    }



    /* [최정인] 기능 함수화 */

    public void init() {
        /* 테이블 이름 받아오고 해당 테이블 서버에서 받아와서 출력 */
        if (getArguments() != null)
        {
            if(currTable == null){
                Log.e("###", "currTable : null");
            }
            tableName = getArguments().getString("tableName");
            TextView tableNameTV = v.findViewById(R.id.tableNameTV);
            tableNameTV.setText(tableName);
            treeResisted = false;

            getSubjectListFromFB();
            progressOFF();
        }
        else if(userAccount.getBasicTableName() != null){
            tableName = userAccount.getBasicTableName();
            TextView tableNameTV = v.findViewById(R.id.tableNameTV);
            tableNameTV.setText("테이블 이름 : " + tableName + " (기본 테이블)");
            treeResisted = false;
            getSubjectListFromFB();
        }
        else{
            Toast.makeText(getContext(), "테이블을 선택해주세요", Toast.LENGTH_LONG).show();
        }
    }

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
                        /*
                        픽률 테이블 초기화 코드
                        for(Subject_ subject_ : subjectList){
                            Map<String, String> l = new HashMap<>();
                            for(Subject_ subject_1 : subjectList){
                                l.put(subject_1.getName(), "0");
                            }
                            Line line = new Line(l, subject_.getName());
                            db.collection("UsersTableInfo").document(line.getRoot()).set(line);
                        }*/

                        Log.e("###", "개수 : " + Integer.toString(subjectList.size()));
                        treeNodeList = new TreeNode[subjectList.size()];

                        //adj 초기화
                        adj = new ArrayList[subjectList.size()];
                        for(int i=0; i<subjectList.size(); i++)
                            adj[i] = new ArrayList<Integer>();

                        mappingSubjectList();

                        getTableFromFB();

                        Log.e("###", "Tree size test : "+treeView.getWidth());

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
                userAccount = documentSnapshot.toObject(UserAccount.class);
                tableNames = userAccount.getTableNames();
                tables = userAccount.getTables();
                for(int i = 0; i < tableNames.size(); i++){
                    if(tableNames.get(i).equals(tableName)){
                        tableLoc = i;
                        currTable = tables.get(i);

                        // 사용자 수강 정보로 테이블 정보 업데이트 후 시각화 시작.
                        String[] rootData = currTable.getRoot().split("\\.");
                        currTable.setRoot(rootData[0] + getSubjectInfo(rootData[0]));

                        for(String currSubject : currTable.getTable().keySet()){
                            for(String nextSubject : currTable.getTable().get(currSubject).keySet()){
                                currTable.getTable().get(currSubject).put(nextSubject, getSubjectInfo(nextSubject));
                            }
                        }
                        userAccount.getTables().set(tableLoc, currTable);
                        db.collection("user").document(mAuth.getUid()).set(userAccount);
                        //////////////////////////////////////////////////
                        changeToAdj(currTable);
                        break;
                    }
                }
                if(currTable == null){
                    Toast.makeText(getContext(), "커리큘럼 시작과목을 선택해주세요.", Toast.LENGTH_SHORT).show();
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
                            treeResisted = true;
                            String choosedSubjectName = searchSubjectList.get(pos).getName();
                            Log.e("###", choosedSubjectName + " 선택 됨");

                            Map<String, Map<String, String>> tb = new HashMap<>();
                            for(Subject_ subject_ : subjectList){
                                Map<String, String> line = new HashMap<>();
                                tb.put(subject_.getName(), line);
                            }
                            Table table;
                            String rootString = choosedSubjectName + getSubjectInfo(choosedSubjectName);
                            table = new Table(tb, rootString);

                            userAccount.getTableNames().add(tableName);
                            userAccount.getTables().add(table);

                            Log.e("###", "트리 추가 " + tableName);
                            db.collection("user").document(mAuth.getUid()).set(userAccount);

                            //테이블 만들어서 넣어줬으니까 여기서부터 다시 시작

                            getTableFromFB();

                            subjectChoiceBottomSheetDialog.dismiss();
                        }

                    });

                    subjectChoiceBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Log.e("###", "dismiss" + treeResisted.toString());
                            if(treeResisted == false){
                                Toast.makeText(getContext(), "커리큘럼 생성을 취소합니다.", Toast.LENGTH_SHORT).show();
                                Curl_List_Fragment curl_list_fragment = new Curl_List_Fragment();
                                ft.replace(R.id.main_frame, curl_list_fragment);
                                ft.commit();
                            }
                        }
                    });
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
            final TreeNode newChild = new TreeNode(nextSubjectName + currTable.getTable().get(currSubjectName).get(nextSubjectName));
            treeNodeList[nextMappingPos] = newChild;

            currNode.addChild(newChild);
            makeTreeByAdj(newChild);
        }
    }

    // SubjectList로 리사이클러뷰 만들기
    public void makeRVBySubjectList(){
        //과목 리스트 볼 수 있는 BottomSheetDialog
        subjectChoiceBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.NewDialog);
        subjectChoiceBottomSheetDialog.setContentView(R.layout.dialog_subjectchoicebottomsheet);
        subjectChoiceBottomSheetDialog.setCanceledOnTouchOutside(true);

        noSearching();

        searchET = subjectChoiceBottomSheetDialog.findViewById(R.id.searchET);
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case IME_ACTION_SEARCH :
                        inputMethodManager.hideSoftInputFromWindow(searchET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        searching();
                        break;
                }
                return true;
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    cancelIV.setVisibility(View.INVISIBLE);
                    noSearching();
                }
                else{
                    cancelIV.setVisibility(View.VISIBLE);
                    searching();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cancelIV = subjectChoiceBottomSheetDialog.findViewById(R.id.cancelIV);
        cancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchET.setText("");
            }
        });
    }

    public void searching(){
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
                if(currTable != null){
                    String choosedSubjectName = searchSubjectList.get(pos).getName();
                    Boolean isSubjectOverlapped = false;

                    Log.e("###", choosedSubjectName + " 선택 됨..");

                    // [ 장준승 ] 노드 중복 방지
                    for(TreeNode tn : treeNodeList)
                    {
                        if(tn != null && choosedSubjectName.equals(tn.getData().toString().split("\\.")[0]))
                        {
                            Toast.makeText(getContext(), "이미 선택된 과목입니다",Toast.LENGTH_LONG).show();
                            isSubjectOverlapped = true;
                            break;
                        }
                    }

                    if(!isSubjectOverlapped) {
                        for (TreeNode tn : treeNodeList) {
                            if (tn != null && curData.equals(tn.getData().toString().split("\\.")[0])) {
                                //UserAccount 정보 업데이트
                                currTable.getTable().get(curData).put(choosedSubjectName, getSubjectInfo(choosedSubjectName));
                                userAccount.getTables().set(tableLoc, currTable);
                                db.collection("user").document(mAuth.getUid()).set(userAccount);

                                int mappingPos = m.get(choosedSubjectName);

                                //[장준승] 위의 규칙에 맞게 SubjectName을 변환합니다.
                                String convertedSubjectName = choosedSubjectName + getSubjectInfo(choosedSubjectName);
                                final TreeNode newChild = new TreeNode(convertedSubjectName);

                                //[장준승] 화면 사이즈 node 개수에 비례하여 변화
                                updateDisplaySize();
                                Log.e("###", "Current displaySize : " + displaySize);

                                adj[m.get(curData)].add(mappingPos);
                                treeNodeList[mappingPos] = newChild;
                                tn.addChild(newChild);
                                break;
                            }
                        }
                    }
                }
                else {
                    treeResisted = true;
                    String choosedSubjectName = searchSubjectList.get(pos).getName();
                    Log.e("###", choosedSubjectName + " 선택 됨");

                    Map<String, Map<String, String>> tb = new HashMap<>();
                    for (Subject_ subject_ : subjectList) {
                        Map<String, String> line = new HashMap<>();
                        tb.put(subject_.getName(), line);
                    }
                    Table table = new Table(tb, choosedSubjectName + getSubjectInfo(choosedSubjectName));

                    userAccount.getTableNames().add(tableName);
                    userAccount.getTables().add(table);
                    db.collection("user").document(mAuth.getUid()).set(userAccount);

                    //테이블 만들어서 넣어줬으니까 여기서부터 다시 시작
                    getTableFromFB();
                }

                subjectChoiceBottomSheetDialog.dismiss();
            }
        });
    }

    public void noSearching(){
        subjectAdapter = new SubjectAdapter(subjectList);
        subjectRecyclerView = subjectChoiceBottomSheetDialog.findViewById(R.id.subjectChoiceRecyclerView);
        subjectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        subjectRecyclerView.setAdapter(subjectAdapter);

        //RecyclerView에서 선택된 아이템에 접근
        subjectAdapter.setOnItemListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                String choosedSubjectName = subjectList.get(pos).getName();
                Boolean isSubjectOverlapped = false;

                Log.e("###", choosedSubjectName + " 선택 됨!");

                // [ 장준승 ] 노드 중복 방지
                for(TreeNode tn : treeNodeList)
                {
                    if(tn != null && choosedSubjectName.equals(tn.getData().toString().split("\\.")[0]))
                    {
                        Toast.makeText(getContext(), "이미 선택된 과목입니다",Toast.LENGTH_LONG).show();
                        isSubjectOverlapped = true;
                        break;
                    }
                }

                // choosedSubjectName이 중복되지 않으면 과목을 추가합니다.
                if(!isSubjectOverlapped)
                {
                    for(TreeNode tn : treeNodeList)
                    {
                        if(tn != null && curData.equals(tn.getData().toString().split("\\.")[0]))
                        {
                            //DBG
                            //사용자 수강 정보 이용해서 노드 추가하기
                            currTable.getTable().get(curData).put(choosedSubjectName, getSubjectInfo(choosedSubjectName));
                            userAccount.getTables().set(tableLoc, currTable);
                            db.collection("user").document(mAuth.getUid()).set(userAccount);

                            Log.e("###", "현재 선택된 DB정보 : "+db.toString());

                            int mappingPos = m.get(choosedSubjectName);

                            //[장준승] 위의 규칙에 맞게 SubjectName을 변환합니다.
                            String convertedSubjectName = choosedSubjectName + getSubjectInfo(choosedSubjectName);
                            final TreeNode newChild = new TreeNode(convertedSubjectName);

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
            }
        });
    }

    // LL4(과목 변경) 눌렀을 때 띄울 BottomSheetDialog
    public void makeRVBySubjectList_LL4(){
        //과목 리스트 볼 수 있는 BottomSheetDialog
        subjectChoiceBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.NewDialog);
        subjectChoiceBottomSheetDialog.setContentView(R.layout.dialog_subjectchoicebottomsheet);
        subjectChoiceBottomSheetDialog.setCanceledOnTouchOutside(true);

        noSearching_LL4();

        searchET = subjectChoiceBottomSheetDialog.findViewById(R.id.searchET);
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case IME_ACTION_SEARCH :
                        inputMethodManager.hideSoftInputFromWindow(searchET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        searching_LL4();
                        break;
                }
                return true;
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    cancelIV.setVisibility(View.INVISIBLE);
                    noSearching_LL4();
                }
                else{
                    cancelIV.setVisibility(View.VISIBLE);
                    searching_LL4();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cancelIV = subjectChoiceBottomSheetDialog.findViewById(R.id.cancelIV);
        cancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchET.setText("");
            }
        });
    }

    public void searching_LL4(){
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
                String choosedSubjectName = searchSubjectList.get(pos).getName();
                Boolean isSubjectOverlapped = false;

                // [ 장준승 ] 노드 중복 방지
                for(TreeNode tn : treeNodeList)
                {
                    if(tn != null && choosedSubjectName.equals(tn.getData().toString().split("\\.")[0]))
                    {
                        Toast.makeText(getContext(), "이미 선택된 과목입니다",Toast.LENGTH_LONG).show();
                        isSubjectOverlapped = true;
                        break;
                    }
                }

                if(!isSubjectOverlapped) {
                    for (TreeNode tn : treeNodeList) {
                        if (tn != null && curData.equals(tn.getData().toString().split("\\.")[0])) {
                            String subjectInfo = userAccount.getTakenSubject().get(choosedSubjectName);

                            TreeNode parent = treeNodeList[m.get(curData)].getParent();
                            if(parent == null){ //루트 노드인 경우
                                currTable.setRoot(choosedSubjectName + subjectInfo);
                                for(String inRoot : currTable.getTable().get(curData).keySet()){
                                    subjectInfo = userAccount.getTakenSubject().get(inRoot);
                                    Map<String, String> tempMap = new HashMap<>();
                                    tempMap.put(inRoot, subjectInfo);
                                    currTable.getTable().put(choosedSubjectName, tempMap);
                                }
                                currTable.getTable().get(curData).clear();
                            }
                            else{ //루트 노드가 아닌 경우
                                String parentName = parent.getData().toString().split("\\.")[0];
                                currTable.getTable().get(parentName).remove(curData);
                                currTable.getTable().get(parentName).put(choosedSubjectName, subjectInfo);

                                Map<String, String> tempMap = new HashMap<>();
                                for(String inRoot : currTable.getTable().get(curData).keySet()){
                                    Log.e("###", "inRoot" + inRoot);
                                    subjectInfo = userAccount.getTakenSubject().get(inRoot);
                                    tempMap.put(inRoot, subjectInfo);
                                }
                                currTable.getTable().put(choosedSubjectName, tempMap);
                                currTable.getTable().get(curData).clear();
                            }
                            userAccount.getTables().set(tableLoc, currTable);
                            db.collection("user").document(mAuth.getUid()).set(userAccount);
                            break;
                        }
                    }
                }

                // [장준승] 새로고침을 위해 프래그먼트 리로딩
                FragmentTransaction tempft = fm.beginTransaction();
                Log.e("###", "For refresh : "+refreshFragment.toString());
                tempft.detach(refreshFragment);
                //tempft.attach(refreshFragment);

                Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                bundle.putString("tableName", tableName);//번들에 넘길 값 저장

                Fragment2 refreshFragment2 = new Fragment2();
                refreshFragment2.setArguments(bundle);//번들을 프래그먼트2로 보낼 준비

                tempft.replace(R.id.main_frame, refreshFragment2);
                tempft.commitAllowingStateLoss();

                subjectChoiceBottomSheetDialog.dismiss();
            }
        });
    }

    public void noSearching_LL4(){
        subjectAdapter = new SubjectAdapter(subjectList);
        subjectRecyclerView = subjectChoiceBottomSheetDialog.findViewById(R.id.subjectChoiceRecyclerView);
        subjectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        subjectRecyclerView.setAdapter(subjectAdapter);

        //RecyclerView에서 선택된 아이템에 접근
        subjectAdapter.setOnItemListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                String choosedSubjectName = subjectList.get(pos).getName();
                Boolean isSubjectOverlapped = false;

                Log.e("###", choosedSubjectName + " 선택 됨#########");

                // [ 장준승 ] 노드 중복 방지
                for(TreeNode tn : treeNodeList)
                {
                    if(tn != null && choosedSubjectName.equals(tn.getData().toString().split("\\.")[0]))
                    {
                        Toast.makeText(getContext(), "이미 선택된 과목입니다",Toast.LENGTH_LONG).show();
                        isSubjectOverlapped = true;
                        break;
                    }
                }

                // choosedSubjectName이 중복되지 않으면 과목을 추가합니다.
                if(!isSubjectOverlapped)
                {
                    for(TreeNode tn : treeNodeList)
                    {
                        if(tn != null && curData.equals(tn.getData().toString().split("\\.")[0]))
                        {
                            String subjectInfo = userAccount.getTakenSubject().get(choosedSubjectName);

                            TreeNode parent = treeNodeList[m.get(curData)].getParent();
                            if(parent == null){ //루트 노드인 경우
                                currTable.setRoot(choosedSubjectName + subjectInfo);
                                for(String inRoot : currTable.getTable().get(curData).keySet()){
                                    subjectInfo = userAccount.getTakenSubject().get(inRoot);
                                    Map<String, String> tempMap = new HashMap<>();
                                    tempMap.put(inRoot, subjectInfo);
                                    currTable.getTable().put(choosedSubjectName, tempMap);
                                }
                                currTable.getTable().get(curData).clear();
                            }
                            else{ //루트 노드가 아닌 경우
                                String parentName = parent.getData().toString().split("\\.")[0];
                                currTable.getTable().get(parentName).remove(curData);
                                currTable.getTable().get(parentName).put(choosedSubjectName, subjectInfo);

                                Map<String, String> tempMap = new HashMap<>();
                                for(String inRoot : currTable.getTable().get(curData).keySet()){
                                    Log.e("###", "inRoot" + inRoot);
                                    subjectInfo = userAccount.getTakenSubject().get(inRoot);
                                    tempMap.put(inRoot, subjectInfo);
                                }
                                currTable.getTable().put(choosedSubjectName, tempMap);
                                currTable.getTable().get(curData).clear();
                            }
                            userAccount.getTables().set(tableLoc, currTable);
                            db.collection("user").document(mAuth.getUid()).set(userAccount);
                            break;
                        }
                    }

                    // [장준승] 새로고침을 위해 프래그먼트 리로딩
                    FragmentTransaction tempft = fm.beginTransaction();
                    Log.e("###", "For refresh : "+refreshFragment.toString());
                    tempft.detach(refreshFragment);
                    //tempft.attach(refreshFragment);

                    Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                    bundle.putString("tableName", tableName);//번들에 넘길 값 저장

                    Fragment2 refreshFragment2 = new Fragment2();
                    refreshFragment2.setArguments(bundle);//번들을 프래그먼트2로 보낼 준비

                    tempft.replace(R.id.main_frame, refreshFragment2);
                    tempft.commitAllowingStateLoss();

                    subjectChoiceBottomSheetDialog.dismiss();
                }
            }
        });
    }

    // DB 바탕으로 트리 노드 삭제
    public void deleteTreeFromDB(String currNode){
        int currNodeValue = m.get(currNode);
        /*
         [ 장준승 ]
         삭제 과정이 재귀적인 과정임에 따라, 삭제되는 과정에서 한 리스트에 대해
         중복 접근이 발생하면 ConcurrentModificationException가 발생합니다.
         따라서 For-each문이 아닌 For문을 이용하여 삭제를 진행하여 줍니다.
         */

        for(int i=0; i<adj[currNodeValue].size(); i++)
        {
            int nextNodeValue = adj[currNodeValue].get(i);

            String nextNode = subjectList.get(nextNodeValue).getName();
            deleteTreeFromDB(nextNode);

            Log.e("###", nextNode+"에 대한 삭제를 진행합니다.");
            TreeNode deleteTn = treeNodeList[nextNodeValue];
            String[] deleteTnData = deleteTn.getData().toString().split("\\.");
            Log.e("###", "삭제하려는 노드의 수강정보 : "+deleteTnData[2]);

            treeNodeList[nextNodeValue] = null;
            if(deleteTnData[2].equals("1"))
            {
                ChangeMatrixToServer(currNode, nextNode, true);
            }

            //UserAccount 정보 업데이트
            currTable.getTable().get(currNode).remove(nextNode);
            setSubjectInfo(nextNode, ".1학년 1학기.0");

            userAccount.getTables().set(tableLoc, currTable);
            db.collection("user").document(mAuth.getUid()).set(userAccount);
        }
        adj[currNodeValue].clear();
    }

    /*
    [ 장준승 ]
    ChangeMatrixToServer(String parentSubjectName, String currSubjectName, Boolean currentTaken)
    : 픽률 계산하는 내용을 isTaken 정보에 따라 업데이트를 진행해줍니다.

    parentSubjectName -> currSubjectName 으로 업데이트를 진행해줍니다.

    currentTaken이 True라면, 듣고 있는데 안듣는 경우를 의미합니다. ( 정보 제거 )
    currentTaken이 False라면, 안듣고 있는데 듣는 경우를 의미합니다. ( 정보 추가 )
     */
    public void ChangeMatrixToServer(String parentSubjectName, String currSubjectName, Boolean currentTaken)
    {
        if(currentTaken)
        {
            Log.e("###", parentSubjectName+" -> "+currSubjectName+" 으로 픽률 정보 제거를 진행합니다. ");
            //듣고 있는데 안 듣는 것으로 수정하는 경우
            // 수강 한다 -> 수강 안한다

            Onprogress(getActivity(),"로딩중...");
            docRef = db.collection("UsersTableInfo").document(parentSubjectName);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Line line = documentSnapshot.toObject(Line.class);

                    int currCount = Integer.parseInt(line.getLine().get(currSubjectName));
                    currCount -= 1;
                    int currParentCount = Integer.parseInt(line.getLine().get(parentSubjectName));
                    currParentCount -= 1;

                    line.getLine().put(currSubjectName, Integer.toString(currCount));
                    line.getLine().put(parentSubjectName, Integer.toString(currParentCount));

                    db.collection("UsersTableInfo").document(parentSubjectName).set(line);
                    progressOFF();
                }
            });

        }else{
            //안 듣고 있는데 듣는 것으로 수정하는 경우
            // 수강 한다 -> 수강 안한다

            Log.e("###", parentSubjectName+" -> "+currSubjectName+" 으로 픽률 정보 추가를 진행합니다. ");

            Onprogress(getActivity(),"로딩중...");
            docRef = db.collection("UsersTableInfo").document(parentSubjectName);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Line line = documentSnapshot.toObject(Line.class);

                    int currCount = Integer.parseInt(line.getLine().get(currSubjectName));
                    currCount += 1;
                    int currParentCount = Integer.parseInt(line.getLine().get(parentSubjectName));
                    currParentCount += 1;

                    line.getLine().put(currSubjectName, Integer.toString(currCount));
                    line.getLine().put(parentSubjectName, Integer.toString(currParentCount));

                    db.collection("UsersTableInfo").document(parentSubjectName).set(line);
                    progressOFF();
                }
            });
        }
    }

    public String getSubjectInfo(String subjectName){
        return takenSubject.get(subjectName);
    }

    public void setSubjectInfo(String subjectName, String info){
        takenSubject.put(subjectName, info);
        userAccount.setTakenSubject(takenSubject);
        db.collection("user").document(mAuth.getUid()).set(userAccount);
    }

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

        ft.replace(R.id.main_frame, new Fragment1()).commitAllowingStateLoss();
        //ft.remove(Curl_List_Fragment.this).commit();
        fm.popBackStack();
    }

    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).setBackPressedlistener(null);
    }

    void Onprogress(Activity activity, String message){

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {

        } else {
            //이 밑부분 떼서 작업전에 AppcompatDialog 변수선언해주고 progressDialog 먼저 만들고
            // 작업시작할때 Onprogress 넣어주고 작업끝나면 밑에 progressOFF 넣어주면됩니다.
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();

        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}