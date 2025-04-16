package com.example.finalyearproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finalyearproject.fragments.HomePage;
import com.example.finalyearproject.fragments.SettingsPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById((R.id.bottom_navigation));
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(navListener);

        Fragment selectedFragment = new HomePage();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , selectedFragment).commit();

    }

    private NavigationBarView.OnItemSelectedListener navListener = item -> {

        int itemId = item.getItemId();

        Fragment selectedFragment = null;

        if(itemId == R.id.nav_home){
            selectedFragment = new HomePage();
        }else if (itemId == R.id.nav_settings){
            selectedFragment = new SettingsPage();
        }


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        return true;
    };

}