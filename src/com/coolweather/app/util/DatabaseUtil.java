package com.coolweather.app.util;

import java.util.List;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class DatabaseUtil {
	public static List<Province> provinceList = null;
	public static List<City> cityList = null;
	public static List<County> countyList = null;

	public static void queryAddress(final CoolWeatherDB coolWeatherDB,final String code,final String type,final DatabaseCallBackListener listener){
		new Thread(new Runnable() {
			public void run() {
				if ("province".equals(type)){
					provinceList = coolWeatherDB.loadProvinces();
				}else if ("city".equals(type)){
					cityList = coolWeatherDB.loadcities(code);
				}else {
					countyList = coolWeatherDB.loadCounties(code);
				}
				if (listener != null){
					listener.onFinish(provinceList,cityList,countyList);
				}
			}
		}).start();
	}
}
