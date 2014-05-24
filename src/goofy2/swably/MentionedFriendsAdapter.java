package goofy2.swably;

import goofy2.swably.UsersAdapter.ViewHolder;
import goofy2.swably.fragment.MyMentionedFriendsFragment;
import goofy2.utils.AsyncImageLoader;

import java.util.ArrayList;
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
import goofy2.swably.AddWatcher;

public class MentionedFriendsAdapter extends UsersAdapter {
	JSONObject mReview;
	HashMap <String, Boolean> justMentioned = new HashMap <String, Boolean>();

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

			boolean isFollowed = info.optBoolean("is_watching", false); 
			setStatus(btnFollow, btnUnfollow, isFollowed);
			btnUnfollow.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
			        if(mContext.redirectAnonymous(false)) return;
			        if(!info.optString("id").equals(Utils.getCurrentUserId(mContext)) && !justMentioned.containsKey(info.optString("id"))){
			        	Utils.showToast(mContext, mContext.getString(R.string.mention_effect));
			        	return;
			        }
			        
//			        mContext.transitWidth(btnUnfollow, btnFollow, null);
					Api.watch(mContext, mReview.optString("id"), info.optString("id"), false, null);
					setStatus(btnFollow, btnUnfollow, false);
					justMentioned.put(info.optString("id"), false);
					Utils.clearCache(mContext, MyMentionedFriendsFragment.cacheId(mReview.optString("id")));
					mContext.sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
					try {
						info.put("is_watching", false);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			btnFollow.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
			        if(mContext.redirectAnonymous(false)) return;
//			        btnUnfollow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_btn_light));
//			        mContext.transitWidth(btnFollow, btnUnfollow, new Runnable(){
//						@Override
//						public void run() {
//							btnUnfollow.setBackgroundColor(mContext.getResources().getColor(R.color.none));
//						}
//					});
					Api.watch(mContext, mReview.optString("id"), info.optString("id"), true, null);
					setStatus(btnFollow, btnUnfollow, true);
					justMentioned.put(info.optString("id"), true);
					Utils.clearCache(mContext, MyMentionedFriendsFragment.cacheId(mReview.optString("id")));
					mContext.sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
					try {
						info.put("is_watching", true);
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
