package com.iBeiKe.InfoPortal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

//TODO:用来处理更多的错误，或者舍弃不用。
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
			error_msg = "\n没有符合查询要求的结果，试试其他楼层？\n\n";
			break;
		case 2:
			error_msg = "今天是周末，你可以选择任何一个教室上课。\n";
			break;
		}
		return error_msg;
	}
	
	protected void onPause() {
		super.onPause();
		finish();
	}
}