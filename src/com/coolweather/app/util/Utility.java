package com.coolweather.app.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.Province;

import android.text.TextUtils;
import android.util.Xml;

/**
 * 用于解析网络请求获取的数据
 * @author Administrator
 *
 */
public class Utility {
	public static final String PROVICE_XML = "string";
	
	/**
	 * 解析返回的省级数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
		if (!TextUtils.isEmpty(response)){
			/*
			 * 解析xml字符串
			 */
			XmlPullParser parser = Xml.newPullParser();
			InputStream in = new ByteArrayInputStream(response.getBytes());
			try {
				parser.setInput(in, "UTF-8");
				int type = parser.getEventType();
				while (type != XmlPullParser.END_DOCUMENT){
					if (type == XmlPullParser.START_TAG){
						String name = parser.getName();
						String provinceString = parser.getText();
						if (PROVICE_XML.equals(name) && !"".equals(provinceString)){
							Province province = new Province();
							province.setProvinceName(provinceString.split(",")[0]);
							province.setProvinceCode(provinceString.split(",")[1]);
							coolWeatherDB.saveProvince(province);
						}
					}
					type = parser.next();
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} 
		}
		return false;
	}
	
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provincedId){
		
		return false;
	}
}
