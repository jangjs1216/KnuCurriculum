package com.example.loginregister.Notice_B;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.loginregister.R;
import com.example.loginregister.adapters.PostAdapter;
import com.example.loginregister.login.FirebaseID;
import com.example.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Fragment_Notice_Board extends Fragment {

   private View view;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private Toolbar toolbar;
    private RecyclerView mPostRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private PostAdapter mAdapter;
    private List<Post> mDatas;
    private String forum_sort;
    private TextView tv_forum_title;
    private ArrayList<String> preLiked;
    private FloatingActionButton likesort_btn;
    private boolean likeSort_TF=true;
    int forum_num;
    private FragmentManager fm;
    private FragmentTransaction ft;

    public Fragment_Notice_Board(int forum_num) {
        this.forum_num = forum_num;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment__notice__board, container, false);

        //      툴바
        toolbar = (Toolbar)view.findViewById(R.id.tb_notice_board);
        ((Activity_Container_Board)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((Activity_Container_Board)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);
        //툴바 끝

        // 게시판 컬렉션 지정
        forum_num = ((Activity_Container_Board)getActivity()).getForum_num();


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

        tv_forum_title = view.findViewById(R.id.tv_notice_board_title);
        tv_forum_title.setText(forum_sort);

        //리싸이클러뷰 설정
        fm = getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        mPostRecyclerView = view.findViewById(R.id.recyclerview);
        swipeRefreshLayout= view.findViewById(R.id.refresh_board);

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

        likesort_btn=view.findViewById(R.id.like_sort);
        likesort_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(likeSort_TF) {
                    likeSort_TF = false;
                    sortDatas();
                    likesort_btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.search_last)));
                }
                else{
                    likeSort_TF = true;
                    updateDatas();
                    likesort_btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.search_pre)));
                }
            }
        });
        return view;
    }
    //          툴바레이아웃설정
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_notice_board,menu);
    }

    //          툴바 작동설정
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.action_btn_search:
                intent=new Intent(getActivity(), Post_Search.class);
                intent.putExtra("게시판",forum_sort);
                startActivity(intent);
                break;
            case R.id.action_btn_create:
                Log.e("notice","글쓰기 선택");
                intent=new Intent(getActivity(), Post_write.class);
                intent.putExtra("게시판",forum_sort);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //      게시판 목록불러오기
    public String getForum_sort() {
        return forum_sort;
    }

    public void Search_mypost()
    {
        String [] strings = new String[7];

        for(int i=0;i<strings.length;++i){
            strings[i]="Post"+(i+1);
        }

        mDatas = new ArrayList<>();
        mDatas.clear();
        if(preLiked.size() > 0) {
            for (String data : strings) {
                mStore.collection(data)
                        .whereIn("post_id", preLiked)
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
                                mAdapter = new PostAdapter(getActivity(), mDatas);
                                mAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListenr() {
                                    @Override
                                    public void onItemClick(View v, int pos) {
                                        ft.replace(R.id.main_board,new Fragment_Post_Comment(mDatas.get(pos)));
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                });
                                mPostRecyclerView.setAdapter(mAdapter);
                            }
                        });

            }
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
                                        mDatas.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                        for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                            Post post = snap.toObject(Post.class);

                                            if (Integer.parseInt(post.getLike()) > 1) {
                                                mDatas.add(post);
                                            }
                                        }
                                    } else {
                                    }
                                    Collections.sort(mDatas);
                                    mAdapter = new PostAdapter(getActivity(), mDatas);//mDatas라는 생성자를 넣어줌
                                    mAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListenr() {
                                        @Override
                                        public void onItemClick(View v, int pos) {
                                            ft.replace(R.id.main_board,new Fragment_Post_Comment(mDatas.get(pos)));
                                            ft.addToBackStack(null);
                                            ft.commit();
                                        }
                                    });
                                    mPostRecyclerView.setAdapter(mAdapter);
                                }
                            });
        }

    }
    //게시판 정렬

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
                                mAdapter = new PostAdapter(getActivity(), mDatas);
                                mAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListenr() {
                                    @Override
                                    public void onItemClick(View v, int pos) {
                                        ft.replace(R.id.main_board,new Fragment_Post_Comment(mDatas.get(pos)));
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                });
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
                                mAdapter = new PostAdapter(getActivity(), mDatas);//mDatas라는 생성자를 넣어줌
                                mAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListenr() {
                                    @Override
                                    public void onItemClick(View v, int pos) {
                                        ft.replace(R.id.main_board,new Fragment_Post_Comment(mDatas.get(pos)));
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                });
                                mPostRecyclerView.setAdapter(mAdapter);
                            }
                        });
    }

}