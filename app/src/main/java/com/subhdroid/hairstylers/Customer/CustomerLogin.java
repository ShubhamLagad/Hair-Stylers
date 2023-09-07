package com.subhdroid.hairstylers.Customer;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.subhdroid.hairstylers.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerLogin extends AppCompatActivity {
    boolean isAllFieldsChecked = false;
    public String customerToken = "";

    private static ArrayList<HashMap<String, String>> customerList;
    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("customer");

    AppCompatButton custLoginBtn;
    EditText custLoginUsername, custLoginPassword;
    TextView customerSignUpTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_login);

        getAllCustomers();
        custLoginUsername = findViewById(R.id.custLoginUsername);
        custLoginPassword = findViewById(R.id.custLoginPassword);
        custLoginBtn = findViewById(R.id.custLoginBtn);
        customerSignUpTxt = findViewById(R.id.customerSignUpTxt);

        getToken();
        Dialog dialog = new Dialog(CustomerLogin.this);
        dialog.setContentView(R.layout.login_failed_dialog);

        custLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckAllFields()) {
                    Boolean result = checkValidCustomer(custLoginUsername.getText().toString(),
                            custLoginPassword.getText().toString());

                    if (result) {
                        SharedPreferences pref = getSharedPreferences("Customer",MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("CustomerLoggedIn",true);
                        editor.putString("customerEmail",custLoginUsername.getText().toString());
                        editor.apply();

                        updateToken(custLoginUsername.getText().toString());
                        Intent intent = new Intent(CustomerLogin.this, CustomerDashboard.class);
                        startActivity(intent);
                    } else {
                        dialog.show();
                    }
                }
            }
        });

        AppCompatButton tryBtn = dialog.findViewById(R.id.tryBtn);
        tryBtn.setOnClickListener(view -> dialog.dismiss());

        customerSignUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerLogin.this,CustomerRegistration.class);
                startActivity(intent);
            }
        });
    }


    private boolean checkValidCustomer(String username, String password) {
        for (HashMap entry : customerList) {
            if (username.equals(entry.get("email")) && password.equals(entry.get("password"))) {
                return true;
            }
        }
        return false;
    }


    public void getAllCustomers() {
        customerList = new ArrayList<HashMap<String, String>>();
        customerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                for (String key : dataMap.keySet()) {
                    customerRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            HashMap<String, String> customerDetails = new HashMap<>();
                            CustomerModel customer = snapshot.getValue(CustomerModel.class);
                            customerDetails.put("email", customer.getCustEmail());
                            customerDetails.put("password", customer.getCustPassword());
                            customerList.add(customerDetails);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("DB Error : ", error.toString());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerLogin.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Log", "Fetching FCM registration token failed",
                                    task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        customerToken = token;
                    }
                });
        return customerToken;
    }

    private void updateToken(String email) {
        customerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                for (String key : dataMap.keySet()) {

                    customerRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            CustomerModel customer = snapshot.getValue(CustomerModel.class);

                            if (email.equals(customer.getCustEmail())) {
                                customerRef.child(key).child("fcmToken").setValue(customerToken);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("DB Error : ", error.toString());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerLogin.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private boolean CheckAllFields() {

        if (custLoginUsername.length() == 0) {
            custLoginUsername.setError("Username is required");
            custLoginUsername.requestFocus();
            return false;
        }
        if (custLoginUsername.length() == 0) {
            custLoginUsername.setError("Password is required");
            custLoginUsername.requestFocus();
            return false;
        }

        return true;
    }
}