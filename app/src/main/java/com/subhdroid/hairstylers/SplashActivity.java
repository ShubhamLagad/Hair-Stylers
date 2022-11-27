package com.subhdroid.hairstylers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        TextView appTitle;

        appTitle = findViewById(R.id.appTitle);
        Animation scale = AnimationUtils.loadAnimation(SplashActivity.this,
                R.anim.scale);
        appTitle.setAnimation(scale);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginChoice.class);
                startActivity(intent);
                finish();
            }
        },1000);
    }
}