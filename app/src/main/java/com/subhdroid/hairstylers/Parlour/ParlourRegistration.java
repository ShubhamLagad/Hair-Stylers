package com.subhdroid.hairstylers.Parlour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.subhdroid.hairstylers.R;


public class ParlourRegistration extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    private ImageView imageView;
    TextView openTime, closeTime;
    AppCompatButton btnChoose, btnUpload;
    TextView signInTxt;
    String imgUrl = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parlour_registration);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        // Set Parlour type
        String[] parlourThreeTypes = {"Men", "Women", "Unisex"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, parlourThreeTypes);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.parlourType);
        textView.setThreshold(3);
        textView.setAdapter(adapter);


        // Opening time and closing time
        openTime = findViewById(R.id.openTime);
        closeTime = findViewById(R.id.closeTime);

        openTime.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(ParlourRegistration.this,
                    (TimePickerDialog.OnTimeSetListener) (view, hourOfDay, minute1) -> {
                        String format = showTime(hourOfDay);
                        if (minute1 == 0) {
                            openTime.setText(hourOfDay + ":" + minute1 + "0" + " " + format);
                        } else {
                            openTime.setText(hourOfDay + ":" + minute1 + " " + format);
                        }

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
                        if (minute1 == 0) {
                            closeTime.setText(hourOfDay + ":" + minute1 + "0" + " " + format);
                        } else {
                            closeTime.setText(hourOfDay + ":" + minute1 + " " + format);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });


        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });


        signInTxt = findViewById(R.id.signInTxt);
        signInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParlourRegistration.this, ParlourLogin.class);
                startActivity(intent);
            }
        });


        AppCompatButton signUpBtn = findViewById(R.id.signUpBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertRecord();
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


    // Select Image method
    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        imageView = findViewById(R.id.imgView);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                btnChoose.setText("Choose another image");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // UploadImage method

    private void uploadImage() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            String timeStamp = new SimpleDateFormat("MMHHddmmssyyyy").format(new java.util.Date());
            StorageReference ref = FirebaseStorage.getInstance().getReference("ParlourImages").child(timeStamp);

            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("ParlourImages").child(
                            timeStamp);
                    Task<Uri> urlTask = storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgUrl = uri.toString();
                            Toast.makeText(ParlourRegistration.this, "Image Uploaded",
                                    Toast.LENGTH_SHORT).show();
//                                    ImageView fimg = findViewById(R.id.fimg);
//                                    Picasso.get().load(imageUrl).into(fimg);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ParlourRegistration.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }


    private void insertRecord() {

        EditText parlourName, subtitle, phone, email, cpassword, address, pincode;
        AutoCompleteTextView parlourType;

        parlourName = findViewById(R.id.parlourName);
        subtitle = findViewById(R.id.subtitle);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        cpassword = findViewById(R.id.cpassword);
        parlourType = findViewById(R.id.parlourType);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
//        To calculate slots
        String T1 = (openTime.getText().toString()).split(" ", 2)[0];
        String T2 = (closeTime.getText().toString()).split(" ", 2)[0];
        int no_of_slots = noOfSlots(T1, T2);

        DatabaseReference parlourRef = FirebaseDatabase.getInstance().getReference(
                "parlour");
        ParlourModel parlourModel = new ParlourModel(parlourName.getText().toString(),
                subtitle.getText().toString(),
                phone.getText().toString(), email.getText().toString(), cpassword.getText().toString(), parlourType.getText().toString(),
                openTime.getText().toString(), closeTime.getText().toString(),no_of_slots,
                address.getText().toString(), pincode.getText().toString(),imgUrl);

        String parloutID = parlourRef.push().getKey();

        parlourRef.child(parloutID).setValue(parlourModel);
    }


    private int noOfSlots(String T1, String T2) {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(T1);
            d2 = format.parse(T2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = d2.getTime() - d1.getTime();
        long diffMinutes = diff / (60 * 1000);

        return (int)diffMinutes/20;
    }
}