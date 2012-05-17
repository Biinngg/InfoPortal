package com.iBeiKe.InfoPortal.classes;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iBeiKe.InfoPortal.R;

/**
 * 教室一周占用情况列表显示控制。
 * 继承自BaseAdapter，提供对教室一周中每节课使用情况的显示，
 * 获取数据条数，获取元素，获取元素ID以及对列表内元素以及动作进行维护支持。
 *
 */
public class RoomInfoWeeklyAdapter extends BaseAdapter {
	private final int[] roomInfoId;
	private LayoutInflater mInflater;
	private ArrayList<RoomStateList> states;
    private class RoomStateList{
    	public String[] state;
    	public RoomStateList(String[] state) {
    		this.state = state;
    	}
    }
	
	public RoomInfoWeeklyAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		states = new ArrayList<RoomStateList>();
		roomInfoId = new int[]{R.id.room_info1, R.id.room_info2,
				R.id.room_info3, R.id.room_info4, R.id.room_info5,
				R.id.room_info6, R.id.room_info7, R.id.room_info8};
	}
	
	public void setData(String[] state) {
		RoomStateList list = new RoomStateList(state);
		states.add(list);
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return states.size();
	}

	public RoomStateList getItem(int arg0) {
		return states.get(arg0);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View v = convertView;
		if((v == null) || (v.getTag() == null)) {
			v = mInflater.inflate(R.layout.room_info_weekly_row, null);
			holder = new ViewHolder();
			for(int i=0; i<8; i++) {
				holder.state[i] = (TextView)v.findViewById(roomInfoId[i]);
			}
			v.setTag(holder);
		} else {
			holder = (ViewHolder)v.getTag();
		}
		holder.resultList = getItem(position);
		for(int i=0; i<8; i++) {
			holder.state[i].setText(holder.resultList.state[i]);
		}
		v.setTag(holder);
		return v;
	}
	
	public class ViewHolder {
		RoomStateList resultList;
		TextView[] state = new TextView[8];
	}
}