package com.example.pomodoroapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.pomodoroapp.util.Dialog_Class;
import com.example.pomodoroapp.R;
import com.example.pomodoroapp.util.TaskAlarmReceiver;
import com.example.pomodoroapp.adapters.TasksAdapter;
import com.example.pomodoroapp.databinding.ActivityScheduleYourDayBinding;
import com.example.pomodoroapp.models.TasksModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleYourDayActivity extends AppCompatActivity implements Dialog_Class.DialogListener {

    ActivityScheduleYourDayBinding binding;
    List<TasksModel> tasksList;
    SharedPreferences dateSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleYourDayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handleDate();
        updateRecylcer();

        binding.addBtnTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        handleDate();
        updateRecylcer();
        super.onResume();
    }

    private void handleDate() {
        dateSharedPreference = getSharedPreferences("currDate",MODE_PRIVATE);
        SharedPreferences.Editor ed = dateSharedPreference.edit();
        String date = dateSharedPreference.getString("date",null);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currDate = (simpleDateFormat.format(new Date())).substring(0,2);
        Log.i("hello",currDate);
        if(date==null){
            ed.putString("date",currDate);
        } else if(!(date.equals(currDate))) {
            SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.sp_tasks), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("task list");
            editor.apply();
            ed.remove("date");
            ed.putString("date",currDate);
        }
        ed.apply();
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

    @Override
    public void setAlarms(int Hour, int Minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,Hour);
        calendar.set(Calendar.MINUTE,Minute);
        calendar.set(Calendar.SECOND,0);

        if(calendar.before(Calendar.getInstance())){
            Toast.makeText(this,
                    "Trying to schedule a task in the past? We will schedule in the future as a punishment ;)",
                    Toast.LENGTH_LONG).show();
             calendar.add(Calendar.DATE,1);
        }

        SharedPreferences sh = getSharedPreferences("Alarm num",MODE_PRIVATE);
        SharedPreferences.Editor ed = sh.edit();
        int num = sh.getInt("num",1);
        ed.remove("num");
        ed.apply();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TaskAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,num,intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

        ed.putInt("num",num+1);
        ed.apply();
    }
}