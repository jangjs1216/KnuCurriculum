package com.example.loginregister.Notice_B;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.FirebaseID;
import com.example.loginregister.R;
import com.example.loginregister.adapters.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoticeBoard extends AppCompatActivity implements View.OnClickListener, PostAdapter.EventListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private RecyclerView mPostRecyclerView;

    private PostAdapter mAdapter;
    private List<Post> mDatas;
    private Button btn_like;
    private String edit_s;//검색어 저장용도
    private EditText search_edit;//검색어 에딧
    private String post_n;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        search_edit=findViewById(R.id.edit_search);
        edit_s=search_edit.getText().toString();
        mPostRecyclerView = findViewById(R.id.recyclerview);
        findViewById(R.id.edit_button).setOnClickListener(this);
        findViewById(R.id.search_btn).setOnClickListener(this);


            getSupportActionBar().setTitle("Board");

        Log.d("확인","여기는 노티스:"+post_n);
    }

    //@Override
   // public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
   //     getMenuInflater().inflate(R.menu.search, menu);
   //     return true;
   // }

    @Override
    public boolean onOptionItemSelected(MenuItem item) {
        Log.d("확인", "선택하세요");
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        Intent intent=getIntent();
        post_n=intent.getStringExtra("post");
        super.onStart();
        mDatas = new ArrayList<>();//
        mStore.collection("Post")//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                .whereEqualTo("post_num",post_n)
                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)//시간정렬순으로
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots != null) {
                                    mDatas.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                        Map<String, Object> shot = snap.getData();
                                        String documentId = String.valueOf(shot.get(FirebaseID.documentId));
                                        String title = String.valueOf(shot.get(FirebaseID.title));
                                        String contents = String.valueOf(shot.get(FirebaseID.contents));
                                        String p_nickname = String.valueOf(shot.get(FirebaseID.nickname));
                                        String p_photo = String.valueOf(shot.get(FirebaseID.p_photo));
                                        String post_photo = String.valueOf(shot.get(FirebaseID.post_photo));
                                        String like= String.valueOf(shot.get(FirebaseID.like));
                                        //int like = FirebaseID.like;

                                        String post_id= String.valueOf(shot.get(FirebaseID.post_id));
                                        String writer_id= String.valueOf(shot.get(FirebaseID.writer_id));
                                        Post data = new Post(documentId, title, contents, p_nickname, p_photo, post_n,post_photo,post_id,writer_id,like);
                                        mDatas.add(data);//여기까지가 게시글에 해당하는 데이터 적용
                                    }
                                }
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
                //post_n=intent2.getStringExtra("post");
                intent2.putExtra("post",post_n);
                Log.d("확인","여기는 게시글:"+post_n);
                startActivity(intent2);
                break;
          //  case R.id.search_btn:
              //  Intent intent=new Intent(this,Search_Post_Activity.class);
             //  intent.putExtra("search",search_edit.getText().toString());//검색어와 관련된 것을 추리는 곳에 보냄
               // intent.putExtra("post",post_n);
              //  startActivity(intent);
               // Log.d("확인","여기는 포스트 코멘트:"+search_edit.getText().toString());
             //   break;
//            case R.id.btn_like:
//                Intent intent3=new Intent(this,Like_NoticeBoard.class);
//                startActivity(intent3);
//                break;
        }
    }

    @Override
    public void onItemClicked(int position) {
        Toast.makeText(this, "몇 번째" + position, Toast.LENGTH_SHORT).show();
        //startActivity(new Intent(this,Post_Comment.class));
    }


}
