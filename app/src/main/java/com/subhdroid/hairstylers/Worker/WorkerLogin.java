package com.subhdroid.hairstylers.Worker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.subhdroid.hairstylers.R;


public class WorkerLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}