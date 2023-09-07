package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.subhdroid.hairstylers.CloudMessage.FCMNotificationSender;
import com.subhdroid.hairstylers.Customer.CustomerDashboard;
import com.subhdroid.hairstylers.Customer.CustomerModel;
import com.subhdroid.hairstylers.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerTodayCustomerAdapter extends RecyclerView.Adapter<RecyclerTodayCustomerAdapter.ViewHolder> {

    Context context;
    ArrayList<ParlourCustomersModel> todayCustomersArrayList;
    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("customer");


    RecyclerTodayCustomerAdapter(Context context,
                                 ArrayList<ParlourCustomersModel> todayCustomersArrayList) {
        this.context = context;
        this.todayCustomersArrayList = todayCustomersArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.todays_single_customer_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rowTodayCustomerName.setText(todayCustomersArrayList.get(position).getCustomerName());
        holder.rowTodayLastService.setText(todayCustomersArrayList.get(position).getService());
        holder.rowTodaySrNo.setText(String.valueOf(position + 1));


        int pos = position;

        holder.rowTodayCustomerNotifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCustomerToken(todayCustomersArrayList.get(pos).getCustomerEmail());
            }
        });
    }

    @Override
    public int getItemCount() {
        return todayCustomersArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rowTodayCustomerName, rowTodayLastService, rowTodaySrNo, rowTodayCustomerNotifyBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowTodayCustomerName = itemView.findViewById(R.id.rowTodayCustomerName);
            rowTodayLastService = itemView.findViewById(R.id.rowTodayLastService);
            rowTodaySrNo = itemView.findViewById(R.id.rowTodaySrNo);
            rowTodayCustomerNotifyBtn = itemView.findViewById(R.id.rowTodayCustomerNotifyBtn);
        }
    }

    private void sendNotification(String customerToken) {
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        String userToken = customerToken;
        String allUserToken = "/topics/all";
        FCMNotificationSender notificationSender = new FCMNotificationSender(userToken,
                "Remainder for your hair cut", "It's been a month since your last haircut just " +
                "checking up with you on what date to book your next appointment ?",
                context.getApplicationContext(),
                CustomerDashboard.class);

        notificationSender.sendNotifications();
        Toast.makeText(context.getApplicationContext(), "Notification send one Customers!",
                Toast.LENGTH_SHORT).show();

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
                                sendNotification(customer.getFcmToken());
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
                Toast.makeText(context.getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
