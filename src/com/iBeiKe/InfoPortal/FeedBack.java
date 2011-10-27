package com.iBeiKe.InfoPortal;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class FeedBack extends Activity {
	private Button btnOK;
	private Button btnCancel;
	private EditText feed_back_text;
	private String feed_back_content;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_back);
        
		feed_back_text = (EditText) findViewById(R.id.feed_back_text);
		btnOK = (Button) findViewById(R.id.post_button);
		btnCancel = (Button) findViewById(R.id.cancel_button);
		btnOK.setOnClickListener(new myOnPostButtonClick());
		btnCancel.setOnClickListener(new myOnCancelButtonClick());
		feed_back_content = feed_back_text.getText().toString();
	}
	public class myOnPostButtonClick implements OnClickListener {
		public void onClick(View v) {
			
		}
	}
	public class myOnCancelButtonClick implements OnClickListener {
		public void onClick(View v) {
			finish();
		}
	}
}
