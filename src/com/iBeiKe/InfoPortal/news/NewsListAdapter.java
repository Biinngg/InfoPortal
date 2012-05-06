package com.iBeiKe.InfoPortal.news;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.client.android.LocaleManager;
import com.iBeiKe.InfoPortal.R;

public class NewsListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private NewsHelper helper;
	private String table = News.table;
	private String[] columns = News.columns;
	
	public NewsListAdapter(Context context) {
		this.context = context;
		helper = new NewsHelper(context);
		mInflater = LayoutInflater.from(context);
		helper.updateNewsCursor(table, columns);
	}
	
	public void notifyDataSetChanged() {
		helper.updateNewsCursor(table, columns);
		super.notifyDataSetChanged();
	}
	
	public int getCount() {
		return helper.getNewsCount(table, columns);
	}

	public Map<String,String> getItem(int arg0) {
		return helper.getNewsData(arg0, columns);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View v = convertView;
		if((v == null) || (v.getTag() == null)) {
			v = mInflater.inflate(R.layout.news_row, null);
			holder = new ViewHolder();
			holder.item = (LinearLayout)v.findViewById(R.id.news_list_item);
			holder.title = (TextView)v.findViewById(R.id.news_list_title);
			holder.description = (TextView)v.findViewById(R.id.news_list_content);
			v.setTag(holder);
		} else {
			holder = (ViewHolder)v.getTag();
		}
		holder.newsList = getItem(position);
		holder.item.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
			    Uri uri = Uri.parse(holder.newsList.get(columns[1]));
			    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			    context.startActivity(intent);
			}
		});
		holder.title.setText(holder.newsList.get(columns[0]));
		holder.description.setText(holder.newsList.get(columns[2]));
		v.setTag(holder);
		return v;
	}
	
	public class ViewHolder {
		Map<String,String> newsList;
		LinearLayout item;
		String link;
		TextView title;
		TextView description;
	}
}