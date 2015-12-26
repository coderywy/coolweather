package com.coolweather.app.util;

import java.io.InputStream;

public interface XmlCallBackListener {

	void onError();

	void onFinish(InputStream in);

}
