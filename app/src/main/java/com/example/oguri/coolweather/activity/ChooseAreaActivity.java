package com.example.oguri.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oguri.coolweather.R;
import com.example.oguri.coolweather.db.CoolWeatherDB;
import com.example.oguri.coolweather.model.City;
import com.example.oguri.coolweather.model.County;
import com.example.oguri.coolweather.model.Province;
import com.example.oguri.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {
    //标示级别的常量
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;
    private int currentLevel;
    //适配器中数据dataList
    private List<String> dataList = new ArrayList<>();
    //省市县list
    private List<Province> provinceList = new ArrayList<>();
    private List<City> cityList = new ArrayList<>();
    private List<County> countyList = new ArrayList<>();
    //选中的省市县
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    //各组件
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    //用于操作数据库的对象
    private CoolWeatherDB coolWeatherDB;
    //记录天气代码
    private String weatherCode;
    //是否从weatheractivity转过来
    private boolean isFromWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        isFromWeather = getIntent().getBooleanExtra("isFromWeather",false);
        SharedPreferences pref = getSharedPreferences("weather",MODE_PRIVATE);
        boolean city_selected = pref.getBoolean("city_selected",false);
        Log.d("city_selected",String.valueOf(city_selected)+ String.valueOf(isFromWeather));

        if (city_selected && !isFromWeather)
        {
            //String weatherCode = pref.getString("weatherCode","");
            Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
            //intent.putExtra("weatherCode",weatherCode);
            startActivity(intent);
            finish();
            return;
        }

        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        queryProvinces();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    selectedCounty = countyList.get(i);
                    weatherCode = selectedCounty.getWeatherCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("weatherCode",weatherCode);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }

    public void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {

            dataList.clear();
            for (Province province : provinceList)
                dataList.add(province.getProvinceName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else
            handleProvince();
    }

    public void queryCities() {
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList)
                dataList.add(city.getCityName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            handleCity();
        }
    }

    public void queryCounties() {
        countyList = coolWeatherDB.loadCounties(selectedCity.getId());
        Log.d("Main", String.valueOf(cityList.size()));
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList)
                dataList.add(county.getCountyName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            handleCounty();
        }
    }

    private void handleProvince() {
        Utility.handleProvinceResponse(coolWeatherDB);
        queryProvinces();
    }

    private void handleCity() {
        Utility.handleCityResponse(coolWeatherDB, selectedProvince);
        queryCities();
    }

    private void handleCounty() {
        Utility.handleCountyResponse(coolWeatherDB, selectedCity);
        queryCounties();
    }

    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_COUNTY)
            queryCities();
        else if (currentLevel==LEVEL_CITY)
            queryProvinces();
        else if (isFromWeather)
        {
            Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
            startActivity(intent);
        }else
            finish();
    }
}
