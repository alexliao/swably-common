package goofy2.swably;

import goofy2.swably.R;
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
		int resId = R.layout.thread_comment_row;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}
	
}
