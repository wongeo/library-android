package com.feng.util.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Setting {
	private SharedPreferences mSP;
	private Editor mEditor;

	public Setting(Context context, String name) {
		mSP = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		mEditor = mSP.edit();
		mEditor.apply();
	}

	public synchronized String getString(String key, String defValue) {
		return mSP.getString(key, defValue);
	}

	public synchronized void setString(String key, String value) {
		mEditor.putString(key, value);
		mEditor.commit();
	}

	public synchronized int getInt(String key, int defValue) {
		return mSP.getInt(key, defValue);
	}

	public synchronized void setInt(String key, int value) {
		mEditor.putInt(key, value);
		mEditor.commit();
	}

	public synchronized long getLong(String key, long defValue) {
		return mSP.getLong(key, defValue);
	}

	public synchronized void setLong(String key, long value) {
		mEditor.putLong(key, value);
		mEditor.commit();
	}

	public synchronized float getFloat(String key, float defValue) {
		return mSP.getFloat(key, defValue);
	}

	public synchronized void seFloat(String key, float value) {
		mEditor.putFloat(key, value);
		mEditor.commit();
	}

	public synchronized boolean getBoolean(String key, boolean defValue) {
		return mSP.getBoolean(key, defValue);
	}

	public synchronized void setBoolean(String key, boolean value) {
		mEditor.putBoolean(key, value);
		mEditor.commit();
	}
}
