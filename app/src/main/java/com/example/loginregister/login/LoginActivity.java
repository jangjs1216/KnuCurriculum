package com.example.loginregister.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregister.MainActivity;
import com.example.loginregister.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

import org.apache.log4j.chainsaw.Main;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mEtPwd; // 회원가입 입력필드

    SignInButton Google_Login; // 구글 로그인 버튼
    private static final int RC_SIGN_IN = 1000; // 구글 로그인 결과 코드
    private FirebaseAuth mAuth; // 파이어베이스 인증 객체
    private GoogleApiClient mGoogleApiClient; // 구글 API 클라이언트 객체


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth=FirebaseAuth.getInstance();
        mEtEmail=findViewById(R.id.et_email);
        mEtPwd=findViewById(R.id.et_pwd);
        Google_Login=findViewById(R.id.Google_Login);
        TextView btn_login = findViewById(R.id.btn_login);
        TextView btn_register = findViewById(R.id.btn_register);
        CheckBox automatic_login=findViewById(R.id.automatic_login);

        //자동로그인
//        SharedPreferences account_info=getSharedPreferences("user_info",Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=account_info.edit();
//        String login_id=account_info.getString("id","");
//        String login_pw=account_info.getString("pw","");

        if(PreferencesManager.getAccount(LoginActivity.this).length()!=0)
        {
            Log.e("###","sibal");
            Toast.makeText(LoginActivity.this,"로그인 성공",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btn_login.setOnClickListener(v -> {
            // 로그인 요청
            String strEmail = mEtEmail.getText().toString();
            String strPwd = mEtPwd.getText().toString();
            Log.e("Login","로그인요청");
            mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.e("###","로그인 정보 맞음");
                        // 자동로그인 버튼을 눌렀을 때
                        if(automatic_login.isChecked()) {
                            Log.e("###","자동로그인 버튼 눌림");
                            PreferencesManager.storeAccount(LoginActivity.this,mAuth.getUid());
                        }
                        Log.e("Login","로그인성공");
                        Toast.makeText(LoginActivity.this,"로그인 성공",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Log.e("Login","로그인실패");
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        mAuth=FirebaseAuth.getInstance();

        // 구글 로그인 버튼 클릭했을 때 여기서 수행
        Google_Login.setOnClickListener(view -> {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent,RC_SIGN_IN);
        });


        btn_register.setOnClickListener(view -> {
            // 회원가입 화면으로 이동
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { // 구글 로그인 인증 요청했을 때 결과 값 돌려받는 곳
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //구글 로그인 성공해서 파베에 인증
                GoogleSignInAccount account = result.getSignInAccount(); // account라는 데이터 : 구글 로그인 정보를 담고 있음
                firebaseAuthWithGoogle(account); // 로그인 결과 값 출력 수행하라는 메소드
            } else {
                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // 로그인 성공했을 경우
                            Log.e("Login","로그인성공");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else { // 로그인 실패했을 경우
                            Log.e("Login","로그인실패");
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
    }


}