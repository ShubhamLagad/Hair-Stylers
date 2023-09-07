package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.subhdroid.hairstylers.R;

import java.util.ArrayList;

public class RecyclerParlourServiceCheckboxAdapter extends RecyclerView.Adapter<RecyclerParlourServiceCheckboxAdapter.ViewHolder> {

    Context context;
    ArrayList<ParlourServiceModel> parlourServicesArrayList;

    public RecyclerParlourServiceCheckboxAdapter(Context context,
                                                 ArrayList<ParlourServiceModel> parlourServicesArrayList) {
        this.context = context;
        this.parlourServicesArrayList = parlourServicesArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.parlour_service_checkbox_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.checkBox.setText(parlourServicesArrayList.get(position).getService_name());
        holder.checkBox.setId((position+1));
    }

    @Override
    public int getItemCount() {
        return parlourServicesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatCheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.serviceCheckbox);
        }
    }
}