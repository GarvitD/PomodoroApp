package com.example.pomodoroapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.pomodoroapp.R;
import com.example.pomodoroapp.databinding.ActivityWeeklyProgressBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WeeklyProgress extends AppCompatActivity {

    private HashMap<String,Integer> daysMap;
    ActivityWeeklyProgressBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeeklyProgressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        daysMap = new HashMap<>();
        daysMap.put("Monday", 0);
        daysMap.put("Tuesday", 0);
        daysMap.put("Wednesday", 0);
        daysMap.put("Thursday", 0);
        daysMap.put("Friday", 0);
        daysMap.put("Saturday", 0);
        daysMap.put("Sunday", 0);

        SharedPreferences weeklyProgressMapSP = getSharedPreferences("weeklyProgressMapSP", MODE_PRIVATE);

        Gson gson1 = new Gson();
        String daysMapString = gson1.toJson(daysMap);
        Log.i("defaultMap", daysMapString);

        String hashMapString = weeklyProgressMapSP.getString("weeklyProgressString", daysMapString);

        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Integer>>() {
        }.getType();
        HashMap<String, Integer> progressMap = gson.fromJson(hashMapString, type);

        Log.i("MapPrinted", hashMapString);


    }

}