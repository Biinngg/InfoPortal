package com.iBeiKe.InfoPortal.campus;

import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.MessageHandler;

/**
 * 校园卡信息的获取显示类。
 * 对基本界面内容的显示，初始化，并且建立子线程，
 * 登录空间的显示，radioGroup的点击时间处理，
 * 获取单选按钮的id，登录按钮事件，以及异步的服务器数据获取存储、界面的更新。
 *
 */
public class Campus extends Activity implements Runnable{
	private ProgressBar bar;
	private Button button;
	private TextView status;
	private EditText userName, password;
	private CampusHandler handler;
	private CampusHelper helper;
	private LinearLayout infoLayout;
	private RelativeLayout loginLayout;
	private CampusListAdapter adapter;
	static String campInfoTable = "camp_info";
	static String campDetailTable = "camp_detail";
	static String[] campInfoColumns = new String[]{
			"name", "id", "cardid", "currentState", "disableState"};
	static String[] campDetailColumns =
			new String[]{"time", "place", "cost", "left"};
	private String[] type = new String[]{
			"day", "mon", "lastmon", "year"};
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campus);

        handler = new CampusHandler(this);
        helper = new CampusHelper(this);
        adapter = new CampusListAdapter(this);
        
        initial();
    }
    
    private void disCampInfo() {
    	Map<String,String> map = helper.getCampInfoData();
		infoLayout.setVisibility(View.VISIBLE);
		loginLayout.setVisibility(View.GONE);
    	TextView tv = (TextView)findViewById(R.id.camp_stu_name);
    	tv.setText(map.get(campInfoColumns[0]));
    	tv = (TextView)findViewById(R.id.camp_stu_id);
    	tv.setText(map.get(campInfoColumns[1]));
    	tv = (TextView)findViewById(R.id.camp_card_id);
    	tv.setText(map.get(campInfoColumns[2]));
    	tv = (TextView)findViewById(R.id.camp_card_cur);
    	tv.setText(map.get(campInfoColumns[3]));
    	tv = (TextView)findViewById(R.id.camp_card_dis);
    	tv.setText(map.get(campInfoColumns[4]));
    }
    
    private void initial() {
    	TextView title = (TextView)findViewById(R.id.header_title);
    	title.setText(R.string.camp_title);
    	Button back = (Button)findViewById(R.id.top_back);
    	back.setVisibility(View.GONE);
    	ListView list = (ListView)findViewById(R.id.camp_card_list);
    	list.setAdapter(adapter);
		RadioGroup rg = (RadioGroup)findViewById(R.id.camp_type);
		rg.setOnCheckedChangeListener(new radioGroupChangedListener());
		status = (TextView)findViewById(R.id.camp_login_status);
		infoLayout = (LinearLayout)findViewById(R.id.camp_info);
		loginLayout = (RelativeLayout)findViewById(R.id.camp_login);
    	String apiUrl = handler.getApiUrl(null);
    	if(apiUrl == null) {
    		relogin();
    	} else {
    		Thread thread = new Thread(this);
    		thread.start();
    	}
    }
    
    private void relogin() {
		infoLayout.setVisibility(View.GONE);
		loginLayout.setVisibility(View.VISIBLE);
		bar = (ProgressBar)findViewById(R.id.camp_login_progress);
		button = (Button)findViewById(R.id.camp_login_button);
        userName = (EditText)findViewById(R.id.camp_username);
        password = (EditText)findViewById(R.id.camp_password);
        bar.setVisibility(View.GONE);
        button.setClickable(true);
        button.setOnClickListener(new LoginClickListener());
    }
    
    class radioGroupChangedListener implements OnCheckedChangeListener {
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch(checkedId) {
			case R.id.camp_type_day:
				handler.getApiUrl(type[0]);
				break;
			case R.id.camp_type_mon:
				handler.getApiUrl(type[1]);
				break;
			case R.id.camp_type_lastmon:
				handler.getApiUrl(type[2]);
				break;
			case R.id.camp_type_year:
				handler.getApiUrl(type[3]);
				break;
			}
			Thread thread = new Thread(Campus.this);
			thread.start();
		}
    }
    
    private int getRadioNum() {
    	RadioButton day = (RadioButton)findViewById(R.id.camp_type_day);
    	RadioButton mon = (RadioButton)findViewById(R.id.camp_type_mon);
    	RadioButton lastmon = (RadioButton)findViewById(R.id.camp_type_lastmon);
    	RadioButton year = (RadioButton)findViewById(R.id.camp_type_year);
    	if(day.isChecked()) {
    		return 0;
    	}
    	if(mon.isChecked()) {
    		return 1;
    	}
    	if(lastmon.isChecked()) {
    		return 2;
    	}
    	if(year.isChecked()) {
    		return 3;
    	}
    	return 0;
    }
    
    class LoginClickListener implements OnClickListener {
    	public void onClick(View v) {
	        String userString = userName.getText().toString();
	        String passString = password.getText().toString();
	        int typeNum = getRadioNum();
	        if(userString != null && passString != null) {
	            button.setClickable(false);
	            bar.setVisibility(View.VISIBLE);
	            status.setVisibility(View.VISIBLE);
	            status.setText(getText(R.string.logining));
	        	helper.saveLoginData(userString, passString, type[typeNum]);
				handler.getApiUrl("day");
		        Thread thread = new Thread(Campus.this);
		        thread.start();
	        }
    	}
    }
	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.getData().containsKey("0")) {
	        	status.setVisibility(View.VISIBLE);
	        	status.setText(R.string.login_faile);
	        	relogin();
			} else {
	        	disCampInfo();
	        	adapter.notifyDataSetChanged();
			}
    	}
	};
	
	public void run() {
		if(!Thread.interrupted()) {
	        MessageHandler mh = new MessageHandler();
	        String htmlBody = handler.fetchData();
	        boolean result = false;
	        if(htmlBody.length() == 0) {
	        	mh.bundle("0", "");
	        } else {
	        	try {
	        		result = handler.parseAndSave(htmlBody);
	        	} catch (JSONException e) {
	        		Log.e("Campus.run", e.toString());
	        	}
	        	if(result) {
	        		mh.bundle("1", "");
	        	} else {
	        		mh.bundle("0", "");
	        	}
			}
	        Log.d("campus run", htmlBody + " test");
	        mHandler.sendMessage(mh.get());
		}
	}
}