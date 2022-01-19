package com.example.pomodoroapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.pomodoroapp.R;
import com.example.pomodoroapp.databinding.ActivityTimerBinding;
import com.example.pomodoroapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TimerActivity extends AppCompatActivity {

    private int START_TIME_IN_MILLIS;

    private int currentProgress=0;
    private boolean timerRunning = true;
    private long timeLeftInMillis ;
    private long ticks=0;
    private int position;
    private int restTime;
    private int[] musics = {R.raw.acoustic_guitars,R.raw.cancion_triste,R.raw.cinematic_fairy_tale,R.raw.folk_instrumental,R.raw.in_the_forest_ambient,R.raw.melody_of_nature};

    CountDownTimer countDownTimer;
    ActivityTimerBinding binding;
    MediaPlayer mediaPlayer;
    FirebaseAuth mAuth;
    DatabaseReference myDbReference;
    private int time;
    private int pomodoros = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        myDbReference = FirebaseDatabase.getInstance().getReference("Users");

        Intent intent = getIntent();
        timeLeftInMillis = intent.getIntExtra("duration",0);
        position = intent.getIntExtra("music_position",0);
        START_TIME_IN_MILLIS = (int) (timeLeftInMillis/(60*1000));
        time = START_TIME_IN_MILLIS;

        buildNotiChannel();

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

    private void buildNotiChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("Rest over Channel","RestOverNoti", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis,1000) {
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

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if(mAuth.getCurrentUser()==null){
                    displayAlertDialog();
                } else {
                    incrementPomodoros();
                }

                if(START_TIME_IN_MILLIS==25) restTime = 5*60*1000;
                else if(START_TIME_IN_MILLIS==45) restTime = 15*60*1000;

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(TimerActivity.this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(TimerActivity.this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        // send notification after required time

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(TimerActivity.this,"Rest over Channel");
                        builder.setContentTitle("Time Over");
                        builder.setAutoCancel(true);
                        builder.setContentText("Hey!Your Rest Time is Over, Start Working Again!!");
                        builder.setSmallIcon(R.drawable.google_icon);
                        builder.setContentIntent(pendingIntent);

                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(TimerActivity.this);
                        managerCompat.notify(1,builder.build());
                    }
                },restTime);
            }
        };

        countDownTimer.start();
        timerRunning = true;
        binding.pauseBtn.setVisibility(View.VISIBLE);
        binding.resumeBtn.setVisibility(View.GONE);
        binding.stopBtn.setVisibility(View.GONE);
    }

    private void incrementPomodoros() {
        String email = mAuth.getCurrentUser().getEmail();
        String[] split = email.split(".com");
        email = split[0];

        String finalEmail = email;
        myDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);

                        String mail = userModel.getEmail();
                        if(mail.equalsIgnoreCase(finalEmail)){
                            time += userModel.getTime();
                            pomodoros += userModel.getPomodoros() + 1;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UserModel user = new UserModel(finalEmail,pomodoros, time);
                myDbReference.child(finalEmail).setValue(user);
            }
        }, 3000);

    }

    private void displayAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User not Signed In")
                .setMessage("Do you want to SignIn ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TimerActivity.this, MyProfile_Activity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
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