package com.iBeiKe.InfoPortal.library;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListViewActivity extends ListActivity implements OnScrollListener {
    private LinearLayout mLoadLayout;
    private LinearLayout mProgressLoadLayout;
    private ListView mListView;
    private ListViewAdapter mListViewAdapter = new ListViewAdapter();
    private int mLastItem = 0;
    private int mCount = 41;
    private final Handler mHandler = new Handler();// 在Handler中加载数据
    private final LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    int scrollState;// 全局变量，用来记录ScrollView的滚动状态，1表示开始滚动，2表示正在滚动，0表示滚动停止
    int visibleItemCount;// 当前可见页面中的Item总数

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * "加载项"布局，此布局被添加到ListView的Footer中。
         */
        mLoadLayout = new LinearLayout(this);
        mLoadLayout.setMinimumHeight(30);
        mLoadLayout.setGravity(Gravity.CENTER);
        mLoadLayout.setOrientation(LinearLayout.VERTICAL);

        /*
         * 当点击按钮的时候显示这个View，此View使用水平方式布局，左边是一个进度条，右边是文本，默认设为不可见
         */
        mProgressLoadLayout = new LinearLayout(this);
        mProgressLoadLayout.setMinimumHeight(30);
        mProgressLoadLayout.setGravity(Gravity.CENTER);
        mProgressLoadLayout.setOrientation(LinearLayout.HORIZONTAL);

        ProgressBar mProgressBar = new ProgressBar(this);
        mProgressBar.setPadding(0, 0, 15, 0);
        mProgressLoadLayout.addView(mProgressBar, mLayoutParams);// 为布局添加进度条

        TextView mTipContent = new TextView(this);
        mTipContent.setText("加载中...");
        mProgressLoadLayout.addView(mTipContent, mLayoutParams);// 为布局添加文本
        mProgressLoadLayout.setVisibility(View.GONE);// 默认设为不可见，注意View.GONE和View.INVISIBLE的区别

        mLoadLayout.addView(mProgressLoadLayout);// 把之前的布局以View对象添加进来
        final Button button = new Button(this);
        button.setText("加载更多");
        // 添加按钮
        mLoadLayout.addView(button, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (mLastItem == mListViewAdapter.count
                        && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    // 当点击时把带进度条的Layout设为可见，把Button设为不可见
                    mProgressLoadLayout.setVisibility(View.VISIBLE);
                    button.setVisibility(View.GONE);
                    if (mListViewAdapter.count <= mCount) {
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                mListViewAdapter.count += 10;
                                mListViewAdapter.notifyDataSetChanged();
                                mListView.setSelection(mLastItem
                                        - visibleItemCount + 1);
                                // 获取数据成功时把Layout设为不可见，把Button设为可见
                                mProgressLoadLayout.setVisibility(View.GONE);
                                button.setVisibility(View.VISIBLE);
                            }
                        }, 2000);
                    }
                }
            }
        });

        mListView = getListView();
        mListView.addFooterView(mLoadLayout);
        setListAdapter(mListViewAdapter);
        mListView.setOnScrollListener(this);
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        mLastItem = firstVisibleItem + visibleItemCount - 1;
        if (mListViewAdapter.count > mCount) {
            mListView.removeFooterView(mLoadLayout);
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;

    }

    class ListViewAdapter extends BaseAdapter {
        int count = 10;

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View view, ViewGroup parent) {
            TextView mTextView;
            if (view == null) {
                mTextView = new TextView(ListViewActivity.this);
            } else {
                mTextView = (TextView) view;
            }
            mTextView.setText("Item " + position);
            mTextView.setTextSize(20f);
            mTextView.setGravity(Gravity.CENTER);
            mTextView.setHeight(60);
            return mTextView;
        }
    }
}