package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.data.App;
import goofy2.utils.AsyncImageLoader;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentsAdapter extends CloudInplaceActionsAdapter {
	boolean mHideUser = false;
	boolean mHideApp = false;
	ReviewActionHelper mActionHelper;

	public CommentsAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages) {
		super(context, stream, loadingImages);
	}

	public CommentsAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages, boolean hideUser, boolean hideApp) {
		super(context, stream, loadingImages);
		mHideUser = hideUser;
		mHideApp = hideApp;
	}

	public void bindView(final View viewInfo, final JSONObject info) {
		super.bindView(viewInfo, info);
		bind(viewInfo, mContext, info, mHideUser, mHideApp);
		handleDivider(viewInfo);
		
		ViewHolder holder = (ViewHolder) viewInfo.getTag();

		final View ib1 = holder.avatar;
		if(ib1 != null){
			Utils.setTouchAnim(mContext, ib1);
			ib1.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					CloudActivity.openUser(mContext, info.optJSONObject("user"));
				}
			});
		}
		
		final View ib2 = holder.icon;
		if(ib2 != null){
			Utils.setTouchAnim(mContext, ib2);
			ib2.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					CloudActivity.openApp(mContext, info.optJSONObject("app"));
				}
			});
		}

//    	View btn;

//    	btn = holder.btnReply;
//    	btn.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				mHelper.hideActionsAnim();
//				mContext.replyReview(info);
//			}
//        });

//    	btn = holder.btnShare;
//    	btn.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				mHelper.hideActionsAnim();
//				mContext.sendOutReview(info);
//			}
//        });
    	
	}
	
	@Override
	void onInplacePanelOpen(View view, JSONObject json){
		// load cache for refreshing dig status
		String str = mContext.loadCache(ReviewProfile.cacheId(json.optString("id")));
		if(str != null)
			try {
				json = new JSONObject(str);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		mActionHelper = new ReviewActionHelper(mContext, json);
		mActionHelper.init(view, new Runnable(){
			@Override
			public void run() {
				mHelper.hideActionsAnim();
			}
    	});
		mActionHelper.bind();
		
    	App app = new App(json.optJSONObject("app"));
    	if(app.getJSON() != null){
			AppHeader header = new AppHeader(mContext);
			header.setApp(app);
			header.setAppFromCache(header.getAppId());
	    	AppTribtn tribtn = new AppTribtn();
	    	tribtn.init(mContext, view, header.getApp(), new Runnable(){
				@Override
				public void run() {
					mHelper.hideActionsAnim();
				}
	    	});
	    	tribtn.setStatus(header.getApp());
    	}
	}

//	private void setTouchAnim(final Context context, View v){
//		v.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if(event.getAction() == MotionEvent.ACTION_DOWN){
//					Animation anim  = AnimationUtils.loadAnimation(context, R.anim.shrink);
//					anim.setFillAfter(true);
//					v.startAnimation(anim);
//				}else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
//					Animation anim  = AnimationUtils.loadAnimation(context, R.anim.grow);
//					anim.setFillAfter(true);
//					v.startAnimation(anim);
//				}
//				return false;
//			}
//		});
//	}

	public void bind(View v, final CloudActivity context, JSONObject review, boolean hideUser, boolean hideApp) {
		try {
			String str;
			final JSONObject user = review.optJSONObject("user");
			final App app = new App(review.optJSONObject("app"));
			ImageView iv;
			TextView tv;
//			View viewApp = v.findViewById(R.id.viewApp);
//			View viewContent = v.findViewById(R.id.viewContent);
			ViewHolder holder = (ViewHolder) v.getTag();
			if(hideUser || user == null){
//				iv.setVisibility(View.GONE);
//				tv.setVisibility(View.GONE);
			}else{
//				iv.setVisibility(View.VISIBLE);
//				tv.setVisibility(View.VISIBLE);
				iv = holder.avatar;
				tv = holder.txtUserName;
				if(!user.isNull("avatar_mask")){
					String mask = user.optString("avatar_mask", "");
					String url = mask.replace("[size]", "bi");
//					Bitmap bm = Utils.getImageFromFile(context, url); 
//					if(bm != null) iv.setImageBitmap(bm);
//					else iv.setImageResource(R.drawable.noname);
//					iv.setTag(mPosition);
					iv.setImageResource(R.drawable.noname);
					new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
				}
				str = user.optString("name");
				tv.setText(str);
				tv.setTypeface(context.mBoldFont);

//				iv.setOnClickListener(new View.OnClickListener(){
//					@Override
//					public void onClick(View v) {
//						CloudActivity.openUser(context, user);
//					}
//					
//				});
			}
			
			tv = holder.txtContent;
			str = review.optString("content");
			if(str.equals("")){
				tv.setVisibility(View.GONE);
			}else{
				tv.setVisibility(View.VISIBLE);
				tv.setText(str);
				tv.setTypeface(context.mNormalFont);
			}

			double dTime = review.getDouble("created_at");
			String time = Utils.formatTimeDistance(context, new Date((long) (dTime*1000)));
			tv = holder.txtTime;
			tv.setText(time);
			tv.setTypeface(context.mLightFont);
			
//			iv = holder.imgHasImage;
			iv = holder.imgScreenshot;
			if(review.optString("image", null) == null){
				iv.setVisibility(View.GONE);
			}else{ 
				iv.setVisibility(View.VISIBLE);
				String url = review.optString("thumbnail");
				iv.setImageResource(R.drawable.tweetpic_placeholder);
				new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
			}


			
			if(hideApp || app.getJSON() == null){
//				viewApp.setVisibility(View.GONE);
				iv = holder.icon;
				iv.setVisibility(View.GONE);
				tv = holder.txtAppName;
				tv.setVisibility(View.GONE);
			}else{
//				viewApp.setVisibility(View.VISIBLE);
				iv = holder.icon;
				iv.setVisibility(View.VISIBLE);
				if(app.getIcon() != null){
					String url = app.getIcon();
//					Bitmap bm = Utils.getImageFromFile(context, url); 
//					if(bm != null) iv.setImageBitmap(bm);
//					else iv.setImageResource(R.drawable.noimage);
//					iv.setTag(mPosition);
					iv.setImageResource(R.drawable.noimage);
					new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
				}

				tv = holder.txtAppName;
				tv.setVisibility(View.VISIBLE);
				tv.setText(app.getName());
				tv.setTypeface(context.mBoldFont);
//				tv = (TextView) v.findViewById(R.id.txtAppVersion);
//				tv.setText(String.format(context.getString(R.string.version_name),app.getVersionName()));

//				viewApp.setOnClickListener(new View.OnClickListener(){
//					@Override
//					public void onClick(View v) {
//						CloudActivity.openApp(context, app.getJSON());
//					}
//					
//				});
			}
			
			if(app.getJSON() == null){
				holder.imgQuestion.setVisibility(View.VISIBLE);
			}else{
				holder.imgQuestion.setVisibility(View.GONE);
			}
			
		} catch (Exception e) {
			Log.d(Const.APP_NAME, Const.APP_NAME + " CommentsAdapter - bind err: " + e.getMessage());
		}
	}
	
	public View newView(ViewGroup parent) {
		View ret = null;
		int resId = R.layout.comment_row;
//		if(mHideUser) resId = R.layout.comment_row_nouser;
//		if(mHideApp) resId = R.layout.comment_row_noapp;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}

	@Override
	protected Object newViewHolder(View convertView){
		ViewHolder holder = new ViewHolder();
		holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
		holder.txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
		holder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);
		holder.imgHasImage = (ImageView) convertView.findViewById(R.id.imgHasImage);
		holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
		holder.icon = (ImageView) convertView.findViewById(R.id.icon);
		holder.imgScreenshot = (ImageView) convertView.findViewById(R.id.imgScreenshot);
		holder.txtAppName = (TextView) convertView.findViewById(R.id.txtAppName);
		holder.imgQuestion = (ImageView) convertView.findViewById(R.id.imgQuestion);
		holder.viewAppBtn = convertView.findViewById(R.id.viewAppBtn);
		holder.btnAddApp = convertView.findViewById(R.id.btnAddApp);

		holder.btnTriangle = convertView.findViewById(R.id.btnTriangle);
		holder.inplacePanel = convertView.findViewById(R.id.inplacePanel);
		holder.btnReply = convertView.findViewById(R.id.btnReply);
		holder.btnShareReview = convertView.findViewById(R.id.btnShareReview);
//		holder.btnLike = convertView.findViewById(R.id.btnDig);
//		holder.btnUnlike = convertView.findViewById(R.id.btnUndig);
		holder.btnDownload = convertView.findViewById(R.id.btnDownload);
		holder.btnUpload = convertView.findViewById(R.id.btnUpload);
		holder.btnPlay = convertView.findViewById(R.id.btnPlay);
		holder.btnInstall = convertView.findViewById(R.id.btnInstall);
		return holder;
	}
	
	static class ViewHolder	implements ReviewActionHelper.ViewHolder, AppTribtn.ViewHolder, CloudInplaceActionsAdapter.ViewHolder {
		ImageView avatar;
		TextView txtUserName;
		TextView txtContent;
		TextView txtTime;
		ImageView imgHasImage;
		ImageView icon;
		ImageView imgScreenshot;
		ImageView imgQuestion;
		View btnAddApp;
		TextView txtAppName;
		View btnTriangle;
		View inplacePanel;
		View btnReply;
		View btnShareReview;
//		View btnLike;
//		View btnUnlike;
		View btnDownload;
		View btnUpload;
		View btnPlay;
		View btnInstall;
		View viewAppBtn;
		@Override
		public View getBtnDownload() {
			return btnDownload;
		}
		@Override
		public View getBtnUpload() {
			return btnUpload;
		}
		@Override
		public View getBtnPlay() {
			return btnPlay;
		}
		@Override
		public View getBtnInstall() {
			return btnInstall;
		}
		@Override
		public View getBtnTriangle() {
			return btnTriangle;
		}
		@Override
		public View getInplacePanel() {
			return inplacePanel;
		}
		@Override
		public View getBtnReply() {
			return btnReply;
		}
//		@Override
//		public View getBtnLike() {
//			return btnLike;
//		}
//		@Override
//		public View getBtnUnlike() {
//			return btnUnlike;
//		}
		@Override
		public View getBtnShareReview() {
			return btnShareReview;
		}
		@Override
		public View getViewAppBtn() {
			return viewAppBtn;
		}
		@Override
		public View getBtnAddApp() {
			return btnAddApp;
		}
	}
	
}
