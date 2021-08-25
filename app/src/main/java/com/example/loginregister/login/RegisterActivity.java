package com.example.loginregister.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loginregister.Notice_B.Where_who_post;
import com.example.loginregister.R;
import com.example.loginregister.Table;
import com.example.loginregister.UserInfo.User_Info_Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private EditText mEtEmail, mEtPwd,mEtPwd2,enick; // 회원가입 입력필드
    private Button mBtnRegister; // 회원가입 버튼
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth=FirebaseAuth.getInstance();
        mEtEmail=findViewById(R.id.et_email);
        mEtPwd=findViewById(R.id.et_pwd);
        mEtPwd2=findViewById(R.id.et_pwd2);
        mBtnRegister=findViewById(R.id.btn_register);
        enick=findViewById(R.id.et_nick);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 처리 시작
                String strEmail=mEtEmail.getText().toString();
                String strPwd1=mEtPwd.getText().toString();
                String strPwd2=mEtPwd2.getText().toString();
                String nickname = enick.getText().toString();

                if(strPwd1.equals(strPwd2) && !TextUtils.isEmpty(nickname)){
                    //Firebase Auth 진행
                    mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd1).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();

                                //String idToken, String emailId, String password, String nickname, ArrayList<String> liked_Post
                                ArrayList<String> liked_Post = new ArrayList<>();
                                ArrayList<String> Mypost = new ArrayList<>();
                                ArrayList<Table> tables = new ArrayList<>();
                                ArrayList<String> tableNames = new ArrayList<>();
                                ArrayList<String> Subscribed= new ArrayList<>();
                                ArrayList<String> specs = new ArrayList<>();
                                String total="",major="";
                                UserAccount userAccount = new UserAccount( user.getUid(),  strEmail, strPwd1, nickname, liked_Post ,Mypost, Subscribed,tables,tableNames,specs,total,major);
                                mStore.collection("user").document(user.getUid()).set(userAccount);
                                finish();


                                Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else if(!TextUtils.isEmpty(nickname)){
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}