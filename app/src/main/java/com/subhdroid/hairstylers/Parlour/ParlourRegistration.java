package com.subhdroid.hairstylers.Parlour;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import com.subhdroid.hairstylers.R;


public class ParlourRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parlour_registration);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        TextView openTime,closeTime;

        openTime = findViewById(R.id.openTime);
        closeTime = findViewById(R.id.closeTime);

//        Parlour type
        String[] parlourType={"Men","Women","Unisex"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,parlourType);
        AutoCompleteTextView textView=(AutoCompleteTextView)findViewById(R.id.parlourTypes);
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
                        openTime.setText(hourOfDay + ":" + minute1+" "+format);
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
                        closeTime.setText(hourOfDay + ":" + minute1+" "+format);
                    }, hour, minute, false);
            timePickerDialog.show();
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