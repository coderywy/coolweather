package com.coolweather.app.util;


import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.util.Xml;

public class XmlParserUtil {
	
	public static void getXMLResource(final Context context,final String filaName,final XmlCallBackListener listener){
		new Thread(new Runnable() {
			public void run() {
				InputStream in = null;
				try {
					in = context.getAssets().open(filaName);
					if (listener != null){
						listener.onFinish(in);
					}
				} catch (IOException e) {
					e.printStackTrace();
					if (listener != null){
						listener.onError();
					}
					if (in != null){
						try {
							in.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	public static boolean parserXml(CoolWeatherDB coolWeatherDB,InputStream in) {
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(in, "UTF-8");
			int eventCode = parser.getEventType();
			Province province = null;
			City city = null;
			County county = null;
			while (eventCode != XmlPullParser.END_DOCUMENT) {
				switch (eventCode) {
				case XmlPullParser.START_TAG:
					if ("Area".equals(parser.getName())){
						province = new Province();
						city = new City();
						county = new County();
					}
					if ("areaid".equals(parser.getName())) {
						String code = parser.nextText();
						province.setProvinceCode(code.substring(0, 5));
						city.setCityCode(code.substring(0,7));
						city.setProvinceCode(code.substring(0, 5));
						county.setCountyCode(code);
						county.setCityCode(code.substring(0,7));
					}
					if ("prov".equals(parser.getName())) {
						String name = parser.nextText();
						province.setProvinceName(name);
					}
					if ("city".equals(parser.getName())) {
						String name = parser.nextText();
						city.setCityName(name);
					}
					if ("district".equals(parser.getName())) {
						String name = parser.nextText();
						county.setCountyName(name);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("Area".equals(parser.getName())){
						coolWeatherDB.saveProvince(province);
						if (province.getProvinceName() == city.getCityName()){
							String code = city.getCityCode().substring(0, 5);
							city.setCityCode(code);
							county.setCityCode(code);
						}
						coolWeatherDB.saveCity(city);
						coolWeatherDB.saveCounty(county);
					}
					break;
				}
				eventCode = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
