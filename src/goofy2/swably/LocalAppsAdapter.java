package goofy2.swably;
import goofy2.swably.data.App;
import goofy2.utils.AsyncImageLoader;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;


// use sqlite cursor as the data source
public class LocalAppsAdapter extends AppsAdapter {
   	PackageManager mPackageManager;
   	Cursor cursor;

	public LocalAppsAdapter(CloudActivity context, JSONArray stream,
			HashMap<String, Integer> loadingImages) {
		super(context, stream, loadingImages);
		mPackageManager = context.getPackageManager();
	}

	public LocalAppsAdapter(CloudActivity context, JSONArray stream,
			HashMap<String, Integer> loadingImages, boolean hideTriangle) {
		super(context, stream, loadingImages, hideTriangle);
		mPackageManager = context.getPackageManager();
	}

	@Override
	void bindIcon(View view, ImageView iv, App app){
		new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadApkPath(app.getPackage(), mPackageManager);
	}
	
	public void setData(Cursor aCursor) {
		cursor = aCursor;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if(cursor == null) return 0;
		return cursor.getCount();
	}

	@Override
	public Object getItem(int arg0) {
		Object ret = null;
		try {
			cursor.moveToPosition(arg0);
			ret = new JSONObject(cursor.getString(cursor.getColumnIndexOrThrow(AppHelper.DETAILS)));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
