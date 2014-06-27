package goofy2.swably;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.R;
import goofy2.swably.CommentsAdapter.ViewHolder;
import goofy2.swably.data.App;
import goofy2.utils.AsyncImageLoader;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AppsAdapter extends CloudInplaceActionsAdapter {
	protected AppHeader header;
	protected AppActionHelper actionHelper;  
   	boolean mHideTriangle = false;
	 

	public AppsAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages) {
		super(context, stream, loadingImages);
	}

	public AppsAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages, boolean hideTriangle) {
		super(context, stream, loadingImages);
		mHideTriangle = hideTriangle;
	}

	public void bindView(View view, final JSONObject jsonApp) {
		super.bindView(view, jsonApp);
		handleDivider(view);
		try {
			//String id = update.getString("id");
			//FeedHelper.bindUpdate(mContext, view, update, this, mLoadingImages, false);
			final App app = new App(jsonApp);
			ViewHolder holder = (ViewHolder) view.getTag();
			
			TextView tv;
			tv = holder.txtName;
			tv.setText(app.getName());
			tv.setTypeface(mContext.mBoldFont);
			
			ImageView iv = holder.icon;
//			Bitmap bm = null;
//			String url = app.getIcon();
//			bm = Utils.getImageFromFile(mContext, url); // file store 
//			if(bm != null) iv.setImageBitmap(bm);
//			else iv.setImageResource(R.drawable.noimage);
			iv.setImageResource(R.drawable.noimage);
			bindIcon(view, iv, app);
			
//			String time = Utils.formatTimeDistance(context, new Date((long) (dTime*1000)));
//			tv = (TextView)v.findViewById(R.id.txtTime);
//			tv.setText(time);

//			iv = (ImageView)view.findViewById(R.id.cloud);
//			if(app.isSharedByMe()){
//				iv.setVisibility(View.VISIBLE);
//			}else{
//				iv.setVisibility(View.GONE);
//			}
			
//			View vNormal = holder.viewNormal;
//			View vUploading = holder.viewUploading;
//			int status = app.getJSON().optInt(App.STATUS);
//			if(status == App.STATUS_UPLOADING){
//				vUploading.setVisibility(View.VISIBLE);
//				vNormal.setVisibility(View.GONE);
//				int percent = app.getJSON().optInt(Const.KEY_PERCENT); 
//				ProgressBar pb = holder.progressBar;
//				if(percent > 0){
//		    		pb.setIndeterminate(false);
//		    		pb.setProgress(percent);
//				}else
//		    		pb.setIndeterminate(true);
//					
//				long sizeSent = app.getJSON().optLong(Const.KEY_SIZE_TRANSFERRED);
//				tv = holder.txtSizeSent;
//	    		if(sizeSent > 0)
//	    			tv.setText(String.format(mContext.getString(R.string.size_sent), percent, sizeSent/1024));
//	    		else
//		    		tv.setText(mContext.getString(R.string.uploading_queued));
//			}else{
//				vUploading.setVisibility(View.GONE);
//				vNormal.setVisibility(View.VISIBLE);
//			}

//			CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox);
//			if(app.isSharedByMe() || status == App.STATUS_UPLOADING){
//				cb.setVisibility(View.GONE);
//			}else{
//				cb.setVisibility(View.VISIBLE);
//				boolean checked = app.getJSON().optBoolean(Const.KEY_CHECKED, true);
//				cb.setChecked(checked);
//			}

//			iv = (ImageView)view.findViewById(R.id.cloud);
			View bt = holder.btnTriangle;
			if(app.isInCloud() && !mHideTriangle){
//				iv.setVisibility(View.GONE);
				// bind actions
				bt.setVisibility(View.VISIBLE);
//				header = new AppHeader(mContext);
//				actionHelper = new AppActionHelper(mContext, header);
//				header.setApp(app);
//				header.setAppFromCache(header.getAppId());
//				actionHelper.init(view, new Runnable(){
//					@Override
//					public void run() {
//						mHelper.hideActionsAnim();
//					}
//		    	});
//				actionHelper.bind();
			}else{
//				iv.setVisibility(View.VISIBLE);
				bt.setVisibility(View.GONE);
			}

			String errMsg = app.getJSON().optString(Const.KEY_FAILED, null);
			tv = holder.txtError;
			tv.setTypeface(mContext.mNormalFont);
			if(errMsg == null){
				tv.setVisibility(View.GONE);
//				JSONObject review = app.getReview();
//				if(review != null){
////					String content = review.optJSONObject("user").optString("name")+": "+review.optString("content");
//					tv.setText(review.optString("content"));
//				}else tv.setText("");
			}else{
				tv.setVisibility(View.VISIBLE);
				tv.setText(errMsg);
			}
			
			tv = holder.txtReviewsCount;
			tv.setTypeface(mContext.mLightFont);
			int c = app.getPostsCount();
			if(c > 0){
				tv.setVisibility(View.VISIBLE);
				tv.setText(""+c);
			}else{
				tv.setVisibility(View.GONE);
			}
			

			double dTime = app.getUpdatedAt();
			String time = Utils.formatTimeDistance(mContext, new Date((long) (dTime*1000)));
			tv = holder.txtUpdatedAt;
			tv.setText(time);
			tv.setTypeface(mContext.mLightFont);
			
//			View ib = view.findViewById(R.id.btnCancel);
//			ib.setOnClickListener(new View.OnClickListener(){
//				@Override
//				public void onClick(View v) {
//					try {
//						Uploader.packageCanceled.put(app.getPackage(), true);
//						jsonApp.put(App.STATUS, 0);
//						jsonApp.put(Const.KEY_PERCENT, 0);
//						jsonApp.put(Const.KEY_SIZE_TRANSFERRED, 0);
//						jsonApp.put(Const.KEY_CHECKED, false);
//						notifyDataSetChanged();
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				
//			});
		
		} catch (Exception e) {
			Log.d(Const.APP_NAME, Const.APP_NAME + " StreamAdapter - bindView err: " + e.getMessage());
		}
	}
	
	void bindIcon(View view, ImageView iv, App app){
		new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(app.getIcon());
	}

	

	public View newView(ViewGroup parent) {
		View ret = null;
		int resId = R.layout.app_row;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}

	@Override
	void onInplacePanelOpen(View view, JSONObject json){
//		Utils.showToast(mContext, "open");
		header = new AppHeader(mContext);
		actionHelper = new AppActionHelper(mContext, header);
		header.setApp(new App(json));
		header.setAppFromCache(header.getAppId());
		actionHelper.init(view, new Runnable(){
			@Override
			public void run() {
				mHelper.hideActionsAnim();
			}
    	});
		actionHelper.bind();
	}
	
	@Override
	protected Object newViewHolder(View convertView){
		ViewHolder holder = new ViewHolder();
		holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
		holder.icon = (ImageView) convertView.findViewById(R.id.icon);
		holder.viewNormal = convertView.findViewById(R.id.viewNormal);
//		holder.viewUploading = convertView.findViewById(R.id.viewUploading);
		holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
		holder.txtSizeSent = (TextView) convertView.findViewById(R.id.txtSizeSent);
		holder.txtError = (TextView) convertView.findViewById(R.id.txtError);
		holder.txtReviewsCount = (TextView) convertView.findViewById(R.id.txtReviewsCount);
		holder.txtUpdatedAt = (TextView) convertView.findViewById(R.id.txtUpdatedAt);
		
		holder.btnTriangle = convertView.findViewById(R.id.btnTriangle);
		holder.inplacePanel = convertView.findViewById(R.id.inplacePanel);
		holder.btnDownload = convertView.findViewById(R.id.btnDownload);
		holder.btnUpload = convertView.findViewById(R.id.btnUpload);
		holder.btnPlay = convertView.findViewById(R.id.btnPlay);
		holder.btnInstall = convertView.findViewById(R.id.btnInstall);
		holder.btnReview = convertView.findViewById(R.id.btnReview);
		holder.btnTags = convertView.findViewById(R.id.btnTags);
		holder.btnShare = convertView.findViewById(R.id.btnShare);
		holder.btnLike = convertView.findViewById(R.id.btnLike);
		holder.btnUnlike = convertView.findViewById(R.id.btnUnlike);
		
		return holder;
	}
	
	static class ViewHolder	implements AppActionHelper.ViewHolder, AppTribtn.ViewHolder, CloudInplaceActionsAdapter.ViewHolder {
		ImageView icon;
		TextView txtName;
		View viewNormal;
//		View viewUploading;
		ProgressBar progressBar;
		TextView txtSizeSent;
		TextView txtError;
		TextView txtReviewsCount;
		TextView txtUpdatedAt;

		View btnTriangle;
		View inplacePanel;
		View btnDownload;
		View btnUpload;
		View btnPlay;
		View btnInstall;
		View btnReview;
		View btnTags;
		View btnShare;
		View btnLike;
		View btnUnlike;
		
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
		public View getBtnReview() {
			return btnReview;
		}
		@Override
		public View getBtnTags() {
			return btnTags;
		}
		@Override
		public View getBtnShare() {
			return btnShare;
		}
		@Override
		public View getBtnLike() {
			return btnLike;
		}
		@Override
		public View getBtnUnlike() {
			return btnUnlike;
		}
	}
}
