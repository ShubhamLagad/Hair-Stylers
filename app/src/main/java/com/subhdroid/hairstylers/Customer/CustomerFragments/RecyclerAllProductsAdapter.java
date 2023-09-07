package com.subhdroid.hairstylers.Customer.CustomerFragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.ParlourProductModel;
import com.subhdroid.hairstylers.Parlour.ParlourModel;
import com.subhdroid.hairstylers.R;

import java.util.ArrayList;

public class RecyclerAllProductsAdapter extends RecyclerView.Adapter<RecyclerAllProductsAdapter.ViewHolder> {
    Context context;
    ArrayList<ParlourProductModel> productList;
    RecyclerAllProductsAdapter(Context context, ArrayList<ParlourProductModel> productList){
        this.context = context;
        this.productList = productList;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.parlour_product_row, parent, false);
        RecyclerAllProductsAdapter.ViewHolder viewHolder = new RecyclerAllProductsAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.productName.setText(productList.get(position).getProdName());
        holder.productPrice.setText(productList.get(position).getProdPrice());
        holder.productParlourName.setText(productList.get(position).getParlourName());
        int revCnt = Integer.parseInt(productList.get(position).getReview());

        for (int i=0;i<revCnt;i++){
            TextView txt = new TextView(context);
            txt.setWidth(50);
            txt.setHeight(50);
            txt.setPadding(8,0,8,0);
            txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_star_24,0,0,0);
            holder.productReview.addView(txt);
        }



    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName,productPrice,productParlourName;
        LinearLayout productReview;
        ImageView productImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productImg = itemView.findViewById(R.id.productImg);
            productPrice = itemView.findViewById(R.id.productPrice);
            productReview = itemView.findViewById(R.id.productReview);
            productParlourName = itemView.findViewById(R.id.productParlourName);

        }
    }
}
