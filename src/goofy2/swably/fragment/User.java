package goofy2.swably.fragment;

import goofy2.swably.Const;
import goofy2.swably.FollowBtn;
import goofy2.swably.R;
import goofy2.swably.TabStripActivity;
import goofy2.swably.UserHeader;
import goofy2.swably.UserProfile;
import goofy2.swably.R.id;
import goofy2.swably.R.layout;
import goofy2.swably.R.string;
import goofy2.swably.fragment.App.RefreshAppBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

public class User extends TabStripActivity
{
	protected UserHeader header = new UserHeader(this);
//	protected FollowBtn followBtn = new FollowBtn(this, header);
	protected FollowBtn followBtn = new FollowBtn(this, header, null); // not work, just for compilation
//	protected String mUserCacheId;
	protected RefreshUserBroadcastReceiver mRefreshUserReceiver = new RefreshUserBroadcastReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.user);
        header.setUserFromIntent();
//		mUserCacheId = UserProfile.cacheId(header.getUserId());
//		String str = loadCache(mUserCacheId);
//		if(str != null){
//			try {
//				header.setUser(new JSONObject(str));
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
        header.setUserFromCache(header.getUserId());
		registerReceiver(mRefreshUserReceiver, new IntentFilter(Const.BROADCAST_REFRESH_USER));
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        followBtn.init(null);
        bind();
        
		Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_USER, header.getUser().toString());
		String title;
		
		String initialTab = getInitialTab(getIntent(), "reviews");
		
		if(header.getUser().optInt("claims_count") > 0 || initialTab.equalsIgnoreCase("claimed")){
	        title = header.getUser().optInt("claims_count")==0 ? getString(R.string.claimed) : String.format(getString(R.string.claimed_count), header.getUser().optInt("claims_count"));
	        mPagerAdapter.addTab("claimed", title, UserClaimedAppsFragment.class, bundle);
		}

        title = header.getUser().optInt("likes_count")==0 ? getString(R.string.starred) : String.format(getString(R.string.starred_count), header.getUser().optInt("likes_count"));
        mPagerAdapter.addTab("starred", title, UserLikedAppsFragment.class, bundle);
        
		title = header.getUser().optInt("reviews_count")==0 ? getString(R.string.reviews) : String.format(getString(R.string.reviews_count), header.getUser().optInt("reviews_count"));
        mPagerAdapter.addTab("reviews", title, UserReviewsFragment.class, bundle);
        
		title = header.getUser().optInt("friends_count")==0 ? getString(R.string.following) : String.format(getString(R.string.following_count), header.getUser().optInt("friends_count"));
        mPagerAdapter.addTab("following", title, UserFollowingFragment.class, bundle);
        
		title = header.getUser().optInt("followers_count")==0 ? getString(R.string.followers) : String.format(getString(R.string.followers_count), header.getUser().optInt("followers_count"));
        mPagerAdapter.addTab("followers", title, UserFollowersFragment.class, bundle);

		mViewPager.setCurrentItem(mPagerAdapter.getIndexByTag(initialTab));
    }

    @Override
    public void onDestroy(){
		unregisterReceiver(mRefreshUserReceiver);
    	super.onDestroy();
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////    	LocalAppsFragment frag = (LocalAppsFragment) getSupportFragmentManager().findFragmentByTag("apps");
//    	LocalAppsFragment frag = (LocalAppsFragment) mPagerAdapter.getItem(0);
//    	if(frag != null) frag.onActivityResult(this, requestCode, resultCode, data);
//    }
    
    public void bind() {
		TextView tv = (TextView)findViewById(R.id.txtTitle);
		tv.setText(header.getUser().optString("name"));

		followBtn.bind();
    }

    public class RefreshUserBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Const.BROADCAST_REFRESH_USER)){
            	String id = intent.getStringExtra(Const.KEY_ID);
        		if(id != null && id.equals(header.getUserId())){
        			String str = loadCache(UserProfile.cacheId(id));
        			if(str != null){
        				try {
							header.setUser(new JSONObject(str));
							bind();
						} catch (JSONException e) {
							e.printStackTrace();
						}
        			}
        		}
        	}
        }
    }
}
