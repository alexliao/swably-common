package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.fragment.LocalAppsFragment;
import goofy2.swably.fragment.OldLocalAppsFragment;
import goofy2.swably.fragment.ShuffleAppsFragment;
import goofy2.swably.fragment.UserLikedAppsFragment;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;

public class Apps extends TabStripActivity {
	protected View btnSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.apps);
//		this.showBehind();
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSearchRequested();
			}
		});
        
//        mPagerAdapter.addTab("installed", getString(R.string.installed), OldLocalAppsFragment.class, null);
        mPagerAdapter.addTab("installed", getString(R.string.installed), LocalAppsFragment.class, null);
//		mPagerAdapter.addTab("trending", getString(R.string.trending_apps), TrendingAppsFragment.class, null);
		mPagerAdapter.addTab("shuffle", getString(R.string.shuffle), ShuffleAppsFragment.class, null);
		if(Utils.getCurrentUser(this) != null){
			Bundle args = new Bundle();
			args.putString(Const.KEY_USER, Utils.getCurrentUser(this).toString());
	        mPagerAdapter.addTab("starred", getString(R.string.starred), UserLikedAppsFragment.class, args);
		}
		mViewPager.setCurrentItem(1);
    }

    @Override
    public void onResume(){
    	super.onResume();
//    	postShowAbove();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    	LocalAppsFragment frag = (LocalAppsFragment) getSupportFragmentManager().findFragmentByTag("apps");
    	OldLocalAppsFragment frag = (OldLocalAppsFragment) mPagerAdapter.getItem(0);
    	if(frag != null) frag.onActivityResult(this, requestCode, resultCode, data);
    }
}
