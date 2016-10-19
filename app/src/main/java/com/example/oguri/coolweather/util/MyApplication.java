package com.example.oguri.coolweather.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by oguri on 2016/10/18.
 */
public class MyApplication extends Application {
    private  static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
    }
    public static Context getContext()
    {
        return context;
    }
}
