package com.iBeiKe.InfoPortal.campus;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iBeiKe.InfoPortal.R;

/**
 * 校园卡消费记录列表显示控制。
 * 继承自BaseAdapter，提供对校园卡消费记录的更新显示，
 * 获取已下载数据数量，获取元素，获取元素ID以及对列表内元素以及动作进行维护支持。
 *
 */
public class CampusListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private CampusHelper helper;
	
	public CampusListAdapter(Context context) {
		helper = new CampusHelper(context);
		mInflater = LayoutInflater.from(context);
		helper.updateCampDetailCursor();
	}
	
	@Override
	public void notifyDataSetChanged() {
		helper.updateCampDetailCursor();
		super.notifyDataSetChanged();
	}
	
	public int getCount() {
		return helper.getCampDetailCount();
	}

	public Map<String,String> getItem(int arg0) {
		return helper.getCampDetailData(arg0);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View v = convertView;
		if((v == null) || (v.getTag() == null)) {
			v = mInflater.inflate(R.layout.campus_row, null);
			holder = new ViewHolder();
			holder.time = (TextView)v.findViewById(R.id.camp_time);
			holder.place = (TextView)v.findViewById(R.id.camp_place);
			holder.cost = (TextView)v.findViewById(R.id.camp_cost);
			holder.left = (TextView)v.findViewById(R.id.camp_left);
			v.setTag(holder);
		} else {
			holder = (ViewHolder)v.getTag();
		}
		holder.detailList = getItem(position);
		holder.time.setText(holder.detailList.get(Campus.campDetailColumns[0]));
		holder.place.setText(holder.detailList.get(Campus.campDetailColumns[1]));
		holder.cost.setText(holder.detailList.get(Campus.campDetailColumns[2]));
		holder.left.setText(holder.detailList.get(Campus.campDetailColumns[3]));
		v.setTag(holder);
		return v;
	}
	
	public class ViewHolder {
		Map<String,String> detailList;
		TextView time;
		TextView place;
		TextView cost;
		TextView left;
	}
}