package com.example.pomodoroapp.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodoroapp.R;
import com.example.pomodoroapp.models.TasksModel;
import com.example.pomodoroapp.util.TaskAlarmReceiver;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.internal.concurrent.Task;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewModel> {

    Context context;
    List<TasksModel> TaskList;

    public TasksAdapter(Context context, List<TasksModel> taskList) {
        this.context = context;
        this.TaskList = taskList;
    }

    @NonNull
    @Override
    public TasksAdapter.TaskViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tasks_sample,parent,false);
        return new TaskViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.TaskViewModel holder, int position) {
        TasksModel model = TaskList.get(position);

        holder.taskTime.setText(model.time);
        holder.taskName.setText(model.name);

        holder.deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTaskFromList(holder);
                deleteAlarm(model,holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return TaskList.size();
    }

    private void deleteTaskFromList(TaskViewModel holder) {
        TaskList.remove(holder.getAdapterPosition());
        notifyItemRemoved(holder.getAdapterPosition());
        notifyItemRangeChanged(holder.getAdapterPosition(), TaskList.size());

        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.sp_tasks),MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(TaskList);
        editor.putString("task list",json);
        editor.apply();
    }

    public void deleteAlarm(TasksModel model,TaskViewModel holder){
        AlarmManager alarmManager = (AlarmManager) (holder.itemView.getContext()).getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent((holder.itemView.getContext()), TaskAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast((holder.itemView.getContext()),model.id,intent,0);
        alarmManager.cancel(pendingIntent);
    }

    public class TaskViewModel extends RecyclerView.ViewHolder {
        TextView taskName,taskTime;
        ImageButton deleteTask;

        public TaskViewModel(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.sampleTaskText);
            taskTime = itemView.findViewById(R.id.sampleTaskTime);
            deleteTask = itemView.findViewById(R.id.deleteTask);
        }
    }
}
