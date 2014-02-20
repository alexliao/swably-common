package goofy2.swably;

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
import android.view.View;
import android.view.ViewGroup;
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
		bindCurrentReview(convertView);
		return convertView;
	}

	private void bindCurrentReview(View v) {
		try {
			final JSONObject user = mReview.optJSONObject("user");
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

			viewUser.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					mContext.openUser(user);
				}
				
			});
			
			String str;
			View viewContent = v.findViewById(R.id.viewContent);
			
			tv = (TextView) v.findViewById(R.id.txtContent);
			str = mReview.optString("content");
			tv.setText(str);
			tv.setTypeface(mContext.mLightFont);

			double dTime = mReview.getDouble("created_at");
			String time = Utils.formatTimeDistance(mContext, new Date((long) (dTime*1000)));
			tv = (TextView) v.findViewById(R.id.txtTime);
			tv.setText(time);
			tv.setTypeface(mContext.mLightFont);

			str = mReview.optString("model");
			if(str != null){
				tv = (TextView) v.findViewById(R.id.txtModel);
				tv.setText(str);
				tv.setTypeface(mContext.mLightFont);
			}

			final App app = new App(mReview.optJSONObject("app"));
			View viewApp = v.findViewById(R.id.viewApp);
//			View dividerApp = v.findViewById(R.id.dividerApp);
			View imgQuestion = v.findViewById(R.id.imgQuestion);
			View btnAdd = v.findViewById(R.id.btnAdd);
			if(app.getJSON() == null){
				viewApp.setVisibility(View.GONE);
//				dividerApp.setVisibility(View.GONE);
				imgQuestion.setVisibility(View.VISIBLE);
				btnAdd.setVisibility(View.VISIBLE);
				
				btnAdd.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						mContext.selectAppToReply(mReview, null);
					}
				});
				
			}else{
				viewApp.setVisibility(View.VISIBLE);
//				dividerApp.setVisibility(View.VISIBLE);
				imgQuestion.setVisibility(View.GONE);
				btnAdd.setVisibility(View.GONE);
				
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
	
				viewApp.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						mContext.openApp(app.getJSON());
					}
					
				});
			}
			
			final ImageView imgThumbnail = (ImageView)v.findViewById(R.id.imgThumbnail);
			final ImageView imgImage = (ImageView)v.findViewById(R.id.imgImage);
			final View loadingImage = v.findViewById(R.id.loadingImage);
			String imageUrl = mReview.optString("image", null);
			if(imageUrl != null){
				imgThumbnail.setVisibility(View.VISIBLE);
				imgImage.setVisibility(View.VISIBLE);
				loadingImage.setVisibility(View.VISIBLE);

				new AsyncImageLoader(mContext, imgImage, 1).setRequestSize(Const.SCREEN_WIDTH, Const.SCREEN_HEIGHT).setCallback(new Runnable(){
					@Override
					public void run() {
						loadingImage.setVisibility(View.GONE);
					}
				})
				.loadUrl(imageUrl);

				imgImage.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if(imgImage.getDrawable() != null){ // image saved
							String imageUrl = mReview.optString("image", null);
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
				imgThumbnail.setVisibility(View.GONE);
				imgImage.setVisibility(View.GONE);
				loadingImage.setVisibility(View.GONE);
			}
			
			if(getInreplytoUser(mReview) != null) bindInreplyto(v);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void bindInreplyto(View v) {
        View viewInreplyto = v.findViewById(R.id.viewInreplyto);
		viewInreplyto.setVisibility(View.VISIBLE);
		try {
			String str;
			ImageView iv;
			TextView tv;
			JSONObject user = getInreplytoUser(mReview);
			
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
						mListView.smoothScrollToPositionFromTop(position-1, 50);
					else if(Build.VERSION.SDK_INT >= 8)
						mListView.smoothScrollToPosition(position-1);
					else
						mListView.setSelectionFromTop(position-1, 50);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONObject getInreplytoUser(JSONObject review){
		String str = review.optString("in_reply_to_user", "");
		JSONObject ret = null;
		if(!Utils.isEmpty(str))
			try {
				ret = new JSONObject(str);
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		return ret;
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
