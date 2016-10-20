package com.example.oguri.coolweather.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.oguri.coolweather.R;
import com.example.oguri.coolweather.service.AutoUpdateService;
import com.example.oguri.coolweather.util.HttpCallbackListener;
import com.example.oguri.coolweather.util.HttpUtil;
import com.example.oguri.coolweather.util.MyApplication;
import com.example.oguri.coolweather.util.Utility;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {

    //组件
    private TextView cityNameText;
    private TextView publishText;
    private TextView tempText;
    private TextView weatherDespText;
    private TextView currentTimeText;
    private Button change_city;
    private Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        update = (Button) findViewById(R.id.update);
        change_city = (Button) findViewById(R.id.change_city);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        tempText = (TextView) findViewById(R.id.temp);
        weatherDespText = (TextView) findViewById(R.id.weather_des);
        currentTimeText = (TextView) findViewById(R.id.current_time);

        change_city.setOnClickListener(this);
        update.setOnClickListener(this);

        Intent intent = getIntent();
        String weatherCode = intent.getStringExtra("weatherCode");
        if (TextUtils.isEmpty(weatherCode))
            readWeatherInfo();
        else {
            //Log.d("weatherCode",weatherCode);
            queryWeatherInfo(weatherCode);

        }
    }

    public void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/sk/" + weatherCode + ".html";

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinished(String response) {
                Utility.handleWeatherResponse(response);
                //从sharedperefences中读并更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        readWeatherInfo();
                    }
                });
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void readWeatherInfo() {
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences
                ("weather", Context.MODE_PRIVATE);
        String currentTime = pref.getString("current_date", "");
        String cityName = pref.getString("cityName", "");
        //String weatherCode = pref.getString("weatherCode","");
        String temp = pref.getString("temp", "");
        String desc = pref.getString("description", "");
        String publishTime = pref.getString("publishTime", "");

        cityNameText.setText(cityName);
        currentTimeText.setText(currentTime);
        tempText.setText(temp);
        weatherDespText.setText(desc);
        publishText.setText(publishTime);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_city:
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                intent.putExtra("isFromWeather", true);
                startActivity(intent);
                finish();
                break;
            case R.id.update:
                SharedPreferences pref = getSharedPreferences("weather",MODE_PRIVATE);
                String weatherCode = pref.getString("weatherCode","");
                queryWeatherInfo(weatherCode);
                break;
            default:
                break;
        }
    }
}
