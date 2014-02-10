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

public class ReviewActionHelper {
	protected CloudActivity mActivity;
	protected JSONObject mReview;
	public View btnReply;
	public View btnLike;
	public View btnUnlike;
	public View btnShareReview;
	public View viewAppBtn;
	
	
	public ReviewActionHelper(final CloudActivity activity, JSONObject review){
		mActivity = activity;
		mReview = review;
	}
	
	public void init(View container){
		init(container, null);
	}
	
	public void init(View container, final Runnable callback){
		ViewHolder holder = (ViewHolder) container.getTag();
		
		if(holder == null) btnReply = container.findViewById(R.id.btnReply);
		else btnReply = holder.getBtnReply();
		if(btnReply != null) btnReply.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				mActivity.replyReview(mReview);
				if(callback != null) callback.run();
			}
		});

//		if(holder == null) btnLike = container.findViewById(R.id.btnDig);
//		else btnLike = holder.getBtnLike();
//		if(btnLike != null) btnLike.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				try {
//			        if(mActivity.redirectAnonymous(false)) return;
//					mActivity.flipView(btnLike, btnUnlike, new Runnable(){
//						@Override
//						public void run() {
//							if(callback != null) callback.run();
//						}
//					});
//					Utils.dig(mActivity, mReview.optString("id"), true, null);
//					mReview.put("is_digged", true);
//					mActivity.cacheData(mReview.toString(), ReviewProfile.cacheId(mReview.optString("id")));
//					bind();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		
//		if(holder == null) btnUnlike = container.findViewById(R.id.btnUndig);
//		else btnUnlike = holder.getBtnUnlike();
//		if(btnUnlike != null) btnUnlike.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				try {
//					mActivity.flipView(btnUnlike, btnLike, new Runnable(){
//						@Override
//						public void run() {
//							if(callback != null) callback.run();
//						}
//					});
//					Utils.dig(mActivity, mReview.optString("id"), false, null);
//					mReview.put("is_digged", false);
//					mActivity.cacheData(mReview.toString(), ReviewProfile.cacheId(mReview.optString("id")));
//					bind();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
////				if(callback != null) callback.run();
//			}
//		});

		if(holder == null) btnShareReview = container.findViewById(R.id.btnShareReview);
		else btnShareReview = holder.getBtnShareReview();
        if(btnShareReview != null) btnShareReview.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				mActivity.sendOutReview(mReview);
				Utils.shareReview(mActivity, mReview);
				if(callback != null) callback.run();
			}
        });

		if(holder == null) viewAppBtn = container.findViewById(R.id.viewAppBtn);
		else viewAppBtn = holder.getViewAppBtn();
	
	}

	public void bind() {
//		if(btnLike != null && btnUnlike != null){
//	    	if(mReview.optBoolean("is_digged")){
//	    		btnLike.setVisibility(View.GONE);
//	    		btnUnlike.setVisibility(View.VISIBLE);
//	    	}else{
//	    		btnLike.setVisibility(View.VISIBLE);
//	    		btnUnlike.setVisibility(View.GONE);
//	    	}
//		}
		if(mReview.optJSONObject("app") == null){
			viewAppBtn.setVisibility(View.GONE);
		}else{
			viewAppBtn.setVisibility(View.VISIBLE);
		}
	}

	static public interface ViewHolder{
		View getBtnReply();
//		View getBtnLike();
//		View getBtnUnlike();
		View getBtnShareReview();
		View getViewAppBtn();
	}
}
