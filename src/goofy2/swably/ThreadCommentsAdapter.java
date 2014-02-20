package goofy2.swably;

import goofy2.swably.R;
import java.util.HashMap;

import org.json.JSONArray;
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
	
}
