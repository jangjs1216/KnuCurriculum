package com.UniPlan.loginregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.UniPlan.loginregister.login.LoginActivity;
import com.UniPlan.loginregister.R;

public class StartLoadActivity extends Activity {


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        setContentView(R.layout.start_show);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
