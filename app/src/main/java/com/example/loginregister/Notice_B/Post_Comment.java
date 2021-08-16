package com.example.loginregister.Notice_B;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.login.FirebaseID;
import com.example.loginregister.R;
import com.example.loginregister.adapters.PostCommentAdapter;
import com.example.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post_Comment extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences.Editor prefEditor;
    SharedPreferences prefs;
    FirebaseUser user;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView com_title;
    private TextView com_text;
    private TextView com_nick;
    private ImageView com_photo;
    private ImageView com_photo2;
    private PostCommentAdapter contentAdapter;
    private RecyclerView mCommentRecyclerView;
    private List<Comment> mcontent;
    private EditText com_edit;
    private String comment_p, post_t, post_num, comment_post;//
    String sub_pos;//코멘트에 들어가있는 게시글의 위치
    int com_pos = 0;//게시글의 등록된 위치
    int like = 0;
    private Button likeButton; //좋아요 버튼
    private TextView likeText; //좋아요 갯수보여주는 텍스트
    String P_comment_id;


    public static Context mcontext;
    public boolean Compared_c = true;


    private String photoUrl, uid, post_id, writer_id_post, current_user; //사진 저장 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__comment);
        mcontext = this;

        com_nick = (TextView) findViewById(R.id.Comment_nickname);//본문 작성자
        com_title = (TextView) findViewById(R.id.Comment_title);//제목
        com_text = (TextView) findViewById(R.id.Comment_text);//본문
        com_edit = (EditText) findViewById(R.id.Edit_comment);//댓글 작성 내용 입력창
        com_photo = (ImageView) findViewById(R.id.Comment_photo); //작성자 프로필 이미지
        com_photo2 = (ImageView) findViewById(R.id.Comment_photo2); //작성자가 올린 이미지
        likeButton = (Button) findViewById(R.id.like_button); //좋아요 버튼
        likeText = (TextView) findViewById(R.id.like_text); // 좋아요 개수 보여주는 텍스트
        mCommentRecyclerView = findViewById(R.id.comment_recycler);//코멘트 리사이클러뷰
        Intent intent = getIntent();//데이터 전달받기
        com_pos = intent.getExtras().getInt("position");
        com_nick.setText(intent.getStringExtra("nickname"));
        com_text.setText(intent.getStringExtra("content"));
        com_title.setText(intent.getStringExtra("title"));

        //likeText.setText(intent.getStringExtra("like").toString());
        like = Integer.parseInt(intent.getStringExtra("like"));
        likeText.setText(intent.getStringExtra("like").toString());
        uid = intent.getStringExtra("uid");//게시글 작성자의 uid를 받아옴
        post_id = intent.getStringExtra("post_id");
        writer_id_post = intent.getStringExtra("writer_id");
        post_num = intent.getStringExtra("number");

        DocumentReference docRef = mStore.collection("Post").document(post_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
            }
        });




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean tgpref = preferences.getBoolean("tgpref", false);  //default is true

        //로그인 유저 정보 받아오기
        user = mAuth.getCurrentUser();



        post_t = intent.getStringExtra("title");//게시글의 위치
        //time=(String)intent.getSerializableExtra("time");//해당 게시글의 등록 시간


        findViewById(R.id.comment_button).setOnClickListener(this);//댓글 입력 버튼

        if (mAuth.getCurrentUser() != null) {//UserInfo에 등록되어있는 닉네임을 가져오기 위해서
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null) {
                                comment_p = (String) task.getResult().getData().get(FirebaseID.nickname);//
                                current_user = (String) task.getResult().getData().get(FirebaseID.documentId);
                            }
                        }
                    });
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference docRef1 = mStore.collection("user").document(mAuth.getUid());
                docRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);
                        ArrayList<String> liked_Post = userAccount.getLiked_Post();


                        DocumentReference docRef2 = mStore.collection("Post").document(post_id);
                        docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Post post = documentSnapshot.toObject(Post.class);

                                int findIndex = -1;
                                for(int i = 0; i < liked_Post.size(); i++){
                                    if(post_id.equals(post_id)){
                                        findIndex = i;
                                    }
                                }
                                if(findIndex != -1){
                                    int curLike = Integer.parseInt(post.getLike());
                                    curLike--;
                                    post.setLike(Integer.toString(curLike));
                                    liked_Post.remove(findIndex);
                                }
                                else{
                                    int curLike = Integer.parseInt(post.getLike());
                                    curLike++;
                                    post.setLike(Integer.toString(curLike));
                                    liked_Post.add(post_id);
                                }
                                likeText.setText(post.getLike());

                                //String documentId, String title, String contents, String p_nickname, String p_photo, String post_num, String post_photo, String post_id, String writer_id, String like
                                //Post temp = new Post(post.getDocumentId(), post.getTitle(), post.getContents(), post.getP_nickname(), post.getP_photo(), post.getPost_num(), post.getPost_photo(), post.getPost_id(), post.getWriter_id(), )
                                mStore.collection("Post").document(post_id).set(post);
                                mStore.collection("user").document(user.getUid()).set(userAccount);
                            }
                        });


                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//게시글 작성자와 현재 사용자와의 uid가 같으면 기능 수행가능하게
        switch (item.getItemId()) {
            case R.id.first:
                if (writer_id_post.equals(current_user)) {
                    mStore.collection("Post").document(post_id)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("확인", "삭제되었습니다.");
                                    finish();
                                }
                            });
                } else {
                    Toast.makeText(this, "작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.second:
                if (writer_id_post.equals(current_user)) {
                    Intent intent = new Intent(this, Post_Update.class);
                    intent.putExtra("Postid", post_id);
                    intent.putExtra("number", post_num);
                    startActivity(intent);//게시글 수정
                } else {
                    Toast.makeText(this, "작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mcontent = new ArrayList<>();//리사이클러뷰에 표시할 댓글 목록
        mStore.collection("Comment")
                .whereEqualTo("title", post_t)//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                .orderBy(FirebaseID.timestamp, Query.Direction.ASCENDING)//시간정렬순으로 이건 처음에 작성한게 제일 위로 올라감 게시글과 반대
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            mcontent.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                Map<String, Object> shot = snap.getData();
                                String documentId = String.valueOf(shot.get(FirebaseID.documentId));
                                String comment = String.valueOf(shot.get(FirebaseID.comment));
                                String c_nickname = String.valueOf(shot.get(FirebaseID.nickname));
                                String num_comment = String.valueOf(shot.get(FirebaseID.comment_post));
                                String comment_id = String.valueOf(shot.get(FirebaseID.commentId));
                                Comment data = new Comment(documentId, c_nickname, comment, Integer.toString(com_pos), post_t, num_comment, comment_id);
                                mcontent.add(data);//여기까지가 게시글에 해당하는 데이터 적용
                            }
                        }
                        contentAdapter = new PostCommentAdapter(mcontent, Post_Comment.this);//mDatas라는 생성자를 넣어줌
                        mCommentRecyclerView.setAdapter(contentAdapter);
                    }

                });


    }

    public void compared(String comment_id) {
        Compared_c = false;
        com_edit.setHint("대댓글 작성하기");

        P_comment_id = comment_id;

    }

    @Override
    public void onClick(View v) {
        if (Compared_c) { // 댓글
            if (mAuth.getCurrentUser() != null) {//새로 Comment란 컬렉션에 넣어줌
                String commentID=mStore.collection("Comment").document().getId();
                Map<String, Object> data = new HashMap<>();
                data.put(FirebaseID.commentId,commentID);
                data.put(FirebaseID.documentId, mAuth.getCurrentUser().getUid());//유저 고유번호
                data.put(FirebaseID.comment, com_edit.getText().toString());//게시글 내용
                data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());//파이어베이스 시간을 저장 그래야 게시글 정렬이 시간순가능
                data.put(FirebaseID.nickname, comment_p);
                Intent intent = getIntent();//데이터 전달받기
                data.put(FirebaseID.title, post_t);//게시글의 제목을 넣어준다 비교하기위해서
                //Log.d("확인",po)
                comment_post = intent.getStringExtra("post_id");
                com_pos = intent.getExtras().getInt("position");//Post 콜렉션의 게시글 등록위치를 전달받아옴
                //Log.d("확인","위치"+com_pos);
                data.put(FirebaseID.post_position, Integer.toString(com_pos));//작성된 게시판의 위치를 댓글에 저장
                data.put(FirebaseID.comment_post, comment_post);

                mStore.collection("Comment").document(commentID).set(data);
                View view = this.getCurrentFocus();//작성버튼을 누르면 에딧텍스트 키보드 내리게 하기
                if (view != null) {//댓글작성시 키보드 내리고 댓글에 작성한 내용 초기화
                    InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    hide.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    com_edit.setText("");
                }
                finish();
                startActivity(intent);
            }
        } else if(P_comment_id != null) { // 대댓글
            if (mAuth.getCurrentUser() != null) {//새로 Comment란 컬렉션에 넣어줌
                Map<String, Object> data = new HashMap<>();
                data.put(FirebaseID.documentId, mAuth.getCurrentUser().getUid());//유저 고유번호
                data.put(FirebaseID.comment, com_edit.getText().toString());//대댓글 내용
                data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());//파이어베이스 시간을 저장 그래야 게시글 정렬이 시간순가능
                data.put(FirebaseID.nickname, comment_p);
                Intent intent = getIntent();//데이터 전달받기
                data.put(FirebaseID.title, post_t);//게시글의 제목을 넣어준다 비교하기위해서
                //Log.d("확인",po)
                comment_post = intent.getStringExtra("post_id");
                com_pos = intent.getExtras().getInt("position");//Post 콜렉션의 게시글 등록위치를 전달받아옴
                //Log.d("확인","위치"+com_pos);
                data.put(FirebaseID.post_position, Integer.toString(com_pos));//작성된 게시판의 위치를 댓글에 저장
                data.put(FirebaseID.comment_post, comment_post);
                mStore.collection("Comment").document(P_comment_id).collection(P_comment_id).add(data);
                View view = this.getCurrentFocus();//작성버튼을 누르면 에딧텍스트 키보드 내리게 하기
                if (view != null) {//댓글작성시 키보드 내리고 댓글에 작성한 내용 초기화
                    InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    hide.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    com_edit.setText("");
                }
                finish();
                startActivity(intent);
                Compared_c=true;

            }
        }
    }
}