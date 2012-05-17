package com.iBeiKe.InfoPortal;

import com.iBeiKe.InfoPortal.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * 关于界面，提供软件的简单介绍与版权信息。
 *
 */
public class About extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}
}