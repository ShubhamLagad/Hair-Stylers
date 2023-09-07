package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subhdroid.hairstylers.R;

import java.util.ArrayList;

public class RecyclerParlourCustomersAdapter extends RecyclerView.Adapter<RecyclerParlourCustomersAdapter.ViewHolder> {

    Context context;
    ArrayList<ParlourCustomersModel> parlourCustomersArrayList;

    RecyclerParlourCustomersAdapter(Context context, ArrayList<ParlourCustomersModel> parlourCustomersArrayList) {
        this.context = context;
        this.parlourCustomersArrayList = parlourCustomersArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.parlour_customer_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rowCustomerName.setText(parlourCustomersArrayList.get(position).getCustomerName());
        holder.rowLastVisit.setText("Last Visit : "+parlourCustomersArrayList.get(position).getDate());
        holder.rowLastService.setText("Last Services : "+parlourCustomersArrayList.get(position).getService());
    }

    @Override
    public int getItemCount() {
        return parlourCustomersArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rowCustomerName, rowLastVisit, rowLastService;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowCustomerName = itemView.findViewById(R.id.rowCustomerName);
            rowLastVisit = itemView.findViewById(R.id.rowLastVisit);
            rowLastService = itemView.findViewById(R.id.rowLastService);
        }
    }
}
