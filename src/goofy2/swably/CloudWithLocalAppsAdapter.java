package goofy2.swably;

import goofy2.swably.data.App;
import goofy2.utils.AsyncImageLoader;

import java.util.HashMap;

import org.json.JSONArray;

import android.view.View;
import android.widget.ImageView;

public class CloudWithLocalAppsAdapter extends LocalAppsAdapter {

	public CloudWithLocalAppsAdapter(CloudActivity context, JSONArray stream,
			HashMap<String, Integer> loadingImages) {
		super(context, stream, loadingImages);
	}

	public CloudWithLocalAppsAdapter(CloudActivity context, JSONArray stream,
			HashMap<String, Integer> loadingImages, boolean hideTriangle) {
		super(context, stream, loadingImages, hideTriangle);
	}

	@Override
	void bindIcon(View view, ImageView iv, App app){
		if(app.getIcon()==null)
			new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadApkPath(app.getPackage(), mPackageManager);
		else
			new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(app.getIcon());
	}
}
