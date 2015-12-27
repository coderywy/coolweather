package com.coolweather.app.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	
	/**
	 * Province�������
	 */
	private static final String CREATE_PROVINCE = "create table Province ("
			+ "id integer primary key autoincrement,"
			+ "province_name text,"
			+ "province_code text)";
	
	/**
	 * City�������
	 */
	private static final String CREATE_CITY = "create table City ("
			+ "id integer primary key autoincrement,"
			+ "city_name text,"
			+ "city_code text,"
			+ "province_code text)";
	
	/**
	 * County������� 
	 */
	private static final String CREATE_COUNTY = "create table county ("
			+ "id integer primary key autoincrement,"
			+ "county_name text,"
			+ "county_code text,"
			+ "city_code text)";
	
	/**
	 *	Ĭ��Ϊfalse��ֱ�Ӹ���db�ļ���
	 *	�����ļ���ȡ�������xml�ļ��н������������ļ� 
	 */
	private Boolean flag = false;

	public CoolWeatherOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		try {
			String path = "/data/data/com.example.coolweather/databases/";
			File file = new File(path+name);
			if (!file.exists()){
				File f = new File(path);
				if (!f.exists()){
					f.mkdir();
				}
				InputStream in = context.getAssets().open(name);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = in.read(buffer)) > 0){
					fos.write(buffer,0,count);
				}
				fos.close();
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			flag = true;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (flag){
			db.execSQL(CREATE_PROVINCE);
			db.execSQL(CREATE_CITY);
			db.execSQL(CREATE_COUNTY);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
