package com.subhdroid.hairstylers;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.subhdroid.hairstylers.Customer.CustomerLogin;
import com.subhdroid.hairstylers.Parlour.ParlourLogin;
import com.subhdroid.hairstylers.Worker.WorkerLogin;


public class LoginChoice extends AppCompatActivity {
    AppCompatButton customerBtn, parlourBtn, workerBtn;
    TextView signUpTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choise);


        customerBtn = findViewById(R.id.customerBtn);
        parlourBtn = findViewById(R.id.parlourBtn);
        workerBtn = findViewById(R.id.workerBtn);
        signUpTxt = findViewById(R.id.signUpTxt);


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

        signUpTxt.setOnClickListener(view -> {
            Intent intent = new Intent(LoginChoice.this, RegistrationChoice.class);
            startActivity(intent);
        });

//        FirebaseMessaging.getInstance().subscribeToTopic("all");
//        signUpTxt.setOnClickListener(view -> {
//
//            Log.d("Log","signup clickes======");
//            String userToken = "d8htoFhYQ6mjZTHXvaiD8y:APA91bGDnsfdvUTZ5Ckm1QmOa7hg-sp7rwShqifCgM55HhqpMXz95ge5iDlu-sS7P1h0Gi36DXJ3e0zPKA9uWTlVkJAoSGgW944pz8seBEIeohOECb-gT3OViX4oIxqwYmqUc58W82J5";
//            String allUserToken = "/topics/all";
//            FCMNotificationSender notificationSender = new FCMNotificationSender(userToken,
//                    "hello","message",getApplicationContext(),LoginChoice.this);
//
//
//            notificationSender.sendNotifications();
//
//        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginChoice.this);
        alertDialog.setTitle("Exit");
        alertDialog.setMessage("Do you want to exit app?");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });

        alertDialog.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }
}