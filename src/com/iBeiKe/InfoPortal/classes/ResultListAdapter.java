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
	private int num;
	private final int[] resultId, layoutId, separateId;
	private LayoutInflater mInflater;
	private ArrayList<ResultList> rooms;
    private class ResultList{
    	public int[] id;
    	public String[] room;
    	public ResultList(int[] id, String[] room) {
    		this.id = id;
    		this.room = room;
    	}
    }
	
	public ResultListAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		rooms = new ArrayList<ResultList>();
		resultId = new int[]{R.id.room_result1, R.id.room_result2,
				R.id.room_result3, R.id.room_result4, R.id.room_result5,
				R.id.room_result6, R.id.room_result7, R.id.room_result8};
		layoutId = new int[]{R.id.room_layout1, R.id.room_layout2,
				R.id.room_layout3, R.id.room_layout4, R.id.room_layout5,
				R.id.room_layout6, R.id.room_layout7, R.id.room_layout8};
		separateId = new int[]{R.id.separater1, R.id.separater2,
				R.id.separater3, R.id.separater4, R.id.separater5,
				R.id.separater6, R.id.separater7};
	}
	
	public void setData(int num, int[] id, String[] room) {
		ResultList result = new ResultList(id, room);
		this.num = num;
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
			holder = new ViewHolder(num);
			for(int i=0; i<num; i++) {
				holder.result[i] = (TextView)v.findViewById(resultId[i]);
				holder.layout[i] = (LinearLayout)v.findViewById(layoutId[i]);
				if(i<num-1)
					holder.view[i] = (View)v.findViewById(separateId[i]);
			}
			v.setTag(holder);
		} else {
			holder = (ViewHolder)v.getTag();
		}
		holder.resultList = getItem(position);
		for(int i=0; i<num; i++) {
			holder.result[i].setText(holder.resultList.room[i]);
			holder.layout[i].setVisibility(View.VISIBLE);
			holder.layout[i].setId(holder.resultList.id[i]);
			if(i<num-1)
				holder.view[i].setVisibility(View.VISIBLE);
			holder.layout[i].setOnClickListener(new OnClickListener() {
		        public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(context, About.class);
					Log.d("id", "id=" + v.getId());
					//Bundle bl = new Bundle();
					//bl.putInt("id", holder.resultList.id[i]);
					//intent.putExtras(bl);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					context.startActivity(intent);
				}
			});
		}
		v.setTag(holder);
		return v;
	}
	
	public class ViewHolder {
		ResultList resultList;
		View[] view;
		TextView[] result;
		LinearLayout[] layout;
		public ViewHolder(int num) {
			result = new TextView[num];
			layout = new LinearLayout[num];
			view = new View[num];
		}
	}
}