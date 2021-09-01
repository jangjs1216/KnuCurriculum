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

import com.example.loginregister.Table;
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
    private String post_num,post_id,writer_id,comment_post,like,title,content;
    private Timestamp timestamp;
    private ImageView post_imageView;
    private String forum_sort;
    private String image_url;
    private Table table;
    private Boolean state;
    private static final int CHOOSE_IMAGE = 101;
    private ArrayList<Comment> comments = new ArrayList<>();
    private ArrayList<String> subscriber;
    int commnet_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__update);

        mTitle=findViewById(R.id.Post_write_title);//제목 , item_post.xml의 변수와 혼동주의
        mContents=findViewById(R.id.Post_write_contents);
        findViewById(R.id.Post_save).setOnClickListener(this);

        Intent intent=getIntent();
        post_id=intent.getStringExtra("post_id");
        forum_sort=intent.getStringExtra("forum_sort");
        Log.d("확인","여기는 게시글 작성위:"+post_num);

        if(mAuth.getCurrentUser()!=null){//UserInfo에 등록되어있는 닉네임을 가져오기 위해서

            DocumentReference docRef2 = mStore.collection(forum_sort).document(post_id);
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
                                                       image_url=post.getImage_url();
                                                       table = post.getTable();
                                                       subscriber = post.getSubscriber();
                                                        title=post.getTitle();
                                                        content=post.getContents();

                                                       mTitle.setText(title);
                                                        mContents.setText(content);
                                                   }
                                               });
        }
    }


    @Override
    public void onClick(View v) {

        if(mAuth.getCurrentUser()!=null){
            Post post = new Post(writer_id, mTitle.getText().toString(), mContents.getText().toString(), p_nickname, like, timestamp, post_id,comments,commnet_num,image_url,forum_sort, table,subscriber);
            mStore.collection(forum_sort).document(post_id).set(post);
            finish();
        }
    }
}
