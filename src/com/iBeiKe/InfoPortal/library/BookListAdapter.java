package com.iBeiKe.InfoPortal.library;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iBeiKe.InfoPortal.R;

public class BookListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private BookHelper helper;
	private String table = Book.tableList;
	private String[] columns = Book.columnsList;
	
	public BookListAdapter(Context context) {
		this.context = context;
		helper = new BookHelper(context);
		mInflater = LayoutInflater.from(context);
		helper.updateNewsCursor(table, columns);
	}
	
	@Override
	public void notifyDataSetChanged() {
		SQLiteCursor cursor = helper.updateNewsCursor(table, columns);
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
			v = mInflater.inflate(R.layout.books_row, null);
			holder = new ViewHolder();
			holder.item = (RelativeLayout)v.findViewById(R.id.book_list_item);
			holder.title = (TextView)v.findViewById(R.id.book_title);
			holder.num = (TextView)v.findViewById(R.id.book_num);
			holder.author = (TextView)v.findViewById(R.id.book_author);
			holder.publisher = (TextView)v.findViewById(R.id.book_publisher);
			holder.total = (TextView)v.findViewById(R.id.book_total);
			holder.left = (TextView)v.findViewById(R.id.book_left);
			v.setTag(holder);
		} else {
			holder = (ViewHolder)v.getTag();
		}
		holder.newsList = getItem(position);
		holder.item.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
			    Uri uri = Uri.parse(Book.baseUrl + holder.newsList.get(columns[1]));
			    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			    context.startActivity(intent);
			}
		});
		holder.title.setText(holder.newsList.get(columns[0]));
		holder.num.setText(holder.newsList.get(columns[2]));
		holder.author.setText(holder.newsList.get(columns[5]));
		holder.publisher.setText(holder.newsList.get(columns[6]));
		holder.total.setText(holder.newsList.get(columns[3]));
		holder.left.setText(holder.newsList.get(columns[4]));
		v.setTag(holder);
		return v;
	}
	
	public class ViewHolder {
		Map<String,String> newsList;
		RelativeLayout item;
		TextView title;
		TextView num;
		TextView author;
		TextView publisher;
		TextView total;
		TextView left;
	}
}