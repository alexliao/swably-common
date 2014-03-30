package goofy2.swably;

import goofy2.swably.R;
import goofy2.utils.AsyncImageLoader;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedsAdapter extends CloudBaseAdapter {
	public FeedsAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages) {
		super(context, stream, loadingImages);
	}

	@Override
	public void bindView(final View viewInfo, final JSONObject info) {
		bind(viewInfo, mContext, info);
		
		ViewHolder holder = (ViewHolder) viewInfo.getTag();
		
		final View ib1 = holder.avatar;
		if(ib1 != null){
			Utils.setTouchAnim(mContext, ib1);
			ib1.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					CloudActivity.openUser(mContext, info.optJSONObject("producer"));
				}
			});
		}
		
	}
	
	public void bind(View v, final CloudActivity context, JSONObject feed) {
		try {
			String str;
			final JSONObject user = feed.optJSONObject("producer");
			ImageView iv;
			TextView tv;
			ViewHolder holder = (ViewHolder) v.getTag();
			if(user == null){
			}else{
				iv = holder.avatar;
				tv = holder.txtUserName;
				if(!user.isNull("avatar_mask")){
					String mask = user.optString("avatar_mask", "");
					String url = mask.replace("[size]", "bi");
					iv.setImageResource(R.drawable.noname);
					new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
				}
				str = user.optString("name");
				tv.setText(str);
				tv.setTypeface(context.mBoldFont);
			}
			
			tv = holder.txtFeedTitle;
			str = feed.optString("title");
			tv.setText(str);
			tv.setTypeface(context.mNormalFont);

			tv = holder.txtFeedContent;
			str = feed.optString("content");
			if(Utils.isEmpty(str)){
				tv.setVisibility(View.GONE);
			}else{
				tv.setVisibility(View.VISIBLE);
				tv.setText(str);
				tv.setTypeface(context.mNormalFont);
			}

			double dTime = feed.getDouble("created_at");
			String time = Utils.formatTimeDistance(context, new Date((long) (dTime*1000)));
			tv = holder.txtTime;
			tv.setText(time);
			tv.setTypeface(context.mLightFont);
			
			setRead(context, feed, holder, feed.optBoolean("read", false));
		} catch (Exception e) {
			Log.e(Const.APP_NAME, Const.APP_NAME + " FeedsAdapter - bind err: " + e.getMessage());
		}
	}
	
	
	void setRead(Context context, JSONObject feed, ViewHolder holder, boolean read){
		if(read){
			int color = context.getResources().getColor(R.color.desc);
			holder.txtUserName.setTextColor(color);
			holder.txtFeedTitle.setTextColor(color);
		}else{
			int color = context.getResources().getColor(R.color.content);
			holder.txtUserName.setTextColor(color);
			holder.txtFeedTitle.setTextColor(color);
		}
	}
	
	public View newView(ViewGroup parent) {
		View ret = null;
		int resId = R.layout.feed_row;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}

	@Override
	protected Object newViewHolder(View convertView){
		ViewHolder holder = new ViewHolder();
		holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
		holder.txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
		holder.txtFeedTitle = (TextView) convertView.findViewById(R.id.txtFeedTitle);
		holder.txtFeedContent = (TextView) convertView.findViewById(R.id.txtFeedContent);
		holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);

		return holder;
	}
	
	static class ViewHolder {
		ImageView avatar;
		TextView txtUserName;
		TextView txtFeedTitle;
		TextView txtFeedContent;
		TextView txtTime;
	}
	
}
