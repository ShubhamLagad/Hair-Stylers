package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.subhdroid.hairstylers.Parlour.ParlourModel;
import com.subhdroid.hairstylers.Parlour.ParlourRegistration;
import com.subhdroid.hairstylers.R;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ParlourSlots extends Fragment {

    TextView manageSlotOpenTime, manageSlotCloseTime;
    EditText manageOneSlotPeriod;
    AppCompatButton manageSlotBtn, manageSlotUpdateBtn;

    String username = "";
    Handler handler = new Handler();

    DatabaseReference parlourRef = FirebaseDatabase.getInstance().getReference("parlour");
    DatabaseReference parlourSlotsRef = FirebaseDatabase.getInstance().getReference("slots");

    private final static HashMap<String, String> parlourData = new HashMap<String, String>();
    ArrayList<ParlourSlotsModel> parlourSlotDeatils = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parlour_slots, container, false);

        SharedPreferences pref = getContext().getSharedPreferences("Parlour", Context.MODE_PRIVATE);
        username = pref.getString("parlourEmail", "Email");

        LottieAnimationView loadingAnimation = view.findViewById(R.id.loadingAnimation);
        loadingAnimation.setVisibility(View.VISIBLE);

        Dialog manageSlotDialog = new Dialog(getContext());
        manageSlotDialog.setContentView(R.layout.manage_slot_dialog);

        manageSlotBtn = view.findViewById(R.id.manageSlotBtn);
        manageSlotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageSlotDialog.show();
            }
        });
        TextView addCustomerClose = manageSlotDialog.findViewById(R.id.addCustomerClose);
        addCustomerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageSlotDialog.dismiss();
            }
        });

        manageSlotOpenTime = manageSlotDialog.findViewById(R.id.manageSlotOpenTime);
        manageSlotCloseTime = manageSlotDialog.findViewById(R.id.manageSlotCloseTime);

        manageSlotOpenTime.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (TimePickerDialog.OnTimeSetListener) (view1, hourOfDay, minute1) -> {
                        String format = showTime(hourOfDay);
                        if (minute1 == 0) {
                            manageSlotOpenTime.setText(hourOfDay + ":" + minute1 + "0" + " " + format);
                        } else {
                            manageSlotOpenTime.setText(hourOfDay + ":" + minute1 + " " + format);
                        }

                    }, hour, minute, false);
            timePickerDialog.show();
        });

        manageSlotCloseTime.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (TimePickerDialog.OnTimeSetListener) (view2, hourOfDay, minute1) -> {
                        String format = showTime(hourOfDay);
                        if (minute1 == 0) {
                            manageSlotCloseTime.setText(hourOfDay + ":" + minute1 + "0" + " " + format);
                        } else {
                            manageSlotCloseTime.setText(hourOfDay + ":" + minute1 + " " + format);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });


        manageSlotUpdateBtn = manageSlotDialog.findViewById(R.id.manageSlotTimeUpdateBtn);

        manageSlotUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSlots();
                manageSlotDialog.dismiss();
            }
        });



        getParlourSlots();
        RecyclerView recyclerParlourSlotView = view.findViewById(R.id.recyclerParlourSlots);
        recyclerParlourSlotView.setLayoutManager(new GridLayoutManager(getContext(), 1));


        handler.postDelayed(() -> {
            Log.d("Log","SLot Length : "+parlourSlotDeatils.size());
            loadingAnimation.setVisibility(View.GONE);
            RecyclerParlourSlotAdapter adapter = new RecyclerParlourSlotAdapter(getContext(),
                    parlourSlotDeatils);
            recyclerParlourSlotView.setAdapter(adapter);
        }, 4000);

        return view;
    }


    public void updateSlots() {
        String T1 = (manageSlotOpenTime.getText().toString()).split(" ", 2)[0];
        String T2 = (manageSlotCloseTime.getText().toString()).split(" ", 2)[0];
        int slotTime = 20;
        int no_of_slots = noOfSlots(T1, T2, slotTime);
        Toast.makeText(getContext(), String.valueOf(no_of_slots), Toast.LENGTH_SHORT).show();
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

    private int noOfSlots(String T1, String T2, int slotTime) {

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

        return (int) diffMinutes / slotTime;
    }


    private void getParlourData() {

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
                                parlourData.put("email", parlour.getEmail());
                                parlourData.put("slotPeriod", "30");
                                parlourData.put("openTime", parlour.getOpeningTime());
                                parlourData.put("closeTime", parlour.getClosingTime());
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


    private void getParlourSlots() {

        parlourSlotsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                for (String key : dataMap.keySet()) {

                    parlourSlotsRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ParlourSlotsModel parlourSlot =
                                    snapshot.getValue(ParlourSlotsModel.class);

                            if (username.equals(parlourSlot.getParlourEmail())) {
                                parlourSlotDeatils.add(parlourSlot);
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