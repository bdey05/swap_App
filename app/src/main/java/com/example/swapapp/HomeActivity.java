package com.example.swapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Button trade;
    private Button listings;
    private Button myStuff;
    public static FirebaseUser user;
    private TextView username, email, navUser;
    private ImageView navImage,homeImage;
    private FirebaseAuth mAuth;
    private Button logout;
    private Button admin;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        username = (TextView) findViewById(R.id.name_ID);
        homeImage = (ImageView) findViewById(R.id.homeIcon);
        email = (TextView) findViewById(R.id.email_ID);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if(user != null) {
            username.setText("Name: " + user.getDisplayName());
            email.setText("Email: " + user.getEmail());
            homeImage.setImageDrawable(getResources().getDrawable(R.drawable.tempusericon));
        }
        /** admin = (Button) findViewById(R.id.adminPage);
        db.collection("accounts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("username").equals(user.getDisplayName())){
                            if(document.getBoolean("admin")){
                                admin.setVisibility(View.VISIBLE);
                            }
                            else{
                                admin.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addListenOnButton(); **/

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        updateNavHeader();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Fragment fragment;

                if (id == R.id.stuff_ID) {
                    fragment = new SwapListFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.drawerLayout, fragment);
                    ft.commit();
                }
                else if(id==R.id.trade_ID){
                    Intent j = new Intent(HomeActivity.this, SwapActivity.class);
                    startActivity(j);
                }
                else if(id==R.id.listing_ID){
                    Intent j = new Intent(HomeActivity.this, Listings.class);
                    startActivity(j);
                }
                else if(id==R.id.trades_ID){
                    Intent j = new Intent(HomeActivity.this, ViewTradesActivity.class);
                    startActivity(j);
                }
                else if(id==R.id.login_Page){
                    fragment = new LoginFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.drawerLayout, fragment);
                    ft.commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }
    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.navigationView);

        View headerView = navigationView.getHeaderView(0);
        navUser = (TextView) headerView.findViewById(R.id.textView1);
        navImage = (ImageView) headerView.findViewById(R.id.navImage);
        if(user != null) {
            navUser.setText("Name: " + user.getDisplayName());

            navImage.setImageDrawable(getResources().getDrawable(R.drawable.navlogo));
        }
    }

}
