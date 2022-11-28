package com.subhdroid.hairstylers.Parlour;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.subhdroid.hairstylers.R;


public class ParlourRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parlour_registration);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        TextView openTime, closeTime;

        openTime = findViewById(R.id.openTime);
        closeTime = findViewById(R.id.closeTime);

//        Parlour type
        String[] parlourThreeTypes = {"Men", "Women", "Unisex"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, parlourThreeTypes);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.parlourType);
        textView.setThreshold(3);
        textView.setAdapter(adapter);

//        Opening time and closing time
        openTime.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(ParlourRegistration.this,
                    (TimePickerDialog.OnTimeSetListener) (view, hourOfDay, minute1) -> {
                        String format = showTime(hourOfDay);
                        openTime.setText(hourOfDay + ":" + minute1 + " " + format);
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        closeTime.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(ParlourRegistration.this,
                    (TimePickerDialog.OnTimeSetListener) (view, hourOfDay, minute1) -> {
                        String format = showTime(hourOfDay);
                        closeTime.setText(hourOfDay + ":" + minute1 + " " + format);
                    }, hour, minute, false);
            timePickerDialog.show();
        });


        AppCompatButton selectImageBtn, uploadImageBtn;

        selectImageBtn = findViewById(R.id.selectImageBtn);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageBtn.setText("Image.jpg");
            }
        });


        TextView signInTxt;
        signInTxt = findViewById(R.id.signInTxt);

        signInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParlourRegistration.this, ParlourLogin.class);
                startActivity(intent);
            }
        });


//        Fetching form details

        EditText parlourName, subtitle, phone, email, password, cpassword, address, pincode;
        AppCompatButton signUpBtn;
        AutoCompleteTextView parlourType;

        parlourName = findViewById(R.id.parlourName);
        subtitle = findViewById(R.id.subtitle);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);
        parlourType = findViewById(R.id.parlourType);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);

        signUpBtn = findViewById(R.id.signUpBtn);


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ParlourRegistration.this, "Sign up clicked", Toast.LENGTH_SHORT).show();
                DatabaseReference parlourRef = FirebaseDatabase.getInstance().getReference(
                        "parlour");
                ParlourModel parlourModel = new ParlourModel(parlourName.getText().toString(),
                        subtitle.getText().toString(),
                        phone.getText().toString(), email.getText().toString(), cpassword.getText().toString(), parlourType.getText().toString(),
                        openTime.getText().toString(), closeTime.getText().toString(), address.getText().toString(), pincode.getText().toString(), selectImageBtn.getText().toString());

                String parloutID = parlourRef.push().getKey();

                parlourRef.child(parloutID).setValue(parlourModel);
            }
        });




    }

    public String showTime(int hour) {
        String format;
        if (hour == 0) {
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            format = "PM";
        } else {
            format = "AM";
        }
        return format;
    }
}