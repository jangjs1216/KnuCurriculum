package com.example.loginregister.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.loginregister.R;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        PreferencesManager.removeAccount(LogoutActivity.this);
        Log.e("###","로그아웃");
        Intent intent=new Intent(LogoutActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}