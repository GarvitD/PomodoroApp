package com.example.pomodoroapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.pomodoroapp.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backgroundImg.animate().translationY(2400).setDuration(1000).setStartDelay(3000);
        binding.appNameSplash.animate().translationY(-2400).setDuration(1000).setStartDelay(3000);
        binding.splashImg.animate().translationY(-2600).setDuration(1000).setStartDelay(3000);
        binding.splashAnimation.animate().translationY(-2600).setDuration(1000).setStartDelay(3000);

        SharedPreferences sharedPreferences = getSharedPreferences("firstTime",MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        Boolean val = sharedPreferences.getBoolean("useFirst",true);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(val){
                    ed.putBoolean("useFirst",false);
                    ed.apply();
                    Intent intent = new Intent(SplashActivity.this, Onboarding_Main.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }, 3900);

    }
}