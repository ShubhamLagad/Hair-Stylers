package com.subhdroid.hairstylers.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class CustomerRegistration extends AppCompatActivity {
    boolean isAllFieldsChecked = false;
    public String customerToken = "";

    private static ArrayList<String> customerList;
    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("customer");

    TextView signInTxt;
    EditText custName, custPhone, custEmail, custPassword, custCPassword, custGender, custPincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_registration);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set Parlour type
        String[] parlourThreeTypes = {"Men", "Women"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, parlourThreeTypes);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.custGender);
        textView.setThreshold(3);
        textView.setAdapter(adapter);

        getAllCustomers();
        getToken();


        signInTxt = findViewById(R.id.signInTxt);
        custName = findViewById(R.id.custName);
        custPhone = findViewById(R.id.custPhone);
        custEmail = findViewById(R.id.custEmail);
        custPassword = findViewById(R.id.custPassword);
        custCPassword = findViewById(R.id.custCPassword);
        custGender = findViewById(R.id.custGender);
        custPincode = findViewById(R.id.custPincode);


        AppCompatButton signUpBtn = findViewById(R.id.customerSignInBtn);

        Dialog successDialog = new Dialog(CustomerRegistration.this);
        successDialog.setContentView(R.layout.activity_parlour_registration_success);
        successDialog.setCancelable(false);

        Dialog alreadyExistsDialog = new Dialog(CustomerRegistration.this);
        alreadyExistsDialog.setContentView(R.layout.already_exists_dialog);
        alreadyExistsDialog.setCancelable(false);

        signUpBtn.setOnClickListener(view -> {

            isAllFieldsChecked = CheckAllFields();

            if (isAllFieldsChecked) {
                if (checkExistingUser(custEmail.getText().toString())) {
                    Log.d("Log", "Existing user=============");
                    alreadyExistsDialog.show();

                } else {
                    insertRecord();
                    successDialog.show();
                }
            }
        });

        AppCompatButton okBtn = successDialog.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successDialog.dismiss();
                Intent intent = new Intent(CustomerRegistration.this, CustomerLogin.class);
                startActivity(intent);
            }
        });


        AppCompatButton loginBtn, cancelBtn;
        loginBtn = alreadyExistsDialog.findViewById(R.id.loginBtn);
        cancelBtn = alreadyExistsDialog.findViewById(R.id.cancelBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(CustomerRegistration.this, CustomerLogin.class);
                startActivity(intent);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alreadyExistsDialog.dismiss();
                custEmail.requestFocus();
            }
        });


        signInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerRegistration.this, CustomerLogin.class);
                startActivity(intent);
            }
        });

    }


    private void insertRecord() {

        Log.d("Log", "Onclick Token : " + customerToken);

        CustomerModel customerModel = new CustomerModel(custName.getText().toString(), custPhone.getText().toString(),
                custEmail.getText().toString(), custCPassword.getText().toString(),
                custGender.getText().toString(), custPincode.getText().toString(), getToken());

        String customerID = customerRef.push().getKey();

        customerRef.child(customerID).setValue(customerModel);
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

                        Log.d("Log ", "token in fun : " + token);
                        Toast.makeText(CustomerRegistration.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                });
        return customerToken;
    }


    private boolean CheckAllFields() {
        if (custName.length() == 0) {
            custName.setError("Name is required");
            custName.requestFocus();
            return false;
        }

        if (custPhone.length() == 0) {
            custPhone.setError("Phone is required");
            custPhone.requestFocus();
            return false;
        }

        if (custEmail.length() == 0) {
            custEmail.setError("Email is required");
            custEmail.requestFocus();
            return false;
        }
        if (custPassword.length() == 0) {
            custPassword.setError("Password is required");
            custPassword.requestFocus();
            return false;
        } else if (custPassword.length() < 8) {
            custPassword.setError("Password must be minimum 8 characters");
            custPassword.requestFocus();
            return false;
        }

        if (!((custCPassword.getText().toString()).equals((custCPassword.getText().toString())))) {
            custCPassword.setError("Password must be same");
            custCPassword.requestFocus();
            return false;
        }

        if (custGender.length() == 0 && (custGender.length() != 3 || custGender.length() != 5)) {
            custGender.setError("Gender is required");
            custGender.requestFocus();
            return false;
        }
        if (custPincode.length() == 0) {
            custPincode.setError("Pincode is required");
            custPincode.requestFocus();
            return false;
        }

        // after all validation return true.
        return true;
    }


    private boolean checkExistingUser(String username) {
        for (String email : customerList) {
            if (username.equals(email)) {
                return true;
            }
        }
        return false;
    }


    public void getAllCustomers() {
        customerList = new ArrayList<String>();
        customerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                for (String key : dataMap.keySet()) {
                    customerRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            CustomerModel customer = snapshot.getValue(CustomerModel.class);
                            customerList.add(customer.getCustEmail());
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
                Toast.makeText(CustomerRegistration.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}