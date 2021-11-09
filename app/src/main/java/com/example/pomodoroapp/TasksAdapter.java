package com.example.pomodoroapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
    }

    @Override
    public int getItemCount() {
        return TaskList.size();
    }

    public class TaskViewModel extends RecyclerView.ViewHolder {
        TextView taskName,taskTime;
        public TaskViewModel(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.sampleTaskText);
            taskTime = itemView.findViewById(R.id.sampleTaskTime);
        }
    }
}
