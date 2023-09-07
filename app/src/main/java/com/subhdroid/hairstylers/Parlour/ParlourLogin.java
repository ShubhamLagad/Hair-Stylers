package com.subhdroid.hairstylers.Parlour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.subhdroid.hairstylers.LoginChoice;
import com.subhdroid.hairstylers.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParlourLogin extends AppCompatActivity {

    EditText parlourUsername, parlourPassword;
    TextView loginWarning, signUpTxt;
    AppCompatButton parlourLoginBtn;

    private static ArrayList<String> parlourKeys;
    private static HashMap<String, String> parlourList;

    DatabaseReference parlourRef = FirebaseDatabase.getInstance().getReference("parlour");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parlour_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getAllParloursKeys();

        parlourLoginBtn = findViewById(R.id.parlourLoginBtn);
        parlourUsername = findViewById(R.id.parlourUsername);
        parlourPassword = findViewById(R.id.parlourPassword);
        loginWarning = findViewById(R.id.loginWarning);
        signUpTxt = findViewById(R.id.signUpTxt);

        Dialog dialog = new Dialog(ParlourLogin.this);
        dialog.setContentView(R.layout.login_failed_dialog);
        parlourPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllParlours();
            }
        });

        parlourLoginBtn.setOnClickListener(view -> {

            String username = parlourUsername.getText().toString();
            String password = parlourPassword.getText().toString();

            if (checkValidUser(username, password)) {

                SharedPreferences pref = getSharedPreferences("Parlour", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("ParlourLoggedIn", true);
                editor.putString("parlourEmail", username);
                editor.apply();
                setNotificationTime();
                Intent intent = new Intent(ParlourLogin.this, ParlourDashboard.class);
                startActivity(intent);
            } else {
                dialog.show();
            }
        });

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParlourLogin.this, ParlourRegistration.class);
                startActivity(intent);
            }
        });

        AppCompatButton tryBtn = dialog.findViewById(R.id.tryBtn);
        tryBtn.setOnClickListener(view -> dialog.dismiss());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(ParlourLogin.this, LoginChoice.class);
        startActivity(intent);
    }

    private boolean checkValidUser(String username, String password) {
        for (Map.Entry<String, String> entry : parlourList.entrySet()) {
            if (username.equals(entry.getKey()) && password.equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }


    public void getAllParloursKeys() {
        parlourKeys = new ArrayList<String>();
        parlourRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                for (String key : dataMap.keySet()) {
                    parlourKeys.add(key);
                }
                getAllParlours();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ParlourLogin.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getAllParlours() {

        parlourList = new HashMap<String, String>();

        for (String record : parlourKeys) {
            parlourRef.child(record).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ParlourModel parlour = snapshot.getValue(ParlourModel.class);
                    parlourList.put(parlour.getEmail(), parlour.getPassword());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("DB Error : ", error.toString());
                }
            });
        }
    }

    private void setNotificationTime() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("time", "9:00 AM");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String userString = jsonObject.toString();
        try {
            File file = new File(getApplicationContext().getFilesDir(), "NotificationTime.json");
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(userString);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}