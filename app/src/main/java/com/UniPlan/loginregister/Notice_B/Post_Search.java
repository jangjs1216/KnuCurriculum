package com.UniPlan.loginregister.Notice_B;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.UniPlan.loginregister.adapters.PostAdapter;
import com.UniPlan.loginregister.login.FirebaseID;
import com.UniPlan.loginregister.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Post_Search extends AppCompatActivity {
    private EditText et_post_search;
    private Intent intent;
    private String forum_sort;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private List<Post> mDatas;
    private String keyword;
    private PostAdapter mAdapter;
    private RecyclerView mPostRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_search);

        intent = getIntent();
        mPostRecyclerView = findViewById(R.id.recyclerview_search);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mPostRecyclerView.setLayoutManager(linearLayoutManager);
        mDatas = new ArrayList<>();//
        forum_sort = intent.getStringExtra("게시판");
        Log.e("검색창",forum_sort);
        et_post_search = findViewById(R.id.et_post_search);
        et_post_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId){
                    case EditorInfo.IME_ACTION_SEARCH:
                        mDatas.clear();
                        keyword = et_post_search.getText().toString();
                        Log.e("search",keyword);
                        mStore.collection(forum_sort)//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)//시간정렬순으로
                                .addSnapshotListener(
                                        new EventListener<QuerySnapshot>() {
                                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                if (queryDocumentSnapshots != null) {
                                                    mDatas.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                                        Post post = snap.toObject(Post.class);
                                                        if(post.getContents().contains(keyword)||post.getTitle().contains(keyword)) {
                                                            mDatas.add(post);//여기까지가 게시글에 해당하는 데이터 적용
                                                        }
                                                    }
                                                }
                                                else {
                                                    Toast.makeText(getApplicationContext(),"일치하는 게시글이 없습니다.",Toast.LENGTH_LONG).show();
                                                }
                                                mAdapter = new PostAdapter(Post_Search.this, mDatas);
                                                if(mAdapter.getItemCount()==0){
                                                    Toast.makeText(getApplicationContext(),"일치하는 게시글이 없습니다.",Toast.LENGTH_LONG).show();
                                                }
                                                mPostRecyclerView.setAdapter(mAdapter);
                                            }
                                        });
                }
                return true;
            }
        });

    }
}