package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(
        @Query("q") String cityName,
        @Query("appid") String apiKey,
        @Query("units") String units
    );
}
