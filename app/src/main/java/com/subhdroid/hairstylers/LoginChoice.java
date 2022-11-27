package com.subhdroid.hairstylers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.subhdroid.hairstylers.Customer.CustomerLogin;
import com.subhdroid.hairstylers.Parlour.ParlourLogin;
import com.subhdroid.hairstylers.Worker.WorkerLogin;


public class LoginChoice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choise);

        getSupportActionBar().setTitle("Login Choice");
        ColorDrawable colorDrawable
                = new ColorDrawable(getResources().getColor(R.color.icon_color));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        AppCompatButton customerBtn,parlourBtn,workerBtn,signUpBtn;

        customerBtn = findViewById(R.id.customerBtn);
        parlourBtn = findViewById(R.id.parlourBtn);
        workerBtn = findViewById(R.id.workerBtn);
        signUpBtn = findViewById(R.id.signUpBtn);


        customerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginChoice.this, CustomerLogin.class);
            startActivity(intent);
        });

        parlourBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginChoice.this, ParlourLogin.class);
            startActivity(intent);
        });

        workerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginChoice.this, WorkerLogin.class);
            startActivity(intent);
        });

        signUpBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginChoice.this, RegistrationChoice.class);
            startActivity(intent);
        });
    }
}