package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.subhdroid.hairstylers.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ParlourServices extends Fragment {

    String username = "";
    Handler handler = new Handler();
    RecyclerView recyclerParlourServicesView;
    LottieAnimationView serviceLoadingAnimation;
    EditText addServiceName;
    EditText addServicePrice;
    Dialog addServiceDialog;
    TextView noServiceTxt;
    private static ArrayList<ParlourServiceModel> parlourServicesList;
    private static ArrayList<HashMap<String, String>> serviceDetails;
    static View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parlour_services, container, false);
        SharedPreferences pref = getContext().getSharedPreferences("Parlour", Context.MODE_PRIVATE);
        username = pref.getString("parlourEmail", "Email");
        v = view;

        serviceLoadingAnimation = view.findViewById(R.id.serviceLoadingAnimation);
        noServiceTxt = view.findViewById(R.id.noServiceTxt);
        serviceLoadingAnimation.setVisibility(View.VISIBLE);
        getParlourServices();
        loadServices();

        AppCompatButton addServiceBtn = view.findViewById(R.id.addServiceBtn);


        addServiceDialog = new Dialog(getContext());
        addServiceDialog.setContentView(R.layout.add_service_dialog);


        addServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addServiceDialog.show();
            }
        });

        recyclerParlourServicesView = view.findViewById(R.id.recyclerParlourServices);
        recyclerParlourServicesView.setLayoutManager(new LinearLayoutManager(getContext()));

        AppCompatButton addServiceAddBtn = addServiceDialog.findViewById(R.id.addServiceAddBtn);

        addServiceAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addServiceName = addServiceDialog.findViewById(R.id.addServiceName);
                addServicePrice = addServiceDialog.findViewById(R.id.addServicePrice);

                if (CheckAllFields()) {
                    addNewService(addServiceName.getText().toString(), addServicePrice.getText().toString());

                }

            }
        });

        TextView addServiceClose = addServiceDialog.findViewById(R.id.addServiceClose);
        addServiceClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addServiceDialog.dismiss();
            }
        });

        return view;
    }

    private void loadServices() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                serviceLoadingAnimation.setVisibility(View.GONE);
                parlourServicesList = new ArrayList<>();
                if (serviceDetails != null) {
                    for (HashMap record : serviceDetails) {
                        parlourServicesList.add(new ParlourServiceModel(record.get("parlourEmail").toString(),
                                record.get("serviceName").toString(), record.get("price").toString()
                        ));
                    }

                    RecyclerParlourServicesAdapter adapter = new RecyclerParlourServicesAdapter(getContext(),
                            parlourServicesList);
                    recyclerParlourServicesView.setAdapter(adapter);
                } else {
                    noServiceTxt.setVisibility(View.VISIBLE);
                }

            }
        }, 4000);
    }


    private void getParlourServices() {

        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services");
        servicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Log", "Get service call");
                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                if (dataMap != null) {
                    serviceDetails = new ArrayList<>();
                    for (String key : dataMap.keySet()) {

                        servicesRef.child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                ParlourServiceModel parlourService =
                                        snapshot.getValue(ParlourServiceModel.class);

                                if (username.equals(parlourService.getParlour_email())) {

                                    HashMap<String, String> singleServiceDetail = new HashMap<>();

                                    singleServiceDetail.put("parlourEmail", parlourService.getParlour_email());
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addNewService(String serviceName, String servicePrice) {
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference("services");
        ParlourServiceModel serviceModel = new ParlourServiceModel(username, serviceName, servicePrice);

        String serviceID = serviceRef.push().getKey();

        serviceRef.child(serviceID).setValue(serviceModel);
        addServiceDialog.dismiss();

    }


    private boolean CheckAllFields() {
        if (addServiceName.length() == 0) {
            addServiceName.setError("Name is required");
            addServiceName.requestFocus();
            return false;
        }

        if (addServicePrice.length() == 0) {
            addServicePrice.setError("Price is required");
            addServicePrice.requestFocus();
            return false;
        }
        return true;
    }

}