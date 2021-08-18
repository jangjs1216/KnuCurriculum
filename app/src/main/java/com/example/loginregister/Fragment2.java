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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.loginregister.adapters.SubjectAdapter;
import com.example.loginregister.curiList.Curl_List_Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.otaliastudios.zoom.ZoomLayout;

import java.util.ArrayList;
import java.util.HashMap;

import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

public class Fragment2 extends Fragment {
    FirebaseFirestore db;
    String curData;
    private View v;
    private Toolbar toolbar;
    private final static String TAG ="Frag2";
    TreeNode rootNode;
    int nodeCount = 0;
    private FragmentManager fm;
    private FragmentTransaction ft;

    TreeNode[] treeNodeList;
    ZoomLayout zoomLayout;
    ArrayList<Subject_> subjectList;
    BottomSheetDialog nodeChoiceBottomSheetDialog, subjectChoiceBottomSheetDialog;
    RecyclerView subjectRecyclerView;
    SubjectAdapter subjectAdapter;
    TreeView treeView;

    //크기 유동적 변화 구현
    private int displaySize = 500;

    //과목 이름 매핑
    HashMap<String, Integer> m;
    boolean adj[][];

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

        /*
            BottomSheetDialog 선언
         */


        BaseTreeAdapter adapter = new BaseTreeAdapter<ViewHolder>(container.getContext(), R.layout.node) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(View view) {
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                viewHolder.mTextView.setText(data.toString());
                viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("###", viewHolder.mTextView.getText().toString());
                        curData = viewHolder.mTextView.getText().toString();

                        //노드선택 BottomSheetDialog 띄우기
                        nodeChoiceBottomSheetDialog = new BottomSheetDialog(getActivity());
                        nodeChoiceBottomSheetDialog.setContentView(R.layout.dialog_nodechoicebottomsheet);
                        nodeChoiceBottomSheetDialog.show();

                        LinearLayout LL1 = nodeChoiceBottomSheetDialog.findViewById(R.id.LL1);
                        LinearLayout LL2 = nodeChoiceBottomSheetDialog.findViewById(R.id.LL2);
                        LinearLayout LL3 = nodeChoiceBottomSheetDialog.findViewById(R.id.LL3);
                        LL1.setOnClickListener(nodeChoiceBottomSheetOnClickListener);
                        LL2.setOnClickListener(nodeChoiceBottomSheetOnClickListener);
                        LL3.setOnClickListener(nodeChoiceBottomSheetOnClickListener);
                    }
                });
            }
        };
        treeView.setAdapter(adapter);
        /* 서버에서 받아 올 과목 정보 */
        db = FirebaseFirestore.getInstance();
        subjectList = new ArrayList<>();
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
                        treeNodeList = new TreeNode[subjectList.size()];

                        /* DB에서 받아온 과목들 매핑 */
                        m = new HashMap<String, Integer>();
                        adj = new boolean[subjectList.size()][subjectList.size()];
                        for(int i=0; i<subjectList.size(); i++){
                            m.put(subjectList.get(i).getName(), m.size());
                        }

                        //rootNode 설정
                        rootNode = new TreeNode(subjectList.get(0).getName());
                        treeNodeList[0] = rootNode;

                        //adj 초기화
                        for(int i=0;i<subjectList.size();i++){
                            for(int j=0;j<subjectList.size();j++){
                                adj[i][j] = false;
                            }
                        }

                        //DB에서 받아와서 트리 구현

                        /* rootNode랑 인접리스트(fromDB) 넣어주면 트리 시각화 */
//        makeTreeFromDB(rootNode, adj);

                        adapter.setRootNode(rootNode);

                        zoomLayout.addView(treeView);
                        // Inflate the layout for this fragment

                        treeView.setMinimumWidth(displaySize);
                        treeView.setMinimumHeight(displaySize);
                    }
                });


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

                    //과목 리스트 볼 수 있는 BottomSheetDialog
                    subjectChoiceBottomSheetDialog = new BottomSheetDialog(getActivity());
                    subjectChoiceBottomSheetDialog.setContentView(R.layout.dialog_subjectchoicebottomsheet);
                    subjectChoiceBottomSheetDialog.show();

                    subjectAdapter = new SubjectAdapter(subjectList);
                    subjectRecyclerView = (RecyclerView) subjectChoiceBottomSheetDialog.findViewById(R.id.subjectChoiceRecyclerView);
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
                                if(tn != null && curData == tn.getData().toString())
                                {
                                    int mappingPos = m.get(choosedSubjectName);
                                    final TreeNode newChild = new TreeNode(choosedSubjectName);

                                    //[장준승] 화면 사이즈 node 개수에 비례하여 변화
                                    updateDisplaySize();
                                    Log.e("###", "Current displaySize : "+displaySize);

                                    adj[m.get(curData)][mappingPos] = true;
                                    treeNodeList[mappingPos] = newChild;
                                    tn.addChild(newChild);
                                    break;
                                }
                            }

                            subjectChoiceBottomSheetDialog.dismiss();
                        }
                    });
                    break;

                case R.id.LL2:
                    deleteTreeFromDB(curData);
                    updateDisplaySize();
                    nodeChoiceBottomSheetDialog.dismiss();
                    break;

                case R.id.LL3:
                    Intent intent=new Intent(getActivity(), SubjectInfoActivity.class);
                    intent.putExtra("subjectName", curData);
                    startActivity(intent);
                    nodeChoiceBottomSheetDialog.dismiss();
                    break;
            }
        }
    };

    public void updateSubjectlist(){
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
    }

    /* [최정인] DB로 얻은 인접리스트로 트리 시각화 */
//    public void makeTreeFromDB(TreeNode currNode, boolean adj[][]){
//        int currNodeIndex = Integer.parseInt(currNode.getData().toString().substring(5));
//
//        for(int i = 0; i < adj.length; i++){
//            if(adj[currNodeIndex][i] == true){
//                final TreeNode newChild = new TreeNode(getNodeText());
//                treeNodeList[i] = newChild;
//                currNode.addChild(newChild);
//                makeTreeFromDB(newChild, adj);
//            }
//        }
//    }

    /* [장준승] DB 바탕으로 트리 노드 삭제 */
    public void deleteTreeFromDB(String currNode){
        int currNodeIndex = m.get(currNode);

        //Log.e("###", currNodeIndex + "삭제요청");
        for(int i = 0; i < subjectList.size(); i++){
            if(adj[currNodeIndex][i] == true){
                //Log.e("###", currNodeIndex + "와" + i + "접근");
                String nextNode = subjectList.get(i).getName();
                deleteTreeFromDB(nextNode);
                adj[currNodeIndex][i] = false;
            }
        }
        treeNodeList[currNodeIndex].getParent().removeChild(treeNodeList[currNodeIndex]);
    }

    private String getNodeText() {
        return "Node " + nodeCount++;
    }
//툴바 레이아웃설정
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_frag2,menu);
    }
//툴바 기능설정
    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_btn_add:
                ft.replace(R.id.main_frame, new Curl_List_Fragment());
                ft.addToBackStack(null);
                ft.commit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateDisplaySize()
    {
        int displaySize = rootNode.getNodeCount() * 300 + 500;
        treeView.setMinimumWidth(displaySize);
        treeView.setMinimumHeight(displaySize);
        zoomLayout.moveTo((float)1.0, 0, 0, false);
        zoomLayout.zoomBy((float)1.0, false);

        return;
    }
}