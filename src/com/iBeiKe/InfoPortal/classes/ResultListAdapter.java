package com.iBeiKe.InfoPortal.classes;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iBeiKe.InfoPortal.About;
import com.iBeiKe.InfoPortal.R;

public class ResultListAdapter extends BaseAdapter {
	private Context context;
	private int i;
	private int[] roomId;
	private LayoutInflater mInflater;
	private ArrayList<ResultList> rooms;
    private class ResultList{
    	public String build;
    	public int[] id;
    	public int[] room;
    	public ResultList(String build, int id[], int[] room) {
    		this.build = build;
    		this.id = id;
    		this.room = room;
    	}
    }
	
	public ResultListAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		rooms = new ArrayList<ResultList>();
	}
	
	public void setData(String build, int[] id, int[] room) {
		roomId = id;
		String ids = "", roomstr = "";
		for(int i=0; i<id.length; i++) {
			ids += " " + id[i];
			roomstr += " " + room[i];
		}
		Log.d("setData", "build=" + build + " id= " + ids + " room= " + roomstr);
		ResultList result = new ResultList(build, id, room);
		rooms.add(result);
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return rooms.size();
	}

	public ResultList getItem(int arg0) {
		return rooms.get(arg0);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View v = convertView;
		if((v == null) || (v.getTag() == null)) {
			v = mInflater.inflate(R.layout.rooms_row, null);
			holder = new ViewHolder();
			holder.build = (TextView)v.findViewById(R.id.room_result1);
			int length = roomId.length;
			int id = R.id.room_result2;
			for(int i=0; i<length; i++) {
				holder.room[i] = (TextView)v.findViewById(id);
				id++;
			}
			v.setTag(holder);
		} else {
			holder = (ViewHolder)v.getTag();
		}
		holder.resultList = getItem(position);
		if(holder.resultList.build != null) {
			holder.build.setText(holder.resultList.build);
		} else {
			int length = roomId.length;
			for(i=0; i<length; i++) {
				holder.room[i].setText(holder.resultList.room[i]+"");
				holder.room[i].setOnClickListener(new OnClickListener() {
		        	public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(context, About.class);
						//Bundle bl = new Bundle();
						//bl.putInt("id", holder.resultList.id[i]);
						//intent.putExtras(bl);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
						context.startActivity(intent);}
				});
			}
		}
		v.setTag(holder);
		return v;
	}
	
	public class ViewHolder {
		ResultList resultList;
		TextView build;
		TextView[] room = new TextView[8];
	}
}