package com.FoodPlanet.FoodPlanet1.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.FoodPlanet.FoodPlanet1.R;
import com.FoodPlanet.FoodPlanet1.ui.help.HelpFragment;
import com.FoodPlanet.FoodPlanet1.ui.myChannel.MyChannelFragment;
import com.FoodPlanet.FoodPlanet1.ui.notification.NotificationFragment;
import com.FoodPlanet.FoodPlanet1.ui.seach.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class homeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        BottomNavigationView navView = view.findViewById(R.id.nav_view);

        //handle bottom nav
        BottomNavigationView bottomNavigationView=view.findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();

                if (id==R.id.nav_home) {
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitle("home");
                    loadFragment(new homeFragment());
                }
                else if (id==R.id.nav_search) {
                    toolbar.setVisibility(View.GONE);
                    loadFragment(new SearchFragment());

                }else if (id==R.id.nav_notification){

                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitle("notification");
                    loadFragment(new NotificationFragment());
                }else if (id==R.id.nav_channel){

                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitle("My Channel");
                    loadFragment(new MyChannelFragment());
                }
                return true;
            }
        });
    }

    // load fragment activity used in nav bottom
    private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}