package com.coolweather.app.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.coolweather.app.model.Province;

import android.content.Context;
import android.util.Xml;

public class XmlParserUtil {

	public static void parserXml(Context context,String path) {
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(context.getAssets().open(path), "UTF-8");
			int eventCode = parser.getEventType();
			Province province = null;
			while (eventCode != XmlPullParser.END_DOCUMENT) {
				switch (eventCode) {
				case XmlPullParser.START_TAG:
					if ("Area".equals(parser.getName())){
						province = new Province();
					}
					if ("areaid".equals(parser.getName())) {
						province.setProvinceCode(parser.nextText());
					}
					if ("prov".equals(parser.getName())) {
						province.setProvinceName(parser.nextText());
					}
					break;
				}
				eventCode = parser.next();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
