package com.iBeiKe.InfoPortal.common;

import java.util.ArrayList;
import java.util.Map;

import android.os.Bundle;
import android.os.Message;

public class MessageHandler {
	private Message msg;
	private Bundle bul;
	
	public MessageHandler() {
		//Message.obtain() is preferable to Message m = new Message()
		//(because it recycles used messages under the hood)
		msg = Message.obtain();
		bul = new Bundle();
	}
	
	public void bundle(String key, String value) {
		bul.putString(key, value);
	}
	
	public void bundle(String key, Map<String,String> value) {
    	ArrayList<Map<String,String>> content = new ArrayList<Map<String,String>>();
    	content.add(value);
		bul.putSerializable(key, content);
	}
	
	public String getString(String key, Message message) {
		String content = message.getData().getString(key);
		return content;
	}
	
	public Map<String,String> getMap(String key, Message message) {
		ArrayList<Map<String,String>> content
				= (ArrayList<Map<String, String>>) message.getData().getSerializable(key);
		Map<String,String> item = content.get(0);
		return item;
	}
	
	public Message get() {
		msg.setData(bul);
		return msg;
	}
	
	public void clean() {
		bul.clear();
	}
}
