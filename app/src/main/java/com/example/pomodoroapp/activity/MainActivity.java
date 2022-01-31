package com.example.pomodoroapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.pomodoroapp.R;
import com.example.pomodoroapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;
    MediaPlayer mediaPlayer = null;

    String selected_music = "none";
    int pos =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationView();

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
                 Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                 intent.putExtra("duration",25*1000*60);
                 intent.putExtra("music_position",pos);
                 startActivity(intent);
             }
         });

         binding.mins45.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(MainActivity.this,TimerActivity.class);
                 intent.putExtra("duration",45*1000*60);
                 intent.putExtra("music_position",pos);
                 startActivity(intent);
             }
         });
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

                if(pos!=0) {
                    if(mediaPlayer!=null)  mediaPlayer.stop();
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