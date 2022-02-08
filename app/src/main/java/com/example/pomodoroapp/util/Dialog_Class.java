package com.example.pomodoroapp.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pomodoroapp.R;

import java.util.Calendar;

public class Dialog_Class extends AppCompatDialogFragment {

    private EditText editTextTaskName;
    private TimePicker editTextTime;
    private DialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_task,null);

        editTextTaskName = view.findViewById(R.id.editTextTaskName);
        editTextTime = view.findViewById(R.id.editTextTaskTime);

        builder.setView(view)
                .setTitle("Enter Details")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing here and override it later
                    }
                });
        AlertDialog dialog =  builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextTaskName.getText().toString();
                if(name.isEmpty()) {
                    editTextTaskName.setError("Please give a name to your task!");
                    editTextTaskName.requestFocus();
                } else {
                    String time = check(editTextTime.getHour()) + " : " + check(editTextTime.getMinute());

                    listener.applyTexts(name,time);
                    listener.setAlarms(editTextTime.getHour(),editTextTime.getMinute());
                    dialog.dismiss();
                }
            }
        });
        return dialog;
    }

    private String check(int hour) {
        String time = String.valueOf(hour);
        if(time.length()<2) time = "0" + time;
        return time;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString()
            + "must implement Dialog Listener");
        }
    }

    public interface DialogListener {
        void applyTexts(String TaskName, String TaskTime);
        void setAlarms(int Hour,int Minute);
    }
}
