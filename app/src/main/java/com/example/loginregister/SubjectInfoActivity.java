package com.example.loginregister;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Notice_B.Comment;
import com.example.loginregister.Notice_B.Post_Comment;
import com.example.loginregister.adapters.SubjectCommentAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class SubjectInfoActivity extends AppCompatActivity {
    private FirebaseUser user;
    SubjectCommentAdapter subjectCommentAdapter;
    RecyclerView subjectCommentRecyclerView;
    Dialog commentDialog;
    String subjectName;
    TextView nameTV, codeTV, semesterTV, gradeTV, openTV;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjectinfo);

        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subjectName");

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(onClickListener);
        nameTV = (TextView) findViewById(R.id.nameTV);
        codeTV = (TextView) findViewById(R.id.codeTV);
        semesterTV = (TextView) findViewById(R.id.semesterTV);
        gradeTV = (TextView) findViewById(R.id.gradeTV);
        openTV = (TextView) findViewById(R.id.openTV);
        subjectCommentRecyclerView = (RecyclerView) findViewById(R.id.subjectCommentRecyclerView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Subject").document(subjectName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Subject_ subject_ = documentSnapshot.toObject(Subject_.class);

                nameTV.setText("과목명 : " + subject_.getName());
                codeTV.setText("과목 코드 : " + subject_.getCode());
                semesterTV.setText("학기 : " + subject_.getSemester());
                gradeTV.setText("학년 : " + subject_.getGrade());
                if(subject_.getOpen() == true) openTV.setText("이번 학기 개설 여부 : YES");
                else openTV.setText("이번 학기 개설 여부 : NO");


                ArrayList<SubjectComment> subjectComments = subject_.getComments();

                subjectCommentAdapter = new SubjectCommentAdapter(subjectComments);
                subjectCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                subjectCommentRecyclerView.setAdapter(subjectCommentAdapter);
            }
        });

        commentDialog = new Dialog(SubjectInfoActivity.this);
        commentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        commentDialog.setContentView(R.layout.dialog_subjectcomment);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Subject").document(subjectName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Subject_ subject_ = documentSnapshot.toObject(Subject_.class);

                nameTV.setText("과목명 : " + subject_.getName());
                codeTV.setText("과목 코드 : " + subject_.getCode());
                semesterTV.setText("학기 : " + subject_.getSemester());
                gradeTV.setText("학년 : " + subject_.getGrade());
                if(subject_.getOpen() == true) openTV.setText("이번 학기 개설 여부 : YES");
                else openTV.setText("이번 학기 개설 여부 : NO");


                ArrayList<SubjectComment> subjectComments = subject_.getComments();

                subjectCommentAdapter = new SubjectCommentAdapter(subjectComments);
                subjectCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                subjectCommentRecyclerView.setAdapter(subjectCommentAdapter);
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.addButton:
                    showDialog();
                    break;
            }
        }
    };

    // dialog01을 디자인하는 함수
    public void showDialog() {
        commentDialog.show(); // 다이얼로그 띄우기

        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */

        // 위젯 연결 방식은 각자 취향대로~
        // '아래 아니오 버튼'처럼 일반적인 방법대로 연결하면 재사용에 용이하고,
        // '아래 네 버튼'처럼 바로 연결하면 일회성으로 사용하기 편함.
        // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.

        // 아니오 버튼
        Button noBtn = commentDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                commentDialog.dismiss(); // 다이얼로그 닫기
            }
        });
        // 네 버튼
        commentDialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                RatingBar commentRB = commentDialog.findViewById(R.id.commentRB);
                EditText contentET = commentDialog.findViewById(R.id.contentET);
                float rating = commentRB.getRating();
                String content = contentET.getText().toString();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("Subject").document(subjectName);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Subject_ subject_ = documentSnapshot.toObject(Subject_.class);

                        ArrayList<SubjectComment> subjectComments = subject_.getComments();

                        SubjectComment subjectComment = new SubjectComment(content, user.getUid(), Float.toString(rating));
                        subjectComments.add(subjectComment);

                        db.collection("Subject").document(subjectName).set(subject_);
                        commentDialog.dismiss();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("Subject").document(subjectName);
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Subject_ subject_ = documentSnapshot.toObject(Subject_.class);

                                nameTV.setText("과목명 : " + subject_.getName());
                                codeTV.setText("과목 코드 : " + subject_.getCode());
                                semesterTV.setText("학기 : " + subject_.getSemester());
                                gradeTV.setText("학년 : " + subject_.getGrade());
                                if(subject_.getOpen() == true) openTV.setText("이번 학기 개설 여부 : YES");
                                else openTV.setText("이번 학기 개설 여부 : NO");


                                ArrayList<SubjectComment> subjectComments = subject_.getComments();

                                subjectCommentAdapter = new SubjectCommentAdapter(subjectComments);
                                subjectCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                subjectCommentRecyclerView.setAdapter(subjectCommentAdapter);
                            }
                        });
                    }
                });
            }
        });
    }
}
