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

public class AppHistoryAdapter extends CloudBaseAdapter {

	public AppHistoryAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages) {
		super(context, stream, loadingImages);
	}

	public void bindView(final View viewInfo, final JSONObject user) {
		bind(viewInfo, mContext, user);
		handleDivider(viewInfo);
		
		ViewHolder holder = (ViewHolder) viewInfo.getTag();

		final View ib1 = holder.avatar;
		if(ib1 != null){
			Utils.setTouchAnim(mContext, ib1);
			ib1.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					CloudActivity.openUser(mContext, user);
				}
			});
		}
	}
	
	public void bind(View v, final CloudActivity context, JSONObject user) {
		try {
			String str;
			ImageView iv;
			TextView tv;
			ViewHolder holder = (ViewHolder) v.getTag();
			iv = holder.avatar;
			tv = holder.txtUserName;
			if(!user.isNull("avatar_mask")){
				String mask = user.optString("avatar_mask", "");
				String url = mask.replace("[size]", "bi");
				iv.setImageResource(R.drawable.noname);
				new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
			}

//			str = user.optString("name");
//			tv.setText(str);
//			tv.setTypeface(context.mNormalFont);

			str = user.optString("version_name");
			tv = holder.txtVersionName;
			tv.setText("");
			if(str != null){
				tv.setText(str);
				tv.setTypeface(context.mNormalFont);
			}
			
			double dTime = user.getDouble("uploaded_at");
			String time = Utils.formatTimeDistance(context, new Date((long) (dTime*1000)));
			tv = holder.txtTime;
			tv.setText(time);
			tv.setTypeface(context.mNormalFont);
			
		} catch (Exception e) {
			Log.d(Const.APP_NAME, Const.APP_NAME + " AppHistoryAdapter - bind err: " + e.getMessage());
		}
	}
	
	public View newView(ViewGroup parent) {
		View ret = null;
		int resId = R.layout.app_history_row;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}

	@Override
	protected Object newViewHolder(View convertView){
		ViewHolder holder = new ViewHolder();
		holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
		holder.txtUserName = (TextView) convertView.findViewById(R.id.txtUserName);
		holder.txtVersionName = (TextView) convertView.findViewById(R.id.txtVersionName);
		holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
		holder.icon = (ImageView) convertView.findViewById(R.id.icon);
		holder.txtAppName = (TextView) convertView.findViewById(R.id.txtAppName);

		return holder;
	}
	
	static class ViewHolder	{
		ImageView avatar;
		TextView txtUserName;
		TextView txtVersionName;
		TextView txtTime;
		ImageView icon;
		TextView txtAppName;
	}
	
}
