package com.example.loginregister;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

public class Fragment2 extends Fragment {
    TreeNode rootNode;
    int nodeCount = 0;
    ArrayList<TreeNode> treeNodeList;

    /*
    [20210807] 장준승 Fragment2 시각화 구현
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_2, container, false);

        TreeView treeView = v.findViewById(R.id.treeview);
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

        TreeNode node1 = new TreeNode(getNodeText());
        rootNode.addChild(node1);
        treeNodeList.add(node1);

        adapter.setRootNode(rootNode);

        // Inflate the layout for this fragment
        return v;
    }

    private String getNodeText() {
        return "Node " + nodeCount++;
    }
}