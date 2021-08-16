package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
                Subejct_ subejct_ = documentSnapshot.toObject(Subejct_.class);

                nameTV.setText("과목명 : " + subejct_.getName());
                codeTV.setText("과목 코드 : " + subejct_.getCode());
                semesterTV.setText("학기 : " + subejct_.getSemester());
                gradeTV.setText("학년 : " + subejct_.getGrade());
                if(subejct_.getOpen() == true) openTV.setText("이번 학기 개설 여부 : YES");
                else openTV.setText("이번 학기 개설 여부 : NO");


                ArrayList<SubjectComment> subjectComments = subejct_.getComments();

                subjectCommentAdapter = new SubjectCommentAdapter(subjectComments);
                subjectCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                subjectCommentRecyclerView.setAdapter(subjectCommentAdapter);
            }
        });







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
