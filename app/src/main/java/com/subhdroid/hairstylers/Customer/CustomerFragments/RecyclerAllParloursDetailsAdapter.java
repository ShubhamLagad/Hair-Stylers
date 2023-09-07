package com.subhdroid.hairstylers.Customer.CustomerFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.subhdroid.hairstylers.Customer.CustomerModel;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.ParlourServiceModel;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.ParlourSlotsModel;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.RecyclerParlourServiceCheckboxAdapter;
import com.subhdroid.hairstylers.Parlour.ParlourModel;
import com.subhdroid.hairstylers.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class RecyclerAllParloursDetailsAdapter extends RecyclerView.Adapter<RecyclerAllParloursDetailsAdapter.ViewHolder> {
    int flag;
    Context context;
    ArrayList<ParlourModel> parloursArrayList;
    DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services");
    DatabaseReference slotRef = FirebaseDatabase.getInstance().getReference("slots");
    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("customer");
    HashMap<String, String> customerDetails = new HashMap<>();
    String username = "";
    RadioGroup slotsRadioGroup;

    private static ArrayList<ParlourServiceModel> serviceDetails = new ArrayList<>();
    private static ArrayList<ParlourSlotsModel> slotsDetails = new ArrayList<>();
    android.os.Handler handler = new Handler();

    RecyclerAllParloursDetailsAdapter(Context context, ArrayList<ParlourModel> parloursArrayList) {
        this.context = context;
        this.parloursArrayList = parloursArrayList;
    }

    @NonNull
    @Override
    public RecyclerAllParloursDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SharedPreferences pref = context.getSharedPreferences("Customer",
                Context.MODE_PRIVATE);
        username = pref.getString("customerEmail", "Email");
        getCustomerDetails();
        View v = LayoutInflater.from(context).inflate(R.layout.single_parlour_details_row, parent, false);
        RecyclerAllParloursDetailsAdapter.ViewHolder viewHolder = new RecyclerAllParloursDetailsAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.parlourName.setText(parloursArrayList.get(position).getParlourName());
        holder.parlourSubtitle.setText(parloursArrayList.get(position).getSubtitle());

        String time = checkTime(parloursArrayList.get(position).getOpeningTime(),
                parloursArrayList.get(position).getClosingTime());
        holder.parlourTime.setText(time);
        if (flag == 1) {
            holder.parlourTime.setTextColor(context.getResources().getColor(R.color.danger));
        }

        holder.parlourOffer.setText("50% off on products");
        Picasso.get().load(parloursArrayList.get(position).getPhoto()).into(holder.parlourImg);
        holder.parlourPhone.setText(parloursArrayList.get(position).getPhone());
        holder.parlourPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + holder.parlourPhone.getText()));
                context.startActivity(intent);
            }
        });

        Dialog slotBookingDialog = new Dialog(context);
        slotBookingDialog.setContentView(R.layout.slot_booking_dialog);
        slotBookingDialog.findViewById(R.id.bookSlotClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slotBookingDialog.dismiss();
            }
        });
        slotsRadioGroup =
                slotBookingDialog.findViewById(R.id.slotRadioGroup);
        LottieAnimationView loadingAnimation =
                slotBookingDialog.findViewById(R.id.loadingAnimation);

        LinearLayout bookingDetails = slotBookingDialog.findViewById(R.id.bookingDetails);

        RecyclerView serviceCheckboxRecyclerView =
                slotBookingDialog.findViewById(R.id.slotBookingServicesCheckboxRecyclerView);

        serviceCheckboxRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        String parlourEmail = parloursArrayList.get(position).getEmail();
        TextView suggestionTxt = slotBookingDialog.findViewById(R.id.slotSuggestionText);
        AppCompatButton dialogBookSlotBtn = slotBookingDialog.findViewById(R.id.dialogBookSlotBtn);

        holder.rowBookSlotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParlourServices(parlourEmail);
                getParlourSlots(parlourEmail);
                slotBookingDialog.show();
                loadingAnimation.setVisibility(View.VISIBLE);
                handler.postDelayed(() -> {
                    if (slotsDetails.size() == 0) {
                        suggestionTxt.setVisibility(View.VISIBLE);
                        dialogBookSlotBtn.setVisibility(View.GONE);
                    }
                    loadingAnimation.setVisibility(View.GONE);
                    bookingDetails.setVisibility(View.VISIBLE);

                    RecyclerParlourServiceCheckboxAdapter serviceAdapter =
                            new RecyclerParlourServiceCheckboxAdapter(context,
                                    serviceDetails);
                    serviceCheckboxRecyclerView.setAdapter(serviceAdapter);

                    slotsRadioGroup.removeAllViews();
                    for (int i=0;i<slotsDetails.size();i++){
                        RadioButton rb = new RadioButton(context);
                        rb.setText(slotsDetails.get(i).getSlotTime());
                        rb.setId((int)(Math.random()*1000000));
                        slotsRadioGroup.addView(rb);
                    }
                }, 4000);
            }
        });
        final String[] slotTime = new String[1];
        slotsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioBtn = slotBookingDialog.findViewById(i);
                slotTime[0] = radioBtn.getText().toString();
            }
        });
        dialogBookSlotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String services = "";
                for (int i = 1; i <= serviceDetails.size(); i++) {
                    AppCompatCheckBox serviceCheckbox =
                            serviceCheckboxRecyclerView.findViewById(i);
                    if (serviceCheckbox.isChecked()) {
                        services += serviceCheckbox.getText() + ",";
                    }
                }

                updateSlot(slotTime, services);

                slotBookingDialog.dismiss();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Success");
                alertDialog.setMessage("Slot Booked on " + slotTime[0]);
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return parloursArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView parlourName, parlourSubtitle, parlourTime, parlourOffer, parlourPhone;
        ImageView parlourImg;
        RecyclerView parlourRecyclerView;
        AppCompatButton rowBookSlotBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parlourName = itemView.findViewById(R.id.rowSalonName);
            parlourSubtitle = itemView.findViewById(R.id.rowSalonSubtitle);
            parlourTime = itemView.findViewById(R.id.rowSalonTime);
            parlourOffer = itemView.findViewById(R.id.rowSalonOffer);
            parlourImg = itemView.findViewById(R.id.rowSalonImg);
            parlourPhone = itemView.findViewById(R.id.rowSalonPhone);
            parlourRecyclerView = itemView.findViewById(R.id.allParlourRecyclerView);
            rowBookSlotBtn = itemView.findViewById(R.id.rowBookSlotBtn);
        }
    }


    private String checkTime(String T1, String T2) {

        int openHrs = Integer.parseInt(T1.split(" ", 2)[0].split(":", 2)[0]);
        int closeHrs = Integer.parseInt(T2.split(" ", 2)[0].split(":", 2)[0]);

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        if (hour < openHrs) {
            flag = 1;
            return "Closed (Opens at " + T1 + ")";
        } else if (hour >= openHrs && hour <= closeHrs) {
            flag = 0;
            return "Opened (Closing at " + T2 + ")";
        } else {
            flag = 0;
            return "Opened";
        }

    }

    private void getParlourServices(String parlourEmail) {
        serviceDetails = new ArrayList<>();
        servicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();

                for (String key : dataMap.keySet()) {

                    servicesRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ParlourServiceModel parlourService =
                                    snapshot.getValue(ParlourServiceModel.class);

                            if (parlourEmail.equals(parlourService.getParlour_email())) {
                                serviceDetails.add(parlourService);
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
                Toast.makeText(context, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getParlourSlots(String parlourEmail) {
        slotsDetails = new ArrayList<>();
        slotRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();

                for (String key : dataMap.keySet()) {

                    slotRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ParlourSlotsModel parlourSlotsModel =
                                    snapshot.getValue(ParlourSlotsModel.class);

                            if (parlourEmail.equals(parlourSlotsModel.getParlourEmail()) && parlourSlotsModel.getCustomerEmail().equals("NONE")) {
                                slotsDetails.add(parlourSlotsModel);

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
                Toast.makeText(context, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getCustomerDetails() {
        customerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                for (String key : dataMap.keySet()) {
                    customerRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            CustomerModel customer = snapshot.getValue(CustomerModel.class);
                            if (username.equals(customer.getCustEmail())) {
                                customerDetails.put("custEmail", customer.getCustEmail());
                                customerDetails.put("custName", customer.getCustName());
                                customerDetails.put("custPhone", customer.getCustPhone());
                                customerDetails.put("custToken", customer.getFcmToken());
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
                Toast.makeText(context, "Fail to get data.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSlot(String[] slotNo, String services) {
        slotRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();

                for (String key : dataMap.keySet()) {

                    slotRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ParlourSlotsModel parlourSlotsModel =
                                    snapshot.getValue(ParlourSlotsModel.class);
                            if (slotNo[0].equals(parlourSlotsModel.getSlotTime())) {
                                slotRef.child(key).child("customerEmail").setValue(customerDetails.get("custEmail"));
                                slotRef.child(key).child("slotCustomerName").setValue(customerDetails.get("custName"));
                                slotRef.child(key).child("customerPhone").setValue(customerDetails.get("custPhone"));
                                slotRef.child(key).child("customerToken").setValue(customerDetails.get(
                                        "custToken"));
                                slotRef.child(key).child("service").setValue(services);
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
                Toast.makeText(context, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
