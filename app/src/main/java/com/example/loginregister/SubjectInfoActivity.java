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
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Notice_B.Comment;
import com.example.loginregister.Notice_B.Post_Comment;
import com.example.loginregister.adapters.SubjectCommentAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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
    TextView nameTV, codeTV, semesterTV, gradeTV, openTV,totalsc;
    int curNum;
    float totalScore;
    RatingBar Trating;
    TabLayout tabLayout;
    NestedScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjectinfo);

        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subjectName");

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(onClickListener);
        nameTV = (TextView) findViewById(R.id.nameTV);
        codeTV = (TextView) findViewById(R.id.codeTV);
        semesterTV = (TextView) findViewById(R.id.semesterTV);
        gradeTV = (TextView) findViewById(R.id.gradeTV);
        openTV = (TextView) findViewById(R.id.openTV);
        totalsc = (TextView)findViewById(R.id.totalSc);
        subjectCommentRecyclerView = (RecyclerView) findViewById(R.id.subjectCommentRecyclerView);
        Trating = (RatingBar)findViewById(R.id.Totalrating);
        tabLayout = (TabLayout)findViewById(R.id.tabBar);
         scrollView = (NestedScrollView)findViewById(R.id.scrollId);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if(position==0){
                    int P1 = (int) nameTV.getY();
                    scrollView.smoothScrollTo(0,P1);
                }
                else if(position==1){
                    int P2 = (int) subjectCommentRecyclerView.getY();
                    scrollView.smoothScrollTo(0,P2);
                }
                else if(position==2){
                    int P3 = (int) subjectCommentRecyclerView.getY();
                    scrollView.smoothScrollTo(0,P3);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


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

                //전체평점 나타내기 박경무
                curNum = subject_.getVoteNum();
                totalScore = subject_.getTotalScore();

                String averse = String.format("%.2f", totalScore/curNum);
                totalsc.setText("전체 평정: "+averse);
                Trating.setRating(totalScore/curNum);


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
        commentDialog.show();

        Button noBtn = commentDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentDialog.dismiss();
            }
        });
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
                        curNum = subject_.getVoteNum();
                        totalScore = subject_.getTotalScore();
                        ++curNum;
                        totalScore+=rating;
                        subject_.setVoteNum(curNum);
                        subject_.setTotalScore(totalScore);

                        SubjectComment subjectComment = new SubjectComment(content, user.getUid(), Float.toString(rating));
                        subjectComments.add(subjectComment);

                        db.collection("Subject").document(subjectName).set(subject_);
                        commentDialog.dismiss();
                        makeComment();


                        String averse = String.format("%.2f", totalScore/curNum);
                        totalsc.setText("전체 평점점: "+averse);
                       Trating.setRating(totalScore/curNum);

                    }
                });
            }
        });
    }
    //달린 강의평들 출력 박경무
    public void makeComment()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Subject").document(subjectName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Subject_ subject_ = documentSnapshot.toObject(Subject_.class);


                ArrayList<SubjectComment> subjectComments = subject_.getComments();

                subjectCommentAdapter = new SubjectCommentAdapter(subjectComments);
                subjectCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                subjectCommentRecyclerView.setAdapter(subjectCommentAdapter);
            }
        });


    }

}
