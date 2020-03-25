package com.FoodPlanet.FoodPlanet1;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.FoodPlanet.FoodPlanet1.data.Chef;
import com.FoodPlanet.FoodPlanet1.ui.Chat;
import com.FoodPlanet.FoodPlanet1.ui.Help;
import com.FoodPlanet.FoodPlanet1.ui.ProfileActivity;
import com.FoodPlanet.FoodPlanet1.ui.Terms;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN ACTIVITY";
    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    AppBarConfiguration appBarConfiguration;
    BottomNavigationView navView;
    Button signOutButton;
    ImageView profilePic;
    TextView profileName, profileEmail;
    private DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Toast.makeText(this, "user Id: " + userId, Toast.LENGTH_SHORT).show();
        //attach some view by id
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        /*final Toolbar toolbar = findViewById(R.id.toolbar);

        //show home button
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);*/
        /*ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(this,drawer,toolbar,R.string.nav_app_bar_open_drawer_description,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/
       /* FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        getProfileInfo();

        //handle signing out
        signOutButton = findViewById(R.id.signOut_btn);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        //handle bottom navigation
        navView = findViewById(R.id.bottom_nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_search, R.id.nav_notification, R.id.nav_messages)
                .build();
        NavController bNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, bNavController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, bNavController);
        //handle nav drawer item selected
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if (id == R.id.nav_profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    Toast.makeText(MainActivity.this, "profile", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(MainActivity.this, Chat.class));
                    Toast.makeText(MainActivity.this, "Chat", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_Terms) {
                    startActivity(new Intent(MainActivity.this, Terms.class));
                    Toast.makeText(MainActivity.this, "Terms", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_help) {
                    startActivity(new Intent(MainActivity.this, Help.class));
                    Toast.makeText(MainActivity.this, "Help", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_home) {
                    drawer.closeDrawers();
                }
                return false;
            }
        });
    }

    // load fragment activity used in nav bottom
    /*private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }*/
    //get profile info
    private void getProfileInfo() {


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profilePic = findViewById(R.id.profile_pic_);
                profileEmail = findViewById(R.id.profile_email);
                profileName = findViewById(R.id.profile_name);
                Chef chef = dataSnapshot.getValue(Chef.class);
                Uri uri = Uri.parse(chef.getChefPic());


                Glide.with(MainActivity.this).asBitmap().load(uri)
                        .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(R.dimen.reduis)))
                        .into(profilePic);
                profileEmail.setText(chef.getChefEmail());
                profileName.setText(chef.getChefName());
                Log.d(TAG, "User name: " + chef.getChefName() + ", email " + chef.getChefEmail());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    //show progrss dialog
    private void showProgressDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(R.layout.progress_dialog);
        dialogBuilder.setCancelable(false);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    //hide progrss dialog
    private void hideProgressDialog(){  alertDialog.dismiss(); }

    //sign out method
    private void signOut() {
        showProgressDialog();
        FirebaseAuth.getInstance().signOut();
        hideProgressDialog();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
        Toast.makeText(MainActivity.this, "log out successfully", Toast.LENGTH_SHORT).show();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
