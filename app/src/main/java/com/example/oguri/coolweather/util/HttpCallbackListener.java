package com.example.oguri.coolweather.util;

/**
 * Created by oguri on 2016/10/17.
 */
public interface HttpCallbackListener  {
     void onFinished(String response);
     void onError(Exception e);
}
