package goofy2.swably;

import goofy2.swably.R;
import goofy2.utils.ParamRunnable;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FollowBtn {
	protected CloudActivity mActivity;
	protected UserHeader mHeader;
	protected View mParent;
	private View btnFollow;
	private View btnUnfollow;
	
	
	public FollowBtn(final CloudActivity activity, UserHeader header, View parent){
		mActivity = activity;
		mHeader = header;
		mParent = parent;
	}
	
	public void init(final Runnable callback){
        btnFollow = mParent.findViewById(R.id.btnFollow);
        btnFollow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
		        if(mActivity.redirectAnonymous(false)) return;
		        Utils.showToast(mActivity, mActivity.getString(R.string.follow_prompt));
//		        btnUnfollow.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.frame_btn));
//		        mActivity.transitWidth(btnFollow, btnUnfollow, new Runnable(){
//					@Override
//					public void run() {
//						btnUnfollow.setBackgroundColor(mActivity.getResources().getColor(R.color.none));
//					}
//				});
//				mActivity.flipView(btnFollow, btnUnfollow, new Runnable(){
//					@Override
//					public void run() {
//						bind();
//						if(callback != null) callback.run();
//					}
//				});
				Utils.follow(mActivity, mHeader.getUserId(), mHeader.getUser().optString("name"), true, null, false);
				JSONObject user = mHeader.getUser();
				try {
					user.put("is_followed", true);
					mHeader.setUser(user);
					// cache user for following status
					mActivity.cacheData(user.toString(), UserProfile.cacheId(user.optString("id")));
					bind();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
        });
        btnUnfollow = mParent.findViewById(R.id.btnUnfollow);
        btnUnfollow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
		        if(mActivity.redirectAnonymous(false)) return;
		        Utils.showToast(mActivity, mActivity.getString(R.string.unfollow_prompt));
//		        mActivity.transitWidth(btnUnfollow, btnFollow, null);
//				mActivity.flipView(btnUnfollow, btnFollow, new Runnable(){
//					@Override
//					public void run() {
//						bind();
//						if(callback != null) callback.run();
//					}
//				});
				Utils.follow(mActivity, mHeader.getUserId(), mHeader.getUser().optString("name"), false, null, false);
				JSONObject user = mHeader.getUser();
				try {
					user.put("is_followed", false);
					mHeader.setUser(user);
					// cache user for following status
					mActivity.cacheData(user.toString(), UserProfile.cacheId(user.optString("id")));
					bind();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
        });
	}

	public void bind() {
    	if(mHeader.isMe()){
    		btnFollow.setVisibility(View.GONE);
    		btnUnfollow.setVisibility(View.GONE);
    	}else{
	    	if(mHeader.isFollowed()){
	    		btnFollow.setVisibility(View.GONE);
	    		btnUnfollow.setVisibility(View.VISIBLE);
	    	}else{
	    		btnFollow.setVisibility(View.VISIBLE);
	    		btnUnfollow.setVisibility(View.GONE);
	    	}
    	}
	}
}
