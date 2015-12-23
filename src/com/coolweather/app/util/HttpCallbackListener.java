package com.coolweather.app.util;

import java.io.IOException;

public interface HttpCallbackListener {

	void onFinish(String string);

	void onError(IOException e);

}
