package com.example.loginregister.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.loginregister.MainActivity;
import com.example.loginregister.R;

public class KeepLoginActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_login);

        if(SavedSharedPreferences.getUserName(getApplicationContext()).length()==0)
        {
            intent=new Intent(KeepLoginActivity.this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }
        else {
            intent=new Intent(KeepLoginActivity.this, MainActivity.class);
            intent.putExtra("GangSeo",SavedSharedPreferences.getUserName(this));
            startActivity(intent);
            this.finish();
        }
    }
}