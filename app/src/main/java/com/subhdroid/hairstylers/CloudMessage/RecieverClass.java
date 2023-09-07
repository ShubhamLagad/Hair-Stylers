package com.subhdroid.hairstylers.CloudMessage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.subhdroid.hairstylers.Customer.CustomerDashboard;
import com.subhdroid.hairstylers.Customer.CustomerModel;
import com.subhdroid.hairstylers.Parlour.ParlourDashboard;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.Notifications;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.ParlourCustomersModel;
import com.subhdroid.hairstylers.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class RecieverClass extends BroadcastReceiver {

    private static ArrayList<ParlourCustomersModel> customerList;
    DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("transaction");
    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("customer");
    Handler handler = new Handler();
    String username = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        getAllCustomers(context);

        if (isInternetConnection(context)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int cnt = 0;
                    for (ParlourCustomersModel rec : customerList) {
                        cnt++;
                        getCustomerToken(rec.getCustomerEmail(), context);

                        if (cnt == customerList.size()) {
                            Toast.makeText(context.getApplicationContext(), "Notification send to all Customers!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }, 4000);

        } else {
            showNotification(context);
        }

    }

    public boolean isInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    public void showNotification(Context context) {
        String channel_id = "Internet error channel";
        Toast.makeText(context, "Show notification call", Toast.LENGTH_SHORT).show();

        NotificationManagerCompat nm = NotificationManagerCompat.from(context.getApplicationContext());

        Intent snoozeIntent = new Intent(context.getApplicationContext(), ParlourDashboard.class);

        snoozeIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        snoozeIntent.putExtra("Retry", true);

        PendingIntent snoozePendingIntent = PendingIntent.getActivity(context.getApplicationContext(),
                1, snoozeIntent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext(), channel_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setSmallIcon(R.mipmap.hair_stylers_icon_round)
                    .setContentTitle("Some notifications are pending!")
                    .setContentText("Please send notifications to today's customers.")
                    .setAutoCancel(false)
                    .setContentIntent(snoozePendingIntent)
                    .addAction(R.mipmap.hair_stylers_icon_foreground, "Retry", snoozePendingIntent)
                    .setChannelId(channel_id)
                    .build();

            nm.createNotificationChannel(new NotificationChannel(channel_id, "Today Customers",
                    NotificationManager.IMPORTANCE_HIGH));
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.hair_stylers_icon_round)
                    .setContentTitle("Some notifications are pending!")
                    .setContentText("Please send notifications to today's customers.")
                    .setAutoCancel(false)
                    .setContentIntent(snoozePendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "Retry", snoozePendingIntent)
                    .build();
        }

        nm.notify(1, notificationBuilder.build());
    }


    public void getAllCustomers(Context context) {

        SharedPreferences pref = context.getSharedPreferences("Parlour", Context.MODE_PRIVATE);
        username = pref.getString("parlourEmail", "Email");

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

                            Boolean days = Notifications.countDays(customer.getDate());

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
                Toast.makeText(context.getApplicationContext(), "Fail to get data.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void notifyAllCustomers(String customerToken, String customerName, Context context) {
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        String userToken = customerToken;
        String allUserToken = "/topics/all";
        FCMNotificationSender notificationSender = new FCMNotificationSender(userToken,
                "Hi," + customerName +
                        "\nRemainder for your hair cut", "It's been a month since your last haircut just checking up with you on what date to book your next appointment ?",
                context.getApplicationContext().getApplicationContext(),
                CustomerDashboard.class);

        notificationSender.sendNotifications();
    }


    private void getCustomerToken(String email, Context context) {
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
                                notifyAllCustomers(customer.getFcmToken(), customer.getCustName(), context);
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
                Toast.makeText(context.getApplicationContext(), "Fail to get data.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
