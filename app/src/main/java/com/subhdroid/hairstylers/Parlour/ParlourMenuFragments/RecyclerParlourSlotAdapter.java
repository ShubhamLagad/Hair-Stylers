package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subhdroid.hairstylers.R;

import java.util.ArrayList;

public class RecyclerParlourSlotAdapter  extends RecyclerView.Adapter<RecyclerParlourSlotAdapter.ViewHolder> {

    Context context;
    ArrayList<ParlourSlotsModel> parlourSlotsArrayList;

    public RecyclerParlourSlotAdapter(Context context,
                                      ArrayList<ParlourSlotsModel> parlourSlotsArrayList) {
        this.context = context;
        this.parlourSlotsArrayList = parlourSlotsArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.parlour_slot_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rowSlotTime.setText(parlourSlotsArrayList.get(position).getSlotTime());
        holder.rowSlotCustomerName.setText(parlourSlotsArrayList.get(position).getSlotCustomerName());
        holder.rowSlotNo.setText(String.valueOf(position+1));
        if (parlourSlotsArrayList.get(position).getSlotCustomerName().equals("NONE")){
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.unbooked));
        }else {
            holder.ll.setBackgroundColor(context.getResources().getColor(R.color.booked));
        }
    }

    @Override
    public int getItemCount() {

        return parlourSlotsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rowSlotTime, rowSlotCustomerName, rowSlotNo;
        LinearLayout ll;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowSlotTime = itemView.findViewById(R.id.rowSlotTime);
            rowSlotCustomerName = itemView.findViewById(R.id.rowSlotCustomerName);
            rowSlotNo = itemView.findViewById(R.id.rowSlotNo);
            ll = itemView.findViewById(R.id.ll);
        }
    }
}