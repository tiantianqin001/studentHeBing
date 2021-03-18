package com.zbv.meeting.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference工具类
 *
 */
public class SharedPreferenceUtil {

	private static String SP_NAME = "huimiaomiao_share_date";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private static SharedPreferenceUtil instance;
	
	public static synchronized SharedPreferenceUtil getInstance(Context context) {
		if (instance == null) {
			instance = new SharedPreferenceUtil(context);
		}
		return instance;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	private SharedPreferenceUtil(Context context) {
		init(context);
	}

	/**
	 * 初使化
	 */
	private void init(Context context) {
		if (context != null) {
			sp = context.getSharedPreferences(SP_NAME, Context.MODE_APPEND);
			editor = sp.edit();
		}
	}
	
	/**
	 * 添加String
	 * @Description 
	 * @param key
	 * @param value
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public void setString(String key, String value){
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * 获取String
	 * @Description 
	 * @param key
	 * @param defValue 默认值
	 * @return
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public String getString(String key, String defValue) {
		return sp.getString(key, defValue);
	}
	
	/**
	 * 获取String
	 * @Description 
	 * @param key
	 * @return
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public String getString(String key) {
		return getString(key, null);
	}
	
	/**
	 * 添加Int
	 * @Description 
	 * @param key
	 * @param value
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public void setInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}


	/**
	 * 获取Int
	 * @Description 
	 * @param key
	 * @param defValue 默认值
	 * @return
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public int getInt(String key, int defValue) {
		return sp.getInt(key, defValue);
	}
	
	/**
	 * 获取Int
	 * @Description 
	 * @param key
	 * @return
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public int getInt(String key) {
		return getInt(key, 0);
	}
	
	
	/**
	 * 添加float
	 * @Description 
	 * @param key
	 * @param value
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public void setFloat(String key, float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	/**
	 * 获取float
	 * @Description 
	 * @param key
	 * @param defValue 默认值
	 * @return
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public float getFloat(String key, float defValue) {
		return sp.getFloat(key, defValue);
	}
	
	/**
	 * 获取float
	 * @Description 
	 * @param key
	 * @return
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public float getFloat(String key) {
		return getFloat(key, 0.0f);
	}

	/**
	 * 添加boolean
	 * @Description 
	 * @param key
	 * @param value
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public void setBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 *  获取boolean
	 * @Description 
	 * @param key
	 * @param defValue
	 * @return
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public boolean getBoolean(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}
	
	/**
	 *  获取boolean
	 * @Description 
	 * @param key
	 * @return
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	
	/**
	 *  删除
	 * @Description 
	 * @param key
	 * @Author zhaoqianpeng(zqp@yitong.com.cn) 2014-7-18
	 */
	public void delContent(String key) {
		editor.remove(key);
		editor.commit();
	}
	
//	public final static String SP_NAME = "SP_NAME_1";

//	public static String getInfoFromShared(String key) {
//		SharedPreferences preferences = MyApplication.mApp
//				.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
//		return preferences.getString(key, null);
//	}
//
//	public static String getInfoFromShared(String key, String defValue) {
//		SharedPreferences preferences = MyApplication.mApp
//				.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
//		return preferences.getString(key, defValue);
//	}
//
//	public static boolean setInfoToShared(String key, String value) {
//		SharedPreferences preferences = MyApplication.mApp
//				.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
//		Editor editor = preferences.edit();
//		editor.putString(key, value);
//		editor.commit();
//		return true;
//	}
}
