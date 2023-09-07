package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.subhdroid.hairstylers.Parlour.ParlourDashboard;
import com.subhdroid.hairstylers.Parlour.ParlourModel;
import com.subhdroid.hairstylers.Parlour.ParlourRegistration;
import com.subhdroid.hairstylers.R;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class ParlourQRCode extends Fragment {
    ImageView imageView;
    TextView qrParlourName, qrDateTime;
    AppCompatButton generateQRCodeBtn;
    LottieAnimationView qrLoading, allLoading;
    LinearLayout linearLayout;
    final Handler handler = new Handler();
    private final static HashMap<String, String> parlourData = new HashMap<String, String>();
    DatabaseReference parlourRef = FirebaseDatabase.getInstance().getReference("parlour");
    DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services");

    String username = "";

    ArrayList<ParlourServiceModel> parlourServicesList = new ArrayList<>();
    private static ArrayList<HashMap<String, String>> serviceDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parlour_q_r_code, container, false);
        getParlourServices();
        getParlourData();

        SharedPreferences pref = getContext().getSharedPreferences("Parlour", Context.MODE_PRIVATE);
        username = pref.getString("parlourEmail", "Email");

        imageView = view.findViewById(R.id.image_view);
        qrParlourName = view.findViewById(R.id.qrParlourName);
        qrDateTime = view.findViewById(R.id.qrDateTime);
        generateQRCodeBtn = view.findViewById(R.id.generateQRCodeBtn);

        qrLoading = view.findViewById(R.id.qrLoading);
        allLoading = view.findViewById(R.id.allLoading);
        linearLayout = view.findViewById(R.id.linearLayout);
        allLoading.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);


        RecyclerView checkboxRecyclerView = view.findViewById(R.id.checkboxRecyclerView);
        checkboxRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                for (HashMap record : serviceDetails) {
                    parlourServicesList.add(new ParlourServiceModel(username,
                            record.get("serviceName").toString(), record.get("price").toString()
                    ));
                }
                allLoading.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                RecyclerParlourServiceCheckboxAdapter adapter = new RecyclerParlourServiceCheckboxAdapter(getContext(),
                        parlourServicesList);
                checkboxRecyclerView.setAdapter(adapter);
            }
        }, 3000);


//        View serviceCheckBoxView = inflater.inflate(R.layout.parlour_service_checkbox_row, container,
//                false);

//        AppCompatCheckBox serviceCheckBox = serviceCheckBoxView.findViewById(R.id.serviceCheckbox);


        generateQRCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String allService = "";
                int price = 0;
                for (int i = 0; i < parlourServicesList.size(); i++) {

                    AppCompatCheckBox serviceCheckBox = checkboxRecyclerView.findViewById(i + 1);
                    Log.d("Log","Value : "+serviceCheckBox.getText());
                    if (serviceCheckBox.isChecked()) {

                        allService += serviceCheckBox.getText() + ",";
                        price += Integer.parseInt(parlourServicesList.get(i).getPrice());
                    }
                }
                if (allService != "") {
                    getParlourData();
                    imageView.setVisibility(View.GONE);
                    qrParlourName.setVisibility(View.GONE);
                    qrDateTime.setVisibility(View.GONE);

                    qrLoading.setVisibility(View.VISIBLE);

                    String finalAllService = allService;
                    int finalPrice = price;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            qrLoading.setVisibility(View.GONE);
                            initQRCode(imageView, qrParlourName, qrDateTime, finalAllService, finalPrice);
                        }
                    }, 3000);
                } else {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("Please select at least one service");

                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.show();
                }

            }
        });

        return view;
    }


    private void initQRCode(ImageView imageView, TextView qrParlourName, TextView qrDateTime,
                            String allServices, int totalPrice) {
        String name = parlourData.get("name");
        String email = parlourData.get("email");
        String date = parlourData.get("date");
        String time = parlourData.get("time");
        String workerEmail = parlourData.get("email");

        Log.d("Log", "Services : " + allServices);
        Log.d("Log", "price : " + totalPrice);

        qrParlourName.setText(parlourData.get("name"));
        qrDateTime.setText(parlourData.get("date") + ",  " + parlourData.get("time"));

        StringBuilder textToSend = new StringBuilder();
        textToSend.append(name + "==" + email + "==" + date + "==" + time + "==" + workerEmail + "==" + allServices + "==" + totalPrice);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(textToSend.toString(), BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            qrParlourName.setVisibility(View.VISIBLE);
            qrDateTime.setVisibility(View.VISIBLE);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void getParlourData() {

        SharedPreferences pref = getContext().getSharedPreferences("Parlour", Context.MODE_PRIVATE);
        final String username = pref.getString("parlourEmail", "Email");

        parlourRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                for (String key : dataMap.keySet()) {

                    parlourRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ParlourModel parlour = snapshot.getValue(ParlourModel.class);

                            if (username.equals(parlour.getEmail())) {
                                Date dNow = new Date();
                                SimpleDateFormat dateFt = new SimpleDateFormat("E, dd-MM-yyyy");

                                SimpleDateFormat timeFt = new SimpleDateFormat("HH:mm a");

                                parlourData.put("email", parlour.getEmail());
                                parlourData.put("name", parlour.getParlourName());
                                parlourData.put("date", dateFt.format(dNow));
                                parlourData.put("time", timeFt.format(dNow));
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
                Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getParlourServices() {

        servicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                serviceDetails = new ArrayList<>();
                for (String key : dataMap.keySet()) {

                    servicesRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ParlourServiceModel parlourService =
                                    snapshot.getValue(ParlourServiceModel.class);

                            if (username.equals(parlourService.getParlour_email())) {

                                HashMap<String, String> singleServiceDetail = new HashMap<>();

                                singleServiceDetail.put("serviceName", parlourService.getService_name());
                                singleServiceDetail.put("price", parlourService.getPrice());

                                serviceDetails.add(singleServiceDetail);
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
                Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}