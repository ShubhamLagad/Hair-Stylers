package com.subhdroid.hairstylers.Parlour;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.subhdroid.hairstylers.R;

public class ParlourLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parlour_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}