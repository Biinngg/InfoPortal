package com.iBeiKe.InfoPortal.library;

import java.util.HashMap;
import java.util.Map;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.AESEncryptor;
import com.iBeiKe.InfoPortal.database.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class LoginHelper {
	Context context;
	Database db;
	String seed = "su4dp5xm37";
	
	public LoginHelper(Context context) {
		this.context = context;
		db = new Database(context);
	}
	
	public Map<String,String> getLoginData() {
		String[] columns = new String[] {"user", "passwd", "type"};
		String where = "table_name=\'lib_login\'";
		Map<String,String> result = new HashMap<String,String>();
		db.read();
		Cursor cursor = db.getCursor("login", columns, where, null);
		if(cursor.getCount() == 0) {
			db.close();
			return null;
		}
		cursor.moveToNext();
		String user = cursor.getString(0);
		String passwd = cursor.getString(1);
		int type = cursor.getInt(2);

        try{
            passwd = AESEncryptor.decrypt(seed, passwd);
        }catch(Exception ex){
            Toast.makeText(context, context.getString(R.string.decrypt_error), Toast.LENGTH_SHORT);
            return null;
        }

		columns = new String[] {"_id", "name", "value"};
		cursor = db.getCursor("lib_login", columns, null, null);
		while(cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String name = cursor.getString(1);
			String value = cursor.getString(2);
			if(id == type) {
				result.put(name, value);
			} else if(name.equals("user")) {
				result.put(value, user);
			} else if(name.equals("passwd")) {
				result.put(value, passwd);
			}
		}
		String[] myLibUrl = db.getString("lib_urls", "value", "name=\'lib_my\'", null, 0);
		String[] loginUrl = db.getString("lib_urls", "value", "name=\'lib_login\'", null, 0);
		result.put("myUrl", myLibUrl[0]);
		result.put("logUrl", loginUrl[0]);
		db.close();
		return result;
	}
	
	public void saveLoginData(String user, String passwd) {
		int type;
		ContentValues cv = new ContentValues();
		if(user.contains("@")) {
			type = 4;
		} else {
			type = 3;
		}
        try{
            passwd = AESEncryptor.encrypt(seed, passwd);
        }catch(Exception ex){
            Toast.makeText(context, context.getString(R.string.encrypt_error), Toast.LENGTH_SHORT);
        }
        Log.d("saveLoginData", "user=" + user + " passwd=" + passwd);
        cv.put("table_name", "lib_login");
		cv.put("user", user);
		cv.put("passwd", passwd);
		cv.put("type", type);
		db.write();
		db.insert("login", cv);
		db.close();
	}
}
