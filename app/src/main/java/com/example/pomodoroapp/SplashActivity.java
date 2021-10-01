package com.example.pomodoroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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

    }
}