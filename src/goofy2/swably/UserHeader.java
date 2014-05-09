package goofy2.swably;

import java.io.File;

import goofy2.swably.R;
import goofy2.utils.AsyncImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class UserHeader {
	protected CloudActivity mActivity;
	protected JSONObject mUser = null;
	//public View viewHeader;
	//public TextView txtTitle;

	public UserHeader(CloudActivity activity) {
		mActivity = activity;
	}

	public void setUserFromIntent(){
        Intent i = mActivity.getIntent();
        String strUser = i.getStringExtra(Const.KEY_USER);
        if(strUser != null){
	        try {
				mUser = new JSONObject(strUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}
	
	public void setUserFromBundle(Bundle bundle){
        String strUser = bundle.getString(Const.KEY_USER);
        if(strUser != null){
	        try {
				mUser = new JSONObject(strUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}

	public String setUserFromCache(String userId){
		String cacheId = UserProfile.cacheId(userId);
		String str = mActivity.loadCache(cacheId);
		if(str != null){
			try {
				setUser(new JSONObject(str));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return cacheId;
	}

	//	public void setView(){
//        viewHeader = mActivity.findViewById(R.id.viewHeader);
//		txtTitle = (TextView) mActivity.findViewById(R.id.txtTitle);
//	}
	
//	public void bindView(){
//		bindView(null);
//	}
////
//	public void bindView(String pageTitle){
//		bindUserHeader();
////		TextView txtTitle = (TextView) mActivity.findViewById(R.id.txtTitle);
////    	if(txtTitle != null && pageTitle != null) txtTitle.setText(pageTitle);
//	}
	
	public void setUser(JSONObject value){
		mUser = value;
	}

	public JSONObject getUser(){
		return mUser;
	}
	
	public String getUserId(){
		return getUser().optString("id");
	}
	
	public boolean isMe(){
		JSONObject currentUser = Utils.getCurrentUser(mActivity);
		return currentUser != null && currentUser.optInt("id") == getUser().optInt("id");
	}

	public boolean isFollowed(){
		return getUser().optBoolean("is_followed", false);
	}

	
	public void bindUserHeader(View container){
		bindUserHeader(container, true);
	}
	
	public void bindUserHeader(View container, boolean bindTitle){
		TextView tv;
		if(bindTitle){
			tv = (TextView)container.findViewById(R.id.txtTitle);
			if(tv != null){ 
				tv.setText(getUser().optString("name"));
				tv.setTypeface(mActivity.mHeaderFont);
			}
		}

		tv = (TextView)container.findViewById(R.id.txtUsername);
		if(tv != null){ 
			tv.setText(getUser().optString("name"));
			tv.setTypeface(mActivity.mLightFont);
		}

		Bitmap bm = null;
		String url = null;
		if(!getUser().isNull("avatar_mask")){
			String mask = getUser().optString("avatar_mask", "");
			url = mask.replace("[size]", "bi");
//			bm = Utils.getImageFromFile(mActivity, url); 
		}
//		if(bm == null)  Utils.asyncLoadImage(mActivity, 0, url, null);
//		ImageView iv = (ImageView)mActivity.findViewById(R.id.icon);
//		if(iv != null) new AsyncImageLoader(mActivity, iv, 0, null).loadUrl(url);

		ImageView iv = (ImageView)container.findViewById(R.id.avatar);
		if(iv != null){
			new AsyncImageLoader(mActivity, iv, 1).loadUrl(url);
			Utils.setTouchAnim(mActivity, iv);
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String userSnsProfile = getUser().optString("user_url");
					if(Utils.isEmpty(userSnsProfile)) return;
					mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userSnsProfile)));
				}
			});
		}


		tv = (TextView)container.findViewById(R.id.txtReviewsCount);
		if(tv != null){ 
			tv.setText(String.format(mActivity.getString(R.string.reviews_count), getUser().optInt("reviews_count")));
			tv.setTypeface(mActivity.mLightFont);
		}
		

		if(!getUser().isNull("banner")){
			ImageView imgBanner = (ImageView)container.findViewById(R.id.imgBanner);
			if(imgBanner != null) new AsyncImageLoader(mActivity, imgBanner, 1).loadUrl(getUser().optString("banner", ""));
		}
		
		//		tv = (TextView)container.findViewById(R.id.txtBio);
//		if(tv != null){
//			tv.setText(getUser().optString("bio"));
//	    	if(tv.getText().equals("null")) tv.setText("");
//			tv.setTypeface(mActivity.mNormalFont);
////	    	if(tv.getText().equals("")) tv.setVisibility(View.GONE);
//	    	final String signup_sns = getUser().optString("signup_sns", null);
//	    	if(!Utils.isEmpty(signup_sns)){
//		    	int iconId = (Integer)Utils.getSnsResource(signup_sns, "icon");
////		    	Drawable d = mActivity.getResources().getDrawable(iconId);
////		    	d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
////		    	tv.setCompoundDrawables(null, null, d, null);
//				iv = (ImageView)container.findViewById(R.id.iconSns);
//				iv.setImageResource(iconId);
//				iv.setOnClickListener(new View.OnClickListener(){
//					@Override
//					public void onClick(View v) {
//						String url = String.format((String) Utils.getSnsResource(signup_sns, "url"), getUser().optString("sns_user_id"));
//						Uri uri = Uri.parse(url);
//						Intent i = new Intent(Intent.ACTION_VIEW, uri);
////		    			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		 		       	mActivity.startActivity(i);
//					}
//					
//				});
//	    	}
//		}
	

	}


//	protected void bindUserHeader(){
//		TextView tv;
//		tv = (TextView)mActivity.findViewById(R.id.txtTitle);
//		if(tv != null){ 
//			tv.setText(getUser().optString("name"));
//			tv.setTypeface(mActivity.mHeaderFont);
//		}
//
//		Bitmap bm = null;
//		String url = null;
//		if(!getUser().isNull("avatar_mask")){
//			String mask = getUser().optString("avatar_mask", "");
//			url = mask.replace("[size]", "bi");
//			bm = Utils.getImageFromFile(mActivity, url); 
//		}
//		if(bm == null)  Utils.asyncLoadImage(mActivity, 0, url, null);
//		ImageView iv = (ImageView)mActivity.findViewById(R.id.icon);
//		if(iv != null)	iv.setImageBitmap(bm);
//
//		iv = (ImageView)mActivity.findViewById(R.id.avatar);
//		if(iv != null)	iv.setImageBitmap(bm);
//
//		tv = (TextView)mActivity.findViewById(R.id.txtBio);
//		if(tv != null){
//			tv.setText(getUser().optString("bio"));
//	    	if(tv.getText().equals("null")) tv.setText("");
////	    	if(tv.getText().equals("")) tv.setVisibility(View.GONE);
//	    	String signup_sns = getUser().optString("signup_sns", null);
//	    	if(signup_sns != null){
//		    	int iconId = (Integer)Utils.getSnsResource(signup_sns, "icon");
//		    	Drawable d = mActivity.getResources().getDrawable(iconId);
//		    	d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
//		    	tv.setCompoundDrawables(null, null, d, null);
//	    	}
//		}
//	
////		tv = (TextView)mActivity.findViewById(R.id.btnReviews);
////		if(tv != null){
////			tv.setText(String.format(mActivity.getString(R.string.reviews_count), getUser().optInt("reviews_count",0)));
//////			tv.setOnClickListener(new OnClickListener(){
//////				@Override
//////				public void onClick(View arg0) {
//////					mActivity.openUserReviews(getUser());
//////				}
//////	        });
////		}
//
////		tv = (TextView)mActivity.findViewById(R.id.btnLikes);
////		if(tv != null){
////			tv.setText(String.format(mActivity.getString(R.string.likes_count), getUser().optInt("likes_count",0)));
//////			tv.setOnClickListener(new OnClickListener(){
//////				@Override
//////				public void onClick(View arg0) {
////////					mActivity.openUserReviews(getUser());
//////					Utils.showToast(mActivity, "Unimplemented");
//////				}
//////	        });
////		}
//
////		tv = (TextView)mActivity.findViewById(R.id.btnFollowing);
////		if(tv != null){
////			tv.setText(String.format(mActivity.getString(R.string.following_count), getUser().optInt("friends_count",0)));
//////			tv.setOnClickListener(new OnClickListener(){
//////				@Override
//////				public void onClick(View arg0) {
//////					mActivity.startActivity(new Intent(mActivity, UserFollowing.class).putExtra(Const.KEY_USER, getUser().toString()));
//////				}
//////	        });
////		}
//	
////		tv = (TextView)mActivity.findViewById(R.id.btnFollowers);
////		if(tv != null){
////			tv.setText(String.format(mActivity.getString(R.string.followers_count), getUser().optInt("followers_count",0)));
//////			tv.setOnClickListener(new OnClickListener(){
//////				@Override
//////				public void onClick(View arg0) {
//////					mActivity.startActivity(new Intent(mActivity, UserFollowers.class).putExtra(Const.KEY_USER, getUser().toString()));
//////				}
//////	        });
////		}
//	}
}
