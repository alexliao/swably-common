package goofy2.swably;

import goofy2.swably.R;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStripEx;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class PagerTabAdapter extends FragmentPagerAdapter
        implements ViewPager.OnPageChangeListener {
    private final Context mContext;
    private final ViewPager mViewPager;
    private PagerTabStripEx mStrip = null;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    static final class TabInfo {
        private final String tag;
        private final String label;
        private final Class<?> clss;
        private final Bundle args;
//        Fragment fragment = null;

        TabInfo(String _tag, String _label, Class<?> _class, Bundle _args) {
            tag = _tag;
            label = _label;
            clss = _class;
            args = _args;
        }
    }

    public PagerTabAdapter(FragmentActivity activity, ViewPager pager) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
//        mViewPager.setPageMargin(8);
        View v = mViewPager.getChildAt(0);
        if(v != null && v instanceof PagerTabStripEx){
        	mStrip = (PagerTabStripEx) v;
        	mStrip.setTabIndicatorColorResource(R.color.tab);
        	mStrip.setDrawFullUnderline(true);
        	mStrip.setNonPrimaryAlpha(0.8f);
        }
    }

    public void addTab(String tag,  String label, Class<?> clss, Bundle args) {
    	label = label.toUpperCase();
        TabInfo info = new TabInfo(tag, label, clss, args);
        mTabs.add(info);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
//        if(info.fragment == null) info.fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
//        return info.fragment;
    	return Fragment.instantiate(mContext, info.clss.getName(), info.args);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        TabInfo info = mTabs.get(position);
        return info.label;
    }
    
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
    
    // return -1 if name not exists
    public int getIndexByTag(String tag){
    	int ret = -1;
    	for(int i=0; i<mTabs.size(); i++){
    		if(mTabs.get(i).tag.equalsIgnoreCase(tag)){
    			ret = i;
    			break;
    		}
    	}
    	return ret;
    }
}
