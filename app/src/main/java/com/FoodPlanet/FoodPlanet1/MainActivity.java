package com.FoodPlanet.FoodPlanet1;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.FoodPlanet.FoodPlanet1.ui.home.homeFragment;
import com.FoodPlanet.FoodPlanet1.ui.notification.NotificationFragment;
import com.FoodPlanet.FoodPlanet1.ui.seach.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity  {

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       /* FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        //controlling nav drawer menu with its fragments
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_chat, R.id.nav_Terms, R.id.nav_help)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //handle signing out
        Button signOutButton=findViewById(R.id.signOut_btn);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                FirebaseAuth.getInstance().signOut();
                hideProgressDialog();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                Toast.makeText(MainActivity.this, "log out successfully", Toast.LENGTH_SHORT).show();
            }
        });


        /*//handle bottom nav
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_search :
                        toolbar.setVisibility(View.GONE);
                        loadFragment(new SearchFragment());
                    case R.id.nav_notification :
                        toolbar.setVisibility(View.VISIBLE);
                        toolbar.setTitle("notification");
                        loadFragment(new NotificationFragment());
                    case R.id.nav_channel :
                        toolbar.setVisibility(View.VISIBLE);
                        toolbar.setTitle("My Channel");
                        loadFragment(new homeFragment());
                }
                return false;
            }
        });*/
        /*navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if (id==R.id.sign_out){
                    showProgressDialog();
                    FirebaseAuth.getInstance().signOut();
                    hideProgressDialog();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    Toast.makeText(MainActivity.this, "log out successfully", Toast.LENGTH_SHORT).show();

                }
                return false;
            }
        });*/
    }

    // load fragment activity used in nav bottom
    private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    private void signOut() {
        // this listener will be called when there is change in firebase user session
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();

    }


}
