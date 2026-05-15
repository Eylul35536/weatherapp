package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private Button getWeatherButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        getWeatherButton = findViewById(R.id.getWeatherButton);

        getWeatherButton.setOnClickListener(v -> {
            String cityName = cityEditText.getText().toString().trim();
            if (!cityName.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                intent.putExtra("CITY_NAME", cityName);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
