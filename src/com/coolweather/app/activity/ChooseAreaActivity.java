package com.coolweather.app.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.example.coolweather.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	
	private static final int LEVEL_PROVINCE = 0;
	private static final int LEVEL_CITY = 1;
	
	private ListView listView;
	private TextView titleText;
	private ArrayAdapter<String> adapter;
	private List<String> dataList = new ArrayList<String>();
	private CoolWeatherDB coolWeatherDB;
	private ProgressDialog progressDialog;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 选中的城市
	 */
	private City selectedCity;
	/**
	 * 定义省级列表
	 */
	private List<Province> provinceList;
	/**
	 * 定义市级列表
	 */
	private List<City> cityList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(arg2);
					queryCities();
				} else {
					selectedCity = cityList.get(arg2);
				}
			}
		});
		queryProvinces();
		
	}

	protected void queryCities() {
		cityList = coolWeatherDB.loadcities(selectedProvince.getProvinceCode());
		if (cityList.size() > 0){
			dataList.clear();
			for (City city :cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}

	/**
	 * 查询所有的省，优先从数据库开始查，如果没有再去服务器查询
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0){
			dataList.clear();
			for (Province province : provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null,"province");
		}
	}

	private void queryFromServer(final String code,final String type) {
		String address;
		if (!TextUtils.isEmpty(code)){
			address = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getRegionProvince";
		} else {
			address = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getSupportCityString?theRegionCode="+code;
		}
		showProgessDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)){
					result = Utility.handleProvincesResponse(coolWeatherDB, response);
				}else if("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getProvinceCode());
				}
				if (result){
					//通过runOnUiThread方法回到主线程
					runOnUiThread(new Runnable() {
						public void run() {
							if ("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)) {
								queryCities();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(IOException e) {
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 显示加载进度条
	 */
	private void showProgessDialog() {
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载。。。");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog(){
		if (progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 处理返回键
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
}
