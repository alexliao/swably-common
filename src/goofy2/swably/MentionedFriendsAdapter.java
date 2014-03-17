package goofy2.swably;

import goofy2.swably.UsersAdapter.ViewHolder;
import goofy2.utils.AsyncImageLoader;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MentionedFriendsAdapter extends UsersAdapter {
	JSONObject mReview;

	public MentionedFriendsAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages, JSONObject currentReview) {
		super(context, stream, loadingImages);
		mReview = currentReview;
	}

	public View newView(ViewGroup parent) {
		View ret = null;
		int resId = R.layout.mentioned_friend_row;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}
	
	@Override
	public void bindView(View viewInfo, final JSONObject info) {
		try {
			String str;
			ViewHolder holder = (ViewHolder) viewInfo.getTag();
			
			ImageView iv = holder.avatar;
			String url = null;
			if(!info.isNull("avatar_mask")){
				String mask = info.optString("avatar_mask", "");
				url = mask.replace("[size]", "bi");
			}else if(!info.isNull("avatar")){
				url = info.optString("avatar", "");
			}
			iv.setImageResource(R.drawable.noname);
			new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
			
			TextView tv;
			tv = holder.txtName;
			tv.setText(info.optString("name"));
			tv.setTypeface(mContext.mBoldFont);

			final View btnFollow = holder.btnFollow;
			final View btnUnfollow = holder.btnUnfollow;

			boolean isFollowed = info.optBoolean("is_followed", false); 
			setStatus(btnFollow, btnUnfollow, isFollowed);
			btnUnfollow.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
			        if(mContext.redirectAnonymous(false)) return;
			        mContext.transitWidth(btnUnfollow, btnFollow);
					Api.watch(mContext, mReview.optString("id"), info.optString("id"), false, null);
					mContext.sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
					try {
						info.put("is_followed", false);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			btnFollow.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
			        if(mContext.redirectAnonymous(false)) return;
			        mContext.transitWidth(btnFollow, btnUnfollow);
					Api.watch(mContext, mReview.optString("id"), info.optString("id"), true, null);
					mContext.sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
					try {
						info.put("is_followed", true);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			Log.d(Const.APP_NAME, Const.APP_NAME + " MentionedFriendsAdapter - bindView err: " + e.getMessage());
		}
	}
	
	private void setStatus(View btnFollow, View btnUnfollow, boolean isFollowed){
		if(isFollowed){
			btnFollow.setVisibility(View.GONE);
			btnUnfollow.setVisibility(View.VISIBLE);
		}else{
			btnFollow.setVisibility(View.VISIBLE);
			btnUnfollow.setVisibility(View.GONE);
			
		}
	}

}
