package com.example.chung_pike;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class MonthlyAverageActivity extends AppCompatActivity {

    DatabaseReference databaseReadings = FirebaseDatabase.getInstance().getReference("users");
    float systolicSum = 0;
    float diastolicSum = 0;
    float sysAvg = 0;
    float diasAvg = 0;
    float count = 0;
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_average);
        databaseReadings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> areas = new ArrayList<String>();
                final List<String> years = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    Reading reading = areaSnapshot.getValue(Reading.class);
                    String name = reading.getUserName().trim();
                    String date = reading.getDateTime();
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
                    SimpleDateFormat yearfmt = new SimpleDateFormat("yyyy");
                    if (!areas.contains(name)) {
                        areas.add(name);
                    }
                    try {
                        Date yearDate = fmt.parse(date);
                        String year = yearfmt.format(yearDate);
                        if (!years.contains(year)) {
                            years.add(year);
                        }

                    }
                    catch(ParseException pe) {

                    }
                }

                Spinner areaSpinner = (Spinner) findViewById(R.id.monthly_name_spinner);
                Spinner yearSpinner = findViewById(R.id.monthly_year);
                ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(
                        MonthlyAverageActivity.this,
                        android.R.layout.simple_spinner_item, years);
                ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(
                        MonthlyAverageActivity.this,
                        android.R.layout.simple_spinner_item, areas);
                namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(namesAdapter);
                yearSpinner.setAdapter(yearAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Spinner monthSpinner = findViewById(R.id.monthly_month);
        String[] months = new String[]{"January", "February", "March", "April", "May",
        "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
//set the spinners adapter to the previously created one.
        monthSpinner.setAdapter(adapter);

        Button getReportBtn = findViewById(R.id.monthly_get_average_btn);
        getReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMonthlyAverage();
            }
        });
    }

    private void getMonthlyAverage() {
        Spinner nameSpinner = findViewById(R.id.monthly_name_spinner);
        Spinner monthSpinner = findViewById(R.id.monthly_month);
        Spinner yearSpinner = findViewById(R.id.monthly_year);
        systolicSum = 0;
        diastolicSum = 0;
        count = 0;

        final String month = monthSpinner.getSelectedItem().toString();
        final String year = yearSpinner.getSelectedItem().toString();
        name = nameSpinner.getSelectedItem().toString().trim();

        // Get name
        TextView tvName = findViewById(R.id.monthly_username);
        tvName.setText(name);


        databaseReadings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // sum of all systolic and diastolic readings

                final List<String> areas = new ArrayList<String>();

                // Get all TextViews to be updated
                TextView tvSysAvg = findViewById(R.id.monthly_systolic);
                TextView tvDiasAvg = findViewById(R.id.monthly_diastolic);
                TextView tvCondition = findViewById(R.id.monthly_condition);
                TextView tvMonthYear = findViewById(R.id.month_year);
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
                SimpleDateFormat yearfmt = new SimpleDateFormat("yyyy");
                SimpleDateFormat monthfmt = new SimpleDateFormat("MMMM");
                Date d = null;
                String readingYear = "";
                String readingMonth = "";

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {

                    Reading reading = areaSnapshot.getValue(Reading.class);
                    String date = reading.getDateTime();

                    try {
                        d = fmt.parse(date);
                        readingYear = yearfmt.format(d);
                        readingMonth = monthfmt.format(d);



                    }
                    catch(ParseException pe) {

                    }
                    if (reading.getUserName().equals(name) && readingYear.equals(year) &&
                    readingMonth.equals(month)) {
                        systolicSum += reading.getSystolicReading();
                        diastolicSum += reading.getDiastolicReading();
                        count++;
                    }


                }
                tvMonthYear.setText(month + " " + year);

                if (systolicSum == 0 || diastolicSum == 0) {
                    // update TextViews
                    tvSysAvg.setText("No readings for this month");
                    tvDiasAvg.setText("No readings for this month");
                    tvCondition.setText("No readings for this month");
                } else {
                    // calculate average
                    sysAvg = systolicSum / count;
                    diasAvg = diastolicSum / count;

                    // update TextViews
                    tvSysAvg.setText(Float.toString(sysAvg));
                    tvDiasAvg.setText(Float.toString(diasAvg));

                    String condition = getCondition(sysAvg, diasAvg);
                    tvCondition.setText(condition);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getCondition(float sysFloat, float diasFloat) {
        String condition = "";
        if (sysFloat > 180 || diasFloat > 120) {
            condition = "Hypertensive Crisis";
        } else if (sysFloat >= 140 || diasFloat >= 90) {
            condition = "High blood pressure (stage 2)";
        } else if ((sysFloat >= 130 && sysFloat <= 139) || (diasFloat >= 80 && diasFloat <= 89)) {
            condition = "High blood pressure (stage 1)";
        } else if (sysFloat >= 120 && sysFloat <= 129 && diasFloat < 80) {
            condition = "Elevated";
        } else if (sysFloat < 120 && diasFloat < 80) {
            condition = "Normal";
        }
        return condition;
    }
}
