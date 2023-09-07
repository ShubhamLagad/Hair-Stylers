package com.subhdroid.hairstylers.Customer.CustomerFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.ParlourProductModel;
import com.subhdroid.hairstylers.Parlour.ParlourModel;
import com.subhdroid.hairstylers.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CustomerHome extends Fragment {
    private static ArrayList<ParlourModel> parloursList;
    DatabaseReference parlourRef = FirebaseDatabase.getInstance().getReference("parlour");
    Handler handler = new Handler();

    LinearLayoutManager linearLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAllParlours();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customer_home, container, false);

        LottieAnimationView singleCardAnimation = view.findViewById(R.id.singleCardAnimation);
        singleCardAnimation.setVisibility(View.VISIBLE);



        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                singleCardAnimation.setVisibility(View.GONE);

                RecyclerView allParlourRecyclerView = view.findViewById(R.id.allParlourRecyclerView);
                linearLayoutManager = new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false);
                allParlourRecyclerView.setLayoutManager(linearLayoutManager);
                RecyclerAllParloursDetailsAdapter adapter = new RecyclerAllParloursDetailsAdapter(getContext(), parloursList);
                allParlourRecyclerView.setAdapter(adapter);

                LinearSnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(allParlourRecyclerView);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < (adapter.getItemCount() - 1)) {
                            linearLayoutManager.smoothScrollToPosition(allParlourRecyclerView,
                                    new RecyclerView.State(),
                                    linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1);
                        } else if (linearLayoutManager.findLastCompletelyVisibleItemPosition() ==
                                (adapter.getItemCount() - 1)) {
                            linearLayoutManager.smoothScrollToPosition(allParlourRecyclerView,
                                    new RecyclerView.State(),
                                    0);
                        }

                    }
                }, 2000, 4000);

            }
        }, 4000);



//        productsRecyclerView
        RecyclerView productRecycler = view.findViewById(R.id.productsRecyclerView);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        productRecycler.setLayoutManager(linearLayoutManager2);


        ArrayList<ParlourProductModel> productList = new ArrayList<>();
        productList.add(new ParlourProductModel( "prodName","prodPrice","description",
                 "5", "parlourName","parlourEmail"));

        productList.add(new ParlourProductModel( "Trimer","800","Good looking",
                 "3", "parlourName","parlourEmail"));

        productList.add(new ParlourProductModel( "Sprey","500","description",
                 "1", "Hair stylers","parlourEmail"));

        productList.add(new ParlourProductModel( "Handwash","200","description",
                 "4", "Goa mens","parlourEmail"));

        RecyclerAllProductsAdapter adapter = new RecyclerAllProductsAdapter(getContext(),
                productList);
        productRecycler.setAdapter(adapter);

        LinearSnapHelper snapHelper1 = new LinearSnapHelper();
        snapHelper1.attachToRecyclerView(productRecycler);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (linearLayoutManager2.findLastCompletelyVisibleItemPosition() < (adapter.getItemCount() - 1)) {
                    linearLayoutManager2.smoothScrollToPosition(productRecycler,
                            new RecyclerView.State(),
                            linearLayoutManager2.findLastCompletelyVisibleItemPosition() + 1);
                } else if (linearLayoutManager2.findLastCompletelyVisibleItemPosition() ==
                        (adapter.getItemCount() - 1)) {
                    linearLayoutManager2.smoothScrollToPosition(productRecycler,
                            new RecyclerView.State(),
                            0);
                }

            }
        }, 4000, 4000);

        return view;
    }


    public void getAllParlours() {
        parloursList = new ArrayList<>();
        parlourRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Array> dataMap = (HashMap<String, Array>) dataSnapshot.getValue();
                parloursList = new ArrayList<>();
                for (String key : dataMap.keySet()) {

                    parlourRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            ParlourModel parlour = snapshot.getValue(ParlourModel.class);

                            parloursList.add(new ParlourModel(
                                    parlour.getParlourName(),
                                    parlour.getSubtitle(),
                                    parlour.getPhone(),
                                    parlour.getEmail(),
                                    parlour.getParlourTypes(),
                                    parlour.getOpeningTime(),
                                    parlour.getClosingTime(),
                                    parlour.getAddress(),
                                    parlour.getPhoto(),
                                    parlour.getToken()));
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