package com.example.pomodoroapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.pomodoroapp.databinding.ActivityScheduleYourDayBinding;
import com.facebook.bolts.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScheduleYourDayActivity extends AppCompatActivity implements Dialog_Class.DialogListener {

    ActivityScheduleYourDayBinding binding;
    List<TasksModel> tasksList;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleYourDayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        updateRecylcer();

        binding.addBtnTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    private void updateRecylcer() {
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.sp_tasks),MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list",null);
        Type type = new TypeToken<ArrayList<TasksModel>>() {}.getType();
        tasksList = gson.fromJson(json,type);

        if(tasksList==null) {
            tasksList = new ArrayList<>();
        }

        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        TasksAdapter adapter = new TasksAdapter(this,tasksList);
        binding.recyclerViewTasks.setAdapter(adapter);
    }

    private void openDialog() {
        Dialog_Class dialog_class = new Dialog_Class();
        dialog_class.show(getSupportFragmentManager(),"example dialog");
    }

    @Override
    public void applyTexts(String TaskName, String TaskTime) {
        TasksModel model = new TasksModel(TaskName,TaskTime);
        tasksList.add(model);

        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.sp_tasks),MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tasksList);
        editor.putString("task list",json);
        editor.apply();

        updateRecylcer();
    }
}