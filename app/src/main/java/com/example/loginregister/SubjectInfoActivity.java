package com.example.loginregister;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Notice_B.Comment;
import com.example.loginregister.Notice_B.Post_Comment;
import com.example.loginregister.adapters.PickAdapter;
import com.example.loginregister.adapters.SubjectCommentAdapter;
import com.example.loginregister.login.UserAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectInfoActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;
    SubjectCommentAdapter subjectCommentAdapter;
    RecyclerView subjectCommentRecyclerView,picksubjectList;
    Dialog commentAddDialog, commentReviseDialog;
    Subject_ subject_;
    String subjectName;
    TextView nameTV, codeTV, semesterTV, gradeTV, openTV,totalsc,Pickname;
    RatingBar Trating;
    TabLayout tabLayout;
    NestedScrollView scrollView;
    PickAdapter pickAdapter;
    ArrayList<Picksubject> Picklist= new ArrayList<>();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

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
        picksubjectList=(RecyclerView)findViewById(R.id.Pick_subjectRecyclerView);
        Pickname = (TextView) findViewById(R.id.Pick_title);


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

                    scrollView.smoothScrollTo(0,subjectCommentRecyclerView.getBottom());

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        docRef = db.collection("Subject").document(subjectName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                subject_ = documentSnapshot.toObject(Subject_.class);

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
                subjectCommentAdapter.setOnItemListener(new SubjectCommentAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos, String option) {
                        if(option.equals("revise")){
                            if(subject_.getComments().get(pos).getUser_id().equals(mAuth.getUid())){
                                showReviseDialog(pos);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "자신이 작성한 수강평만 수정할 수 있습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            if(subjectComments.get(pos).getUser_id().equals(mAuth.getUid())){
                                subject_.getComments().remove(pos);
                                db.collection("Subject").document(subjectName).set(subject_);
                                subjectCommentAdapter.notifyDataSetChanged();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "자신이 작성한 수강평만 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        commentAddDialog = new Dialog(SubjectInfoActivity.this);
        commentAddDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        commentAddDialog.setContentView(R.layout.dialog_subjectcomment);

        commentReviseDialog = new Dialog(SubjectInfoActivity.this);
        commentReviseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        commentReviseDialog.setContentView(R.layout.dialog_subjectcomment);

        Pick();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.addButton:
                    boolean alreadyCommented = false;
                    for(SubjectComment subjectComment : subject_.getComments()){
                        if(subjectComment.getUser_id().equals(mAuth.getUid())) alreadyCommented = true;
                    }

                    if(alreadyCommented == true){
                        Toast.makeText(getApplicationContext(), "이미 수강평을 작성한 과목입니다.", Toast.LENGTH_LONG).show();
                    }
                    else{
                        showAddDialog();
                        break;
                    }
            }
        }
    };

    // comment 추가하는 다이얼로그
    public void showAddDialog() {
        commentAddDialog.show();
        RatingBar commentRB = commentAddDialog.findViewById(R.id.commentRB);
        EditText contentET = commentAddDialog.findViewById(R.id.contentET);
        commentRB.setRating(0);
        contentET.setText("");

        Button noBtn = commentAddDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentAddDialog.dismiss();
            }
        });
        commentAddDialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                float rating = commentRB.getRating();
                String content = contentET.getText().toString();

                SubjectComment subjectComment = new SubjectComment(content, mAuth.getUid(), Float.toString(rating));
                subject_.getComments().add(subjectComment);
                db.collection("Subject").document(subjectName).set(subject_);
                commentAddDialog.dismiss();
                makeComment();
            }
        });
    }

    public void showReviseDialog(int pos) {
        commentReviseDialog.show();
        RatingBar commentRB = commentReviseDialog.findViewById(R.id.commentRB);
        EditText contentET = commentReviseDialog.findViewById(R.id.contentET);
        commentRB.setRating(Float.parseFloat(subject_.getComments().get(pos).getRating()));
        contentET.setText(subject_.getComments().get(pos).getContent());

        Button noBtn = commentReviseDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentReviseDialog.dismiss();
            }
        });
        commentReviseDialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                float rating = commentRB.getRating();
                String content = contentET.getText().toString();

                subject_.getComments().get(pos).setContent(content);
                subject_.getComments().get(pos).setRating(Float.toString(rating));
                subject_.getComments().get(pos).setUser_id(mAuth.getUid());

                db.collection("Subject").document(subjectName).set(subject_);
                commentReviseDialog.dismiss();
                makeComment();
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
                subjectCommentAdapter.setOnItemListener(new SubjectCommentAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos, String option) {
                        if(option.equals("revise")){
                            if(subject_.getComments().get(pos).getUser_id().equals(mAuth.getUid())){
                                showReviseDialog(pos);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "자신이 작성한 수강평만 수정할 수 있습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            if(subjectComments.get(pos).getUser_id().equals(mAuth.getUid())){
                                subject_.getComments().remove(pos);
                                db.collection("Subject").document(subjectName).set(subject_);
                                subjectCommentAdapter.notifyDataSetChanged();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "자신이 작성한 수강평만 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });


    }

    //픽률 추출하기
    public void Pick(){

        ArrayList<String> nextnames = new ArrayList<>();
        ArrayList<Float> firsts = new ArrayList<Float>();
        ArrayList<String> n_nextnames = new ArrayList<>();
        ArrayList<Float> seconds = new ArrayList<Float>();

        mStore.collection("UsersTableInfo").document("Matrix")// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Table table = documentSnapshot.toObject(Table.class);
                        Map<String,String> curtable =  new HashMap<>();
                        curtable=table.getTable().get(subjectName);

                        float total = Integer.parseInt(curtable.get(subjectName));

                        List<String> listKeySet = new ArrayList<>(curtable.keySet());

                        Map<String, String> finalCurtable = curtable;

                        listKeySet.sort((o1,o2) -> Integer.parseInt(finalCurtable.get(o2)) - Integer.parseInt(finalCurtable.get(o1)) );


                      
                        int i=0;
                        for( String key : listKeySet ){
                            if(i>4) break;
                            if(!key.equals(subjectName)){
                                nextnames.add(key);
                                float curr = Integer.parseInt(finalCurtable.get(key));
                                if (total > 0) {
                                    firsts.add((float) (curr / total));

                                } else {
                                    firsts.add((float) (0));
                                }
                                ++i;
                            }
                        }

                        for(int j=0;j<5;++j) {
                            Map<String, String> nextnametable = table.getTable().get(nextnames.get(j));
                            float ntotal = Integer.parseInt(nextnametable.get(nextnames.get(j)));
                            float maxi=0;
                            String maxstring = null;
                            for( String Nkey : nextnametable.keySet()){
                                if(Integer.parseInt(nextnametable.get(Nkey))>maxi){
                                    maxstring=Nkey;
                                    maxi=Integer.parseInt(nextnametable.get(Nkey));
                                }
                            }
                            if(maxstring!=null){ n_nextnames.add(maxstring);}
                            else{n_nextnames.add("데이터가 없습니다");}

                            if(ntotal>0) {
                                seconds.add((float)maxi/ntotal);
                            }
                            else{
                                seconds.add((float) (0));
                            }
                        }


                        for(int ii=0;ii<5;++ii){
                            Picksubject picksubject = new Picksubject(subjectName, nextnames.get(ii),n_nextnames.get(ii),firsts.get(ii),seconds.get(ii));
                            Picklist.add(picksubject);
                        }
                        pickAdapter = new PickAdapter(Picklist);
                        picksubjectList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        picksubjectList.setAdapter(pickAdapter);

                    }
                });


    }
}
