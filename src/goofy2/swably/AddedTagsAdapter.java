package goofy2.swably;

import goofy2.swably.fragment.AppTagsFragment;
import goofy2.swably.fragment.MyAddedTagsFragment;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import goofy2.swably.data.App;

public class AddedTagsAdapter extends CloudBaseAdapter {
	App mApp;

	public AddedTagsAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages, App app) {
		super(context, stream, loadingImages);
		mApp = app;
	}

	public View newView(ViewGroup parent) {
		View ret = null;
		int resId = R.layout.added_tags_row;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}
	
	@Override
	public void bindView(View viewInfo, final JSONObject info) {
		try {
			ViewHolder holder = (ViewHolder) viewInfo.getTag();
			
			TextView tv;
			tv = holder.txtTagName;
			String str = "#"+info.optString("name");
			tv.setText(str);
			tv.setTypeface(mContext.mNormalFont);

			final View btnCheck = holder.btnCheck;
			final View btnUncheck = holder.btnUncheck;

			boolean isChecked = info.optBoolean("is_mine", false); 
			setStatus(btnCheck, btnUncheck, isChecked);
			btnUncheck.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
			        if(mContext.redirectAnonymous(false)) return;
//			        if(!info.optString("id").equals(Utils.getCurrentUserId(mContext)) && !justMentioned.containsKey(info.optString("id"))){
//			        	Utils.showToast(mContext, mContext.getString(R.string.mention_effect));
//			        	return;
//			        }
//			        
					Api.appTag(mContext, mApp.getCloudId(), info.optString("name"), false, null);
					setStatus(btnCheck, btnUncheck, false);
					Utils.clearCache(mContext, AppTagsFragment.cacheId(mApp.getCloudId()));
					Utils.clearCache(mContext, MyAddedTagsFragment.cacheId(mApp.getCloudId()));
					mContext.sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
					try {
						info.put("is_mine", false);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			btnCheck.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
			        if(mContext.redirectAnonymous(false)) return;
					Api.appTag(mContext, mApp.getCloudId(), info.optString("name"), true, null);
					setStatus(btnCheck, btnUncheck, true);
					Utils.clearCache(mContext, AppTagsFragment.cacheId(mApp.getCloudId()));
					Utils.clearCache(mContext, MyAddedTagsFragment.cacheId(mApp.getCloudId()));
					mContext.sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
					try {
						info.put("is_mine", true);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			Log.d(Const.APP_NAME, Const.APP_NAME + " MentionedFriendsAdapter - bindView err: " + e.getMessage());
		}
	}
	
	private void setStatus(View btnCheck, View btnUncheck, boolean isChecked){
		if(isChecked){
			btnCheck.setVisibility(View.GONE);
			btnUncheck.setVisibility(View.VISIBLE);
		}else{
			btnCheck.setVisibility(View.VISIBLE);
			btnUncheck.setVisibility(View.GONE);
			
		}
	}

	@Override
	protected Object newViewHolder(View convertView){
		ViewHolder holder = new ViewHolder();
		holder.txtTagName = (TextView) convertView.findViewById(R.id.txtTagName);
		holder.btnCheck = convertView.findViewById(R.id.btnCheck);
		holder.btnUncheck = convertView.findViewById(R.id.btnUncheck);
		return holder;
	}
	
	static class ViewHolder	{
		TextView txtTagName;
		View btnCheck;
		View btnUncheck;
	}
}
