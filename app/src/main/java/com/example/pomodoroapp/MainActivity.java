package com.example.pomodoroapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.pomodoroapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationView();

         binding.mins25.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(MainActivity.this,TimerActivity.class);
                 intent.putExtra("duration",25*1000*60);
                 startActivity(intent);
             }
         });

         binding.mins45.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(MainActivity.this,TimerActivity.class);
                 intent.putExtra("duration",45*1000*60);
                 startActivity(intent);
             }
         });
    }

    private void navigationView() {
        binding.navigationViewActivityMain.bringToFront();
        binding.navigationViewActivityMain.setNavigationItemSelectedListener(this);
        binding.navigationViewActivityMain.setCheckedItem(R.id.title1);

        binding.navDrawerOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.drawerLayoutMainActivity.isDrawerVisible(GravityCompat.START)) binding.drawerLayoutMainActivity.closeDrawer(GravityCompat.START);
                else binding.drawerLayoutMainActivity.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }
}