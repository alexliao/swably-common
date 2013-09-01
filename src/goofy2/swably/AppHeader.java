package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.data.App;
import goofy2.utils.AsyncImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AppHeader {
	protected CloudActivity mActivity;
	protected App mApp = null;

	public AppHeader(CloudActivity activity) {
		mActivity = activity;
	}

	public void setAppFromIntent(){
        Intent i = mActivity.getIntent();
        String str = i.getStringExtra(Const.KEY_APP);
        if(str != null){
	        try {
				mApp = new App(new JSONObject(str));
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}
	
	public void setAppFromBundle(Bundle bundle){
        String strApp = bundle.getString(Const.KEY_APP);
        if(strApp != null){
	        try {
				mApp = new App(new JSONObject(strApp));
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}

	public String setAppFromCache(String appId){
		String cacheId = AppProfile.cacheId(appId);
		String str = mActivity.loadCache(cacheId);
		if(str != null){
			try {
				setApp(new goofy2.swably.data.App(new JSONObject(str)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return cacheId;
	}

	public void setAppFromDb(App cloudApp){
		AppHelper helper = new AppHelper(mActivity);
		App localApp = helper.getApp(cloudApp.getPackage());
		cloudApp.mergeLocalApp(localApp);
		setApp(cloudApp);
	}

//	public void bindView(){
//		bindView(null);
//	}
//
//	public void bindView(String pageTitle){
//		bindHeader();
////		TextView txtTitle = (TextView) mActivity.findViewById(R.id.txtTitle);
////    	if(txtTitle != null && pageTitle != null) txtTitle.setText(pageTitle);
//	}
	
	public void setApp(App value){
		mApp = value;
	}

	public App getApp(){
		return mApp;
	}
	
	public String getAppId(){
		return mApp == null ? null : mApp.getCloudId();
	}
	
//	public void bindHeader(){
//		if(mApp == null || mApp.getJSON() == null) return;
//		TextView tv;
//		tv = (TextView)mActivity.findViewById(R.id.txtTitle);
//		if(tv != null){
//			tv.setText(mApp.getName());
//			tv.setTypeface(mActivity.mHeaderFont);
//		}
//		
//		tv = (TextView)mActivity.findViewById(R.id.txtVersion);
//		if(tv != null && mApp.getVersionName() != ""){
//			tv.setText(String.format(mActivity.getString(R.string.app_version), mApp.getVersionName()));
//		}
//
//		tv = (TextView)mActivity.findViewById(R.id.txtSize);
//		if(tv != null){
//			long size = mApp.getCloudSize();
//			if(size == 0) size = mApp.getSize();
//			tv.setText(String.format(mActivity.getString(R.string.app_size), size/1048576.0));
//		}
//
////		tv = (TextView)mActivity.findViewById(R.id.txtReviews);
////		if(tv != null){
////			int count = mApp.getJSON().optInt("reviews_count",0);
////			if(count == 0) tv.setVisibility(View.INVISIBLE);
////			else tv.setVisibility(View.VISIBLE);
////			tv.setText(String.format(mActivity.getString(R.string.reviews_count), count));
////		}
////
////		tv = (TextView)mActivity.findViewById(R.id.txtLikes);
////		if(tv != null){
////			int count = mApp.getJSON().optInt("likes_count",0);
////			if(count == 0) tv.setVisibility(View.INVISIBLE);
////			else tv.setVisibility(View.VISIBLE);
////			tv.setText(String.format(mActivity.getString(R.string.likes_count), count));
////		}
//		
//		Bitmap bm = null;
//		String url = mApp.getIcon();
//		bm = Utils.getImageFromFile(mActivity, url); // file store 
//		ImageView iv = (ImageView)mActivity.findViewById(R.id.icon);
//		if(bm == null)  Utils.asyncLoadImage(mActivity, 0, url, null);
//		if(iv != null)	iv.setImageBitmap(bm);
//
//		iv = (ImageView)mActivity.findViewById(R.id.avatar);
//		if(iv != null){	
//			iv.setImageBitmap(bm);
////			iv.setOnClickListener(new View.OnClickListener(){
////				@Override
////				public void onClick(View v) {
////					mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id="+mApp.getPackage())));
////				}
////			});
//		}
//	}

	public void bindAppHeader(View container){
		if(mApp == null || mApp.getJSON() == null) return;
		TextView tv;
		
//		tv = (TextView)mActivity.findViewById(R.id.txtTitle);
//		if(tv != null){
//			tv.setText(mApp.getName());
//			tv.setTypeface(mActivity.mHeaderFont);
//		}

		tv = (TextView)container.findViewById(R.id.txtAppName);
		if(tv != null){
			tv.setText(mApp.getName());
			tv.setTypeface(mActivity.mLightFont);
		}

		tv = (TextView)container.findViewById(R.id.txtReviewsCount);
		if(tv != null){
			int count = mApp.getReviewsCount();
			tv.setText(String.format(mActivity.getString(R.string.reviews_count), count));
			tv.setTypeface(mActivity.mLightFont);
		}

		tv = (TextView)container.findViewById(R.id.txtVersion);
		if(tv != null && mApp.getVersionName() != ""){
//			tv.setText(String.format(mActivity.getString(R.string.app_version), mApp.getVersionName()));
//			tv.setText(mApp.getVersionName());
			tv.setText(String.format(mActivity.getString(R.string.app_version_short), mApp.getVersionName()));
			tv.setTypeface(mActivity.mLightFont);
		}

		tv = (TextView)container.findViewById(R.id.txtSize);
		if(tv != null){
			long size = mApp.getCloudSize();
			if(size == 0) size = mApp.getSize();
			tv.setText(String.format(mActivity.getString(R.string.app_size), size/1048576.0));
//			tv.setText(String.format(mActivity.getString(R.string.app_size_short), size/1048576.0));
			tv.setTypeface(mActivity.mLightFont);
		}

		Bitmap bm = null;
		String url = mApp.getIcon();
		ImageView iv = (ImageView)container.findViewById(R.id.icon);
//		bm = Utils.getImageFromFile(mActivity, url); // file store 
//		if(bm == null)  Utils.asyncLoadImage(mActivity, 0, url, null);
//		if(iv != null && bm != null) iv.setImageBitmap(bm);
		if(iv != null){
			iv.setImageResource(R.drawable.noimage);
			new AsyncImageLoader(mActivity, iv, 0).loadUrl(url);
		}

	}
}
