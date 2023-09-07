package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.subhdroid.hairstylers.Parlour.ParlourDashboard;
import com.subhdroid.hairstylers.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerParlourServicesAdapter extends RecyclerView.Adapter<RecyclerParlourServicesAdapter.ViewHolder> {

    Context context;
    ArrayList<ParlourServiceModel> parlourServicesArrayList;

    RecyclerParlourServicesAdapter(Context context,
                                   ArrayList<ParlourServiceModel> parlourServicesArrayList) {
        this.context = context;
        this.parlourServicesArrayList = parlourServicesArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.parlour_service_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rowServiceName.setText(parlourServicesArrayList.get(position).getService_name());
        holder.rowServicePrice.setText(parlourServicesArrayList.get(position).getPrice());
        holder.rowServiceNo.setText(String.valueOf(position + 1));
        int pos = position;
        holder.rowServiceEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Delete");
                alertDialog.setMessage("Do you want to delete service?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteService(parlourServicesArrayList.get(pos).getPrice(),
                                parlourServicesArrayList.get(pos).getService_name());
                        parlourServicesArrayList.remove(pos);
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
        return parlourServicesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rowServiceName, rowServicePrice, rowServiceNo, rowServiceEditBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowServiceName = itemView.findViewById(R.id.rowServiceName);
            rowServicePrice = itemView.findViewById(R.id.rowServicePrice);
            rowServiceNo = itemView.findViewById(R.id.rowServiceNo);
            rowServiceEditBtn = itemView.findViewById(R.id.rowServiceEditBtn);
        }
    }


    private void deleteService(String price, String serviceName) {
        SharedPreferences pref = context.getSharedPreferences("Parlour", Context.MODE_PRIVATE);
        String username = pref.getString("parlourEmail", "Email");
        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services");

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

                            if (username.equals(parlourService.getParlour_email()) && price.equals(parlourService.getPrice()) && serviceName.equals(parlourService.getService_name())) {
                                snapshot.getRef().removeValue();

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