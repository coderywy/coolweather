package com.coolweather.app.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.adapter.WeatherAdapter;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.JsonUtil;
import com.example.coolweather.R;
import com.example.coolweather.R.id;
import com.example.coolweather.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	private TextView cityName;
	private TextView temp;
	private TextView windDirection;
	private TextView windLevel;
	private TextView dampness;
	private TextView weather;
	private TextView home;
	private TextView refresh;
	private ListView futureList;
	private List<String> infoList = new ArrayList<String>();
	private WeatherAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather);
		
		//初始化各个控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weatherInfoLayout);
		cityName = (TextView) findViewById(R.id.city_name);
		temp = (TextView) findViewById(R.id.temp);
		windDirection = (TextView) findViewById(R.id.windDirection);
		windLevel = (TextView) findViewById(R.id.windLevel);
		dampness = (TextView) findViewById(R.id.dampness);
		weather = (TextView) findViewById(R.id.weather);
		home = (TextView) findViewById(R.id.home);
		refresh = (TextView) findViewById(R.id.refresh);
		futureList = (ListView) findViewById(R.id.futureList);
		adapter = new WeatherAdapter(this, infoList);
		futureList.setAdapter(adapter);
		
		home.setOnClickListener(this);
		refresh.setOnClickListener(this);
		
		String countyCode = getIntent().getStringExtra("conty_code");
		if (!TextUtils.isEmpty(countyCode)){
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			queryWeatherByCode(countyCode,"now");
			queryFutureByCode(countyCode,"future");
		}else {
			showWeather();
			showFutureWeather();
		}
		
	}

	private void queryWeatherByCode(String countyCode,String type) {
		String address = "http://api.k780.com:88/?app=weather.today&weaid="+countyCode+"&&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
		queryFromServer(address,type);
	}
	
	private void queryFutureByCode(String countyCode,String type) {
		String address = "http://api.k780.com:88/?app=weather.future&weaid="+countyCode+"&&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
		queryFromServer(address,type);
	}

	private void queryFromServer(String address,final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if ("now".equals(type)){
					JsonUtil.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});
				} else {
					JsonUtil.handleFutureWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						public void run() {
							showFutureWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(IOException e) {
				Toast.makeText(WeatherActivity.this, "未能查询该城市天气信息。。。", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityName.setText(prefs.getString("city_name", ""));
		temp.setText(prefs.getString("temp", ""));
		windDirection.setText(prefs.getString("windDirection", ""));
		windLevel.setText(prefs.getString("windLevel", ""));
		dampness.setText("湿度："+prefs.getString("dampness", ""));
		weather.setText(prefs.getString("weather", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}
	
	private void showFutureWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		for (int i = 0;i <=7;i++){
			infoList.add(prefs.getString(""+i, ""));
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weatherActivity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh:
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String countyCode = prefs.getString("weatherCode", "");
			queryWeatherByCode(countyCode,"now");
			Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

}
