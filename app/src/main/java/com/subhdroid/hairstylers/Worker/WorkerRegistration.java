package com.subhdroid.hairstylers.Worker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.subhdroid.hairstylers.R;

public class WorkerRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_registration);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}