package com.subhdroid.hairstylers.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.subhdroid.hairstylers.R;

public class CustomerLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}