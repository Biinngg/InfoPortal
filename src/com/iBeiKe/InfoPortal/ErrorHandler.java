package com.iBeiKe.InfoPortal;

import com.iBeiKe.InfoPortal.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

//TODO:����������Ĵ��󣬻��������á�
public class ErrorHandler extends Activity {
	private StringBuilder builder = new StringBuilder();
	private String error_msg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.error_handler);
		
		Intent intent=this.getIntent();
		Bundle bl=intent.getExtras();
		int ErrorNum = bl.getInt("ErrorNum");
		DisplayError(ErrorNum);
		builder.append(error_msg);
    	TextView text = (TextView) findViewById(R.id.error_content);
    	text.setText(builder);
    }
	
	public String DisplayError(int ErrorNum) {
		switch(ErrorNum) {
		case 1:
			error_msg = "\nû�з�ϲ�ѯҪ��Ľ����������¥�㣿\n\n";
			break;
		case 2:
			error_msg = "��������ĩ�������ѡ���κ�һ�������ϿΡ�\n";
			break;
		}
		return error_msg;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}