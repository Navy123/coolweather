package com.example.oguri.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.oguri.coolweather.receiver.AutoUpdateReceiver;
import com.example.oguri.coolweather.util.HttpCallbackListener;
import com.example.oguri.coolweather.util.HttpUtil;
import com.example.oguri.coolweather.util.Utility;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60 * 60 * 8 * 1000;
        long triggerTime = SystemClock.elapsedRealtime()+anHour;
        PendingIntent pi = PendingIntent.getBroadcast(this,0,new Intent(this,AutoUpdateReceiver.class),0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }
    public void updateWeather(){
        SharedPreferences pref = getSharedPreferences("weather",MODE_PRIVATE);
        String weatherCode = pref.getString("weatherCode","");
        String address = "http://www.weather.com.cn/data/sk/" + weatherCode + ".html";

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinished(String response) {
                Utility.handleWeatherResponse(response);
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
