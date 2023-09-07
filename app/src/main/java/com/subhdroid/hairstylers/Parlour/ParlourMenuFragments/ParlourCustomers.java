package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.subhdroid.hairstylers.Customer.CustomerModel;
import com.subhdroid.hairstylers.Parlour.ParlourLogin;
import com.subhdroid.hairstylers.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ParlourCustomers extends Fragment {

    ArrayList<ParlourCustomersModel> parlourCustomersList = new ArrayList<>();
    AppCompatButton addCustomerFab;
    DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("transaction");
    String username;
    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_parlour_customers, container, false);
        SharedPreferences pref = getContext().getSharedPreferences("Parlour", Context.MODE_PRIVATE);
        username = pref.getString("parlourEmail", "Email");
        Dialog addCustomerDialog = new Dialog(getContext());
        addCustomerDialog.setContentView(R.layout.add_customer_dialog);
        //        successDialog.setCancelable(false);
        getParlourCustomers();
        LottieAnimationView loadingAnimation = view.findViewById(R.id.loadingAnimation);
        loadingAnimation.setVisibility(View.VISIBLE);
        addCustomerFab = view.findViewById(R.id.addCustomerFab);
        addCustomerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Log", "Button click");
                addCustomerDialog.show();
            }
        });

        TextView addCustomerClose = addCustomerDialog.findViewById(R.id.addCustomerClose);
        addCustomerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCustomerDialog.dismiss();
            }
        });

        AppCompatButton newCustomerAddBtn = addCustomerDialog.findViewById(R.id.newCustomerAddBtn);

        Dialog addCustomerSuccessDialog = new Dialog(getContext());
        addCustomerSuccessDialog.setContentView(R.layout.new_customer_added_succes_dialog);
        addCustomerSuccessDialog.setCancelable(false);

        newCustomerAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCustomerDialog.dismiss();
                addCustomerSuccessDialog.show();
            }
        });

        AppCompatButton customerAddedOkBtn =
                addCustomerSuccessDialog.findViewById(R.id.customerAddedOkBtn);
        customerAddedOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCustomerSuccessDialog.dismiss();
            }
        });

        RecyclerView recyclerParlourCustomersView = view.findViewById(R.id.recyclerParlourCustomers);
        recyclerParlourCustomersView.setLayoutManager(new LinearLayoutManager(getContext()));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingAnimation.setVisibility(View.GONE);
                RecyclerParlourCustomersAdapter adapter =
                        new RecyclerParlourCustomersAdapter(getContext(), parlourCustomersList);
                recyclerParlourCustomersView.setAdapter(adapter);
            }
        }, 4000);

        return view;
    }


    private void getParlourCustomers() {
        transactionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                for (String key : dataMap.keySet()) {
                    transactionRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ParlourCustomersModel customer = snapshot.getValue(ParlourCustomersModel.class);
                            if (username.equals(customer.getParlourEmail())) {
                                parlourCustomersList.add(new ParlourCustomersModel(customer.getCustomerName(), customer.getMobile(), customer.getService(), customer.getDate()));
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
                Toast.makeText(getContext().getApplicationContext(), "Fail to get data.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}