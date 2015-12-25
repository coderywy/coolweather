package com.coolweather.app.util;


import org.xmlpull.v1.XmlPullParser;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.util.Xml;

public class XmlParserUtil {
	private Context context;
	private CoolWeatherDB coolWeatherDB;

	private XmlParserUtil(Context context) {
		super();
		this.context = context;
		this.coolWeatherDB = CoolWeatherDB.getInstance(context);
	}

	/**
	 * 得到xml解析对象
	 * @param context
	 * @return
	 */
	public static XmlParserUtil getInstance(Context context){
		return new XmlParserUtil(context);
	}


	public static boolean parserXml(Context context,CoolWeatherDB coolWeatherDB,String path) {
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(context.getAssets().open(path), "UTF-8");
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
						city.setCityCode(code.substring(0,6));
						city.setProvinceCode(code.substring(0, 5));
						county.setCountyCode(code);
						county.setCityCode(code.substring(0,6));
					}
					if ("prov".equals(parser.getName())) {
						String name = parser.nextText();
						province.setProvinceName(name);
					}
					if ("city".equals(parser.getName())) {
						String name = parser.nextText();
						city.setCityName(name);
					}
					if ("county".equals(parser.getName())) {
						String name = parser.nextText();
						county.setCountyName(name);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("Area".equals(parser.getName())){
						coolWeatherDB.saveProvince(province);
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
