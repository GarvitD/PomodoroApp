package com.example.pomodoroapp.activity;

import static android.content.Context.MODE_PRIVATE;

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
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimerActivity extends AppCompatActivity {

    private int START_TIME_IN_MILLIS;

    private int currentProgress=0;
    private boolean timerRunning = true;
    private long timeLeftInMillis ;
    private long ticks=0;
    private int position;
    private int restTime;
    private int[] musics = {R.raw.acoustic_guitars,R.raw.cancion_triste,R.raw.cinematic_fairy_tale,R.raw.folk_instrumental,R.raw.in_the_forest_ambient,R.raw.melody_of_nature};
    private int time;
    private Boolean musicPlaying = true;
    private boolean takenToBackground = false;

    private Map<Integer, Integer> weeklyPomodoroTimeMap = new HashMap<Integer, Integer>() {
        {
            put(1,0);
            put(2,0);
            put(3,0);
            put(4,0);
            put(5,0);
            put(6,0);
            put(7,0);
        }
    };
    private String weeklyTimeMapToString = (new Gson()).toJson(weeklyPomodoroTimeMap);

    CountDownTimer countDownTimer;
    ActivityTimerBinding binding;
    MediaPlayer mediaPlayer;
    FirebaseAuth mAuth;
    DatabaseReference myDbReference;
    Uri customAudioUri;
    String audioReceived;
    NotificationChannel notificationChannel;
    NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(musicPlaying) binding.musicBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);

        mAuth = FirebaseAuth.getInstance();
        myDbReference = FirebaseDatabase.getInstance().getReference("Users");

        getPreviousIntents();

        buildNotiChannel();

        startTimer();

        binding.pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                if(position != 0) mediaPlayer.pause();
            }
        });

        updateCountdown();

        binding.resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                if(position != 0) mediaPlayer.start();
            }
        });

        binding.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                binding.timerTimeLeft.setText(Long.toString(START_TIME_IN_MILLIS) +":00");
                timeLeftInMillis = START_TIME_IN_MILLIS*60*1000;
                Intent intent1 = new Intent(TimerActivity.this,MainActivity.class);
                startActivity(intent1);
            }
        });

        binding.musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseOrStartMusic();
            }
        });
    }

    private void pauseOrStartMusic() {
        if(musicPlaying) {
            binding.musicBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
            musicPlaying = false;
            if(mediaPlayer != null) mediaPlayer.pause();
        } else {
            binding.musicBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            musicPlaying = true;
            if(mediaPlayer!=null) mediaPlayer.start();
        }
    }

    private void getPreviousIntents() {
        Intent intent = getIntent();
        timeLeftInMillis = intent.getIntExtra("duration",0);
        position = intent.getIntExtra("music_position",0);

        audioReceived = intent.getExtras().getString("customAudioUri","no_string");
        customAudioUri = Uri.parse(audioReceived);
        if(!(audioReceived.equalsIgnoreCase("no_string"))){
            mediaPlayer = MediaPlayer.create(this,customAudioUri);
            mediaPlayer.start();
        }

        START_TIME_IN_MILLIS = (int) (timeLeftInMillis/(60*1000));
        time = START_TIME_IN_MILLIS;

        if(position != 0) {
            mediaPlayer = MediaPlayer.create(TimerActivity.this,musics[position-1]);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }


    private void buildNotiChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            notificationChannel = new NotificationChannel("Rest over Channel","RestOverNoti", NotificationManager.IMPORTANCE_HIGH);
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!timerRunning) {
                    cancel();
                }
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

                Log.i("Pomodoro Test","method call");

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if(mAuth.getCurrentUser() == null){
                    displayAlertDialog();
                } else {
                    incrementPomodoros();
                }

                updateMap(START_TIME_IN_MILLIS);

                if(START_TIME_IN_MILLIS==25) restTime = 5*60*1000;
                else if(START_TIME_IN_MILLIS==45) restTime = 15*60*1000;

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(TimerActivity.this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(TimerActivity.this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        sendNotification(pendingIntent);
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

    private void updateMap(int start_time_in_millis) {
        SharedPreferences weeklyProgressMapSP = getSharedPreferences("weeklyProgressMapSP",MODE_PRIVATE);
        SharedPreferences.Editor editor = weeklyProgressMapSP.edit();
        String hashMapString = weeklyProgressMapSP.getString("weeklyProgressString",weeklyTimeMapToString);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String currDay = dateFormat.format(date);

        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
        HashMap<String,Integer> progressMap = gson.fromJson(hashMapString,type);
        progressMap.replace(currDay,progressMap.get(currDay)+start_time_in_millis);

        Gson gson1 = new Gson();
        String newMapString = gson1.toJson(progressMap);

        editor.remove("weeklyProgressString");
        editor.putString("weeklyProgressString",newMapString);

        editor.apply();
    }


    private void sendNotification(PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(TimerActivity.this,"Rest over Channel");
        builder.setContentTitle("Time Over");
        builder.setAutoCancel(true);
        builder.setContentText("Hey!Your Rest Time is Over, Start Working Again!!");
        builder.setSmallIcon(R.drawable.alarm_noti);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(TimerActivity.this);
        managerCompat.notify(1,builder.build());
    }

    private void incrementPomodoros() {
        String email = mAuth.getCurrentUser().getEmail();
        String[] split = email.split(".com");
        email = split[0];
        String finalEmail = email;

        myDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(finalEmail)) {
                    UserModel model = snapshot.child(finalEmail).getValue(UserModel.class);
                    UserModel user = new UserModel(finalEmail,model.getPomodoros()+1, model.getTime()+time);
                    myDbReference.child(finalEmail).setValue(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void showExitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to exit?");
        builder.setMessage("You won't be able to resume again!");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(mediaPlayer != null) mediaPlayer.stop();
                binding.timerProgress.setProgress(0);
                Intent intent = new Intent(TimerActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        showExitAlert();
    }

    @Override
    protected void onPause() {
        if (mediaPlayer != null) mediaPlayer.pause();
        timerRunning = false;
        takenToBackground = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mediaPlayer != null) mediaPlayer.start();
        if(takenToBackground){
            takenToBackground =  false;
            startTimer();
        }
        super.onResume();
    }
}