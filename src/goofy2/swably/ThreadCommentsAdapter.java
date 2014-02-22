package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.CommentsAdapter.ViewHolder;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.view.ViewGroup;

public class ThreadCommentsAdapter extends CommentsAdapter {

	public ThreadCommentsAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages) {
		super(context, stream, loadingImages);
	}

	public ThreadCommentsAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages, boolean hideUser, boolean hideApp) {
		super(context, stream, loadingImages);
	}

	@Override
	public View newView(ViewGroup parent) {
		View ret = null;
		int resId = R.layout.review_thread_row;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}
	
	@Override
	void bindReplies(ViewHolder holder, JSONObject review) throws JSONException{
	}
}
