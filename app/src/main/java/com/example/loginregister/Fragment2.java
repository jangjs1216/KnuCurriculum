package com.example.loginregister;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.annotations.NotNull;
import com.otaliastudios.zoom.ZoomLayout;

import java.util.ArrayList;
import java.util.HashMap;

import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

public class Fragment2 extends Fragment {
    String curData;
    private View v;
    private Toolbar toolbar;
    private final static String TAG ="Frag2";
    TreeNode rootNode;
    int nodeCount = 0;

    TreeNode[] treeNodeList;
    String[] subjectName;
    ZoomLayout zoomLayout;
    BottomSheetDialog bottomSheetDialog;

    //과목 이름 매핑
    HashMap<String, Integer> m;
    boolean adj[][] = new boolean[4][4];

    /*
    [20210807] 장준승 Fragment2 시각화 구현
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_2, container, false);
        //툴바 시작
        toolbar = (androidx.appcompat.widget.Toolbar)v.findViewById(R.id.tb_frag3);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        //툴바끝

        zoomLayout = v.findViewById(R.id.layout_zoom);
        TreeView treeView = new TreeView(container.getContext()){
            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent event, float distanceX, float distanceY) {
                return false;
            }
        };
        treeView.setLevelSeparation(50);
        treeView.setLineColor(Color.BLACK);
        treeView.setLineThickness(5);

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
                        //노드 클릭시 추가 구현
                        Log.e("###", viewHolder.mTextView.getText().toString());
                        curData = viewHolder.mTextView.getText().toString();

                        bottomSheetDialog = new BottomSheetDialog(getActivity());
                        bottomSheetDialog.setContentView(R.layout.dialog_bottomsheet);
                        bottomSheetDialog.show();

                        LinearLayout LL1 = bottomSheetDialog.findViewById(R.id.LL1);
                        LinearLayout LL2 = bottomSheetDialog.findViewById(R.id.LL2);
                        LinearLayout LL3 = bottomSheetDialog.findViewById(R.id.LL3);
                        LL1.setOnClickListener(bottomSheetOnClickListener);
                        LL2.setOnClickListener(bottomSheetOnClickListener);
                        LL3.setOnClickListener(bottomSheetOnClickListener);
                    }
                });
            }
        };
        treeView.setAdapter(adapter);

        treeNodeList = new TreeNode[10];

        /* 최정인 DB 인접리스트를 통한 트리 표현 */
        subjectName = new String[4];
        m = new HashMap<String, Integer>();

        subjectName[0] = "C프로그래밍";
        subjectName[1] = "C++프로그래밍";
        subjectName[2] = "JAVA프로그래밍";
        subjectName[3] = "PYTHON프로그래밍";

        for(int i=0; i<4; i++)
            m.put(subjectName[i], m.size());

        rootNode = new TreeNode(subjectName[0]);
        treeNodeList[0] = rootNode;

        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                adj[i][j] = false;
            }
        }

        //DB에서 받아와서 트리 구현

        /* rootNode랑 인접리스트(fromDB) 넣어주면 트리 시각화 */
//        makeTreeFromDB(rootNode, adj);

        adapter.setRootNode(rootNode);

        treeView.setMinimumWidth(3000);
        treeView.setMinimumHeight(3000);

        zoomLayout.addView(treeView);
        // Inflate the layout for this fragment
        return v;
    }

    /* [최정인] 노드 선택시 나오는 BottomSheetDialog 클릭 리스너 */
    View.OnClickListener bottomSheetOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.LL1:

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    //Log.e("###", "alertDialog 접근");
                    builder.setTitle("과목을 선택해주세요");

                    builder.setItems(subjectName, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int pos)
                        {
                            Toast.makeText(v.getContext(),subjectName[pos],Toast.LENGTH_LONG).show();
                            for(TreeNode tn : treeNodeList)
                            {
                                if(tn != null && curData == tn.getData().toString())
                                {
                                    int mappingPos = m.get(subjectName[pos]);
                                    //Log.e("###", "현재 노드는 "+subjectName[pos]+ "이고, 매핑된 번호는 "+mappingPos);
                                    final TreeNode newChild = new TreeNode(subjectName[pos]);

                                    adj[m.get(curData)][mappingPos] = true;
                                    //Log.e("###", m.get(curData) + "와" + mappingPos + "연결");
                                    treeNodeList[mappingPos] = newChild;
                                    tn.addChild(newChild);
                                    break;
                                }
                            }
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    bottomSheetDialog.dismiss();
                    break;
                case R.id.LL2:
                    deleteTreeFromDB(curData);
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.LL3:
                    break;
            }
        }
    };

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
        for(int i = 0; i < 4; i++){
            if(adj[currNodeIndex][i] == true){
                //Log.e("###", currNodeIndex + "와" + i + "접근");
                String nextNode = subjectName[i];
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
        Log.e(TAG,"sex");
    }
//툴바 기능설정
    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_add:
                //추가 일단넣어둠
                break;
            case R.id.action_btn_modify:
                //수정할때;
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}