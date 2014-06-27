package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.CommentsAdapter.ViewHolder;
import goofy2.swably.data.App;
import goofy2.utils.AsyncImageLoader;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AppTagsAdapter extends CloudBaseAdapter {

	public AppTagsAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages) {
		super(context, stream, loadingImages);
	}

	public void bindView(final View viewInfo, final JSONObject tag) {
		bind(viewInfo, mContext, tag);
	}
	
	public void bind(View v, final CloudActivity context, JSONObject tag) {
		try {
			String str;
			ImageView iv;
			TextView tv;
			ViewHolder holder = (ViewHolder) v.getTag();
			tv = holder.txtTagName;

			str = "#"+tag.optString("name");
			tv.setText(str);
			tv.setTypeface(context.mNormalFont);
			
			bindUsers(holder, tag);
			
		} catch (Exception e) {
			Log.d(Const.APP_NAME, Const.APP_NAME + " AppTagAdapter - bind err: " + e.getMessage());
		}
	}
	
	void bindUsers(ViewHolder holder, JSONObject tag) throws JSONException{
		holder.avatar1.setVisibility(View.GONE);
		holder.avatar2.setVisibility(View.GONE);
		holder.avatar3.setVisibility(View.GONE);
		JSONObject json = new JSONObject(tag.optString("recent_users_json"));
		JSONArray icons = json.optJSONArray("icons");
		if(icons.length() >= 1){
			holder.avatar1.setVisibility(View.VISIBLE);
			holder.avatar1.setImageResource(R.drawable.noname);
			new AsyncImageLoader(mContext, holder.avatar1, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(icons.getString(1-1));
		}
		if(icons.length() >= 2){
			holder.avatar2.setVisibility(View.VISIBLE);
			holder.avatar2.setImageResource(R.drawable.noname);
			new AsyncImageLoader(mContext, holder.avatar2, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(icons.getString(2-1));
		}
		if(icons.length() >= 3){
			holder.avatar3.setVisibility(View.VISIBLE);
			holder.avatar3.setImageResource(R.drawable.noname);
			new AsyncImageLoader(mContext, holder.avatar3, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(icons.getString(3-1));
		}
	}

	public View newView(ViewGroup parent) {
		View ret = null;
		int resId = R.layout.app_tags_row;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}

	@Override
	protected Object newViewHolder(View convertView){
		ViewHolder holder = new ViewHolder();
		holder.avatar1 = (ImageView) convertView.findViewById(R.id.avatar1);
		holder.avatar2 = (ImageView) convertView.findViewById(R.id.avatar2);
		holder.avatar3 = (ImageView) convertView.findViewById(R.id.avatar3);
		holder.txtTagName = (TextView) convertView.findViewById(R.id.txtTagName);
		return holder;
	}
	
	static class ViewHolder	{
		ImageView avatar1;
		ImageView avatar2;
		ImageView avatar3;
		TextView txtTagName;
	}
	
}
