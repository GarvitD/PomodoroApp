package com.example.pomodoroapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

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
                        String name = editTextTaskName.getText().toString();
                        String time = editTextTime.getHour() + " : " + editTextTime.getMinute();

                        listener.applyTexts(name,time);
                    }
                });



        return builder.create();
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
    }
}
