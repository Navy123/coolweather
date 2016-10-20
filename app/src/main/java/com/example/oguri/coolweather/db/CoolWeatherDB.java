package com.example.oguri.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.oguri.coolweather.model.City;
import com.example.oguri.coolweather.model.County;
import com.example.oguri.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oguri on 2016/10/14.
 */
public class CoolWeatherDB {
    private static final int VERSION = 1;
    private static final String DB_NAME = "cool_weather";
    private static CoolWeatherDB coolWeatherDB;
    private static SQLiteDatabase db;

    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper coolWeatherOpenHelper = new CoolWeatherOpenHelper(context, DB_NAME,
                null, VERSION);
        db = coolWeatherOpenHelper.getReadableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context) {
        if (coolWeatherDB == null)
            coolWeatherDB = new CoolWeatherDB(context);
        return coolWeatherDB;
    }

    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("province_name", province.getProvinceName());
            db.insert("Province", null, contentValues);
        }
    }

    public List<Province> loadProvinces() {

        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst())
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);
            } while (cursor.moveToNext());
        if(cursor!=null)
            cursor.close();
        return list;
    }

    public void saveCity(City city) {
        if (city != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name", city.getCityName());
            contentValues.put("province_id",city.getProvinceId());
            db.insert("City", null, contentValues);
        }
    }

    public List<City> loadCities(int provinceId) {

        List<City> list = new ArrayList<>();
        int count=0;
        Cursor cursor = db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst())
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(provinceId);
                list.add(city);
                count++;
            } while (cursor.moveToNext());
        Log.d("loadcity",String.valueOf(count));
        if(cursor!=null)
            cursor.close();
        return list;
    }
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("county_name", county.getCountyName());
            contentValues.put("city_id",county.getCityId());
            contentValues.put("weather_code",county.getWeatherCode());
            db.insert("County", null, contentValues);
        }
    }

    public List<County> loadCounties(int cityId) {

        List<County> list = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst())
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCityId(cityId);
                county.setWeatherCode(cursor.getString(cursor.getColumnIndex("weather_code")));
                list.add(county);
            } while (cursor.moveToNext());
        if(cursor!=null)
            cursor.close();
        return list;
    }
}
