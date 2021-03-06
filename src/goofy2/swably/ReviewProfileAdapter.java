package goofy2.swably;

import goofy2.swably.CommentsAdapter.ViewHolder;
import goofy2.swably.data.App;
import goofy2.utils.AsyncImageLoader;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.ClipboardManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ReviewProfileAdapter extends ThreadCommentsAdapter {
	private static final int CURRENT_REVIEW = 0;
	private static final int THREAD = 1;
	JSONObject mReview;

	public ReviewProfileAdapter(CloudActivity context, JSONArray stream, HashMap<String, Integer> loadingImages, JSONObject currentReview) {
		super(context, stream, loadingImages);
		mReview = currentReview;
	}

    @Override
    public int getItemViewType(int position) {
		JSONObject json = (JSONObject)getItem(position);
		if(json.optInt("id") == mReview.optInt("id")){
            return CURRENT_REVIEW;
        } else {
            return THREAD;
        }
    }

    @Override
    public int getViewTypeCount() {
            return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;               
    }

@Override
	public View getView (int position, View convertView, ViewGroup parent){
		
		if(getItemViewType(position) == THREAD){
			return super.getView(position, convertView, parent);
		}

		if(convertView == null){
			int resId = R.layout.review_profile_row;
			convertView = mInflater.inflate(resId, parent, false);
			mIsFirst = (position == 0);
			mIsLast = (position == getCount()-1);
			mPosition = position;
		}
		bindCurrentReview(convertView, (JSONObject)getItem(position));
		return convertView;
	}

	private void bindCurrentReview(View v, final JSONObject review) {
		try {
			final JSONObject user = review.optJSONObject("user");
			ImageView iv;
			TextView tv;
			Bitmap bm = null;
			String url = null;
			
			View viewUser = v.findViewById(R.id.viewUser);

			tv = (TextView)v.findViewById(R.id.txtUserName);
			tv.setText(user.optString("name"));
			tv.setTypeface(mContext.mBoldFont);

			if(!user.isNull("avatar_mask")){
				String mask = user.optString("avatar_mask", "");
				url = mask.replace("[size]", "bi");
			}
			iv = (ImageView)v.findViewById(R.id.avatar);
			iv.setImageResource(R.drawable.noname);
			new AsyncImageLoader(mContext, iv, 0).loadUrl(url);

			Utils.setTouchAnim(mContext, iv);
			iv.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					mContext.openUser(user);
				}
				
			});
			
			String str;
			View viewContent = v.findViewById(R.id.viewContent);
			
			tv = (TextView) v.findViewById(R.id.txtContent);
			str = review.optString("content");
			JSONObject inreplytoUser = Utils.getInreplytoUser(review);
			if(inreplytoUser != null){
				str = str.replace(Utils.genAtInreplytoUser(inreplytoUser), "").trim();
			}
			tv.setText(str);
			tv.setTypeface(mContext.mLightFont);

	        tv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					ClipboardManager cbm = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
					cbm.setText(review.optString("content"));		
					Utils.showToast(mContext, mContext.getString(R.string.copied));
				}
	        });

			double dTime = review.getDouble("created_at");
			String time = Utils.formatTimeDistance(mContext, new Date((long) (dTime*1000)));
			tv = (TextView) v.findViewById(R.id.txtTime);
			tv.setText(time);
			tv.setTypeface(mContext.mLightFont);

			str = review.optString("model");
			if(str != null){
				tv = (TextView) v.findViewById(R.id.txtModel);
				tv.setText(str);
				tv.setTypeface(mContext.mLightFont);
			}

			final App app = new App(review.optJSONObject("app"));
			View viewApp = v.findViewById(R.id.viewApp);
			View viewAdd = v.findViewById(R.id.viewAdd);
			View btnAddApp = v.findViewById(R.id.btnAddApp);
			if(app.getJSON() == null){
				viewAdd.setVisibility(View.VISIBLE);
				viewApp.setVisibility(View.GONE);
//				
//				btnAddApp.setOnClickListener(new View.OnClickListener(){
//					@Override
//					public void onClick(View v) {
//						mContext.selectAppToReply(review, null);
//					}
//				});
				
			}else{
				viewAdd.setVisibility(View.GONE);
				viewApp.setVisibility(View.VISIBLE);
				
				iv = (ImageView) v.findViewById(R.id.icon);
				if(app.getIcon() != null){
					url = app.getIcon();
					new AsyncImageLoader(mContext, iv, 1).loadUrl(url);
				}
				tv = (TextView) v.findViewById(R.id.txtAppName);
				tv.setText(app.getName());
				tv.setTypeface(mContext.mBoldFont);
				tv = (TextView) v.findViewById(R.id.txtAppSize);
				tv.setText(String.format(mContext.getString(R.string.app_size_short), app.getCloudSize()/1048576.0));
				tv.setTypeface(mContext.mLightFont);
				
				Utils.setTouchAnim(mContext, iv);
				iv.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						mContext.openApp(app.getJSON());
					}
					
				});
			}
			
			View viewImage = v.findViewById(R.id.viewImage);
			final ImageView imgThumbnail = (ImageView)v.findViewById(R.id.imgThumbnail);
			final ImageView imgImage = (ImageView)v.findViewById(R.id.imgImage);
			final View loadingImage = v.findViewById(R.id.loadingImage);
			final View txtThumbnailHint = v.findViewById(R.id.txtThumbnailHint);
//			View dividerImage = v.findViewById(R.id.dividerImage);
			final String imageUrl = review.optString("image", null);
			if(imageUrl != null){
				viewImage.setVisibility(View.VISIBLE);

				String thumbnailUrl = review.optString("thumbnail");
				new AsyncImageLoader(mContext, imgThumbnail, 1).setCallback(new Runnable(){
					@Override
					public void run() {
						loadingImage.setVisibility(View.GONE);
						if(Utils.isWifi(mContext)) downloadScreenshot(imageUrl, imgImage, loadingImage);
						else txtThumbnailHint.setVisibility(View.VISIBLE);
					}
				})
				.loadUrl(thumbnailUrl);

//				new AsyncImageLoader(mContext, imgImage, 1).setRequestSize(Const.SCREEN_WIDTH, Const.SCREEN_HEIGHT).setCallback(new Runnable(){
//					@Override
//					public void run() {
//						loadingImage.setVisibility(View.GONE);
//					}
//				})
//				.loadUrl(imageUrl);
//				downloadScreenshot(imageUrl, imgImage, loadingImage);
//				if(Utils.isWifi(mContext)) downloadScreenshot(imageUrl, imgImage, loadingImage);
				if(!Utils.isWifi(mContext)){
					imgThumbnail.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View v) {
							downloadScreenshot(imageUrl, imgImage, loadingImage);
						}
					});
				}
				
				imgImage.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if(imgImage.getDrawable() != null){ // image saved
							String imageUrl = review.optString("image", null);
							Intent intent = new Intent("android.intent.action.VIEW");  
						    intent.addCategory("android.intent.category.DEFAULT");  
						    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
						    Uri uri = Uri.fromFile(new File(Utils.getImageFileName(imageUrl)));  
						    intent.setDataAndType(uri, "image/*");
						    mContext.startActivity(intent);
						}
					}
				});

			}else{
				viewImage.setVisibility(View.GONE);
//				dividerImage.setVisibility(View.GONE);
			}
			
			if(Utils.getInreplytoUser(review) != null) bindInreplyto(v, review);
			
			bindWatchers(v, review);

			// bind tribtn
	    	if(app.getJSON() != null){
				AppHeader header = new AppHeader(mContext);
				header.setApp(app);
				header.setAppFromCache(header.getAppId());
		    	AppTribtnText tribtn = new AppTribtnText();
		    	tribtn.init(mContext, v, null, review);
		    	tribtn.setStatus(header.getApp());
	    	}
	    	
	    	// bind review actions
			ReviewActionHelper reviewActionHelper = new ReviewActionHelper(mContext, review);
	        reviewActionHelper.init(v);
			reviewActionHelper.bind();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void downloadScreenshot(String imageUrl, ImageView imgImage, final View loadingImage){
		loadingImage.setVisibility(View.VISIBLE);
		new AsyncImageLoader(mContext, imgImage, 1).setRequestSize(Const.SCREEN_WIDTH, Const.SCREEN_HEIGHT).setCallback(new Runnable(){
			@Override
			public void run() {
				loadingImage.setVisibility(View.GONE);
			}
		})
		.loadUrl(imageUrl);
	}
	
	void bindWatchers(View v, final JSONObject review) throws JSONException{
		JSONArray watchers = review.optJSONArray("recent_watchers");
		
//		for(int i=0; i<3; i++){
//			ImageView iv = (ImageView) v.findViewWithTag("watcher"+(i+1));
//			iv.setImageResource(R.drawable.noname);
//			iv.setVisibility(View.GONE);
//		}
//		for(int i=0; i<watchers.length(); i++){
//			JSONObject user = watchers.optJSONObject(i);
//			ImageView iv = (ImageView) v.findViewWithTag("watcher"+(i+1));
//			if(iv != null){
//				iv.setVisibility(View.VISIBLE);
//				if(!user.isNull("avatar_mask")){
//					String mask = user.optString("avatar_mask", "");
//					String url = mask.replace("[size]", "sq");
////					iv.setImageResource(R.drawable.noname);
//					new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
//				}
//			}
//		}
		
		ImageView imgWatcher1 = (ImageView) v.findViewById(R.id.imgWatcher1);
		ImageView imgWatcher2 = (ImageView) v.findViewById(R.id.imgWatcher2);
		ImageView imgWatcher3 = (ImageView) v.findViewById(R.id.imgWatcher3);
		imgWatcher1.setVisibility(View.GONE);
		imgWatcher2.setVisibility(View.GONE);
		imgWatcher3.setVisibility(View.GONE);
		JSONObject user;
		if(watchers.length() >= 1){
			imgWatcher1.setVisibility(View.VISIBLE);
			imgWatcher1.setImageResource(R.drawable.noname);
			user = watchers.optJSONObject(1-1);
			if(!user.isNull("avatar_mask")){
				String url = user.optString("avatar_mask", "").replace("[size]", "sq");
				new AsyncImageLoader(mContext, imgWatcher1, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
			}
		}
		if(watchers.length() >= 2){
			imgWatcher2.setVisibility(View.VISIBLE);
			imgWatcher2.setImageResource(R.drawable.noname);
			user = watchers.optJSONObject(2-1);
			if(!user.isNull("avatar_mask")){
				String url = user.optString("avatar_mask", "").replace("[size]", "sq");
				new AsyncImageLoader(mContext, imgWatcher2, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
			}
		}
		if(watchers.length() >= 3){
			imgWatcher3.setVisibility(View.VISIBLE);
			imgWatcher3.setImageResource(R.drawable.noname);
			user = watchers.optJSONObject(3-1);
			if(!user.isNull("avatar_mask")){
				String url = user.optString("avatar_mask", "").replace("[size]", "sq");
				new AsyncImageLoader(mContext, imgWatcher3, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
			}
		}
		
		View btnAddWatcher = v.findViewById(R.id.btnAddWatcher);
		btnAddWatcher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mContext.redirectAnonymous()) return;
				Intent i = new Intent(mContext, AddWatcher.class);
				i.putExtra(Const.KEY_REVIEW, review.toString());
				mContext.startActivity(i);
			}
		});
	}
	
	protected void bindInreplyto(View v, JSONObject review) {
        View viewInreplyto = v.findViewById(R.id.viewInreplyto);
		viewInreplyto.setVisibility(View.VISIBLE);
		try {
			String str;
			ImageView iv;
			TextView tv;
			JSONObject user = Utils.getInreplytoUser(review);
			
			tv = (TextView) v.findViewById(R.id.txtInreplyto);
			tv.setTypeface(mContext.mLightFont);
			tv = (TextView) v.findViewById(R.id.txtInreplytoName);
			str = user.optString("name");
			tv.setText(str);
			tv.setTypeface(mContext.mLightFont);
//
			final int position = getCurrentReviewPosition();
			viewInreplyto.setOnClickListener(new View.OnClickListener(){
				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					if(Build.VERSION.SDK_INT >= 11)
						mListView.smoothScrollToPositionFromTop(position-1, 70);
					else if(Build.VERSION.SDK_INT >= 8)
						mListView.smoothScrollToPosition(position-1);
					else
						mListView.setSelectionFromTop(position-1, 70);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int getCurrentReviewPosition(){
    	int ret = 1;
    	for(int i=0; i<mListData.length(); i++){
    		if(mReview.optInt("id") == mListData.optJSONObject(i).optInt("id")){
    			ret = i+1;
    			break;
    		}
    	}
    	return ret;
    }
}
