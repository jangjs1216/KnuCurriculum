package com.example.loginregister.Notice_B;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.loginregister.Subject_;
import com.example.loginregister.Table;
import com.example.loginregister.ViewHolder;
import com.example.loginregister.login.FirebaseID;
import com.example.loginregister.R;
import com.example.loginregister.adapters.PostCommentAdapter;
import com.example.loginregister.push_alram.Msg;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.otaliastudios.zoom.ZoomLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

public class Post_Comment extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences.Editor prefEditor;
    SharedPreferences prefs;
    FirebaseUser user;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView com_title, com_text, com_nick, com_date, com_click;
    private ImageView com_photo;
    private ImageView url_image; // 게시글 이미지
    private String forum_sort;
    private Timestamp timestamp;
    private ImageView btn_comment;
    private PostCommentAdapter contentAdapter;
    private RecyclerView mCommentRecyclerView;
    private List<Comment> mcontent;
    private EditText com_edit;
    private String comment_p, post_t, post_num, comment_post;//
    String sub_pos;//코멘트에 들어가있는 게시글의 위치
    private Button treeButton; //[ 장준승 ] 트리 보여주기 버튼
    private ImageView likeButton; //좋아요 버튼
    private TextView likeText; //좋아요 갯수보여주는 텍스트 이번엔 다르다
    String P_comment_id;
    private ArrayList<Comment> Cdata;
    private Integer ll;
    SwipeRefreshLayout swipeRefreshLayout;
    DocumentReference docRef;
    private CardView cv_image;
    public static Context mcontext;
    public boolean Compared_c = true;
    private ArrayList<String> subs, Liked = new ArrayList<>();
    private Menu menu;
    private MenuItem subscribe;
    private String photoUrl, uid, post_id, writer_id_post, current_user, image_url, isTreeExist,token;
    private Boolean isChecked, isLiked;
    private Post post;

    // [ 장준승 ] TreeView 바로 보이도록 구현
    ZoomLayout zoomLayout;
    TreeView treeView;
    BaseTreeAdapter adapter;
    ArrayList<Subject_> subjectList = new ArrayList<>();
    TreeNode[] treeNodeList;
    ArrayList<Integer> adj[];
    HashMap<String, Integer> m;
    Table userTableInfo;
    TreeNode rootNode;

    //크기 유동적 변화 구현
    ViewHolder[] viewHolderList;
    private int displaySize = 500;
    private float displayHeight = 0;
    private float displayWidth = 0;
    private int displayWidthMargin = 1200;
    private int displayHeightMargin = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__comment);
        mcontext = this;
        ll = new Integer(0);
        btn_comment = (ImageView) findViewById(R.id.btn_comment);
        com_nick = (TextView) findViewById(R.id.Comment_nickname);          //본문 작성자
        com_title = (TextView) findViewById(R.id.Comment_title);            //제목
        com_text = (TextView) findViewById(R.id.Comment_text);              //본문
        com_date = (TextView)findViewById(R.id.Comment_date);               //작성날짜
        com_click = (TextView) findViewById(R.id.Comment_click);            //조회수
        com_edit = (EditText) findViewById(R.id.Edit_comment);              //댓글 작성 내용 입력창
        url_image = (ImageView) findViewById(R.id.linear_layout);           //작성자가 올린 이미지
        treeButton = (Button) findViewById(R.id.btn_post_treeview);         //트리 보여주는 버튼
        likeButton = (ImageView) findViewById(R.id.like_button);            //좋아요 버튼
        likeText = (TextView) findViewById(R.id.like_text);                 //좋아요 개수 보여주는 텍스트
        zoomLayout = (ZoomLayout) findViewById(R.id.post_zoomlayout);
        mCommentRecyclerView = findViewById(R.id.comment_recycler);         //코멘트 리사이클러뷰
        cv_image=findViewById(R.id.cv_image);
        Intent intent = getIntent();//데이터 전달받기
        forum_sort = getIntent().getExtras().getString("forum_sort");
        post_id = intent.getStringExtra("post_id");
        Log.e("dkstmdwo", forum_sort + post_id);


        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                token = s;
            }
        });

        /* [ 장준승 ] Post_comment에서 TreeView 보이도록 구현 */

        //과목 갯수
        int subjectNumber = 200;
        viewHolderList = new ViewHolder[subjectNumber];

        treeView = new TreeView(getApplicationContext()){
            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent event, float distanceX, float distanceY) {
                return false;
            }
        };
        treeView.setLevelSeparation(50);
        treeView.setLineColor(Color.BLACK);
        treeView.setLineThickness(5);

        docRef = mStore.collection(forum_sort).document(post_id);

        mStore.collection(forum_sort).document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                post = task.getResult().toObject(Post.class);
                post.setClick(post.getClick() + 1);
                mStore.collection(forum_sort).document(post_id).set(post);
                Log.e("postcomment1", String.valueOf(post) + subs);

                post_t = post.getTitle();
                subs = post.getSubscriber();
                timestamp = post.getTimestamp();

                Log.e("postcomment2", String.valueOf(post) + subs);

                if (subs.contains(token)) {
                    subscribe.setIcon(R.drawable.ic_baseline_notifications_active_24);
                    isChecked = true;
                } else {
                    subscribe.setIcon(R.drawable.ic_baseline_notifications_off_24);
                    isChecked = false;
                }

                if (post.getTable() == null) {
                    isTreeExist = "no";
                } else isTreeExist = "yes";

                likeText.setText(post.getLike());

                writer_id_post = post.getWriter_id();
                image_url = post.getImage_url();

                if (image_url != null) {
                    Log.d("###", "image_url : " + image_url);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    StorageReference pathReference = storageReference.child("post_image");
                    if (pathReference == null) {
                        Toast.makeText(Post_Comment.this, "해당 사진이 없습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("###", "최종 사진 주소 : " + "post_image/" + image_url + ".jpg");
                        StorageReference submitImage = storageReference.child("post_image/" + image_url + ".jpg");
                        submitImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("###", String.valueOf(uri));
                                Glide.with(Post_Comment.this).load(uri).into(url_image);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 실패
                            }
                        });
                    }
                }
                else {
                    cv_image.setVisibility(View.INVISIBLE);
                }

                if (isTreeExist.equals("yes")) {
                    zoomLayout.setVisibility(View.VISIBLE);
                    adapter = new BaseTreeAdapter<ViewHolder>(getApplicationContext(), R.layout.node) {
                        @NonNull
                        @Override
                        public ViewHolder onCreateViewHolder(View view) {
                            return new ViewHolder(view);
                        }

                        @Override
                        public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                /*
                [장준승] treenode에 정보를 업데이트 할 때, 오픈소스의 특성상 textview의
                        값 자체를 변환하기 어려우므로, String 값 자체에 모든 정보를 일괄적으로 넘겨주어 처리합니다.

                        Ex) 논리회로.1학년 1학기.1 (논리회로를 1학년 1학기에 듣고, 선택되었다.)
                 */

                            String[] nodeData = data.toString().split("\\.");
                            viewHolder.mTextView.setText(nodeData[0]);

                            viewHolderList[m.get(nodeData[0])] = viewHolder;

                            if(nodeData[2].equals("1") && nodeData[2] != null)
                            {
                                viewHolder.setViewHolderSelected();
                            }
                            else{
                                viewHolder.setViewHoldernotSelected();
                            }

                            if(nodeData[1] != null)
                            {
                                viewHolder.semesterTv.setText(nodeData[1]);
                            }else{
                                viewHolder.semesterTv.setText("인식 오류");
                            }
                            viewHolder.setSemesterColored();

                /*
                각 노드 선택시 BottomSheetDialog 띄우는 작업 수행
                 */
                        }
                    };
                    treeView.setAdapter(adapter);

                    getSubjectListFromFB();
                }
            }
        });

        url_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Post_Comment.this,Image_zoom.class);
                intent.putExtra("url",image_url);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = findViewById(R.id.refresh_commnet);

        Toolbar toolbar = findViewById(R.id.tb_post_comment);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean tgpref = preferences.getBoolean("tgpref", false);  //default is true

        //로그인 유저 정보 받아오기
        user = mAuth.getCurrentUser();
        Liked = new ArrayList<>();


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

                                Liked = (ArrayList<String>) task.getResult().getData().get(FirebaseID.Liked);

                                if (Liked != null) {

                                    isLiked = Liked.contains(post_id);
                                    if (isLiked)
                                        likeButton.setImageResource(R.drawable.ic_baseline_favorite_24);
                                    else
                                        likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                } else {
                                    likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                    isLiked = false;
                                }

                            }
                        }
                    });
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStore.collection(forum_sort).document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ll = Integer.parseInt(task.getResult().get("like").toString());

                        if (isLiked) {
                            Liked.remove(post_id);
                            likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                            ll--;
                        } else {
                            Liked.add(post_id);
                            likeButton.setImageResource(R.drawable.ic_baseline_favorite_24);
                            ll++;
                        }
                        isLiked = !isLiked;
                        likeText.setText(Integer.toString(ll));
                        Map map1 = new HashMap<String, ArrayList<String>>();
                        map1.put(FirebaseID.Liked, Liked);
                        mStore.collection("user").document(mAuth.getUid()).set(map1, SetOptions.merge());
                        Map map2 = new HashMap<String, String>();
                        map2.put(FirebaseID.like, Integer.toString(ll));
                        Log.e("Post_Comment", Integer.toString(ll));
                        mStore.collection(forum_sort).document(post_id).set(map2, SetOptions.merge());
                    }
                });

            }
        });
    }

    public void getSubjectListFromFB(){
        mStore.collection("Subject")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                subjectList.add(document.toObject(Subject_.class));
                            }
                        } else {
                        }
                        treeNodeList = new TreeNode[subjectList.size()];

                        //adj 초기화
                        adj = new ArrayList[subjectList.size()];
                        for(int i=0; i<subjectList.size(); i++)
                            adj[i] = new ArrayList<Integer>();

                        mappingSubjectList();
                        getTableFromFB();
                    }
                });
    }

    public void mappingSubjectList(){
        /* DB에서 받아온 과목들 매핑 */
        m = new HashMap<String, Integer>();
        for(int i=0; i<subjectList.size(); i++){
            m.put(subjectList.get(i).getName(), m.size());
        }
    }

    public void getTableFromFB(){
        docRef = mStore.collection(forum_sort).document(post_id);
        Log.e("###", "Current forum ID : "+forum_sort+"and postID : "+post_id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);

                userTableInfo = post.getTable();
                Log.e("###", "UserTableInfo : "+userTableInfo.getRoot().toString());
                changeToAdj(userTableInfo);

                if(userTableInfo == null){
                    Toast.makeText(getApplicationContext(), "트리를 추가해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void changeToAdj(Table table){
        for(String currSubject : table.getTable().keySet()){
            Map<String, String> currRow = table.getTable().get(currSubject);

            for(String nextSubject : currRow.keySet()){
                if(!currRow.get(nextSubject).equals("0")){
                    int currMappingPos = m.get(currSubject);
                    int nextMappingPos = m.get(nextSubject);

                    adj[currMappingPos].add(nextMappingPos);
                }
            }
        }

        //Table의 root 값으로 루트노드 설정 후 adj로 트리 만들기
        rootNode = new TreeNode(table.getRoot());
        treeNodeList[m.get(table.getRoot().split("\\.")[0])] = rootNode;

        makeTreeByAdj(rootNode);
        adapter.setRootNode(rootNode);

        Log.e("###", "zoomLayout Test : "+zoomLayout.toString());
        zoomLayout.removeAllViews();
        zoomLayout.addView(treeView);

        updateDisplaySize();
    }

    // adj로 트리 만들기
    public void makeTreeByAdj(TreeNode currNode){
        String currSubjectName = currNode.getData().toString().split("\\.")[0];
        int currMappingPos = m.get(currSubjectName);

        for(int nextMappingPos : adj[currMappingPos])
        {
            String nextSubjectName = subjectList.get(nextMappingPos).getName();
            final TreeNode newChild = new TreeNode(nextSubjectName + userTableInfo.getTable().get(currSubjectName).get(nextSubjectName));
            treeNodeList[nextMappingPos] = newChild;

            currNode.addChild(newChild);
            makeTreeByAdj(newChild);
        }
    }

    public void updateDisplaySize()
    {
        treeView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                for(ViewHolder viewHolder : viewHolderList)
                {
                    if(viewHolder != null) {
                        if (displayHeight < viewHolder.tn_layout.getY()) {
                            displayHeight = viewHolder.tn_layout.getY();
                        }
                        if (displayWidth < viewHolder.tn_layout.getX()) {
                            displayWidth = viewHolder.tn_layout.getX();
                        }
                    }
                }
                treeView.setMinimumWidth((int) displayWidth + displayWidthMargin);
                treeView.setMinimumHeight((int) displayHeight + displayHeightMargin);

                treeView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });

        zoomLayout.moveTo((float)1.0, 0, 0, false);
        zoomLayout.zoomBy((float)1.0, false);
        zoomLayout.zoomOut();

        return;
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
        subscribe = menu.findItem(R.id.action_btn_notification);
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
                    Toast.makeText(this, "작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_btn_modify:
                if (writer_id_post.equals(mAuth.getCurrentUser().getUid())) {
                    Intent intent = new Intent(this, Post_Update.class);
                    intent.putExtra("forum_sort", forum_sort);
                    intent.putExtra("post_id", post_id);
                    startActivity(intent);//게시글 수정
                } else {
                    Toast.makeText(this, "작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_btn_notification:
                if (isChecked) {
                    Log.e("Post_Comment", "알람해제");
                    isChecked = !isChecked;
                    item.setIcon(R.drawable.ic_baseline_notifications_off_24);

                    mStore.collection(forum_sort).document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                post = task.getResult().toObject(Post.class);
                                subs = post.getSubscriber();
                                subs.remove(token);
                                post.setSubscriber(subs);
                                mStore.collection(forum_sort).document(post.getPost_id()).set(post);
                            }
                        }
                    });
                } else {
                    Log.e("Post_Comment", "알람설정");
                    isChecked = !isChecked;
                    item.setIcon(R.drawable.ic_baseline_notifications_active_24);
                    mStore.collection(forum_sort).document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                post = task.getResult().toObject(Post.class);
                                subs = post.getSubscriber();
                                subs.add(token);
                                post.setSubscriber(subs);
                                mStore.collection(forum_sort).document(post.getPost_id()).set(post);
                            }
                        }
                    });
                }
                break;

            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        Cdata = new ArrayList<Comment>();
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
                com_nick.setText(post.getP_nickname());          //본문 작성자
                com_title.setText(post.getTitle());            //제목
                com_text.setText(post.getContents());             //본문
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy.MM.dd HH:mm");
                com_date.setText(simpleDateFormat.format(timestamp.toDate()));
                com_click.setText("조회 " + post.getClick());
                Log.e("###",post.getTimestamp().toDate().toString());

                Cdata.clear();
                Cdata = post.getComments();
                contentAdapter = new PostCommentAdapter(Cdata, Post_Comment.this, docRef);//mDatas라는 생성자를 넣어줌
                mCommentRecyclerView.setAdapter(contentAdapter);
            }
        });
    }

    public void compared(String comment_id) {
        Compared_c = false;
        com_edit.setHint("대댓글 작성하기");
        P_comment_id = comment_id;

        //키보드 올리기 코드
        com_edit.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


    }

    //댓글 대댓글 작성 함수
    @Override
    public void onClick(View v) {
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (Compared_c) { // 댓글
                    if (mAuth.getCurrentUser() != null) {
                        post = documentSnapshot.toObject(Post.class);
                        Log.e("TLqkf", String.valueOf(post.getSubscriber()));
                        ArrayList<Comment> data = new ArrayList<>();

                        if (post.getComments() != null) {
                            data = post.getComments();
                        }

                        Comment cur_comment = new Comment();

                        int Csize = post.getcoment_Num();

                        cur_comment.setComment(com_edit.getText().toString());
                        cur_comment.setC_nickname(comment_p);
                        cur_comment.setDocumentId(mAuth.getCurrentUser().getUid());
                        cur_comment.setComment_id(Integer.toString((1 + Csize) * 100));

                        if (Csize + 1 >= 100) {
                            Toast.makeText(Post_Comment.this, "댓글수 제한 100개을 넘었습니다", Toast.LENGTH_LONG).show();
                            return;
                        }
                        post.setcoment_Num(Csize + 1);
                        data.add(cur_comment);
                        Collections.sort(data);
                        post.setComments(data);
                        subs = post.getSubscriber();

                        if (!subs.contains(token)) {
                            subs.add(token);
                            post.setSubscriber(subs);
                        }
                        Log.e("TLqkf", post.getSubscriber().toString());
                        mStore.collection(forum_sort).document(post_id).set(post);
                        String mId = mStore.collection("message").document().getId();

                        long datetime = System.currentTimeMillis();
                        Date date = new Date(datetime);
                        Timestamp timestamp = new Timestamp(date);
                        Msg msg = new Msg(forum_sort, post_id,token, timestamp);
                        mStore.collection("message").document(mId).set(msg);


                        View view = getCurrentFocus();//작성버튼을 누르면 에딧텍스트 키보드 내리게 하기

                        if (view != null) {//댓글작성시 키보드 내리고 댓글에 작성한 내용 초기화

                            InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            hide.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            com_edit.setText("");
                        }

                        Intent intent = getIntent();//데이터 전달받기

                        finish();
                        startActivity(intent);
                    }

                }
                // 대댓글
                else if (P_comment_id != null) {
                    if (mAuth.getCurrentUser() != null) {//새로 Comment란 컬렉션에 넣어줌

                        post = documentSnapshot.toObject(Post.class);
                        ArrayList<Comment> data = new ArrayList<>();
                        int Csize = 1;

                        if (post.getComments() != null) {
                            data = post.getComments();

                            for (int i = 0; i < data.size(); ++i) {
                                Log.e("&&&", data.get(i).getComment_id());
                                if ((data.get(i).getComment_id()).equals(P_comment_id)) {
                                    Csize = 1 + data.get(i).getCcoment_Num();
                                    Log.e("&&&", P_comment_id + ' ' + Integer.toString(Csize));
                                }
                                data.get(i).setCcoment_Num(Csize);
                            }
                        }

                        if (Csize >= 100) {
                            Toast.makeText(Post_Comment.this, "대댓글수 제한 100개을 넘었습니다", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Comment cur_comment = new Comment();


                        cur_comment.setComment(com_edit.getText().toString());
                        cur_comment.setC_nickname(comment_p);
                        cur_comment.setDocumentId(mAuth.getCurrentUser().getUid());
                        cur_comment.setComment_id(Integer.toString((Integer.parseInt(P_comment_id)) + Csize));

                        data.add(cur_comment);
                        Collections.sort(data);
                        post.setComments(data);

                        subs=post.getSubscriber();
                        if (!subs.contains(token)) {
                            subs.add(token);
                            post.setSubscriber(subs);
                        }
                        mStore.collection(forum_sort).document(post_id).set(post);

                        String mId = mStore.collection("message").document().getId();

                        long datetime = System.currentTimeMillis();
                        Date date = new Date(datetime);
                        Timestamp timestamp = new Timestamp(date);
                        Msg msg = new Msg(forum_sort, post_id, token, timestamp);
                        mStore.collection("message").document(mId).set(msg);

                        View view = getCurrentFocus();//작성버튼을 누르면 에딧텍스트 키보드 내리게 하기

                        if (view != null) {//댓글작성시 키보드 내리고 댓글에 작성한 내용 초기화

                            InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            hide.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            com_edit.setText("");
                        }

                        Intent intent = getIntent();//데이터 전달받기

                        finish();
                        startActivity(intent);

                    }
                    Compared_c = true;
                }
            }
        });
    }
}