package com.example.pomodoroapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;

import com.example.pomodoroapp.R;
import com.example.pomodoroapp.adapters.OnBoardingViewPagerAdapter;
import com.example.pomodoroapp.databinding.ActivityOnboardingMainBinding;

public class Onboarding_Main extends AppCompatActivity {

    ActivityOnboardingMainBinding binding;
    OnBoardingViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewPagerAdapter = new OnBoardingViewPagerAdapter(this);
        binding.onboardingViewPager.setAdapter(viewPagerAdapter);
        binding.wormDotsIndicator.setViewPager(binding.onboardingViewPager);

        binding.skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Onboarding_Main.this, MainActivity.class);
                startActivity(intent);
            }
        });

        binding.onboardingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0) {
                    binding.onboardingLayout.setBackgroundResource(R.drawable.gradient_animation789);
                    addAnimation();
                    binding.skipButton.setText("Skip");
                } else if(position==1) {
                    binding.onboardingLayout.setBackgroundResource(R.drawable.gradient_animation456);
                    addAnimation();
                    binding.skipButton.setText("Skip");

                } else if(position==2) {
                    binding.onboardingLayout.setBackgroundResource(R.drawable.gradient_animation123);
                    addAnimation();
                    binding.skipButton.setText("Next");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void addAnimation() {

        AnimationDrawable animationDrawable = (AnimationDrawable) binding.onboardingLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
    }
}