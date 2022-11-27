package com.subhdroid.hairstylers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.subhdroid.hairstylers.Customer.CustomerLogin;
import com.subhdroid.hairstylers.Customer.CustomerRegistration;
import com.subhdroid.hairstylers.Parlour.ParlourLogin;
import com.subhdroid.hairstylers.Parlour.ParlourRegistration;
import com.subhdroid.hairstylers.Worker.WorkerLogin;
import com.subhdroid.hairstylers.Worker.WorkerRegistration;

public class RegistrationChoice extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_choise);

        getSupportActionBar().setTitle("Registration Choice");
        ColorDrawable colorDrawable
                = new ColorDrawable(getResources().getColor(R.color.icon_color));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        AppCompatButton customerRegBtn,parlourRegBtn,workerRegBtn;

        customerRegBtn = findViewById(R.id.customerRegBtn);
        parlourRegBtn = findViewById(R.id.parlourRegBtn);
        workerRegBtn = findViewById(R.id.workerRegBtn);


        customerRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationChoice.this, CustomerRegistration.class);
                startActivity(intent);
            }
        });

        parlourRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationChoice.this, ParlourRegistration.class);
                startActivity(intent);
            }
        });

        workerRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationChoice.this, WorkerRegistration.class);
                startActivity(intent);
            }
        });
    }
}