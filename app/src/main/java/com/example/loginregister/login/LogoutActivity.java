package com.example.loginregister.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.loginregister.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        PreferencesManager.removeAccount(LogoutActivity.this);
        Log.e("###","로그아웃");
        mAuth.signOut();
        Intent intent=new Intent(LogoutActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}