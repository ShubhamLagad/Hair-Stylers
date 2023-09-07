package com.subhdroid.hairstylers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.subhdroid.hairstylers.Customer.CustomerDashboard;
import com.subhdroid.hairstylers.Parlour.ParlourDashboard;
import com.subhdroid.hairstylers.Parlour.ParlourRegistration;

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

                Intent intent;

                SharedPreferences parlourPref = getSharedPreferences("Parlour", MODE_PRIVATE);
                SharedPreferences customerPref = getSharedPreferences("Customer", MODE_PRIVATE);
                SharedPreferences workerPref = getSharedPreferences("Worker", MODE_PRIVATE);

                if (parlourPref.getBoolean("ParlourLoggedIn", false)) {

                    intent = new Intent(SplashActivity.this, ParlourDashboard.class);

                } else if (customerPref.getBoolean("CustomerLoggedIn", false)) {

                    intent = new Intent(SplashActivity.this, CustomerDashboard.class);

                } else if (workerPref.getBoolean("WorkerLoggedIn", false)) {

                    intent = new Intent(SplashActivity.this, LoginChoice.class);

                } else {

                    intent = new Intent(SplashActivity.this, LoginChoice.class);

                }

                startActivity(intent);
                finish();

            }
        }, 1000);
    }
}