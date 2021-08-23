package com.example.loginregister.Notice_B;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.loginregister.MainActivity;
import com.example.loginregister.curiList.Curl_List_Fragment;
import com.example.loginregister.login.FirebaseID;
import com.example.loginregister.R;
import com.example.loginregister.adapters.PostAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
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

public class NoticeBoard extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private Toolbar toolbar;
    private RecyclerView mPostRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private PostAdapter mAdapter;
    private List<Post> mDatas;
    private String forum_sort;
    private TextView tv_forum_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        toolbar =(Toolbar)findViewById(R.id.tb_notice_board);
        setSupportActionBar(toolbar);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // 게시판 컬렉션 지정
        Intent intent=getIntent();
        int forum_num=intent.getExtras().getInt("게시판");
        forum_sort="Post"+forum_num;
        tv_forum_title = findViewById(R.id.tv_notice_board_title);
        tv_forum_title.setText(forum_sort);

        //          기본 날짜순 정렬
        updateDatas();


        mPostRecyclerView = findViewById(R.id.recyclerview);
        swipeRefreshLayout=findViewById(R.id.refresh_board);



//        String[] items = getResources().getStringArray(R.array.sort_spinner_array);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                this, android.R.layout.simple_spinner_item, items);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
//
//        sort_spinner.setAdapter(adapter);
//        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (items[position].equals("좋아요순")) {
//                    Log.e("###","좋아요순");
//                    sortDatas();
//                }
//                if(items[position].equals("최신순")) {
//                    Log.e("###","최신순");
//                    updateDatas();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                updateDatas();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

//          툴바레이아웃설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_notice_board,menu);
        return true;
    }
//          툴바 작동설정
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.action_btn_search:
                intent=new Intent(this, Post_Search.class);
                intent.putExtra("게시판",forum_sort);
                startActivity(intent);
                break;
            case R.id.action_btn_create:
                Log.e("notice","글쓰기 선택");
                intent=new Intent(this, Post_write.class);
                intent.putExtra("게시판",forum_sort);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void updateDatas() {
        mDatas = new ArrayList<>();//
        mStore.collection(forum_sort)//리사이클러뷰에 띄울 파이어베이스 테이블 경로
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
                                mAdapter = new PostAdapter(NoticeBoard.this, mDatas, forum_sort);
                                mPostRecyclerView.setAdapter(mAdapter);
                            }
                        });
    }

    public void sortDatas() {
        mDatas = new ArrayList<>();//
        mStore.collection(forum_sort)//리사이클러뷰에 띄울 파이어베이스 테이블 경로
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
                                mAdapter = new PostAdapter(NoticeBoard.this, mDatas, forum_sort);//mDatas라는 생성자를 넣어줌
                                mPostRecyclerView.setAdapter(mAdapter);
                            }
                        });
    }





}
