package com.example.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "cdae4e66fb7d0e63c799f1bf8fae5a53";

    private TextView cityNameText, tempText, humidityText, windSpeedText;
    private ImageView weatherIcon;
    private ProgressBar progressBar;
    private LinearLayout weatherDataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityNameText = findViewById(R.id.cityNameText);
        tempText = findViewById(R.id.tempText);
        humidityText = findViewById(R.id.humidityText);
        windSpeedText = findViewById(R.id.windSpeedText);
        weatherIcon = findViewById(R.id.weatherIcon);
        progressBar = findViewById(R.id.progressBar);
        weatherDataLayout = findViewById(R.id.weatherDataLayout);

        String cityName = getIntent().getStringExtra("CITY_NAME");

        if (cityName != null && !cityName.isEmpty()) {
            fetchWeatherData(cityName);
        } else {
            Toast.makeText(this, "City name is empty.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchWeatherData(String cityName) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection. Please check your settings.", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        weatherDataLayout.setVisibility(View.INVISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        Call<WeatherResponse> call = weatherApi.getCurrentWeather(cityName, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    displayWeather(response.body());
                } else {
                    String errorMsg = "Error: " + response.code();
                    if (response.code() == 401) {
                        errorMsg = "Invalid API Key or not active yet.";
                    } else if (response.code() == 404) {
                        errorMsg = "City not found.";
                    }
                    Toast.makeText(WeatherActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WeatherActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayWeather(WeatherResponse weatherResponse) {
        weatherDataLayout.setVisibility(View.VISIBLE);
        cityNameText.setText(weatherResponse.name);
        tempText.setText(String.format(Locale.getDefault(), "%.0f°", weatherResponse.main.temp));
        humidityText.setText(String.format(Locale.getDefault(), "%d%%", weatherResponse.main.humidity));
        windSpeedText.setText(String.format(Locale.getDefault(), "%.1f m/s", weatherResponse.wind.speed));

        if (weatherResponse.weather != null && !weatherResponse.weather.isEmpty()) {
            String iconCode = weatherResponse.weather.get(0).icon;
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";
            Glide.with(this).load(iconUrl).into(weatherIcon);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || 
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
        }
        return false;
    }
}
