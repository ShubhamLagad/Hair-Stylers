package com.subhdroid.hairstylers.Customer.CustomerFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.subhdroid.hairstylers.Customer.CustomerModel;
import com.subhdroid.hairstylers.Customer.CustomerRegistration;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.TransactionModel;
import com.subhdroid.hairstylers.Parlour.ParlourModel;
import com.subhdroid.hairstylers.Parlour.ParlourRegistration;
import com.subhdroid.hairstylers.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;


public class CustomerQRScanner extends Fragment {
    String str;
    View view;
    AppCompatButton scanAgainBtn;
    Toolbar customerToolbar;

    final HashMap<String, String> customerDetails = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View toolView = inflater.inflate(R.layout.customer_app_bar_layout, container, false);
        customerToolbar = toolView.findViewById(R.id.customerToolbar);

        view = inflater.inflate(R.layout.fragment_customer_q_r_scanner, container, false);
        ScanCode();
        scanAgainBtn = view.findViewById(R.id.scanAgainBtn);

        scanAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanCode();
            }
        });
        return view;
    }


    private void ScanCode() {
        SharedPreferences pref = getContext().getSharedPreferences("Customer", Context.MODE_PRIVATE);
        String custEmail = pref.getString("customerEmail", "customer@gmail.com");
        getCustomerDetails(custEmail);

        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureActivity.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {

            Dialog thankYouDialog = new Dialog(getContext());
            thankYouDialog.setContentView(R.layout.thank_you_dialog);
            thankYouDialog.setCancelable(false);
            thankYouDialog.show();

            AppCompatButton thankYouOkBtn = thankYouDialog.findViewById(R.id.thankYouOkBtn);

            thankYouOkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    insertTransaction(result.getContents());
                    thankYouDialog.dismiss();
                    customerToolbar.setTitle("Dashboard");

                    CustomerHome fragment2 = new CustomerHome();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.customerFragmentContainer, fragment2);
                    fragmentTransaction.commit();

                }
            });
        }
    });


    private void insertTransaction(String parlourDetails) {


        String[] arrOfStr = parlourDetails.split("==", 7);


        String parlourName = arrOfStr[0];
        String parlourEmail = arrOfStr[1];
        String date = arrOfStr[2];
        String time = arrOfStr[3];
        String workerEmail = arrOfStr[4];
        String service = arrOfStr[5];
        String price = arrOfStr[6];

        String mobile = customerDetails.get("mobile");
        String token = customerDetails.get("token");
        String custEmail = customerDetails.get("email");
        String custName = customerDetails.get("name");


        DatabaseReference transactionrRef = FirebaseDatabase.getInstance().getReference(
                "transaction");

        TransactionModel transactionModel = new TransactionModel(custName,custEmail, mobile,
                parlourEmail,
                parlourName, workerEmail, service, price, date, time, token);

        String transactionID = transactionrRef.push().getKey();

        transactionrRef.child(transactionID).setValue(transactionModel);

    }


    private void getCustomerDetails(String email) {

        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference(
                "customer");

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
                                customerDetails.put("name", customer.getCustName());
                                customerDetails.put("email", email);
                                customerDetails.put("mobile", customer.getCustPhone());
                                customerDetails.put("token", customer.getFcmToken());
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