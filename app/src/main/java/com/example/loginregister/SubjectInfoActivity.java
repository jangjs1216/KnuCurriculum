package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.adapters.SubjectCommentAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class SubjectInfoActivity extends AppCompatActivity {
    ArrayList<Subject> subjectList;
    HashMap<String, Integer> m;
    private FirebaseUser user;
    SubjectCommentAdapter subjectCommentAdapter;
    RecyclerView subjectCommentRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjectinfo);

        Intent intent = getIntent();
        String subjectName = intent.getStringExtra("subjectName");

        TextView nameTV = (TextView) findViewById(R.id.nameTV);
        TextView codeTV = (TextView) findViewById(R.id.codeTV);
        TextView semesterTV = (TextView) findViewById(R.id.semesterTV);
        TextView gradeTV = (TextView) findViewById(R.id.gradeTV);
        TextView openTV = (TextView) findViewById(R.id.openTV);
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




        //새로운 수강평 추가
        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Subject").document("논리회로");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Subject_ subject_ = documentSnapshot.toObject(Subject_.class);

                ArrayList<SubjectComment> subjectComments = subject_.getComments();

                SubjectComment subjectComment1 = new SubjectComment("경무 병신", user.getUid(), "1");
                SubjectComment subjectComment2 = new SubjectComment("종하 병신", user.getUid(), "2");
                SubjectComment subjectComment3 = new SubjectComment("승재 병신", user.getUid(), "3");
                SubjectComment subjectComment4 = new SubjectComment("ㅔㅐㅑㅕ", user.getUid(), "2");
                SubjectComment subjectComment5 = new SubjectComment("ㅣㅏㅓㅗ", user.getUid(), "1");
                subjectComments.add(subjectComment1);
                subjectComments.add(subjectComment2);
                subjectComments.add(subjectComment3);
                subjectComments.add(subjectComment4);
                subjectComments.add(subjectComment5);

                db.collection("Subject").document("논리회로").set(subject_);

            }
        });*/



        /*user = FirebaseAuth.getInstance().getCurrentUser();
        ArrayList<SubjectComment> subjectComments = new ArrayList<>();
        SubjectComment subjectComment1 = new SubjectComment("ㅂㅈㄷㄱ", user.getUid(), "1");
        SubjectComment subjectComment2 = new SubjectComment("ㅁㄴㅇㄹ", user.getUid(), "2");
        SubjectComment subjectComment3 = new SubjectComment("ㅋㅌㅊㅍ", user.getUid(), "3");
        SubjectComment subjectComment4 = new SubjectComment("ㅔㅐㅑㅕ", user.getUid(), "2");
        SubjectComment subjectComment5 = new SubjectComment("ㅣㅏㅓㅗ", user.getUid(), "1");
        subjectComments.add(subjectComment1);
        subjectComments.add(subjectComment2);
        subjectComments.add(subjectComment3);
        subjectComments.add(subjectComment4);
        subjectComments.add(subjectComment5);





        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Subejct_ Subejct_ = new Subejct_("논리회로", "ELEC000000", "2", "1", true, subjectComments);

        db.collection("Subject").document(Subejct_.getName()).set(Subejct_);*/
    }
}
