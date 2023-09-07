package com.subhdroid.hairstylers.Parlour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.subhdroid.hairstylers.LoginChoice;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.Notifications;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.ParlourCustomers;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.ParlourQRCode;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.ParlourReport;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.ParlourServices;
import com.subhdroid.hairstylers.Parlour.ParlourMenuFragments.ParlourSlots;
import com.subhdroid.hairstylers.R;

public class ParlourDashboard extends AppCompatActivity {

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parlour_dashboard);

        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar.setTitle("Dashboard");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.openDrawer, R.string.closeDrawer);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        loadFragment(new ParlourReport(), 0);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.parlourCustomerItem) {
                    loadFragment(new ParlourCustomers(), 1);
                    toolbar.setTitle("Customers");
                } else if (id == R.id.parlourDashboardItem) {
                    loadFragment(new ParlourReport(), 1);
                    toolbar.setTitle("Dashboard");
                } else if (id == R.id.parlourSlotsItem) {
                    loadFragment(new ParlourSlots(), 1);
                    toolbar.setTitle("Today's Time Slots");
                } else if (id == R.id.parlourQRCcode) {
                    loadFragment(new ParlourQRCode(), 1);
                    toolbar.setTitle("QR Code");
                } else if (id == R.id.parlourServiceItem) {
                    loadFragment(new ParlourServices(), 1);
                    toolbar.setTitle("Services");
                }else if (id == R.id.parlourNotifications) {
                    loadFragment(new Notifications(), 1);
                    toolbar.setTitle("Notifications");
                }else if (id == R.id.parlourLogout) {
                    logOut();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ParlourDashboard.this);
            alertDialog.setTitle("Exit");
            alertDialog.setMessage("Do you want to exit app?");

            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            });

            alertDialog.setNeutralButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    public void loadFragment(Fragment fragment, int flag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (flag == 0) {
            ft.add(R.id.parlourFragmentContainer, fragment);

        } else {

            ft.replace(R.id.parlourFragmentContainer, fragment);
        }

        ft.commit();
    }


    private void logOut() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ParlourDashboard.this);
        alertDialog.setTitle("Logout");
        alertDialog.setMessage("Do you want to logout?");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences pref = getSharedPreferences("Parlour", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.putBoolean("ParlourLoggedIn", false);
                editor.apply();

                Intent intent = new Intent(ParlourDashboard.this, LoginChoice.class);
                startActivity(intent);
            }
        });

        alertDialog.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }





}