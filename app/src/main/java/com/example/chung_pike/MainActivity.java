package com.example.chung_pike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addReading = findViewById(R.id.add_reading_btn);
        addReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddReadingActivity.class);
                startActivity(i);
            }
        });

        Button viewReadings = findViewById(R.id.list_readings_btn);
        viewReadings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ReadingsListActivity.class);
                startActivity(i);
            }
        });

        Button averageBtn = findViewById(R.id.month_average_btn);
        averageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MonthlyAverageActivity.class);
                startActivity(i);
            }
        });
    }
}
