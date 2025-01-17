package com.UniPlan.loginregister.Notice_B;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.UniPlan.loginregister.adapters.PostAdapter;
import com.UniPlan.loginregister.login.FirebaseID;
import com.UniPlan.loginregister.login.UserAccount;
import com.UniPlan.loginregister.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoticeBoard extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    int forum_num;
    private Toolbar toolbar;
    private RecyclerView mPostRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private PostAdapter mAdapter;
    private List<Post> mDatas;
    private String forum_sort, forum_name;
    private TextView tv_forum_title;
    private TextView tv_notice_board_title;
    private ArrayList<String> preLiked;
    private ExtendedFloatingActionButton likesort_btn;
    private boolean likeSort_TF=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        tv_notice_board_title=findViewById(R.id.tv_notice_board_title);
        likesort_btn=findViewById(R.id.like_sort);
        toolbar =(Toolbar)findViewById(R.id.tb_notice_board);
        setSupportActionBar(toolbar);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // 게시판 컬렉션 지정
        Intent intent=getIntent();
        forum_num=intent.getExtras().getInt("게시판");
        forum_name=intent.getExtras().getString("이름");
        tv_notice_board_title.setText(forum_name);

        if(forum_num==8){
            forum_sort="내가 누른 좋아요 글";
            User_like_postlist();
        }
        else if(forum_num==9){
            forum_sort="내가쓴 글";
            Mypost_list();
        }
        else if(forum_num==10){
            forum_sort="베스트 게시판";
            Best_postlist();
        }
        else{
            forum_sort="Post"+forum_num;
            updateDatas();
        }

        tv_forum_title = findViewById(R.id.tv_notice_board_title);


        mPostRecyclerView = findViewById(R.id.recyclerview);
        swipeRefreshLayout=findViewById(R.id.refresh_board);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(forum_num==8){

                    User_like_postlist();
                }
                else if(forum_num==9){

                    Mypost_list();
                }
                else if(forum_num==10){

                    Best_postlist();
                }
                else{

                    updateDatas();
                }

                swipeRefreshLayout.setRefreshing(false);

            }
        });

        if(forum_num>7){likesort_btn.setVisibility(View.INVISIBLE);}
        likesort_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(likeSort_TF) {
                    likeSort_TF = false;
                    sortDatas();
                    likesort_btn.setTextColor(Color.WHITE);
                    likesort_btn.setText(" 최신순 정렬");
                    likesort_btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.search_last)));
                }
                else{
                    likeSort_TF = true;
                    updateDatas();
                    likesort_btn.setTextColor(Color.parseColor("#1C599A"));
                    likesort_btn.setText(" 좋아요순 정렬");
                    likesort_btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.search_pre)));
                }
            }
        });

    }

//          툴바레이아웃설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_notice_board,menu);
        MenuItem item = menu.findItem(R.id.action_btn_create);
       if(forum_num>7){ item.setVisible(false);}
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
                                mAdapter = new PostAdapter(NoticeBoard.this, mDatas);
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


                                            mDatas.add(post);

                                    }
                                } else {
                                }
                                Collections.sort(mDatas);
                                mAdapter = new PostAdapter(NoticeBoard.this, mDatas);//mDatas라는 생성자를 넣어줌
                                mPostRecyclerView.setAdapter(mAdapter);
                            }
                        });
    }

    public void Search_mypost()
    {


        mDatas = new ArrayList<>();
        mDatas.clear();

        if(preLiked.size() > 0) {

            int listsize = preLiked.size();
            int num = listsize/10;
            int remainder = listsize%10;

            ArrayList<String> remainlist = new ArrayList<>();


            if(num>0){
                for(int i=0;i<num;++i){
                    ArrayList<String> numlist = new ArrayList<>( preLiked.subList(i*10,10+i*10));
                    searchInFB(numlist);
                }
            }
            for(int i=0;i<remainder;++i){
                remainlist.add(preLiked.get(i+num*10));
            }
            searchInFB(remainlist);
        }

    }
    public void searchInFB(ArrayList<String> sslist){
        String [] strings = new String[7];

        for(int i=0;i<strings.length;++i){
            strings[i]="Post"+(i+1);
        }

        for (String data : strings) {

            mStore.collection(data)
                    .whereIn("post_id", sslist)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Post post = document.toObject(Post.class);
                                    mDatas.add(post);
                                }
                            } else {

                            }

                            Collections.sort(mDatas, new Comparator<Post>() {
                                @Override
                                public int compare(Post o1, Post o2) {

                                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                                }
                            });

                            mAdapter = new PostAdapter(NoticeBoard.this, mDatas);
                            mPostRecyclerView.setAdapter(mAdapter);
                        }
                    });

        }
    }

    public void User_like_postlist(){

        mStore.collection("user").document(mAuth.getCurrentUser().getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                        preLiked = userAccount.getLiked_Post();

                        Search_mypost();
                    }
                });

    }

    public void Mypost_list(){

        mStore.collection("user").document(mAuth.getCurrentUser().getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                        preLiked = userAccount.getMypost();

                        Search_mypost();
                    }
                });

    }

    public void Best_postlist() {

        String[] strings = new String[7];

        for (int i = 0; i < strings.length; ++i) {
            strings[i] = "Post" + (i + 1);
        }

        mDatas = new ArrayList<>();
        mDatas.clear();

            for (String data : strings) {
                mStore.collection(data)//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                        .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)//시간정렬순으로
                        .addSnapshotListener(
                                new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if (queryDocumentSnapshots != null) {
                                                    //미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                                Post post = snap.toObject(Post.class);

                                                if (Integer.parseInt(post.getLike()) > 10) {
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

    }



}
