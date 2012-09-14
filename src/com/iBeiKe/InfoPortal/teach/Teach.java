package com.iBeiKe.InfoPortal.teach;

import java.util.HashMap;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.teach.Teach.TabManager.TabInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;


public class Teach extends Activity {
    TabHost mTabHost;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teach);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        LayoutInflater inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tab1View = inflater.inflate(R.layout.teach_tab, null);
        TextView tab1TextView = (TextView) tab1View.findViewById(R.id.teach_tab_label);
        tab1TextView.setText(R.string.teach_tab1);
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(tab1View).setContent(R.id.teach_tab1));
        View tab2View = inflater.inflate(R.layout.teach_tab, null);
        TextView tab2TextView = (TextView)tab2View.findViewById(R.id.teach_tab_label);
        tab2TextView.setText(R.string.teach_tab2);
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(tab2View).setContent(R.id.teach_tab2));
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			
			public void onTabChanged(String tabId) {
	            TabInfo newTab = mTabs.get(tabId);
	            if (mLastTab != newTab) {
	                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
	                if (mLastTab != null) {
	                    if (mLastTab.fragment != null) {
	                        ft.detach(mLastTab.fragment);
	                    }
	                }
	                if (newTab != null) {
	                    if (newTab.fragment == null) {
	                        newTab.fragment = Fragment.instantiate(mActivity,
	                                newTab.clss.getName(), newTab.args);
	                        ft.add(mContainerId, newTab.fragment, newTab.tag);
	                    } else {
	                        ft.attach(newTab.fragment);
	                    }
	                }

	                mLastTab = newTab;
	                ft.commit();
	                mActivity.getSupportFragmentManager().executePendingTransactions();
	            }
			}
		});

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }

	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    public static class TabManager implements TabHost.OnTabChangeListener {
        private final FragmentActivity mActivity;
        private final TabHost mTabHost;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            mTabHost = tabHost;
            mContainerId = containerId;
            mTabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(tag, info);
            mTabHost.addTab(tabSpec);
        }

        public void onTabChanged(String tabId) {
            TabInfo newTab = mTabs.get(tabId);
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }

                mLastTab = newTab;
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
            }
        }
    }

}
