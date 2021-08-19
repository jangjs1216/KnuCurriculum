package com.example.loginregister.Notice_B;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.loginregister.login.FirebaseID;
import com.example.loginregister.R;
import com.example.loginregister.adapters.PostAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NoticeBoard extends AppCompatActivity implements View.OnClickListener, PostAdapter.EventListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private RecyclerView mPostRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private PostAdapter mAdapter;
    private List<Post> mDatas;
    private String edit_s;//검색어 저장용도
    private EditText search_edit;//검색어 에딧

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner sort_spinner = (Spinner) findViewById(R.id.sort_spinner);
        search_edit = findViewById(R.id.edit_search);
        edit_s = search_edit.getText().toString();
        mPostRecyclerView = findViewById(R.id.recyclerview);
        findViewById(R.id.edit_button).setOnClickListener(this);
        findViewById(R.id.search_btn).setOnClickListener(this);
        swipeRefreshLayout=findViewById(R.id.refresh_board);

        String[] items = getResources().getStringArray(R.array.sort_spinner_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sort_spinner.setAdapter(adapter);
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (items[position].equals("좋아요순")) {
                    Log.e("###","좋아요순");
                    sortDatas();
                }
                if(items[position].equals("최신순")) {
                    Log.e("###","최신순");
                    updateDatas();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateDatas();
            }
        });
        getSupportActionBar().setTitle("Board");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                updateDatas();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    @Override
    public boolean onOptionItemSelected(MenuItem item) {
        Log.d("확인", "선택하세요");
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void updateDatas() {
        mDatas = new ArrayList<>();//
        mStore.collection("Post")//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)//시간정렬순으로
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots != null) {
                                    mDatas.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                        Post post = snap.toObject(Post.class);
                                        mDatas.add(post);//여기까지가 게시글에 해당하는 데이터 적용
                                    }
                                } else {
                                }
                                mAdapter = new PostAdapter(NoticeBoard.this, mDatas);
                                mPostRecyclerView.setAdapter(mAdapter);
                            }
                        });
    }

    public void sortDatas() {
        mDatas = new ArrayList<>();//
        mStore.collection("Post")//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)//시간정렬순으로
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots != null) {
                                    mDatas.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                        Post post = snap.toObject(Post.class);

                                        if(Integer.parseInt(post.getLike()) > 1) {
                                            mDatas.add(post);
                                        }
                                    }
                                } else {
                                }
                                Collections.sort(mDatas);
                                mAdapter = new PostAdapter(NoticeBoard.this, mDatas);//mDatas라는 생성자를 넣어줌
                                mPostRecyclerView.setAdapter(mAdapter);
                            }
                        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_button:
                Intent intent2=new Intent(this, Post_write.class);
                startActivity(intent2);
                break;
          //  case R.id.search_btn:
              //  Intent intent=new Intent(this,Search_Post_Activity.class);
             //  intent.putExtra("search",search_edit.getText().toString());//검색어와 관련된 것을 추리는 곳에 보냄
               // intent.putExtra("post",post_n);
              //  startActivity(intent);
               // Log.d("확인","여기는 포스트 코멘트:"+search_edit.getText().toString());
             //   break;
        }
    }

    @Override
    public void onItemClicked(int position) {
        Toast.makeText(this, "몇 번째" + position, Toast.LENGTH_SHORT).show();
        //startActivity(new Intent(this,Post_Comment.class));
    }


}
