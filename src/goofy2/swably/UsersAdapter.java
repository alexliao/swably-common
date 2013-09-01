package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.CommentsAdapter.ViewHolder;
import goofy2.utils.AsyncImageLoader;
import goofy2.utils.ParamRunnable;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UsersAdapter extends CloudBaseAdapter {

	public UsersAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages) {
		super(context, stream, loadingImages);
	}

	public void bindView(View viewInfo, final JSONObject info) {
		handleDivider(viewInfo);
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
//			if(url != null){
//				Bitmap bm = Utils.getImageFromFile(mContext, url); 
//				//if(bm == null) FeedHelper.asyncLoadImage(context, 0, url, null);
//				if(bm != null) iv.setImageBitmap(bm);
//				else iv.setImageResource(R.drawable.noname);
//			}
//			iv.setTag(mPosition);
			iv.setImageResource(R.drawable.noname);
			new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
//			if(url != null){
//				Bitmap bm = Utils.getImageFromFile(mContext, url); 
//				if(bm != null) iv.setImageBitmap(bm);
//				else 
//					new AsyncImageLoader(mContext, iv, mPosition).setUrl(url);
//			}
			
			TextView tv;
			tv = holder.txtName;
			tv.setText(info.optString("name"));
			tv.setTypeface(mContext.mBoldFont);
//			if(info.optBoolean("protected")) tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock_small, 0);
//			else tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

//			tv = holder.txtUsername;
//			str = "@"+info.optString("username");
//			tv.setText(str);
//			tv.setTypeface(mContext.mNormalFont);
			
//			tv = (TextView) viewInfo.findViewById(R.id.txtBio);
//			if(!info.isNull("bio")){
//				tv.setText(info.optString("bio"));
//				tv.setVisibility(View.VISIBLE);
//			}else tv.setVisibility(View.GONE);

			final View btnFollow = holder.btnFollow;
			final View btnUnfollow = holder.btnUnfollow;
			if(Utils.getCurrentUserId(mContext).equals(info.optString("id"))){
				btnFollow.setVisibility(View.GONE);
				btnUnfollow.setVisibility(View.GONE);
			}else{
				boolean isFollowed = info.optBoolean("is_followed", false); 
				setStatus(btnFollow, btnUnfollow, isFollowed);
				btnUnfollow.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
				        if(mContext.redirectAnonymous(false)) return;
//				        mContext.flipView(btnUnfollow, btnFollow, null);
				        mContext.transitWidth(btnUnfollow, btnFollow);
						Utils.follow(mContext, info.optString("id"), info.optString("name"), false, null, false);
//						setStatus(btnFollow, btnUnfollow, false);
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
//				        mContext.flipView(btnFollow, btnUnfollow, null);
				        mContext.transitWidth(btnFollow, btnUnfollow);
						Utils.follow(mContext, info.optString("id"), info.optString("name"), true, null, false);
//						setStatus(btnFollow, btnUnfollow, true);
						try {
							info.put("is_followed", true);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (Exception e) {
			Log.d(Const.APP_NAME, Const.APP_NAME + " UsersAdapter - bindView err: " + e.getMessage());
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
	
	public View newView(ViewGroup parent) {
		View ret = null;
		int resId = R.layout.user_row;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}

	@Override
	protected Object newViewHolder(View convertView){
		ViewHolder holder = new ViewHolder();
		holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
		holder.txtUsername = (TextView) convertView.findViewById(R.id.txtUsername);
		holder.txtName = (TextView) convertView.findViewById(R.id.txtName);

		holder.btnFollow = convertView.findViewById(R.id.btnFollow);
		holder.btnUnfollow = convertView.findViewById(R.id.btnUnfollow);
		return holder;
	}
	
	static class ViewHolder	{
		ImageView avatar;
		TextView txtUsername;
		TextView txtName;
		View btnFollow;
		View btnUnfollow;
	}
}
