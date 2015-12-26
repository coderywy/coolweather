package com.coolweather.app.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.DatabaseCallBackListener;
import com.coolweather.app.util.DatabaseUtil;
import com.coolweather.app.util.XmlCallBackListener;
import com.coolweather.app.util.XmlParserUtil;
import com.example.coolweather.R;
import com.example.coolweather.WeatherActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.DownloadManager.Query;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	private static final int LEVEL_COUNTY = 2;
	
	private ListView listView;
	private TextView titleText;
	private ArrayAdapter<String> adapter;
	private List<String> dataList = new ArrayList<String>();
	private CoolWeatherDB coolWeatherDB;
	private ProgressDialog progressDialog;
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/**
	 * ѡ�еĳ���
	 */
	private City selectedCity;
	/**
	 * ����ʡ���б�
	 */
	private List<Province> provinceList;
	/**
	 * �����м��б�
	 */
	private List<City> cityList;
	/**
	 * �������ؼ��б�
	 */
	private List<County> countyList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//����Ѿ�ѡ����ص���ֱ����ʾ
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false)){
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		queryProvinces();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(arg2);
					queryCities();
				} else if (currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(arg2);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY){
					String countyCode = countyList.get(arg2).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("conty_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
	}
	
	/**
	 * ��ѯ���е�ʡ�����ȴ����ݿ⿪ʼ�飬���û����ȥ��������ѯ
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
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromXML(null,"province");
		}
	}
	
	private void queryCities(){
		DatabaseUtil.queryAddress(coolWeatherDB, selectedProvince.getProvinceCode(), "city", new DatabaseCallBackListener() {
			
			@Override
			public void onFinish(List<Province> provinceList, final List<City> cityList, List<County> countyList) {
				runOnUiThread(new Runnable() {
					public void run() {
						ChooseAreaActivity.this.cityList = cityList;
						if (cityList.size() >0){
							dataList.clear();
							for (City city : cityList){
								dataList.add(city.getCityName());
							}
							adapter.notifyDataSetChanged();
							listView.setSelection(0);
							titleText.setText(selectedProvince.getProvinceName());
							currentLevel = LEVEL_CITY;
						}
					}
				});
			}
		});
	}
	
	private void queryCounties(){
		DatabaseUtil.queryAddress(coolWeatherDB, selectedCity.getCityCode(), "county", new DatabaseCallBackListener() {
			
			@Override
			public void onFinish(List<Province> provinceList, final List<City> cityList, final List<County> countyList) {
				runOnUiThread(new Runnable() {
					public void run() {
						ChooseAreaActivity.this.countyList = countyList;
						if (countyList.size() > 0){
							dataList.clear();
							for (County county : countyList){
								dataList.add(county.getCountyName());
							}
							adapter.notifyDataSetChanged();
							listView.setSelection(0);
							titleText.setText(selectedCity.getCityName());
							currentLevel = LEVEL_COUNTY;
						}
					}
				});
			}
		});
	}
	
	private void queryFromXML(final String code,final String type){
		showProgessDialog();
		XmlParserUtil.getXMLResource(ChooseAreaActivity.this, "data.xml", new XmlCallBackListener() {
			
			@Override
			public void onFinish(InputStream in) {
				Boolean flag = XmlParserUtil.parserXml(coolWeatherDB, in);
				if (flag){
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							queryProvinces();
						}
					});
				}
			}
			
			@Override
			public void onError() {
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "�����б����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * ��ʾ���ؽ�����
	 */
	private void showProgessDialog() {
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڳ�ʼ�����ݡ�����");
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
	 * �����ؼ�
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if (currentLevel == LEVEL_CITY){
			queryProvinces();
		} else {
			finish();
		}
	}
}
