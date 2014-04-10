package goofy2.swably;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import goofy2.swably.R;
import goofy2.swably.DownloadingApp.OnClickListener_btnCancel;
import goofy2.swably.UploadingApp.UploaderExServiceConnection;
import goofy2.swably.data.App;
import goofy2.utils.JSONUtils;
import goofy2.utils.ParamRunnable;

public class LocalApps extends CloudWithLocalAppsListActivity {
	private ImageButton btnRefresh;

	//protected JSONArray mMyApps = new JSONArray();
	private View viewProgress;
	private ProgressBar progressBar;
	private TextView txtSizeSent;
	protected CacheProgressBroadcastReceiver mCacheProgressReceiver = new CacheProgressBroadcastReceiver();
	UploadingApp.UploaderExServiceConnection mConnection;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        if(redirectAnonymous()) return;
    }
    
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
      	super.onPostCreate(savedInstanceState);
//        if(mListContainer != null) mListContainer.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshDb();
//            }
//        });

    	viewProgress = this.findViewById(R.id.viewProgress);
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        txtSizeSent = (TextView) this.findViewById(R.id.txtSizeSent);

    	registerReceiver(mCacheProgressReceiver, new IntentFilter(Const.BROADCAST_CACHE_APPS_PROGRESS));

//        setStatus();
//		mAdapter.setData(mListData);
//		refreshListView();
    	
//		tryCacheApps(); // disable to avoid disturb user
    	
    	View btnRefresh = findViewById(R.id.btnRefresh);
    	btnRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshDb();
			}
		});
    }

    @Override
    public void onDestroy(){
    	try{
    		unregisterReceiver(mCacheProgressReceiver);
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	super.onDestroy();
    }
    
//    @Override 
//    public void onStart(){
////    	Utils.cancelNotify(this, mApp);
//    	super.onStart();
////debug
////    	if(mAdapter == null) return;
////		setStatus();
////		mAdapter.setData(mListData);
////		refreshListView();
//    }
    
    protected void setContent(){
    	enableSlidingMenu();
	    setContentView(R.layout.old_local_apps_fragment);
    }

    @Override
	protected String loadStream(String url, String lastId) {
		Log.d(Const.APP_NAME, Const.APP_NAME + " LocalApps loadStream lastId: " + lastId);
//    	if(lastId == null){
//    		mLoadingImages.clear();
//    		mListData = new JSONArray();
//    	}else{
//    		//url += "&max_id=" + lastId;
//    	}
		String err = null;
		try{
			AppHelper helper = new AppHelper(LocalApps.this);
////			mListData = JSONUtils.appendArray(mListData, helper.getApps());
//			mListData = helper.getApps();
			cursor = helper.getApps(db, false);
//			setStatus();
		}catch (Exception e){
//	    	Utils.alertTitle(this, getString(R.string.err_no_network_title), e.getMessage());
			err = e.getMessage();
			Log.e(Const.APP_NAME, Const.APP_NAME + " LocalApps loadStream err: " + err);
		}
		return err;
	}

	@Override
	protected String getUrl() {
		return "";
	}

	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	protected void refreshDb(){
		//showDialog(0);
//		final Handler handler = new Handler();
		mListData = new JSONArray();
		new Thread() {
			public void run(){
		    	if(Utils.isCaching) return;
				Utils.cacheMyApps(LocalApps.this);
//				handler.post(new Runnable(){
//					public void run(){
//						refreshWithoutLoading();
//					}
//				});
			}
		}.start();
		if(mAdapter instanceof CloudInplaceActionsAdapter) ((CloudInplaceActionsAdapter) mAdapter).mHelper.hideActionsAnim();
	}
	
    public class CacheProgressBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_CACHE_APPS_PROGRESS)){
            	int count = intent.getIntExtra(Const.KEY_COUNT, 0);
            	int total = intent.getIntExtra(Const.KEY_TOTAL, 1);
        		//if(count > 0) txtHeader.setText(String.format(getString(R.string.app_count), count));
        		boolean finished = intent.getBooleanExtra(Const.KEY_FINISHED, false);
        		//boolean refresh = intent.getBooleanExtra(Const.KEY_REFRESH, false);
        		boolean loading = intent.getBooleanExtra(Const.KEY_LOADING, false);
        		boolean loaded = intent.getBooleanExtra(Const.KEY_LOADED, false);
        		
        		if(loading){
        			viewProgress.setVisibility(View.VISIBLE);
        		}else if(loaded){
        			refreshWithoutLoading();
        			if(progressBar.isIndeterminate()){
            			viewProgress.setVisibility(View.GONE);
        			}
        		}else if(finished){
        			refreshWithoutLoading();
        			//loading.setVisibility(View.GONE);
        			//txtHeader.setVisibility(View.GONE);
        			viewProgress.setVisibility(View.GONE);
        		}else{
        			//loading.setVisibility(View.VISIBLE);
        			//txtHeader.setVisibility(View.VISIBLE);
        			viewProgress.setVisibility(View.VISIBLE);
        			int percent = count*100/total;
        			if(percent >= 100){
	        			progressBar.setIndeterminate(true);
	            		txtSizeSent.setText(getString(R.string.refreshing_app_status));
        			}else{
	        			progressBar.setIndeterminate(false);
	        			progressBar.setProgress(percent);
	            		txtSizeSent.setText(String.format(getString(R.string.caching_progress), count, total));
        			}
//        			try {
//						JSONObject json = new JSONObject(intent.getStringExtra(Const.KEY_APP));
//						JSONArray left = new JSONArray();
//						left.put(json);
//						mListData = JSONUtils.appendArray(left, mListData);
//						mAdapter.setData(mListData);
//						refreshListView();
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
        		}
            }
        }
    }

	@Override
	protected void refresh(){
		refreshWithoutLoading();
//		new Thread() {
//			public void run(){
//				Utils.cacheAppsStatus(LocalApps.this, mListData);
//			}
//		}.start();
	}

	@Override
    protected void loadedMore(boolean succeeded){
		//txtHeader.setText(String.format(getString(R.string.app_count), mListData.length()));
    }

//	@Override
//    public void loadListImagesEx(final JSONArray jsonList){
//    	final ExecutorService threadPool = Executors.newFixedThreadPool(Const.MULITI_DOWNLOADING);
//        threadPool.execute(new Runnable(){
//        	public void run() {
//        	   	final PackageManager mPackageManager = getPackageManager();
//				for(int i=0; i < jsonList.length() ; i++){
//					JSONObject item;
//					try {
//						item = jsonList.getJSONObject(i);
//						ParamRunnable pr = new ParamRunnable(){
//				        	public void run() {
//				        		App app = (App)param;
//								File f = new File(app.getIconPath());
//								if(!f.exists())
//									if(Utils.saveLocalApkIcon(mPackageManager, app.getPackage()))
//										sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
//										
//				        	}
//						};
//						pr.param = new App(item);
//						threadPool.execute(pr);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//        	}
//        });  
//    }

	// disable caching
	@Override
	public String getCacheId(){
//    	return cacheId();
    	return null;
	}
    
//	static public String cacheId(){
//    	return LocalAppsFragment.cacheId();
//    }

    
    @Override
	public long getCacheExpiresIn(){
//		return 3600*24*1000;  
		return 0; // no caching
	}
}
