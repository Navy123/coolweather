package com.example.oguri.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.oguri.coolweather.R;
import com.example.oguri.coolweather.db.CoolWeatherDB;
import com.example.oguri.coolweather.model.City;
import com.example.oguri.coolweather.model.County;
import com.example.oguri.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by oguri on 2016/10/17.
 */
public class Utility {
    public static String readXML() {
        StringBuilder response = new StringBuilder();
        InputStream inputStream = MyApplication.getContext().getResources().openRawResource(R.raw
                .city);
        try {
            InputStreamReader reader = new InputStreamReader(inputStream, "utf8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String tempString;
            while ((tempString = bufferedReader.readLine()) != null) {
                response.append(tempString);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();

    }

    //从city.xml中读出所有的省
    public static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB) {

        String response = Utility.readXML();
        //Log.d("Main",response);
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            InputStream is = new ByteArrayInputStream(response.getBytes());
            parser.setInput(is, "utf-8");
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                //Log.d("MAIN",eventType+" "+XmlPullParser.START_TAG);
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("province".equals(nodeName)) {
                            Province province = new Province();
                            province.setProvinceName(parser.getAttributeValue(1));
                            coolWeatherDB.saveProvince(province);
                            //Log.d("MAIN", province.getProvinceName());
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //从xml中读出某province的所有市
    public static boolean handleCityResponse(CoolWeatherDB coolWeatherDB, final Province province) {
        //Log.d("Main","handlecity");
        String response = Utility.readXML();
        //Log.d("Main",response);
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            InputStream is = new ByteArrayInputStream(response.getBytes());
            parser.setInput(is, "utf-8");
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                //Log.d("MAIN",eventType+" "+XmlPullParser.START_TAG);
                if (eventType == XmlPullParser.START_TAG) {
                    //Log.d("MAIN", "START_TAG");
                    if ("province".equals(nodeName) && parser.getAttributeValue(1).equals
                            (province
                                    .getProvinceName())) {
                        //Log.d("Main", parser.getAttributeValue(1));
                        break;
                    }

                }
                eventType = parser.next();

            }
            eventType = parser.next();
            String nodeName = parser.getName();
            while (!(eventType == XmlPullParser.END_TAG && ("province".equals(nodeName)))) {
                //Log.d("MAIN",String.valueOf(parser.getAttributeCount()));
                if ("city".equals(nodeName) && eventType == XmlPullParser.START_TAG) {
                    City city = new City();
                    city.setCityName(parser.getAttributeValue(1));
                    city.setProvinceId(province.getId());
                    //Log.d("MAIN", city.getCityName());
                    coolWeatherDB.saveCity(city);
                }
                eventType = parser.next();
                nodeName = parser.getName();

            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //从xml中读指定城市的所有县
    public static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB, City city) {
        String response = Utility.readXML();
        //Log.d("Main",response);
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            InputStream is = new ByteArrayInputStream(response.getBytes());
            parser.setInput(is, "utf-8");
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                //Log.d("MAIN",eventType+" "+XmlPullParser.START_TAG);
                if (eventType == XmlPullParser.START_TAG) {
                    //Log.d("MAIN", "START_TAG");
                    if ("city".equals(nodeName) && parser.getAttributeValue(1).equals
                            (city.getCityName())) {
                        //Log.d("Main", parser.getAttributeValue(1));
                        break;
                    }

                }
                eventType = parser.next();

            }
            eventType = parser.next();
            String nodeName = parser.getName();
            while (!(eventType == XmlPullParser.END_TAG && ("city".equals(nodeName)))) {
                //Log.d("MAIN",String.valueOf(parser.getAttributeCount()));
                if ("county".equals(nodeName) && eventType == XmlPullParser.START_TAG) {
                    County county = new County();
                    county.setCountyName(parser.getAttributeValue(1));
                    county.setCityId(city.getId());
                    county.setWeatherCode(parser.getAttributeValue(2));
                    //Log.d("MAIN", county.getCountyName());
                    coolWeatherDB.saveCounty(county);
                }
                eventType = parser.next();
                nodeName = parser.getName();

            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp = weatherInfo.getString("temp");
            //String temp2 = weatherInfo.getString("temp2");
            String wind = weatherInfo.getString("WD");
            String windScale = weatherInfo.getString("WS");
            String humid = weatherInfo.getString("SD");
            String publishTime = weatherInfo.getString("time");
            saveWeatherInfo(MyApplication.getContext(), cityName, weatherCode, temp, wind,
                    windScale, humid, publishTime);
            Log.d("data",cityName+temp+wind+windScale+humid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context, String cityName,String weatherCode,  String
            temp, String wind, String windScale, String humid, String publishTime) {
        //将天气存放在SharedPerefences中
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences
                ("weather", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.putBoolean("city_selected",true);
        editor.putString("cityName", cityName);
        editor.putString("weatherCode", weatherCode);
        editor.putString("temp", temp+"℃");
        editor.putString("description", wind + windScale + " 湿度:" + humid);
        editor.putString("publishTime", "发布于"+publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }


}
