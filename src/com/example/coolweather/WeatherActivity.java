package com.example.coolweather;

import java.io.IOException;

import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.JsonUtil;

import android.R.layout;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {
	private LinearLayout weatherInfoLayout;
	private TextView cityName;
	private TextView temp;
	private TextView wind;
	private TextView dampness;
	private TextView pressure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//初始化各个控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weatherInfoLayout);
		cityName = (TextView) findViewById(R.id.city_name);
		temp = (TextView) findViewById(R.id.temp);
		wind = (TextView) findViewById(R.id.wind);
		dampness = (TextView) findViewById(R.id.dampness);
		pressure = (TextView) findViewById(R.id.pressure);
		
		String countyCode = getIntent().getStringExtra("conty_code");
		if (!TextUtils.isEmpty(countyCode)){
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			queryWeatherByCode(countyCode);
		}
		
	}

	private void queryWeatherByCode(String countyCode) {
		String address = "http://www.weather.com.cn/adat/sk/"+countyCode+".html";
		queryFromServer(address);
	}

	private void queryFromServer(String address) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				JsonUtil.handleWeatherResponse(WeatherActivity.this, response);
				runOnUiThread(new Runnable() {
					public void run() {
						showWeather();
					}
				});
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
		wind.setText(prefs.getString("windDirection", "") + prefs.getString("windLevel", ""));
		dampness.setText("湿度："+prefs.getString("dampness", ""));
		pressure.setText("气压："+prefs.getString("pressure", "")+"hpa");
		weatherInfoLayout.setVisibility(View.VISIBLE);
	}

}
