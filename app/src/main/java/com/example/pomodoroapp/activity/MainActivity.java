package com.example.pomodoroapp.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.pomodoroapp.R;
import com.example.pomodoroapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String selected_music = "none";
    int pos =0;

    ActivityMainBinding binding;
    MediaPlayer mediaPlayer = null;
    Uri customAudioUri;
    Intent startActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationView();
        startActivityIntent = new Intent(MainActivity.this, TimerActivity.class);

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyProfile_Activity.class);
                startActivity(intent);
            }
        });

        binding.musicOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMusicAlertDialog();
            }
        });

         binding.mins25.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivityIntent.putExtra("duration",25*1000*60);
                 startActivityIntent.putExtra("music_position",pos);
                 startActivity(startActivityIntent);
             }
         });

         binding.mins45.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivityIntent.putExtra("duration",45*1000*60);
                 startActivityIntent.putExtra("music_position",pos);
                 startActivity(startActivityIntent);
             }
         });

         binding.addCustomMusic.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 selectCustomMusic();
             }
         });
    }

    ActivityResultLauncher<Intent> activityResultLauncherPickAudio = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()==RESULT_OK){
                Intent data = result.getData();
                customAudioUri = data.getData();

                startActivityIntent.putExtra("customAudioUri",customAudioUri.toString());
            }
        }
    });

    private void selectCustomMusic() {
        Intent selectAudioIntent = new Intent();
        selectAudioIntent.setType("audio/*");
        selectAudioIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        activityResultLauncherPickAudio.launch(selectAudioIntent);
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    private void showMusicAlertDialog() {
        String[] music_names = {"none","acoustic guitars","cancion triste","cinematic fairy tale","instrumental folk","forest ambient","melody nature"};
        int[] musics = {R.raw.acoustic_guitars,R.raw.cancion_triste,R.raw.cinematic_fairy_tale,R.raw.folk_instrumental,R.raw.in_the_forest_ambient,R.raw.melody_of_nature};


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Music Options");
        builder.setSingleChoiceItems(music_names, pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pos = which;
                selected_music = music_names[pos];
                if(mediaPlayer!=null)  {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }

                if(pos!=0) {
                    mediaPlayer = MediaPlayer.create(MainActivity.this, musics[pos-1]);
                    mediaPlayer.start();
                }
            }
        });

        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mediaPlayer != null) mediaPlayer.stop();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mediaPlayer != null) mediaPlayer.stop();
                dialog.dismiss();
            }
        });

        builder.show();
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
        switch(item.getItemId()){
            case R.id.title1: {
                // some code for new idea
                break;
            }
            case R.id.title2: {
                Intent intent = new Intent(MainActivity.this, ScheduleYourDayActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.title3: {
                Intent intent = new Intent(MainActivity.this, QuotesActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.title4 : {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                break;
            }
        }
        binding.drawerLayoutMainActivity.closeDrawer(GravityCompat.START);
        return true;
    }
}