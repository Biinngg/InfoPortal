package com.iBeiKe.InfoPortal.library;

import java.util.ArrayList;
import java.util.Map;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iBeiKe.InfoPortal.R;

public class BooksListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<BookList> books;
    private class BookList{
    	public String title;
    	public String num;
    	public String author;
    	public String publisher;
    	public String link;
    	public BookList(String title, String[] descripItem, String link) {
    		this.title = title;
    		this.num = descripItem[0];
    		this.author = descripItem[1];
    		this.publisher = descripItem[2];
    		this.link = link;
    	}
    }
	
	public BooksListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		books = new ArrayList<BookList>();
	}
	
	public BookList resultFormater(Map<String,String> item) {
		String title = null, link = null, description = null;
		String[] descripItem = new String[3];
		int[] n = new int[]{0,0,0};
		String[] descripName = new String[]{"责任者:","索书号:","出版信息:"};
		final int WIDTH = 30;
		for(String key : item.keySet()) {
			if(key.equals("title")) {
				title = item.get(key);
			} else if(key.equals("link")) {
				link = item.get(key);
			} else if(key.equals("description")) {
				description = item.get(key);
			} else {
				Log.d("Unknow Tag", "key: " + key
						+ " content: " + item.get(key));
			}
		}
		for(int i=0;i<3;i++) {
			n[i] = description.indexOf(descripName[i],n[(i-1+3)%3]);
			if(n[i] == -1) {
				Log.e("Error Desctiption", "n["+i+"]=" + n[i] + " " + description);
			}
		}
		descripItem[0] = description.substring(n[1]+descripName[1].length(), n[2]-1);
		descripItem[1] = description.substring(n[0]+descripName[0].length(), n[1]-2);
		descripItem[2] = description.substring(n[2]+descripName[2].length());
		int totalLength = descripItem[1].length() + descripItem[2].length();
		for(int i=1;i<3;i++) {
			if(totalLength >= WIDTH && descripItem[i].length()>=WIDTH/2) {
				descripItem[i] = descripItem[i].substring(0, WIDTH/2-3);
				descripItem[i] += "..";
			}
		}
		descripItem[1] += "著";
		BookList result = new BookList(title,descripItem,link);
		return result;
	}
	
	public void getdata(Map<String,String> item) {
		BookList result;
		result = resultFormater(item);
		books.add(result);
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return books.size();
	}

	public BookList getItem(int arg0) {
		return books.get(arg0);
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
			holder.title = (TextView)v.findViewById(R.id.book_title);
			holder.num = (TextView)v.findViewById(R.id.book_num);
			holder.author = (TextView)v.findViewById(R.id.book_author);
			holder.publisher = (TextView)v.findViewById(R.id.book_publisher);
			v.setTag(holder);
		} else {
			holder = (ViewHolder)v.getTag();
		}
		holder.bookList = getItem(position);
		holder.title.setText(holder.bookList.title);
		holder.num.setText(holder.bookList.num);
		holder.author.setText(holder.bookList.author);
		holder.publisher.setText(holder.bookList.publisher);
		v.setTag(holder);
		return v;
	}
	
	public class ViewHolder {
		BookList bookList;
		TextView title;
		TextView num;
		TextView author;
		TextView publisher;
	}
}