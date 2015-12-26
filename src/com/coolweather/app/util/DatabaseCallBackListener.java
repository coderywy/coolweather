package com.coolweather.app.util;

import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public interface DatabaseCallBackListener {

	void onFinish(List<Province> provinceList, List<City> cityList, List<County> countyList);

}
