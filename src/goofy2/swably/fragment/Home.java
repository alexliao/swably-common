package goofy2.swably.fragment;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;

import goofy2.swably.About;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.SelectLocalAppToReview;
import goofy2.swably.TabStripActivity;
import goofy2.swably.Utils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class Home extends TabStripActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.home_with_tab);
		this.showBehind();

		Timer timer = new Timer();
    	timer.schedule(new TimerTask(){
    		@Override
    		public void run(){
    	    	long lastCheckTime = Long.parseLong(Utils.getPrefString(Home.this, "check_version_time", "0"));
    	    	if(System.currentTimeMillis() - lastCheckTime > 3600*8*1000){
    	    		if(Utils.checkVersion(Home.this))	notifyNewVersion();	
    	    	}
    		}
    	}, 10*1000); // delay execution
		
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
		final View btnAdd = findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Home.this, SelectLocalAppToReview.class));
//    				startActivity(new Intent(PublicReviews.this, PostReview.class));
			}
		});

		if(Utils.getCurrentUser(this) != null){
			mPagerAdapter.addTab("following", getString(R.string.following_reviews), MyFollowingReviewsFragment.class, null);
			mPagerAdapter.addTab("everyone", getString(R.string.public_reviews), PublicReviewsFragment.class, null);
			mPagerAdapter.addTab("mine", getString(R.string.my_reviews), MyReviewsFragment.class, null);
			mViewPager.setCurrentItem(mPagerAdapter.getIndexByTag(getInitialTab(getIntent(), "everyone")));
		}else{
			mPagerAdapter.addTab("everyone", getString(R.string.public_reviews), PublicReviewsFragment.class, null);
			mViewPager.setCurrentItem(mPagerAdapter.getIndexByTag("everyone"));
		}
    }

    @Override
    public void onResume(){
    	super.onResume();
//    	postShowAbove();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////    	LocalAppsFragment frag = (LocalAppsFragment) getSupportFragmentManager().findFragmentByTag("apps");
//    	LocalAppsFragment frag = (LocalAppsFragment) mPagerAdapter.getItem(0);
//    	if(frag != null) frag.onActivityResult(this, requestCode, resultCode, data);
//    }
    
	protected void notifyNewVersion(){
    	try{
			String s = Utils.getPrefString(Home.this, "version_changes", null);
			JSONArray changes = new JSONArray(s);
			Log.d(Const.APP_NAME, Const.APP_NAME + " CloudActitivy get " + changes.length() + " new version");
			if(changes.length() > 0){
				int newVersion = changes.getJSONObject(0).getInt("code");
				String versionName = ""+(newVersion/1000.0);
				String text = String.format(getString(R.string.not_up2date), versionName);
				NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = Utils.getDefaultNotification(text);
				Intent i = new Intent(this, About.class);
				PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, 0);
				notification.setLatestEventInfo(this, getString(R.string.cloud_update), text, launchIntent);
				
				nm.notify(100, notification);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
