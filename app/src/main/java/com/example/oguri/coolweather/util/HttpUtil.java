package com.example.oguri.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by oguri on 2016/10/17.
 */
public class HttpUtil {
    public static void sendHttpRequest(String address,final HttpCallbackListener listener){
        HttpURLConnection connection = null;
        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            StringBuilder response = new StringBuilder();
            String line;
            while((line=reader.readLine())!=null)
            {
                response.append(line);
            }
            if(listener!=null)
                listener.onFinished(response.toString());

        } catch (java.io.IOException e) {
            e.printStackTrace();
            if(listener!=null)
                listener.onError(e);
        }
        finally {
            if(connection!=null)
                connection.disconnect();
        }
    }
}