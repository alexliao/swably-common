package goofy2.swably;

import com.slidingmenu.lib.SlidingMenu;

import goofy2.swably.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

public class TabStripActivity extends WithHeaderActivity {
    protected ViewPager  mViewPager;
    protected PagerTabAdapter mPagerAdapter;

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
	      mViewPager = (ViewPager)findViewById(R.id.pager);
	      mPagerAdapter = getAdapter(this, mViewPager);
//        if (savedInstanceState != null) {
//            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
//        }
	      getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
    }

	protected PagerTabAdapter getAdapter(FragmentActivity activity, ViewPager pager){
		return new PagerTabAdapter(activity, pager);
	}
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    protected String getInitialTab(Intent intent, String defaultTab){
    	String ret = defaultTab;
    	String uri = intent.getDataString();
    	if(uri != null)
    		ret = uri;
    	return ret;
    }
    
    
}
