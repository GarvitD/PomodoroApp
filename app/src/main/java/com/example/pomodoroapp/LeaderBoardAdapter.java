package com.example.pomodoroapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardViewHolder> {
    Context context;
    List<UserModel> userList;

    public LeaderBoardAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public LeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_sample,parent,false);
        return new LeaderBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.userStats.setText(String.valueOf(user.getPomodoros()));

        String email = user.getEmail();
        int pos=0;
        for(int i = 0;i<email.length();i++){
            if(email.charAt(i)=='@') {
                pos = i;
                break;
            }
        }
        String name = email.substring(0,pos);
        holder.userName.setText(name);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class LeaderBoardViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userStats;
        public LeaderBoardViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userStats = itemView.findViewById(R.id.userStats);
        }
    }
}
