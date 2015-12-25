package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 通过请求网络接口服务，得到所有的城市列表
 * @author Administrator
 *
 */
public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					//connection.setRequestMethod("GET");
					//connection.setReadTimeout(80000);
					//connection.setConnectTimeout(80000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null){
						response.append(line);
					}
					if (listener != null){
						listener.onFinish(response.toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
					if (listener != null){
						listener.onError(e);
					}
				} finally {
					if (connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
