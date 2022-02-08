package com.example.pomodoroapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.pomodoroapp.adapters.LeaderBoardAdapter;
import com.example.pomodoroapp.databinding.ActivityLeaderBoardBinding;
import com.example.pomodoroapp.models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    ActivityLeaderBoardBinding binding;
    DatabaseReference database;
    List<UserModel> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaderBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LeaderBoardAdapter adapter = new LeaderBoardAdapter(this,userList);
        database = FirebaseDatabase.getInstance().getReference("Users");
        binding.recyclerView.setAdapter(adapter);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        userList.add(user);
                    }
                    userList.sort(new Comparator<UserModel>() {
                        @Override
                        public int compare(UserModel t1, UserModel t2) {
                            if(t1.getTime()>t2.getTime()) return -1;
                            else if(t1.getTime()<t2.getTime()) return 1;
                            else return 0;
                        }
                    });
                    for(int i=0;i<userList.size();i++) {
                        Log.i("Pomo time "+ String.valueOf(i+1) + ": ", String.valueOf(userList.get(i).getTime()));
                    }
                    adapter.notifyDataSetChanged();
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}