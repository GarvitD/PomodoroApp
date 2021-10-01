package com.example.pomodoroapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.example.pomodoroapp.databinding.ActivityTimerBinding;

public class TimerActivity extends AppCompatActivity {

    private long START_TIME_IN_MILLIS;

    private int currentProgress=0;
    private boolean timerRunning = false;
    private long timeLeftInMillis ;
    private long ticks=0;

    CountDownTimer countDownTimer;
    ActivityTimerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        timeLeftInMillis = intent.getIntExtra("duration",0);
        START_TIME_IN_MILLIS = timeLeftInMillis/(60*1000);

        binding.pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timerRunning){
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        updateCountdown();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis ,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdown();

                ticks++;
                Log.i("ticks",Long.toString(ticks));
                if(START_TIME_IN_MILLIS==25 && ticks%15==0) {
                    currentProgress += 1;
                    binding.timerProgress.setMax(100);
                    binding.timerProgress.setProgress(currentProgress);

                } else if(START_TIME_IN_MILLIS==45 && ticks%27==0) {
                    currentProgress += 1;
                    binding.timerProgress.setMax(100);
                    binding.timerProgress.setProgress(currentProgress);
                }
            }

            @Override
            public void onFinish() {
                timerRunning = false;
            }
        };

        countDownTimer.start();
        timerRunning = true;
    }

    private void updateCountdown() {
        String minutes = Long.toString(timeLeftInMillis/(60*1000));
        String seconds = Long.toString((timeLeftInMillis/1000)%60);

        if(minutes.length()==1) minutes = "0" + minutes;
        if(seconds.length()==1) seconds = "0" + seconds;

        binding.timerTimeLeft.setText(minutes+":"+seconds);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }
}