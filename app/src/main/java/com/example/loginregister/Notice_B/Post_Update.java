package com.example.loginregister.Notice_B;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

public class Post_Update extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth= FirebaseAuth.getInstance();//사용자 정보 가져오기
    private FirebaseFirestore mStore= FirebaseFirestore.getInstance();
    private EditText mTitle,mContents;//제목, 내용
    private String p_nickname;//게시판에 표기할 닉네잉 //이게 가져온 값을 저장하는 임시 변수
    private String post_num,post_id,writer_id,comment_post,like;
    private Timestamp timestamp;
    private ImageView post_imageView;
    private static final int CHOOSE_IMAGE = 101;
    ArrayList<Comment> comments = new ArrayList<>();
    int commnet_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__update);

        mTitle=findViewById(R.id.Post_write_title);//제목 , item_post.xml의 변수와 혼동주의
        mContents=findViewById(R.id.Post_write_contents);
        findViewById(R.id.Post_save).setOnClickListener(this);
        post_imageView = findViewById(R.id.post_imageview);
        post_imageView.setVisibility(View.INVISIBLE);

        if(mAuth.getCurrentUser()!=null){//UserInfo에 등록되어있는 닉네임을 가져오기 위해서

            DocumentReference docRef2 = mStore.collection("Post").document(mAuth.getCurrentUser().getUid());
            docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                   @Override
                                                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                       Post post = documentSnapshot.toObject(Post.class);
                                                       writer_id = post.getWriter_id();
                                                       p_nickname = post.getP_nickname();
                                                       like = post.getLike();
                                                       timestamp = post.getTimestamp();
                                                      comments = post.getComments();
                                                       commnet_num=post.getcoment_Num();
                                                       post_id=post.getPost_id();
                                                   }
                                               });

//            mStore.collection("user").document(mAuth.getCurrentUser().getUid())//
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if(task.getResult()!=null){
//                                //DocumentSnapshot post = task.getResult();
//                                Post post = task.toObject(Post.class);
//
//                                p_nickname=(String)task.getResult().getData().get(FirebaseID.nickname);//
//                                writer_id=(String)task.getResult().getData().get(FirebaseID.documentId);
//                                Log.d("확인","현재 사용자 uid입니다:"+writer_id);
//                            }
//                        }
//                    });
        }




        Intent intent=getIntent();
        post_num=intent.getStringExtra("post");
        Log.d("확인","여기는 게시글 작성위:"+post_num);
    }





    @Override
    public void onClick(View v) {

        if(mAuth.getCurrentUser()!=null){
//            String PostID=mStore.collection("Post").document().getId();//제목이 같아도 게시글이 겹치지않게
//            Intent intent=getIntent();
//            post_num=intent.getStringExtra("number");
//            post_id=intent.getStringExtra("Postid");
//            Log.d("확인","여기는 게시글 작성:"+post_num);
//            Map<String, Object> data=new HashMap<>();
//            data.put(FirebaseID.title,mTitle.getText().toString());//게시글제목
//            data.put(FirebaseID.contents,mContents.getText().toString());//게시글 내용
//            data.put(FirebaseID.timestamp, FieldValue.serverTimestamp());//파이어베이스 시간을 저장 그래야 게시글 정렬이 시간순가능
//            data.put(FirebaseID.nickname,p_nickname);
//            data.put(FirebaseID.post_num,post_num);
//            data.put(FirebaseID.post_id,intent.getStringExtra("Postid"));//게시글 ID번호
//            data.put(FirebaseID.writer_id,writer_id);
//
//            mStore.collection("Post").document(post_id).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    Toast.makeText(getApplicationContext(),"Update complite", Toast.LENGTH_SHORT).show();
//                }
//            });//Post라는 테이블에 데이터를 입력하는것/ 문서 이름을 PostID로 등록


            Post post = new Post(post_id, mTitle.getText().toString(), mContents.getText().toString(), p_nickname, like, timestamp, post_id,comments,commnet_num);
            mStore.collection("Post").document(post_id).set(post);


            startActivity(new Intent(this,NoticeBoard.class));
            finish();
        }
    }
}
