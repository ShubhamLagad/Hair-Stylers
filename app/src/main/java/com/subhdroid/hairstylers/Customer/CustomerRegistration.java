package com.subhdroid.hairstylers.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.subhdroid.hairstylers.R;

public class CustomerRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_registration);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}