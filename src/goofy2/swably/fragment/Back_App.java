package goofy2.swably.fragment;

import goofy2.swably.AppActionHelper;
import goofy2.swably.AppHeader;
import goofy2.swably.AppHelper;
import goofy2.swably.AppProfile;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.TabStripActivity;
import goofy2.swably.R.id;
import goofy2.swably.R.layout;
import goofy2.swably.R.string;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Back_App extends TabStripActivity
//	implements AppCommentsFragment.OnRefreshListener
	implements AppCommentsFragment.OnAboutListener
{
	String mId = null;
	protected AppHeader header = new AppHeader(this);
	protected AppActionHelper actionHelper = new AppActionHelper(this, header);  
//	protected String mAppCacheId;
//	View btnLike;
//	View btnUnlike;
//	View btnShare;
//	View btnReview;
//	View groupLike;
//	protected AppTribtn tribtn = new AppTribtn(); 
	protected RefreshAppBroadcastReceiver mRefreshAppReceiver = new RefreshAppBroadcastReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        mId = getIdFromUrl(i);
    	if(mId != null){
    		JSONObject json = new JSONObject();
    		try {
				json.put("id", mId);
	    		String str = loadCache(AppProfile.cacheId(mId));
	    		if(str != null){
	    			i.putExtra(Const.KEY_APP, str);
	    		}else{
		    		i.putExtra(Const.KEY_APP, json.toString());
	    		}
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.app);
        header.setAppFromIntent();
        header.setAppFromCache(header.getAppId());
//        if(header.getApp().getPackage() != null) header.setAppFromDb(header.getApp());
		registerReceiver(mRefreshAppReceiver, new IntentFilter(Const.BROADCAST_REFRESH_APP));
    }

    private String getIdFromUrl(Intent intent){
    	String ret = null;
    	Uri data = intent.getData();
    	if(data != null){
	    	List<String> params = data.getPathSegments();
	    	//String action = params.get(0); // "a"
	    	if("http".equalsIgnoreCase(data.getScheme())) ret = params.get(1);
    	}
    	return ret;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        
//        btnShare = findViewById(R.id.btnShare);
//        btnShare.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				sendOutApp(header.getApp());
//			}
//        });
//		
//        btnReview = findViewById(R.id.btnReview);
//		btnReview.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(App.this, PostReview.class);
//				i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
//				startActivity(i);
////				mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//			}
//		});
//		btnLike = findViewById(R.id.btnLike);
//		btnLike.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				goofy2.swably.data.App app = header.getApp();
//				try {
//			        if(redirectAnonymous(false)) return;
////					mActivity.flipView(btnLike, btnUnlike, null);
//					Utils.like(App.this, app.getCloudId(), true, null);
//					app.getJSON().put(goofy2.swably.data.App.IS_LIKED, true);
//					header.setApp(app);
//					// cache user for following status
//					cacheData(app.getJSON().toString(), AppProfile.cacheId(app.getCloudId()));
//					bind();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		btnUnlike = findViewById(R.id.btnUnlike);
//		btnUnlike.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				goofy2.swably.data.App app = header.getApp();
//				try {
//			        if(redirectAnonymous(false)) return;
////					mActivity.flipView(btnUnlike, btnLike, null);
//					Utils.like(App.this, app.getCloudId(), false, null);
//					app.getJSON().put(goofy2.swably.data.App.IS_LIKED, false);
//					header.setApp(app);
//					// cache user for following status
//					cacheData(app.getJSON().toString(), AppProfile.cacheId(app.getCloudId()));
//					bind();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		actionHelper.init(viewHeaderBar);
		actionHelper.init(findViewById(R.id.viewFooterBar));
//		groupLike = findViewById(R.id.groupLike);
		
//		tribtn.init(this, header.getApp());
		
		bind();
		
		Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_APP, header.getApp().getJSON().toString());
		String title;

		mPagerAdapter.addTab("about", getString(R.string.about), AppAboutFragment.class, bundle);
		
		title = header.getApp().getJSON().optInt("reviews_count")==0 ? getString(R.string.reviews) : String.format(getString(R.string.reviews_count), header.getApp().getJSON().optInt("reviews_count"));
        mPagerAdapter.addTab("reviews", title, AppCommentsFragment.class, bundle);

        title = header.getApp().getJSON().optInt("likes_count")==0 ? getString(R.string.starred) : String.format(getString(R.string.starred_count), header.getApp().getJSON().optInt("likes_count"));
        mPagerAdapter.addTab("starred", title, AppLikedByUsersFragment.class, bundle);

		mViewPager.setCurrentItem(1);
    }

//	protected PagerTabAdapter getAdapter(FragmentActivity activity, ViewPager pager){
//		return new AppPagerTabAdapter(activity, pager);
//	}

	@Override
    public void onDestroy(){
		unregisterReceiver(mRefreshAppReceiver);
    	super.onDestroy();
    }

    public void bind() {
//    	if(header.getApp().isLiked()){
//    		btnLike.setVisibility(View.GONE);
//    		btnUnlike.setVisibility(View.VISIBLE);
//    	}else{
//    		btnLike.setVisibility(View.VISIBLE);
//    		btnUnlike.setVisibility(View.GONE);
//    	}
//		tribtn.setStatus(header.getApp());
		TextView tv = (TextView)findViewById(R.id.txtTitle);
		tv.setText(header.getApp().getName());
    	actionHelper.bind();
	}

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////    	LocalAppsFragment frag = (LocalAppsFragment) getSupportFragmentManager().findFragmentByTag("apps");
//    	LocalAppsFragment frag = (LocalAppsFragment) mPagerAdapter.getItem(0);
//    	if(frag != null) frag.onActivityResult(this, requestCode, resultCode, data);
//    }
    
    public void onAbout(){
		mViewPager.setCurrentItem(0);
    }
    
    public class RefreshAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_REFRESH_APP)){
            	String pkg = intent.getStringExtra(Const.KEY_PACKAGE);
            	if(pkg != null && pkg.equalsIgnoreCase(header.getApp().getPackage())){
        			AppHelper helper = new AppHelper(Back_App.this);
        			goofy2.swably.data.App app = helper.getApp(pkg);
            		if(app != null) header.setApp(app);
            		bind();
            	}else{
                	String id = intent.getStringExtra(Const.KEY_ID);
            		if(id != null && id.equals(header.getApp().getCloudId())){
            			String str = loadCache(AppProfile.cacheId(id));
            			if(str != null){
            				try {
								header.setApp(new goofy2.swably.data.App(new JSONObject(str)));
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

//    protected class AppPagerTabAdapter extends PagerTabAdapter{
//
//		public AppPagerTabAdapter(FragmentActivity activity, ViewPager pager) {
//			super(activity, pager);
//		}
//    	
//	    @Override
//	    public void onPageSelected(int position) {
////	    	Utils.showToast(App.this, ""+position);
//	    	actionHelper.btnShare.setVisibility(View.GONE);
//	    	actionHelper.btnReview.setVisibility(View.GONE);
//	    	groupLike.setVisibility(View.GONE);
//	    	if(position == 0){
//	    		actionHelper.btnShare.setVisibility(View.VISIBLE);
//	    	}else if(position == 1){
//	    		actionHelper.btnReview.setVisibility(View.VISIBLE);
//	    	}else if(position == 2){
//	    		groupLike.setVisibility(View.VISIBLE);
//	    	}	    		
//	    }
//		
//    }

//    @Override
//    public void showLoading(){
//    	if(mId != null) showDialog(0);
//    	else super.showLoading();
//    }
//    @Override
//    public void hideLoading(){
//    	if(mId != null) removeDialog(0);
//    	else super.hideLoading();
//    }

//	@Override
//	public void onRefresh(goofy2.swably.data.App newApp) {
//		header.setApp(newApp);
//		bind();
//
//// This does not work because getItem() always return new instance.
////		AppAboutFragment frag = (AppAboutFragment) mPagerAdapter.getItem(0);
////		frag.setApp(newApp);
//		
//// also doesn't work
////		// modify arguments for AppAboutFragment tab
////		Bundle bundle = new Bundle();
////		bundle.putString(Const.KEY_APP, header.getApp().getJSON().toString());
////		mPagerAdapter.mTabs.get(0).args = bundle;
////		mPagerAdapter.notifyDataSetChanged();
//	}

}
