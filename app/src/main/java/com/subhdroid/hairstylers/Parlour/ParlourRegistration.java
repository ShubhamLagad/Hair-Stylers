package com.subhdroid.hairstylers.Parlour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
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
import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.ParlourSlotsModel;
import com.subhdroid.hairstylers.R;


public class ParlourRegistration extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 22;
    private ImageView imageView;
    private String imgUrl = "";
    boolean isAllFieldsChecked = false;
    int uploadFlag = 0;
    public String parlourToken = "";

    private static ArrayList<String> parlourKeys;
    private static ArrayList<String> parlourList;
    DatabaseReference parlourRef = FirebaseDatabase.getInstance().getReference("parlour");
    DatabaseReference parlourSlotRef = FirebaseDatabase.getInstance().getReference(
            "slots");
    private Uri filePath;

    EditText parlourName, subtitle, phone, email, password, cpassword, address, pincode;
    AutoCompleteTextView parlourType;
    TextView openTime, closeTime, parlourSignInTxt, photoWarn;
    AppCompatButton btnChoose, btnUpload;
    CardView imageCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parlour_registration);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getAllParloursKeys();
        getToken();

        // Set Parlour type
        String[] parlourThreeTypes = {"Men", "Women", "Unisex"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, parlourThreeTypes);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.parlourType);
        textView.setThreshold(3);
        textView.setAdapter(adapter);

        parlourName = findViewById(R.id.parlourName);
        subtitle = findViewById(R.id.subtitle);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);
        parlourType = findViewById(R.id.parlourType);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        // Opening time and closing time
        openTime = findViewById(R.id.openTime);
        closeTime = findViewById(R.id.closeTime);
        photoWarn = findViewById(R.id.photoWarn);


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
        imageView = findViewById(R.id.imgView);
        imageCardView = findViewById(R.id.imageCardView);

        btnChoose.setOnClickListener(view -> SelectImage());

        btnUpload.setOnClickListener(view -> uploadImage());


        parlourSignInTxt = findViewById(R.id.parlourSignInTxt);
        parlourSignInTxt.setOnClickListener(view -> {
            Intent intent = new Intent(ParlourRegistration.this, ParlourLogin.class);
            startActivity(intent);
        });


        AppCompatButton signUpBtn = findViewById(R.id.signUpBtn);

        Dialog successDialog = new Dialog(ParlourRegistration.this);
        successDialog.setContentView(R.layout.activity_parlour_registration_success);
        successDialog.setCancelable(false);

        Dialog alreadyExistsDialog = new Dialog(ParlourRegistration.this);
        alreadyExistsDialog.setContentView(R.layout.already_exists_dialog);
        alreadyExistsDialog.setCancelable(false);

        signUpBtn.setOnClickListener(view -> {

            isAllFieldsChecked = CheckAllFields();

            if (isAllFieldsChecked) {
                if (checkExistingUser(email.getText().toString())) {
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
                Intent intent = new Intent(ParlourRegistration.this, ParlourLogin.class);
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
                Intent intent = new Intent(ParlourRegistration.this, ParlourLogin.class);
                startActivity(intent);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alreadyExistsDialog.dismiss();
                email.requestFocus();
            }
        });
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


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                btnChoose.setText("Choose another image");
                imageCardView.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // UploadImage method world file

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

            uploadFlag = 1;
            photoWarn.setVisibility(View.INVISIBLE);
        }
    }


    private void insertRecord() {

        int no_of_slots = createSlots(openTime.getText().toString(), closeTime.getText().toString(),
                email.getText().toString());

        DatabaseReference parlourRef = FirebaseDatabase.getInstance().getReference(
                "parlour");
        ParlourModel parlourModel = new ParlourModel(parlourName.getText().toString(),
                subtitle.getText().toString(),
                phone.getText().toString(), email.getText().toString(), cpassword.getText().toString(), parlourType.getText().toString(),
                openTime.getText().toString(), closeTime.getText().toString(), no_of_slots,
                address.getText().toString(), pincode.getText().toString(), imgUrl,parlourToken);

        String parloutID = parlourRef.push().getKey();

        parlourRef.child(parloutID).setValue(parlourModel);
    }


    private int createSlots(String parlourOpenTime, String parlourCloseTime,
                            String parlourSlotEmail) {
        String otime = parlourOpenTime.split(" ")[0];
        String ctime = parlourCloseTime.split(" ")[0];
        int slot = 0;
        try {
            String myTime = otime;
            String newTime = "";


            while (checkTime(newTime, ctime)) {
                slot++;

                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                Date d = df.parse(myTime);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.MINUTE, 40);
                newTime = df.format(cal.getTime());


                String mytimeFormat = showTime(Integer.parseInt(myTime.split(":")[0]));
                String newTimeFormat = showTime(Integer.parseInt(newTime.split(":")[0]));

                String slotTime = myTime + " " + mytimeFormat + " - " + newTime + " " + newTimeFormat;

                ParlourSlotsModel parlourSlotModel =
                        new ParlourSlotsModel(slotTime, "NONE", "NONE",
                                parlourSlotEmail, "NONE","NONE","NONE");

                String parloutSlotID = parlourSlotRef.push().getKey();

                parlourSlotRef.child(parloutSlotID).setValue(parlourSlotModel);

                myTime = newTime;
            }

        } catch (ParseException e) {
            Log.d("Log", "Parse Exception : " + e);
        }
        return slot;
    }


    private static Boolean checkTime(String T1, String T2) {
        String[] allTimes = {T1, T2};
        String maxTime = Stream.of(allTimes).max(String::compareTo).get();

        if (maxTime.equals(T2)) {
            return true;
        } else {
            return false;
        }
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

    private boolean CheckAllFields() {
        if (parlourName.length() == 0) {
            parlourName.setError("Parlour name is required");
            parlourName.requestFocus();
            return false;
        }

        if (phone.length() == 0) {
            phone.setError("Phone is required");
            phone.requestFocus();
            return false;
        }

        if (email.length() == 0) {
            email.setError("Email is required");
            email.requestFocus();
            return false;
        }
        if (password.length() == 0) {
            password.setError("Password is required");
            password.requestFocus();
            return false;
        } else if (password.length() < 8) {
            password.setError("Password must be minimum 8 characters");
            password.requestFocus();
            return false;
        }

        if (!((password.getText().toString()).equals((cpassword.getText().toString())))) {
            cpassword.setError("Password must be same");
            cpassword.requestFocus();
            return false;
        }

        if (parlourType.length() == 0) {
            parlourType.setError("Parlour type is required");
            parlourType.requestFocus();
            return false;
        }
        if (openTime.length() == 0) {
            openTime.setError("Open time is required");
            openTime.requestFocus();
            return false;
        }
        if (closeTime.length() == 0) {
            closeTime.setError("Close time is required");
            closeTime.requestFocus();
            return false;
        }
        if (address.length() == 0) {
            address.setError("Address is required");
            address.requestFocus();
            return false;
        } 
        if (pincode.length() == 0) {
            pincode.setError("Pincode is required");
            pincode.requestFocus();
            return false;
        }
        if (uploadFlag == 0) {
            btnChoose.setError("Required");
            photoWarn.setVisibility(View.VISIBLE);
            btnChoose.requestFocus();
            return false;
        }
        return true;
    }


    private boolean checkExistingUser(String username) {
        for (String email : parlourList) {
            if (username.equals(email)) {
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
                Toast.makeText(ParlourRegistration.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getAllParlours() {

        parlourList = new ArrayList<String>();

        for (String record : parlourKeys) {
            parlourRef.child(record).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ParlourModel parlour = snapshot.getValue(ParlourModel.class);
                    parlourList.add(parlour.getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("DB Error : ", error.toString());
                }
            });
        }
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
                        parlourToken = token;
                    }
                });
        return parlourToken;
    }
}