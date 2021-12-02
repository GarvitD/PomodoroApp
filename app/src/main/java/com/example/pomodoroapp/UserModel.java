package com.example.pomodoroapp;

public class UserModel {
    private String email;
    private int pomodoros;
    private int time;

    public UserModel() {

    }

    public UserModel(String email, int pomodoros, int time) {
        this.email = email;
        this.pomodoros = pomodoros;
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPomodoros() {
        return pomodoros;
    }

    public void setPomodoros(int pomodoros) {
        this.pomodoros = pomodoros;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
