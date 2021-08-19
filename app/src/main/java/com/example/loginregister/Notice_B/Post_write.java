package com.example.loginregister.Notice_B;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.login.FirebaseID;
import com.example.loginregister.R;
import com.example.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Post_write extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth=FirebaseAuth.getInstance();//사용자 정보 가져오기
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private EditText mTitle,mContents;//제목, 내용
    private String p_nickname;//게시판에 표기할 닉네잉 //이게 가져온 값을 저장하는 임시 변수
    private Button post_photo;
    private ProgressBar post_progressBar;
    private String writer_id;
    private Uri uriProfileImage;
    private ImageView post_imageView;
    private static final int CHOOSE_IMAGE = 101;
    private String forum_sort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

        mTitle=findViewById(R.id.Post_write_title);//제목 , item_post.xml의 변수와 혼동주의
        mContents=findViewById(R.id.Post_write_contents);
        findViewById(R.id.Post_save).setOnClickListener(this);
        post_photo =findViewById(R.id.Post_photo);
        post_imageView = findViewById(R.id.post_imageview);
        post_imageView.setVisibility(View.INVISIBLE);
        post_progressBar = findViewById(R.id.post_progressbar);

        Intent intent=getIntent();
        forum_sort=intent.getExtras().getString("게시판");

        if(mAuth.getCurrentUser()!=null){//UserInfo에 등록되어있는 닉네임을 가져오기 위해서
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    //우리 파이어베이스의 user 컬렉션의 정보를 가져올 수 있어. email, password, Uid(주민번호같은 거), nickname
                    //현재는 user에는 저 4개의 정보밖에 없어. 만약 다른 정보를 추가로 받고싶다면 어제 말했듯이 너가 가입에서부터 새로 입력값을 받고
                    //FirebaseID에 똑같이 추가하고 SignupActivity에 UserMap부분을 수정하면되. 아마 UserInfo에 객체 생성은 따로 안해줘도 될거야 내가 형식을 바꿔놔서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){

                                //닉네임 뿐만아니라 여기서 FirebaseID.password를 하면 비밀번호도 받아올 수 있음. 즉 원하는 것을 넣으면 됨
                                //파이어베이스에 등록된 닉네임을 불러옴
                                writer_id=(String)task.getResult().getData().get(FirebaseID.documentId);
                                Log.d("확인","현재 사용자 uid입니다:"+writer_id);
                            }
                        }
                    });
        }

        post_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //treeView 보여주기
            }
        });

    }


    @Override
    public void onClick(View v) {

        if(mAuth.getCurrentUser()!=null){
            String PostID=mStore.collection(forum_sort).document().getId();//제목이 같아도 게시글이 겹치지않게
            Intent intent=getIntent();
            final Post[] post = new Post[1];

            DocumentReference docRef1 = mStore.collection("user").document(mAuth.getUid());
            docRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);

                    long datetime = System.currentTimeMillis();
                    Date date = new Date(datetime);
                    Timestamp timestamp = new Timestamp(date);
                    post[0] = new Post(mAuth.getUid(), mTitle.getText().toString(), mContents.getText().toString(), userAccount.getNickname(), "0", timestamp, PostID,new ArrayList<>(),0);
                    mStore.collection(forum_sort).document(PostID).set(post[0]);
                }
            });
            finish();
        }
    }
}