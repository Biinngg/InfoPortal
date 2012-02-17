package com.iBeiKe.InfoPortal.classes;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iBeiKe.InfoPortal.About;
import com.iBeiKe.InfoPortal.R;

public class ResultListAdapter extends BaseAdapter {
	private Context context;
	private int[] roomId;
	private LayoutInflater mInflater;
	private ArrayList<ResultList> rooms;
    private class ResultList{
    	public String build;
    	//public int[] id;
    	public String room1;
    	public String room2;
    	public String room3;
    	public String room4;
    	public String room5;
    	public String room6;
    	public String room7;
    	public String room8;
    	public ResultList(String build, int id[], int[] room) {
    		this.build = build;
    		//this.id = id;
    		this.room1 = room[0] + "";
    		this.room2 = room[1] + "";
    		this.room3 = room[2] + "";
    		this.room4 = room[3] + "";
    		//this.room5 = room[4] + "";
    		//this.room6 = room[5] + "";
    		//this.room7 = room[6] + "";
    		//this.room8 = room[7] + "";
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
		Log.d("getItem", "room1=" + rooms.get(arg0).room1 + " length=" + rooms.size());
		return rooms.get(arg0);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		View v = convertView;
		int length = roomId.length;
		if((v == null) || (v.getTag() == null)) {
			v = mInflater.inflate(R.layout.rooms_row, null);
			holder = new ViewHolder();
			holder.build = (TextView)v.findViewById(R.id.room_result);
			holder.room1 = (TextView)v.findViewById(R.id.room_result1);
			holder.room2 = (TextView)v.findViewById(R.id.room_result2);
			holder.room3 = (TextView)v.findViewById(R.id.room_result3);
			holder.room4 = (TextView)v.findViewById(R.id.room_result4);
			holder.room5 = (TextView)v.findViewById(R.id.room_result5);
			holder.room6 = (TextView)v.findViewById(R.id.room_result6);
			holder.room7 = (TextView)v.findViewById(R.id.room_result7);
			holder.room8 = (TextView)v.findViewById(R.id.room_result8);
			holder.layout1 = (LinearLayout)v.findViewById(R.id.room_layout1);
			holder.layout2 = (LinearLayout)v.findViewById(R.id.room_layout2);
			holder.layout3 = (LinearLayout)v.findViewById(R.id.room_layout3);
			holder.layout4 = (LinearLayout)v.findViewById(R.id.room_layout4);
			holder.layout5 = (LinearLayout)v.findViewById(R.id.room_layout5);
			holder.layout6 = (LinearLayout)v.findViewById(R.id.room_layout6);
			holder.layout7 = (LinearLayout)v.findViewById(R.id.room_layout7);
			holder.layout8 = (LinearLayout)v.findViewById(R.id.room_layout8);
			v.setTag(holder);
		} else {
			holder = (ViewHolder)v.getTag();
		}
		holder.resultList = getItem(position);
		if(holder.resultList.build != null) {
			View[] sepView = new View[7];
			holder.build.setText(holder.resultList.build);
			sepView[0] = (View)v.findViewById(R.id.separater1);
			sepView[1] = (View)v.findViewById(R.id.separater2);
			sepView[2] = (View)v.findViewById(R.id.separater3);
			sepView[3] = (View)v.findViewById(R.id.separater4);
			sepView[4] = (View)v.findViewById(R.id.separater5);
			sepView[5] = (View)v.findViewById(R.id.separater6);
			sepView[6] = (View)v.findViewById(R.id.separater7);
			for(int i=0; i<7; i++) {
				sepView[i].setVisibility(View.INVISIBLE);
			}
		} else {
			holder.room1.setText(holder.resultList.room1 + "");
			holder.room2.setText(holder.resultList.room2 + "");
			holder.room3.setText(holder.resultList.room3 + "");
			holder.room4.setText(holder.resultList.room4 + "");
			//holder.room5.setText(holder.resultList.room[4] + "");
			//holder.room6.setText(holder.resultList.room[5] + "");
			//holder.room7.setText(holder.resultList.room[6] + "");
			//holder.room8.setText(holder.resultList.room[7] + "");
			//holder.layout1.setId(holder.resultList.id[0]);
			//holder.layout2.setId(holder.resultList.id[1]);
			//holder.layout3.setId(holder.resultList.id[2]);
			//holder.layout4.setId(holder.resultList.id[3]);
			/*
			for(int i=0; i<length; i++) {
				holder.layout[i].setId(holder.resultList.id[i]);
				holder.layout[i].setOnClickListener(new OnClickListener() {
		        	public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(context, About.class);
						Log.d("id", "id=" + v.getId());
						//Bundle bl = new Bundle();
						//bl.putInt("id", holder.resultList.id[i]);
						//intent.putExtras(bl);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
						context.startActivity(intent);}
				});
			}
			*/
		}
		v.setTag(holder);
		return v;
	}
	
	public class ViewHolder {
		ResultList resultList;
		TextView build;
		TextView room1;
		TextView room2;
		TextView room3;
		TextView room4;
		TextView room5;
		TextView room6;
		TextView room7;
		TextView room8;
		LinearLayout layout1;
		LinearLayout layout2;
		LinearLayout layout3;
		LinearLayout layout4;
		LinearLayout layout5;
		LinearLayout layout6;
		LinearLayout layout7;
		LinearLayout layout8;
	}
}