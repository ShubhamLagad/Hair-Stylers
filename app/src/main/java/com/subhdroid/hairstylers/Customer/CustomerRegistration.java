package com.subhdroid.hairstylers.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.subhdroid.hairstylers.R;

public class CustomerRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_registration);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        TextView signInTxt;

        signInTxt = findViewById(R.id.signInTxt);

        signInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerRegistration.this,CustomerLogin.class);
                startActivity(intent);
            }
        });
    }
}