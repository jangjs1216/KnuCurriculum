package com.example.loginregister;

import android.app.Activity;
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
import android.widget.TextView;

import com.google.firebase.database.annotations.NotNull;
import com.otaliastudios.zoom.ZoomLayout;

import java.util.ArrayList;

import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

public class Fragment2 extends Fragment {
    private FragmentActivity myContext;
    private View v;
    private Toolbar toolbar;
    private final static String TAG ="Frag2";
    TreeNode rootNode;
    int nodeCount = 0;
    ArrayList<TreeNode> treeNodeList;
    ZoomLayout zoomLayout;

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
                        String curData = viewHolder.mTextView.getText().toString();

                        FragmentManager fragmentManager = myContext.getSupportFragmentManager();
                        BottomSheetDialog bottomSheetDialog = BottomSheetDialog.getInstance();
                        bottomSheetDialog.show(fragmentManager, "bottomsheetdialog");

                        for(TreeNode tn : treeNodeList)
                        {
                            if(curData == tn.getData().toString())
                            {
                                final TreeNode newChild = new TreeNode(getNodeText());
                                treeNodeList.add(newChild);
                                tn.addChild(newChild);
                                break;
                            }
                        }
                        if("Node 0" == curData)
                        {
                            final TreeNode newChild = new TreeNode(getNodeText());
                            treeNodeList.add(newChild);
                            rootNode.addChild(newChild);
                        }
                    }
                });
            }
        };
        treeView.setAdapter(adapter);

        treeNodeList = new ArrayList<>();

        rootNode = new TreeNode(getNodeText());
        treeNodeList.add(rootNode);

        /* 최정인 DB 인접리스트를 통한 트리 표현 */
        boolean adj[][] = new boolean[4][4];
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                adj[i][j] = false;
            }
        }
        adj[0][1] = adj[0][2] = adj[2][3] = true;

        /* rootNode랑 인접리스트(fromDB) 넣어주면 트리 시각화 */
        makeTreeFromDB(rootNode, adj);

        adapter.setRootNode(rootNode);

        treeView.setMinimumWidth(3000);
        treeView.setMinimumHeight(3000);

        zoomLayout.addView(treeView);
        // Inflate the layout for this fragment
        return v;
    }

    public void makeTreeFromDB(TreeNode currNode, boolean adj[][]){
        int currNodeIndex = Integer.parseInt(currNode.getData().toString().substring(5));

        for(int i = 0; i < adj.length; i++){
            if(adj[currNodeIndex][i] == true){
                final TreeNode newChild = new TreeNode(getNodeText());
                treeNodeList.add(newChild);
                currNode.addChild(newChild);
                makeTreeFromDB(newChild, adj);
            }
        }
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

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }
}