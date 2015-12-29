package com.coolweather.app.adapter;

import java.util.List;

import com.example.coolweather.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WeatherAdapter extends BaseAdapter {
	
	List<String> futureList;
	LayoutInflater inflater;
	
	public WeatherAdapter(Context context,List<String> futureList) {
		this.futureList = futureList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return futureList.size();
	}

	@Override
	public Object getItem(int position) {
		return futureList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null){
			convertView = inflater.inflate(R.layout.weather_item, null);
			viewHolder = new ViewHolder();
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.info = (TextView) convertView.findViewById(R.id.info);
		viewHolder.info.setText(futureList.get(position));
		convertView.setTag(viewHolder);
		return convertView;
	}
	
	class ViewHolder{
		public TextView info;
	}

}
