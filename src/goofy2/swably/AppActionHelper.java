package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.AppTribtn.ViewHolder;
import goofy2.swably.data.App;
import goofy2.utils.ParamRunnable;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AppActionHelper {
	protected CloudActivity mActivity;
	protected AppHeader mHeader;
	public View btnReview;
	public View btnLike;
	public View btnUnlike;
	public View btnShare;
	private View btnDownload; // representation of all tri-buttons
	public AppTribtn tribtn = new AppTribtn(); 
	
	
	public AppActionHelper(final CloudActivity activity, AppHeader header){
		mActivity = activity;
		mHeader = header;
	}
	
	public void init(View container){
		init(container, null);
	}
	
	public void init(View container, final Runnable callback){
		ViewHolder holder = (ViewHolder) container.getTag();
		
		if(holder == null) btnReview = container.findViewById(R.id.btnReview);
		else btnReview = holder.getBtnReview();
		if(btnReview != null) btnReview.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mActivity, PostReview.class);
				i.putExtra(Const.KEY_APP, mHeader.getApp().getJSON().toString());
				mActivity.startActivity(i);
//				mActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				if(callback != null) callback.run();
			}
		});

		if(holder == null) btnLike = container.findViewById(R.id.btnLike);
		else btnLike = holder.getBtnLike();
		if(btnLike != null) {
			((TextView)btnLike).setTypeface(mActivity.mNormalFont);
			btnLike.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					App app = mHeader.getApp();
					try {
				        if(mActivity.redirectAnonymous(false)) return;
	////					mActivity.flipView(btnLike, btnUnlike, null);
	//					mActivity.flipView(btnLike, btnUnlike, new Runnable(){
	//						@Override
	//						public void run() {
	//							if(callback != null) callback.run();
	//						}
	//					});
						mActivity.transitWidth(btnLike, btnUnlike);
						Utils.starApp(mActivity, app.getCloudId(), true, null);
						app.getJSON().put(App.IS_LIKED, true);
						mHeader.setApp(app);
						// cache user for following status
						mActivity.cacheData(app.getJSON().toString(), AppProfile.cacheId(app.getCloudId()));
	//					bind();
					} catch (JSONException e) {
						e.printStackTrace();
					}
	//				if(callback != null) callback.run();
				}
			});
		}
		
		if(holder == null) btnUnlike = container.findViewById(R.id.btnUnlike);
		else btnUnlike = holder.getBtnUnlike();
		if(btnUnlike != null) btnUnlike.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				App app = mHeader.getApp();
				try {
			        if(mActivity.redirectAnonymous(false)) return;
////					mActivity.flipView(btnUnlike, btnLike, null);
//					mActivity.flipView(btnUnlike, btnLike, new Runnable(){
//						@Override
//						public void run() {
//							if(callback != null) callback.run();
//						}
//					});
					mActivity.transitWidth(btnUnlike, btnLike);
					Utils.starApp(mActivity, app.getCloudId(), false, null);
					app.getJSON().put(App.IS_LIKED, false);
					mHeader.setApp(app);
					// cache user for following status
					mActivity.cacheData(app.getJSON().toString(), AppProfile.cacheId(app.getCloudId()));
//					bind();
				} catch (JSONException e) {
					e.printStackTrace();
				}
//				if(callback != null) callback.run();
			}
		});

		if(holder == null) btnShare = container.findViewById(R.id.btnShare);
		else btnShare = holder.getBtnShare();
        if(btnShare != null) btnShare.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				mActivity.sendOutApp(mHeader.getApp());
				if(callback != null) callback.run();
			}
        });
//		txtFollowing = (TextView) mActivity.findViewById(R.id.txtFollowing);
//		txtUnfollowed = (TextView) mActivity.findViewById(R.id.txtUnfollowed);
//		txtMe = (TextView) mActivity.findViewById(R.id.txtMe);
//
//        btnFollow = mActivity.findViewById(R.id.btnFollow);
//        btnFollow.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//		        if(mActivity.redirectAnonymous(false)) return;
//				Utils.follow(mActivity, mHeader.getUserId(), mHeader.getUser().optString("name"), true, null, false);
//				JSONObject user = mHeader.getUser();
//				try {
//					user.put("is_followed", true);
//					mHeader.setUser(user);
//					bind();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//        });
//        btnUnfollow = mActivity.findViewById(R.id.btnUnfollow);
//        btnUnfollow.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//		        if(mActivity.redirectAnonymous(false)) return;
//				Utils.follow(mActivity, mHeader.getUserId(), mHeader.getUser().optString("name"), false, null, false);
//				JSONObject user = mHeader.getUser();
//				try {
//					user.put("is_followed", false);
//					mHeader.setUser(user);
//					// cache user for following status
//					mActivity.cacheData(user.toString(), UserProfile.cacheId(user.optString("id")));
//					bind();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//        });
		if(holder == null) btnDownload = container.findViewById(R.id.btnDownload);
		else btnDownload = holder.getBtnDownload();
		if(btnDownload != null) tribtn.init(mActivity, container, mHeader.getApp(), callback);
	}

	public void bind() {
		if(btnLike != null && btnUnlike != null){
	    	if(mHeader.getApp().isLiked()){
	    		btnLike.setVisibility(View.GONE);
	    		btnUnlike.setVisibility(View.VISIBLE);
	    	}else{
	    		btnLike.setVisibility(View.VISIBLE);
	    		btnUnlike.setVisibility(View.GONE);
	    	}
		}
		if(btnDownload != null) tribtn.setStatus(mHeader.getApp());
	}

	static public interface ViewHolder{
		View getBtnReview();
		View getBtnLike();
		View getBtnUnlike();
		View getBtnShare();
		View getBtnDownload();
	}
}
