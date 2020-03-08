package com.example.chung_pike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReadingsListActivity extends AppCompatActivity {
    ListView lvReadings;
    List<Reading> readingList;
    DatabaseReference databaseReadings = FirebaseDatabase.getInstance().getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readings_list);

        lvReadings = findViewById(R.id.lvReading);
        readingList = new ArrayList<Reading>();

        lvReadings.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Reading student = readingList.get(position);

                showUpdateDialog(student.getUserId(),
                        student.getUserName(),
                        student.getDateTime(),
                        student.getSystolicReading(),
                        student.getDiastolicReading(),
                        student.getCondition());

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReadings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readingList.clear();
                for (DataSnapshot readingSnapshot : dataSnapshot.getChildren()) {
                    Reading reading = readingSnapshot.getValue(Reading.class);
                    readingList.add(reading);
                }
                ReadingsListAdapter adapter = new ReadingsListAdapter(ReadingsListActivity.this, readingList);
                lvReadings.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateReading(String userId, String userName, String dateTime,
                               float systolicReading, float diastolicReading, String condition) {
        DatabaseReference dbRef = databaseReadings.child(userId);

        Reading reading = new Reading(userId,userName,dateTime,
                systolicReading,diastolicReading,condition);

        Task setValueTask = dbRef.setValue(reading);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(ReadingsListActivity.this,
                        "Reading Updated.",Toast.LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReadingsListActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateDialog(final String userId, String userName, String dateTime,
                                  float systolicReading, float diastolicReading, String condition) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.update_name_entry);
        editTextName.setText(userName);

        final EditText editTextSystolic = dialogView.findViewById(R.id.update_systolic_reading);
        editTextSystolic.setText(String.valueOf(systolicReading));

        final EditText editTextDiastolic = dialogView.findViewById(R.id.update_diastolic_reading);
        editTextDiastolic.setText(String.valueOf(diastolicReading));

        final Button btnUpdate = dialogView.findViewById(R.id.update_reading);

        dialogBuilder.setTitle("Update Reading " + userName);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String sysReading = editTextSystolic.getText().toString().trim();
                float sysFloat;
                float diasFloat;
                String diasReading = editTextDiastolic.getText().toString().trim();
                String condition = "Normal";

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(ReadingsListActivity.this, "You must enter a name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(sysReading) || sysReading.matches("^[a-zA-Z]+$")) {
                    Toast.makeText(ReadingsListActivity.this,
                            "You must enter a valid Systolic Reading", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    sysFloat = Float.valueOf(sysReading);
                }
                if (TextUtils.isEmpty(diasReading) || diasReading.matches("^[a-zA-Z]+$")) {
                    Toast.makeText(ReadingsListActivity.this,
                            "You must enter a valid Diastolic Reading", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    diasFloat = Float.valueOf(diasReading);
                }

                SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'", Locale.getDefault());

                String dateTime = ISO_8601_FORMAT.format(new Date());

                if (sysFloat > 180 || diasFloat > 120) {
                    condition = "Hypertensive Crisis";
                    CrisisDialogFragment dialog = new CrisisDialogFragment();
                    dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
                } else if (sysFloat >= 140 || diasFloat >= 90) {
                    condition = "High blood pressure (stage 2)";
                } else if ((sysFloat >= 130 && sysFloat <= 139) || (diasFloat >= 80 && diasFloat <= 89)) {
                    condition = "High blood pressure (stage 1)";
                } else if (sysFloat >= 120 && sysFloat <= 129 && diasFloat < 80) {
                    condition = "Elevated";
                } else if (sysFloat < 120 && diasFloat < 80) {
                    condition = "Normal";
                }

                updateReading(userId,name,dateTime,sysFloat,diasFloat,condition);

                alertDialog.dismiss();
            }
        });

        final Button btnDelete = dialogView.findViewById(R.id.delete_btn);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReading(userId);

                alertDialog.dismiss();
            }
        });

    }

    private void deleteReading(String id) {
        DatabaseReference dbRef = databaseReadings.child(id);

        Task setRemoveTask = dbRef.removeValue();
        setRemoveTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(ReadingsListActivity.this,
                        "Reading Deleted.",Toast.LENGTH_LONG).show();
            }
        });

        setRemoveTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReadingsListActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
