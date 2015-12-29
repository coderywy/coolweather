package com.coolweather.app.util;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.LruCache;

public class JsonUtil {
	
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("result");
			String cityName = weatherInfo.getString("citynm");
			String weatherCode = weatherInfo.getString("cityid");
			String temp = weatherInfo.getString("temperature_curr");
			String windDirection = weatherInfo.getString("wind");
			String windLevel = weatherInfo.getString("winp");
			String dampness = weatherInfo.getString("humidity");
			String weather = weatherInfo.getString("weather");
			saveWeatherInfo(context,cityName,weatherCode,temp,windDirection,windLevel,dampness,weather);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void handleFutureWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONArray array = jsonObject.getJSONArray("result");
			for (int i = 0;i < array.length();i++){
				JSONObject weather = (JSONObject)array.get(i);
				String info = weather.getString("week") + "|" + weather.getString("weather") + "\t" + weather.getString("temperature") + "|" + weather.getString("winp");
				saveFutureWeatherInfo(context,info,i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private static void saveFutureWeatherInfo(Context context, String info, int i) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString(""+i, info);
		editor.commit();
	}

	private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp,
			String windDirection, String windLevel, String dampness, String weather) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weatherCode", weatherCode);
		editor.putString("temp", temp);
		editor.putString("windDirection", windDirection);
		editor.putString("windLevel", windLevel);
		editor.putString("dampness", dampness);
		editor.putString("weather", weather);
		editor.commit();
	}

}
