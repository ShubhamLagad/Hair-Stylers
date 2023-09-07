package com.subhdroid.hairstylers.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.subhdroid.hairstylers.Customer.CustomerFragments.CustomerHome;
import com.subhdroid.hairstylers.Customer.CustomerFragments.CustomerQRScanner;
import com.subhdroid.hairstylers.Customer.CustomerFragments.CustomerSlot;
import com.subhdroid.hairstylers.LoginChoice;
import com.subhdroid.hairstylers.R;

public class CustomerDashboard extends AppCompatActivity {
    Toolbar customerToolbar;
    NavigationView customerNavigationView;
    DrawerLayout customerDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        customerToolbar = findViewById(R.id.customerToolbar);
        customerNavigationView = findViewById(R.id.customerNavigationView);
        customerDrawerLayout = findViewById(R.id.customerDrawerLayout);
        customerToolbar.setTitle("Dashboard");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, customerDrawerLayout, customerToolbar,
                R.string.openDrawer, R.string.closeDrawer);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        customerDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        loadFragment(new CustomerHome(), 0);

        customerNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.customerHomer) {

                    loadFragment(new CustomerHome(), 1);
                    customerToolbar.setTitle("Dashboard");

                } else if (id == R.id.customerQRCodeScanner) {

                    loadFragment(new CustomerQRScanner(), 1);
                    customerToolbar.setTitle("QR Scanner");

                } else if (id == R.id.customerSlot) {

                    loadFragment(new CustomerSlot(), 1);
                    customerToolbar.setTitle("Slot");

                } else if (id == R.id.customerLogout) {

                    logOut();

                }
                customerDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (customerDrawerLayout.isDrawerOpen(GravityCompat.START))
            customerDrawerLayout.closeDrawer(GravityCompat.START);
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CustomerDashboard.this);
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
            ft.add(R.id.customerFragmentContainer, fragment);

        } else {

            ft.replace(R.id.customerFragmentContainer, fragment);
        }

        ft.commit();
    }


    private void logOut() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CustomerDashboard.this);
        alertDialog.setTitle("Logout");
        alertDialog.setMessage("Do you want to logout?");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences pref = getSharedPreferences("Customer", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.putBoolean("CustomerLoggedIn", false);
                editor.apply();

                Intent intent = new Intent(CustomerDashboard.this, LoginChoice.class);
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