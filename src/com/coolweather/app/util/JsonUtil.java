package com.coolweather.app.util;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class JsonUtil {
	
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp = weatherInfo.getString("temp");
			String windDirection = weatherInfo.getString("WD");
			String windLevel = weatherInfo.getString("WS");
			String dampness = weatherInfo.getString("SD");
			String pressure = weatherInfo.getString("qy");
			saveWeatherInfo(context,cityName,weatherCode,temp,windDirection,windLevel,dampness,pressure);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp,
			String windDirection, String windLevel, String dampness, String pressure) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weatherCode", weatherCode);
		editor.putString("temp", temp);
		editor.putString("windDirection", windDirection);
		editor.putString("windLevel", windLevel);
		editor.putString("dampness", dampness);
		editor.putString("pressure", pressure);
		editor.commit();
	}

}
