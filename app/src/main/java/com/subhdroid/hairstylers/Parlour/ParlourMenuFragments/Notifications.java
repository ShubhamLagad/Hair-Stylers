package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.subhdroid.hairstylers.CloudMessage.FCMNotificationSender;
import com.subhdroid.hairstylers.CloudMessage.RecieverClass;
import com.subhdroid.hairstylers.Customer.CustomerDashboard;
import com.subhdroid.hairstylers.Customer.CustomerModel;
import com.subhdroid.hairstylers.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class Notifications extends Fragment {

    private static ArrayList<ParlourCustomersModel> customerList;
    DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("transaction");
    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("customer");
    Handler handler = new Handler();
    String username = "";
    AlarmManager alarmMnager;
    PendingIntent pi;
    Intent iBroadcast;
    TimePickerDialog timePickerDialog;
    TextView todayNotifyTimeTxt;
    AppCompatButton todayChangeTimeBtn;
    SwitchCompat todayNotificationSwitchBtn;

    public static final int DAILY_NOTIFICATION_TIMER_1 = 100;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        SharedPreferences pref = getContext().getSharedPreferences("Parlour", Context.MODE_PRIVATE);
        username = pref.getString("parlourEmail", "Email");
        getAllCustomers();
        LottieAnimationView todayCustomerLoadingAnimation =
                view.findViewById(R.id.todayCustomerLoadingAnimation);
        TextView browCustomerNotFoundTxt = view.findViewById(R.id.rowCustomerNotFoundTxt);

        todayCustomerLoadingAnimation.setVisibility(View.VISIBLE);

        todayNotificationSwitchBtn = view.findViewById(R.id.todayNotificationSwitchBtn);
        Intent i = new Intent(getContext(), RecieverClass.class);
        boolean alarmUp = (PendingIntent.getBroadcast(getContext(), Notifications.DAILY_NOTIFICATION_TIMER_1, i,
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            todayNotificationSwitchBtn.setChecked(true);
        }

        todayNotificationSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    setAlarm();
                } else {
                    cancelAlarm();
                }
            }
        });

        todayNotifyTimeTxt = view.findViewById(R.id.todayNotifyTimeTxt);
        todayNotifyTimeTxt.setText(getNotificationTime());

        todayChangeTimeBtn = view.findViewById(R.id.todayChangeTimeBtn);
        todayChangeTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePickerDialog(false);
            }
        });


        RecyclerView recyclerTodayCustomers = view.findViewById(R.id.recyclerTodayCustomers);
        recyclerTodayCustomers.setLayoutManager(new LinearLayoutManager(getContext()));

        AppCompatButton todayNotifyAllBtn = view.findViewById(R.id.todayNotifyAllBtn);

        todayNotifyAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cnt = 0;
                for (ParlourCustomersModel rec : customerList) {
                    cnt++;
                    getCustomerToken(rec.getCustomerEmail());
                    if (cnt == customerList.size()) {
                        Toast.makeText(getContext(), "Notification send to all Customers!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                todayCustomerLoadingAnimation.setVisibility(View.GONE);
                if (customerList.size() == 0) {
                    browCustomerNotFoundTxt.setVisibility(View.VISIBLE);
                }

                RecyclerTodayCustomerAdapter adapter = new RecyclerTodayCustomerAdapter(getContext(), customerList);
                recyclerTodayCustomers.setAdapter(adapter);
            }
        }, 4000);

        return view;
    }


    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(getContext(),
                onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                calSet.add(Calendar.DATE, 1);
            }

            setNotificationTime(hourOfDay + ":" + minute + " " + AMPMTime(hourOfDay));
            todayNotifyTimeTxt.setText(getNotificationTime());
            todayNotificationSwitchBtn.setChecked(false);
            cancelAlarm();
        }
    };


    private String getNotificationTime() {
        String time = "";
        try {
            File file = new File(getContext().getFilesDir(), "NotificationTime.json");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            String response = stringBuilder.toString();
            JSONObject jsonObject = new JSONObject(response);
            time = jsonObject.get("time").toString();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return time;
    }

    private void setNotificationTime(String ntime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("time", ntime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String userString = jsonObject.toString();
        try {
            File file = new File(getContext().getFilesDir(), "NotificationTime.json");
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(userString);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String AMPMTime(int hour) {
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

    public void getAllCustomers() {
        customerList = new ArrayList<ParlourCustomersModel>();

        transactionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                for (String key : dataMap.keySet()) {
                    transactionRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ParlourCustomersModel customer = snapshot.getValue(ParlourCustomersModel.class);
                            Boolean days = countDays(customer.getDate());
                            if (username.equals(customer.getParlourEmail()) && days) {
                                customerList.add(new ParlourCustomersModel(customer.getCustomerName(), customer.getCustomerEmail(), customer.getService()));
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

    public static Boolean countDays(String lastDate) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String todayDate = sdf.format(date);
        String ldate = lastDate.split(",", 2)[1];
        if (findDifference(ldate, todayDate) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static int findDifference(String start_date, String end_date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            long difference_In_Time = d2.getTime() - d1.getTime();
            int difference_In_Days = (int) ((difference_In_Time / (1000 * 60 * 60 * 24)) % 365);
            return difference_In_Days;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void notifyAllCustomers(String customerToken, String customerName) {
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        String userToken = customerToken;
        String allUserToken = "/topics/all";
        FCMNotificationSender notificationSender = new FCMNotificationSender(userToken,
                "Hi," + customerName +
                        "\nRemainder for your hair cut", "You have last 30 day ago you cuts your hair",
                getContext().getApplicationContext(),
                CustomerDashboard.class);

        notificationSender.sendNotifications();
    }

    private void getCustomerToken(String email) {
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
                                notifyAllCustomers(customer.getFcmToken(), customer.getCustName());
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


    public void setAlarm() {
        Toast.makeText(getContext(), "Notifications Set", Toast.LENGTH_LONG).show();
        String strTime = getNotificationTime();
        int hrs = Integer.parseInt(strTime.split(" ", 2)[0].split(":", 2)[0]);
        int min = Integer.parseInt(strTime.split(" ", 2)[0].split(":", 2)[1]);
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        calSet.set(Calendar.HOUR_OF_DAY, hrs);
        calSet.set(Calendar.MINUTE, min);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (calSet.compareTo(calNow) <= 0) {
            calSet.add(Calendar.DATE, 1);
        }

        iBroadcast = new Intent(getContext(), RecieverClass.class);
        pi = PendingIntent.getBroadcast(getContext(), DAILY_NOTIFICATION_TIMER_1, iBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMnager = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);

        alarmMnager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pi);
//        alarmMnager.setRepeating(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

    public void cancelAlarm() {
        Intent iBroadcast = new Intent(getContext(), RecieverClass.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 100, iBroadcast
                , 0);
        if (alarmMnager == null) {
            alarmMnager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        }
        alarmMnager.cancel(pendingIntent);
        Toast.makeText(getContext(), "Notifications Cancelled", Toast.LENGTH_LONG).show();
    }

}