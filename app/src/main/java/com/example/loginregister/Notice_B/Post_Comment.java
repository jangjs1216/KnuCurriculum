package com.example.loginregister.Notice_B;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.loginregister.MainActivity;
import com.example.loginregister.login.FirebaseID;
import com.example.loginregister.R;
import com.example.loginregister.adapters.PostCommentAdapter;
import com.example.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
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
    private ImageView url_image; // 게시글 이미지
    private String forum_sort;
    private ImageView btn_comment;
    private PostCommentAdapter contentAdapter;
    private RecyclerView mCommentRecyclerView;
    private List<Comment> mcontent;
    private EditText com_edit;
    private String comment_p, post_t, post_num, comment_post;//
    String sub_pos;//코멘트에 들어가있는 게시글의 위치
    int com_pos = 0;//게시글의 등록된 위치
    int like = 0;
    private Button treeButton; //[ 장준승 ] 트리 보여주기 버튼
    private ImageView likeButton; //좋아요 버튼
    private TextView likeText; //좋아요 갯수보여주는 텍스트 이번엔 다르다
    String P_comment_id;
    private ArrayList<Comment> Cdata;
    private Integer ll;
    SwipeRefreshLayout swipeRefreshLayout;

    public static Context mcontext;
    public boolean Compared_c = true;
    private ArrayList<String> Subscribed,Liked;
    private ArrayList<Where_who_post> preLiked;
    private Menu menu;
    private MenuItem subscribe;
    private String photoUrl, uid, post_id, writer_id_post, current_user, image_url;
    private Boolean isChecked,isLiked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__comment);
        mcontext = this;
        ll= new Integer(0);
        btn_comment = (ImageView)findViewById(R.id.btn_comment);
        com_nick = (TextView) findViewById(R.id.Comment_nickname);          //본문 작성자
        com_title = (TextView) findViewById(R.id.Comment_title);            //제목
        com_text = (TextView) findViewById(R.id.Comment_text);              //본문
        com_edit = (EditText) findViewById(R.id.Edit_comment);              //댓글 작성 내용 입력창
        com_photo = (ImageView) findViewById(R.id.Comment_photo);           //작성자 프로필 이미지
        url_image = (ImageView) findViewById(R.id.url_image);               //작성자가 올린 이미지
        treeButton = (Button) findViewById(R.id.btn_post_treeview);         //트리 보여주는 버튼
        likeButton = (ImageView) findViewById(R.id.like_button);            //좋아요 버튼
        likeText = (TextView) findViewById(R.id.like_text);                 //좋아요 개수 보여주는 텍스트
        mCommentRecyclerView = findViewById(R.id.comment_recycler);         //코멘트 리사이클러뷰
        Intent intent = getIntent();//데이터 전달받기
        com_pos = intent.getExtras().getInt("position");
        com_nick.setText(intent.getStringExtra("nickname"));
        com_text.setText(intent.getStringExtra("content"));
        com_title.setText(intent.getStringExtra("title"));
        forum_sort=getIntent().getExtras().getString("게시판");
        //likeText.setText(intent.getStringExtra("like").toString());
        like = Integer.parseInt(intent.getStringExtra("like"));
        likeText.setText(intent.getStringExtra("like").toString());
        uid = intent.getStringExtra("uid");//게시글 작성자의 uid를 받아옴
        post_id = intent.getStringExtra("post_id");
        writer_id_post = intent.getStringExtra("writer_id");
        post_num = intent.getStringExtra("number");
        image_url=getIntent().getExtras().getString("image_url");
        if(image_url!=null)
        {
            Glide.with(this).load(image_url).into(url_image);
        }

//        Bitmap bitmap=GetImageFromUrl(image_url);
//        url_image.setImageBitmap(bitmap);
//        Log.d("###","image_url : "+image_url);
//        if(bitmap==null)
//        {
//            Log.d("###","bitmap은 null값");
//        }


        swipeRefreshLayout=findViewById(R.id.refresh_commnet);

        Toolbar toolbar = findViewById(R.id.tb_post_comment);
        setSupportActionBar(toolbar);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean tgpref = preferences.getBoolean("tgpref", false);  //default is true

        //로그인 유저 정보 받아오기
        user = mAuth.getCurrentUser();
        Subscribed = new ArrayList<>();
        Liked = new ArrayList<>();

        post_t = intent.getStringExtra("title");//게시글의 위치
        //time=(String)intent.getSerializableExtra("time");//해당 게시글의 등록 시간

       findViewById(R.id.btn_comment).setOnClickListener(this);//댓글 입력 버튼

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               onStart();
               swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (mAuth.getCurrentUser() != null) {//UserInfo에 등록되어있는 닉네임을 가져오기 위해서
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null) {

                                comment_p = (String) task.getResult().getData().get(FirebaseID.nickname);//
                                current_user = (String) task.getResult().getData().get(FirebaseID.documentId);
                                Subscribed = (ArrayList<String>)task.getResult().getData().get(FirebaseID.Subscribed);
                                if(Subscribed!=null) {
                                    Log.e("tlqkf", Subscribed + post_id);
                                    isChecked = Subscribed.contains(post_id);
                                    if (isChecked)
                                        subscribe.setIcon(R.drawable.ic_baseline_notifications_active_24);
                                    else
                                        subscribe.setIcon(R.drawable.ic_baseline_notifications_off_24);
                                }
                                else
                                    subscribe.setIcon(R.drawable.ic_baseline_notifications_off_24);


                                if((ArrayList<Where_who_post>) task.getResult().getData().get(FirebaseID.Liked)!=null) {
                                    preLiked = (ArrayList<Where_who_post>) task.getResult().getData().get(FirebaseID.Liked);
                                    for(Where_who_post predata : preLiked){
                                        Liked.add(predata.getPostid());
                                    }
                                    isLiked = Liked.contains(post_id);
                                    if (isLiked)
                                        likeButton.setImageResource(R.drawable.ic_baseline_favorite_24);
                                    else
                                        likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                }
                                else {
                                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                    isLiked = false;
                                }

                            }
                        }
                    });
        }


        treeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, Post_Treeview.class);
                intent.putExtra("writerID", writer_id_post);
                intent.putExtra("writerNickname", com_nick.getText().toString());
                startActivity(intent); //게시글 수정`
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mStore.collection(forum_sort).document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            ll = Integer.parseInt(task.getResult().get("like").toString());
                        }
                    });
                if(isLiked){
                    Liked.remove(post_id);
                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    ll--;
                }
                else{
                    Liked.add(post_id);
                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_24);
                    ll++;
                }
                isLiked= !isLiked;
                likeText.setText(Integer.toString(ll));
                Map map1 = new HashMap<String, ArrayList<String>>();
                map1.put(FirebaseID.Liked,Liked);
                mStore.collection("user").document(mAuth.getUid()).set(map1, SetOptions.merge());
                Map map2 = new HashMap<String,String>();
                map2.put(FirebaseID.like,Integer.toString(ll));
                Log.e("Post_Comment",Integer.toString(ll));
                mStore.collection(forum_sort).document(post_id).set(map2, SetOptions.merge());
                }
        });
    }

//    public static Bitmap GetImageFromUrl(String image_url) {
//        Bitmap bitmap=null;
//        URLConnection connection=null;
//        BufferedInputStream bis=null;
//        Log.d("###","여기는 GetImageFromUrl");
//        try{
//            Log.d("###","try문 들어옴1");
//            URL url=new URL(image_url);
//            Log.d("###","try문 들어옴2");
//            connection= (HttpURLConnection) url.openConnection();
//            Log.d("###","try문 들어옴3");
//            connection.connect();
//            Log.d("###","try문 들어옴4");
//
//            int nSize=connection.getContentLength();
//            Log.d("###","size는 "+nSize);
//            bis=new BufferedInputStream(connection.getInputStream(),nSize);
//            bitmap= BitmapFactory.decodeStream(bis);
//
//            bis.close();
//        } catch (Exception e) {
//            Log.d("###","오류발생");
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_post_comment, menu);
        subscribe=menu.findItem(R.id.action_btn_notification);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//게시글 작성자와 현재 사용자와의 uid가 같으면 기능 수행가능하게

        switch (item.getItemId()) {
            case R.id.action_btn_delete:
                if (mAuth.getCurrentUser().getUid().equals(writer_id_post)) {

                    mStore.collection(forum_sort).document(post_id)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Log.d("확인", "삭제되었습니다.");
                                    finish();
                                }
                            });
                } else {

                }
                break;
            case R.id.action_btn_modify:
                if (writer_id_post.equals(mAuth.getCurrentUser().getUid())) {
                    Intent intent = new Intent(this, Post_Update.class);
                    intent.putExtra("게시판",forum_sort);
                    intent.putExtra("Postid", post_id);
                    intent.putExtra("number", post_num);
                    startActivity(intent);//게시글 수정
                } else {
                    Toast.makeText(this, "작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_btn_notification:
                isChecked = item.isChecked();
                if(item.isChecked()){
                    Log.e("Post_Comment","알람해제");

                    item.setIcon(R.drawable.ic_baseline_notifications_off_24);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(post_id);
                }
                else{
                    Log.e("Post_Comment","알람설정");
                    item.setIcon(R.drawable.ic_baseline_notifications_active_24);
                    FirebaseMessaging.getInstance().subscribeToTopic(post_id);
                }
                item.setChecked(!isChecked);

        }
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();

        Cdata=new ArrayList<Comment>();
        DocumentReference docRef = mStore.collection(forum_sort).document(post_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
                Cdata.clear();
                Cdata = post.getComments();
                contentAdapter = new PostCommentAdapter(Cdata, Post_Comment.this);//mDatas라는 생성자를 넣어줌
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
        FirebaseMessaging.getInstance().unsubscribeFromTopic(post_id);
        if (Compared_c) { // 댓글
            if (mAuth.getCurrentUser() != null) {
                DocumentReference docRef = mStore.collection(forum_sort).document(post_id);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Post post = documentSnapshot.toObject(Post.class);

                        ArrayList<Comment> data = new ArrayList<>();

                        if(post.getComments() !=null) {
                            data = post.getComments();
                        }

                        Comment cur_comment = new Comment();

                        int Csize = post.getcoment_Num();

                        cur_comment.setComment(com_edit.getText().toString());
                        cur_comment.setC_nickname(comment_p);
                        cur_comment.setDocumentId(mAuth.getCurrentUser().getUid());
                        cur_comment.setComment_id(Integer.toString( (1+Csize)*100 ));

                        if(Csize+1 >=100)
                        {
                            Toast.makeText(Post_Comment.this, "댓글수 제한 100개을 넘었습니다",Toast.LENGTH_LONG).show();
                            return;
                        }

                        data.add(cur_comment);
                        Collections.sort(data);

                        post.setcoment_Num(Csize+1);
                        post.setComments(data);
                        post.setCur_comment(post.getCur_comment()+1);
                        mStore.collection(forum_sort).document(post_id).set(post);

                        View view = getCurrentFocus();//작성버튼을 누르면 에딧텍스트 키보드 내리게 하기

                        if (view != null) {//댓글작성시 키보드 내리고 댓글에 작성한 내용 초기화

                            InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            hide.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            com_edit.setText("");
                        }

                        Intent intent = getIntent();//데이터 전달받기

                        comment_post = intent.getStringExtra("post_id");
                        com_pos = intent.getExtras().getInt("position");//Post 콜렉션의 게시글 등록위치를 전달받아옴
                        finish();
                        startActivity(intent);
                    }
                });



            }
        } else if(P_comment_id != null) { // 대댓글
            if (mAuth.getCurrentUser() != null) {//새로 Comment란 컬렉션에 넣어줌

                DocumentReference docRef = mStore.collection(forum_sort).document(post_id);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Post post = documentSnapshot.toObject(Post.class);



                        ArrayList<Comment> data = new ArrayList<>();
                        int Csize = 1;

                        if(post.getComments() !=null) {
                            data = post.getComments();

                            for(int i=0;i<data.size();++i){
                                Log.e("&&&",data.get(i).getComment_id());
                                if((data.get(i).getComment_id()).equals(P_comment_id)){
                                    Csize=+1+data.get(i).getCcoment_Num();
                                    Log.e("&&&",P_comment_id+' '+Integer.toString(Csize));
                                }
                                data.get(i).setCcoment_Num(Csize);
                            }
                        }

                        if(Csize >=100)
                        {
                            Toast.makeText(Post_Comment.this, "대댓글수 제한 100개을 넘었습니다",Toast.LENGTH_LONG).show();
                            return;
                        }
                        Comment cur_comment = new Comment();


                        cur_comment.setComment(com_edit.getText().toString());
                        cur_comment.setC_nickname(comment_p);
                        cur_comment.setDocumentId(mAuth.getCurrentUser().getUid());
                        cur_comment.setComment_id(Integer.toString( (Integer.parseInt(P_comment_id)) + Csize  ));



                        data.add(cur_comment);
                        Collections.sort(data);

                        post.setComments(data);
                        post.setCur_comment(post.getCur_comment()+1);
                        mStore.collection(forum_sort).document(post_id).set(post);




                        FirebaseMessaging.getInstance().subscribeToTopic(post_id)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        Log.e("댓글 생성"," 구독성공");
                                    }
                                    else{
                                        Log.e("댓글 생성"," 구독실패");
                                    }
                                });

                        View view = getCurrentFocus();//작성버튼을 누르면 에딧텍스트 키보드 내리게 하기

                        if (view != null) {//댓글작성시 키보드 내리고 댓글에 작성한 내용 초기화

                            InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            hide.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            com_edit.setText("");
                        }

                        Intent intent = getIntent();//데이터 전달받기

                        comment_post = intent.getStringExtra("post_id");
                        com_pos = intent.getExtras().getInt("position");//Post 콜렉션의 게시글 등록위치를 전달받아옴
                        finish();
                        startActivity(intent);
                    }
                });


            }
            Compared_c=true;
        }
        FirebaseMessaging.getInstance().subscribeToTopic(post_id);
    }
}