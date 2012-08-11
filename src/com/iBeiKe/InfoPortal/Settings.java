package com.iBeiKe.InfoPortal;


import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.update.Update;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

/**
 * 设置页面提供的功能。
 *
 */
public class Settings extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if(preference.getKey().equals("db_initialize")) {
			Intent intent = new Intent(this,Initialize.class);
			startActivity(intent);
		} else if(preference.getKey().equals("feed_back")) {
			Intent intent = new Intent(this,FeedBack.class);
			startActivity(intent);
		} else if(preference.getKey().equals("about")) {
			Intent intent = new Intent(this,About.class);
			startActivity(intent);
		} else if(preference.getKey().equals("db_update")) {
			Update update = new Update();
		}
	return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}