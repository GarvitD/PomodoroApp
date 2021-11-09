package com.example.pomodoroapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.example.pomodoroapp.databinding.ActivityTimerBinding;

public class TimerActivity extends AppCompatActivity {

    private long START_TIME_IN_MILLIS;

    private int currentProgress=0;
    private boolean timerRunning = true;
    private long timeLeftInMillis ;
    private long ticks=0;
    private int position;
    private int[] musics = {R.raw.acoustic_guitars,R.raw.cancion_triste,R.raw.cinematic_fairy_tale,R.raw.folk_instrumental,R.raw.in_the_forest_ambient,R.raw.melody_of_nature};

    CountDownTimer countDownTimer;
    ActivityTimerBinding binding;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        timeLeftInMillis = intent.getIntExtra("duration",0);
        position = intent.getIntExtra("music_position",0);
        START_TIME_IN_MILLIS = timeLeftInMillis/(60*1000);

        if(position!=0) {
            mediaPlayer = MediaPlayer.create(TimerActivity.this,musics[position-1]);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        startTimer();

        binding.pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                if(position!=0) mediaPlayer.pause();
            }
        });
        updateCountdown();

        binding.resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                if(position!=0) mediaPlayer.start();
            }
        });

        binding.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                binding.timerTimeLeft.setText(Long.toString(START_TIME_IN_MILLIS) +":00");
                timeLeftInMillis = START_TIME_IN_MILLIS*60*1000;
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis ,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdown();

                ticks++;
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
                binding.celebrateAnimation.setVisibility(View.VISIBLE);
                binding.celebrateAnimation.playAnimation();
            }
        };

        countDownTimer.start();
        timerRunning = true;
        binding.pauseBtn.setVisibility(View.VISIBLE);
        binding.resumeBtn.setVisibility(View.GONE);
        binding.stopBtn.setVisibility(View.GONE);
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
        binding.pauseBtn.setVisibility(View.GONE);
        binding.resumeBtn.setVisibility(View.VISIBLE);
        binding.stopBtn.setVisibility(View.VISIBLE);
    }
}