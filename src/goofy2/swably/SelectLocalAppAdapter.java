package goofy2.swably;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class SelectLocalAppAdapter extends LocalAppsAdapter{

	public SelectLocalAppAdapter(CloudActivity context, JSONArray list,
			HashMap<String, Integer> loadingImages) {
		super(context, list, loadingImages);
	}

//	@Override
//	public View newView(ViewGroup parent) {
//		View ret = null;
//		int resId = R.layout.local_app_row;
//		ret = mInflater.inflate(resId, parent, false);
//		return ret;
//	}


}
