package com.example.chung_pike;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;

public class AddReadingActivity extends AppCompatActivity {
    DatabaseReference databaseReadings;
    EditText userId, systolicReading, diastolicReading;
    LocalDateTime dateTime;
    String condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reading);

        databaseReadings = FirebaseDatabase.getInstance().getReference("users");
        userId = findViewById(R.id.name_entry);
        systolicReading = findViewById(R.id.systolic_reading);
        diastolicReading = findViewById(R.id.diastolic_reading);

        Button addReadBtn = findViewById(R.id.submit_reading);
        addReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReading();
            }
        });
    }

    private void addReading() {
        String name = userId.getText().toString().trim();
        String sysReading = systolicReading.getText().toString().trim();
        float sysFloat = 0;
        float diasFloat = 0;
        String diasReading = diastolicReading.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "You must enter a name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sysReading) || sysReading.matches("^[a-zA-Z0-9]+$")
        || sysReading.equals("")) {
            Toast.makeText(this,
                    "You must enter a valid Systolic Reading", Toast.LENGTH_SHORT).show();
            return;
        } else {
            sysFloat = Float.valueOf(sysReading);
        }
        if (TextUtils.isEmpty(diasReading) || diasReading.matches("^[a-zA-Z0-9]+$")
        || diasReading.equals("")) {
            Toast.makeText(this,
                    "You must enter a valid Diastolic Reading", Toast.LENGTH_SHORT).show();
            return;
        } else {
            diasFloat = Float.valueOf(diasReading);
        }

        dateTime = LocalDateTime.now();

        if (sysFloat < 120 && diasFloat < 80) {
            condition = "Normal";
        } else if (sysFloat >= 120 && sysFloat <= 129 && diasFloat < 80) {
            condition = "Elevated";
        } else if ((sysFloat >= 130 && sysFloat <= 139) || (diasFloat >= 80 && diasFloat <= 89)) {
            condition = "High blood pressure (stage 1)";
        } else if (sysFloat >= 140 || diasFloat >= 90) {
            condition = "High blood pressure (stage 2)";
        } else if (sysFloat > 180 || diasFloat > 120) {
            condition = "Hypertensive Crisis";
        }

        String id = databaseReadings.push().getKey();
        Reading reading = new Reading(name, dateTime, sysFloat, diasFloat, condition);

        Task setValueTask= databaseReadings.child(id).setValue(reading);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(AddReadingActivity.this,
                        "Reading added.", Toast.LENGTH_SHORT).show();
                userId.setText("");
                systolicReading.setText("");
                diastolicReading.setText("");
            }
        });

    }

}