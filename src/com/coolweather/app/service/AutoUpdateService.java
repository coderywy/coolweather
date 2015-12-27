package com.coolweather.app.service;

import java.io.IOException;

import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.JsonUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			public void run() {
				updateWeather();
			}
		}).start();
		//���ö�ʱ�����Զ�����
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int hour = 8 * 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + hour;
		Intent i = new Intent(this,AutoUpdateService.class);
		//����һ���㲥�����ù㲥���͵��������������������ķ���
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	protected void updateWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weatherCode", "");
		String address = "http://www.weather.com.cn/adat/sk/"+weatherCode+".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String string) {
				JsonUtil.handleWeatherResponse(AutoUpdateService.this, string);
			}
			
			@Override
			public void onError(IOException e) {
				e.printStackTrace();
			}
		});
	}

}
