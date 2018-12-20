package com.stangil44.weatherapp.WeatherApp.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.vaadin.ui.Notification;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WeatherService {
    private OkHttpClient client;
    private Response response;
    private String weatherAPi = "ADD YOUR API HERE";
    public JSONObject getWeather(String name, String units) throws JSONException {
        client = new OkHttpClient();
        Request request= new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?q="+name+"&units="+units+"&appid="+ weatherAPi)
                .build();

        try {
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONArray returnWeatherArray(String name, String units) throws JSONException {
        //JSONArray weatherJsonArray = getWeather(name, units).getJSONArray("weather");

        return getWeather(name, units).getJSONArray("weather");
    }
    public JSONObject returnMainWeather(String name, String units) throws JSONException {
        //JSONObject mainObject = getWeather(name, units).getJSONObject("main");
        return getWeather(name, units).getJSONObject("main");
    }
    public JSONObject returnWind(String name, String units) throws JSONException {
        //JSONObject windObject = getWeather(name, units).getJSONObject("wind");
        return getWeather(name, units).getJSONObject("wind");
    }
    public JSONObject returnSys(String name, String units) throws JSONException {
        //JSONObject sysObject = getWeather(name, units).getJSONObject("sys");
        return getWeather(name, units).getJSONObject("sys");
    }
}
