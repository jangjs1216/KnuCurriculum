package com.UniPlan.loginregister.Intro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.UniPlan.loginregister.MainActivity;
import com.UniPlan.loginregister.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class SplashActivity extends AppIntro {
    Fragment mSplash1 = new SplashFragment1();
    Fragment mSplash2 = new SplashFragment2();
    Fragment mSplash3 = new SplashFragment3();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("반갑습니다!", "앱 사용법을 소개해 드려요",  R.drawable.uniplan_logo, ContextCompat.getColor(getApplicationContext(), R.color.main_color)));
        addSlide(mSplash1);
        addSlide(mSplash2);
        addSlide(mSplash3);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startMainActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startMainActivity();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment,
                               @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}