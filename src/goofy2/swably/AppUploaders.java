package goofy2.swably;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import goofy2.swably.fragment.AppAboutFragment;
import goofy2.swably.fragment.AppCommentsFragment;
import goofy2.swably.fragment.AppUploadersFragment;
import goofy2.swably.fragment.UserReviewsFragment;
import goofy2.swably.fragment.Back_App.RefreshAppBroadcastReceiver;
import goofy2.utils.AsyncImageLoader;

public class AppUploaders extends WithHeaderActivity
{
//	String mId = null;
//	int currentMenu;
	protected AppHeader header = new AppHeader(this);
//	protected AppActionHelper actionHelper = new AppActionHelper(this, header);  
//	protected RefreshAppBroadcastReceiver mRefreshAppReceiver = new RefreshAppBroadcastReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Intent i = getIntent();
//        mId = getIdFromUrl(i);
//    	if(mId != null){
//    		JSONObject json = new JSONObject();
//    		try {
//				json.put("id", mId);
//	    		String str = loadCache(AppProfile.cacheId(mId));
//	    		if(str != null){
//	    			i.putExtra(Const.KEY_APP, str);
//	    		}else{
//		    		i.putExtra(Const.KEY_APP, json.toString());
//	    		}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//    	}
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.app_uploaders);
        header.setAppFromIntent();
        header.setAppFromCache(header.getAppId());
//		registerReceiver(mRefreshAppReceiver, new IntentFilter(Const.BROADCAST_REFRESH_APP));
    }

//    private String getIdFromUrl(Intent intent){
//    	String ret = null;
//    	Uri data = intent.getData();
//    	if(data != null){
//	    	List<String> params = data.getPathSegments();
//	    	//String action = params.get(0); // "a"
//	    	if("http".equalsIgnoreCase(data.getScheme())) ret = params.get(1);
//    	}
//    	return ret;
//    }
//
//    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//		actionHelper.init(findViewById(R.id.body));
		bind();
		addUploadersFragment();		
    }

    protected void addUploadersFragment(){
		Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_APP, header.getApp().getJSON().toString());

		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();	
		AppUploadersFragment fragment = new AppUploadersFragment();
		fragment.setArguments(bundle);
		ft.add(R.id.fragment, fragment);
		ft.commit();

//		currentMenu = R.menu.app_reviews;
    }
    

    @Override
    public void onDestroy(){
//		unregisterReceiver(mRefreshAppReceiver);
    	super.onDestroy();
    }

    public void bind() {
//		TextView tv = (TextView)findViewById(R.id.txtTitle);
//		tv.setText(header.getApp().getName());
//    	actionHelper.bind();
    	ImageView iv = (ImageView) findViewById(R.id.btnBack);
    	iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    	
		String url = header.getApp().getIcon();
		new AsyncImageLoader(AppUploaders.this, iv, 0).loadUrl(url);
    	
	}

}
