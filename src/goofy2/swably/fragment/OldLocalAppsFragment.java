package goofy2.swably.fragment;


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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import goofy2.swably.AppHelper;
import goofy2.swably.CloudActivity;
import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.CloudInplaceActionsAdapter;
import goofy2.swably.CloudWithLocalAppsAdapter;
import goofy2.swably.CloudWithLocalAppsListActivity;
import goofy2.swably.Const;
import goofy2.swably.LocalApps;
import goofy2.swably.LocalAppsAdapter;
import goofy2.swably.R;
import goofy2.swably.UploadApp;
import goofy2.swably.UploaderEx;
import goofy2.swably.UploadingApp;
import goofy2.swably.Utils;
import goofy2.swably.R.id;
import goofy2.swably.R.layout;
import goofy2.swably.R.string;
import goofy2.swably.UploadingApp.UploaderExServiceConnection;
import goofy2.swably.data.App;
import goofy2.utils.JSONUtils;
import goofy2.utils.ParamRunnable;

public class OldLocalAppsFragment extends CloudListFragment {
//	private ImageButton btnRefresh;
	protected CacheProgressBroadcastReceiver mCacheProgressReceiver = new CacheProgressBroadcastReceiver();
//	protected UploadProgressBroadcastReceiver mUploadProgressReceiver = new UploadProgressBroadcastReceiver();
	protected RefreshAppBroadcastReceiver mRefreshAppProgressReceiver = new RefreshAppBroadcastReceiver();
	private HashMap<String, Integer> mIndex = null;

	//protected JSONArray mMyApps = new JSONArray();
	private View viewProgress;
	private ProgressBar progressBar;
	private TextView txtSizeSent;
	UploadingApp.UploaderExServiceConnection mConnection;

   	Cursor cursor;
	SQLiteDatabase db;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        if(getCloudActivity().redirectAnonymous()) return;
    	AppHelper helper = new AppHelper(a());
    	db = helper.getHelper().getReadableDatabase();
        super.onCreate(savedInstanceState);
//    	a().registerReceiver(mUploadProgressReceiver, new IntentFilter(Const.BROADCAST_UPLOAD_PROGRESS));
        a().registerReceiver(mCacheProgressReceiver, new IntentFilter(Const.BROADCAST_CACHE_APPS_PROGRESS));
        a().registerReceiver(mRefreshAppProgressReceiver, new IntentFilter(Const.BROADCAST_REFRESH_APP));
        Intent intent = new Intent(getActivity(), UploaderEx.class);
        mConnection = new UploadingApp.UploaderExServiceConnection();
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//		getCloudActivity().tryCacheApps();


    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View v = super.onCreateView(inflater, container, savedInstanceState);
        if(mListContainer != null) mListContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDb();
            }
        });

    	viewProgress = v.findViewById(R.id.viewProgress);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        txtSizeSent = (TextView) v.findViewById(R.id.txtSizeSent);
        
		return v;
    }


    @Override
    public void onDestroy(){
    	try{
    		a().unregisterReceiver(mRefreshAppProgressReceiver);
//    		a().unregisterReceiver(mUploadProgressReceiver);
    		a().unregisterReceiver(mCacheProgressReceiver);
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	if(mConnection.getService() != null) getActivity().unbindService(mConnection);
//    	try{
//    		getActivity().unregisterReceiver(mProgressReceiver);
//    	}catch (Exception e){
//    		e.printStackTrace();
//    	}
    	super.onDestroy();
    }
    
//    @Override 
//    public void onStart(){
//    	super.onStart();
////    	Utils.cancelNotify(this, mApp);
//		setStatus();
//		mAdapter.setData(mListData);
//		onDataChanged(-1);
//    }
    
    @Override 
    public void onResume(){
    	super.onResume();
//    	Utils.cancelNotify(this, mApp);
//		setStatus();
//		mAdapter.setData(mListData);
//		onDataChanged(-1);
    }

    @Override 
    public void onStop(){
    	super.onStop();
    }

    protected void setContent(){
	    setContentView(R.layout.local_apps_fragment);
    }

//    private void setStatus(){
//        UploaderEx service = mConnection.getService();
//        if(service != null){
//        	for(int i=0; i<mListData.length(); i++){
//				try {
//	        		JSONObject json = mListData.getJSONObject(i);
//	        		if(service.getPackages().contains(json.optString(App.PACKAGE)))
//						json.put(App.STATUS, App.STATUS_UPLOADING);
//					else
//						json.put(App.STATUS, null);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//        	}
////        	for(String packageName : service.getPackages()){
////        		try {
////					JSONObject json = mListData.getJSONObject(getIndex().get(packageName));
////					json.put(App.STATUS, App.STATUS_UPLOADING);
////				} catch (JSONException e) {
////					e.printStackTrace();
////				}
////        	}
//        	
//        }
//    }
    
//    @Override
//	protected String loadStream(String url, String lastId) {
//		Log.d(Const.APP_NAME, Const.APP_NAME + " LocalAppsFragment loadStream lastId: " + lastId);
//    	if(lastId == null){
//    		mLoadingImages.clear();
//    		mListData = new JSONArray();
//    	}else{
//    		//url += "&max_id=" + lastId;
//    	}
//		String err = null;
//		try{
//			AppHelper helper = new AppHelper(getActivity());
//			mListData = JSONUtils.appendArray(mListData, helper.getApps());
////			setStatus();
//		}catch (Exception e){
////	    	Utils.alertTitle(this, getString(R.string.err_no_network_title), e.getMessage());
//			err = e.getMessage();
//			Log.e(Const.APP_NAME, Const.APP_NAME + " LocalAppsFragment loadStream err: " + err);
//		}
//		return err;
//	}

    @Override
	protected String loadStream(String url, String lastId) {
		Log.d(Const.APP_NAME, Const.APP_NAME + " LocalApps loadStream lastId: " + lastId);
		String err = null;
		try{
			AppHelper helper = new AppHelper(a());
			cursor = helper.getApps(db);
		}catch (Exception e){
			err = e.getMessage();
			Log.e(Const.APP_NAME, Const.APP_NAME + " LocalApps loadStream err: " + err);
		}
		return err;
	}


	@Override
    protected void setData(){
		((LocalAppsAdapter) mAdapter).setData(cursor);
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

	@Override
	protected void onClickHeader() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected View getListHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void refreshDb(){
		if(Utils.isCaching) return;
		//showDialog(0);
//		final Handler handler = new Handler();
//		mListData = new JSONArray();
		new Thread() {
			public void run(){
				Utils.cacheMyApps(getActivity());
//				handler.post(new Runnable(){
//					public void run(){
//						refreshWithoutLoading();
//					}
//				});
			}
		}.start();
		if(mAdapter instanceof CloudInplaceActionsAdapter) ((CloudInplaceActionsAdapter) mAdapter).mHelper.hideActionsAnim();
	}
	
    protected class CacheProgressBroadcastReceiver extends BroadcastReceiver {
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
	protected CharSequence getListTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void refresh(){
//		new Thread() {
//			public void run(){
//				Utils.cacheAppsStatus(a(), mListData);
//			}
//		}.start();
		refreshWithoutLoading();
	}

    @Override
    public String getCacheId(){
    	return cacheId();
    }

    static public String cacheId(){
    	return OldLocalAppsFragment.class.getName();
    }

    @Override
	protected void loadMore(){
		// disable auto loading
	}	
//	@Override
//    protected void loadedMore(boolean succeeded){
//		//txtHeader.setText(String.format(getString(R.string.app_count), mListData.length()));
//    }


	@Override
	protected CloudBaseAdapter getAdapter() {
		return new LocalAppsAdapter(getCloudActivity(), mListData, mLoadingImages, true);
	}

//	protected void onClickItem(int position) throws JSONException {
////		if(Utils.isCaching) return;
//    	JSONObject json = mListData.optJSONObject(position);
//    	App app = new App(json);
//		if(app.getStatus() == App.STATUS_UPLOADING){
//			Intent i = new Intent(getActivity(), UploadingApp.class);
//			i.setData(Uri.parse(app.getPackage()));
//			i.putExtra(Const.KEY_APP, json.toString());
//			startActivity(i);
//		}else{
//	        if(!app.isInCloud()){
//	        	if(Utils.HttpTest(getActivity())) checkStatus(app);
//	        }else if(app.isLocalNew(getActivity())){
//				onNotUploaded(app);
//	        }else{
//	        	 onCloudAction(getCloudActivity(), app.getJSON());
//	        }
//		}
//	}

	protected void onClickItem(int position) throws JSONException {
//		if(Utils.isCaching) return;
//    	JSONObject json = mListData.optJSONObject(position);
		JSONObject json = (JSONObject) ((LocalAppsAdapter) mAdapter).getItem(position);
    	App app = new App(json);
//		if(app.getStatus() == App.STATUS_UPLOADING){
		if(isUploading(app)){
			Intent i = new Intent(a(), UploadingApp.class);
			i.setData(Uri.parse(app.getPackage()));
			i.putExtra(Const.KEY_APP, json.toString());
			i.putExtra(Const.KEY_PERCENT, getProgress(app));
			startActivity(i);
		}else{
	        if(!app.isInCloud()){
	        	if(Utils.HttpTest(a())) checkStatus(app);
	        }else if(app.isLocalNew(a())){
				onNotUploaded(app);
	        }else{
	        	 onCloudAction(ca(), app.getJSON());
	        }
		}
	}

	private boolean isUploading(App app){
		boolean result = false;
		UploaderEx service = mConnection.getService();
		if(service != null)
    		result = service.isUploading(app.getPackage());
		return result;
	}
	
	private int getProgress(App app){
		int result = 0;
		UploaderEx service = mConnection.getService();
		if(service != null)
    		result = service.getProgress(app.getPackage());
		return result;
	}

	private void checkStatus(final App app){
    	AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
			private String mErr = null;
			private JSONObject mRet = null;
			protected void onPreExecute() {
				getActivity().showDialog(0);
			}
			protected Long doInBackground(Void... params) {
				try {
					mRet = Utils.getAppStatus(getActivity(), app, false);
				} catch (Exception e) {
					mErr = e.getMessage();
				}
				return null;
			}
            protected void onPostExecute(Long result) {
            	getActivity().removeDialog(0);
		    	if(mRet != null){
					App cloudApp = new App(mRet);
					if(cloudApp.getVersionCode() >= app.getVersionCode()){
						AppHelper helper = new AppHelper(getActivity());
						cloudApp.mergeLocalApp(app);
						helper.updateOrAddApp(cloudApp);
						//updateStatus(mCloudApp.getCloudId());
//							notifyList();
						onCloudAction(getCloudActivity(), cloudApp.getJSON());
					}else{
						onNotUploaded(app);
					}
		    	}
		    	if(mErr != null) Utils.showToastLong(getActivity(), mErr);
            }
        };
        loadTask.execute();
    }
    
    protected void onNotUploaded(App app){
		Intent i = new Intent(getActivity(), UploadApp.class);
		i.putExtra(Const.KEY_APP, app.getJSON().toString());
		startActivityForResult(i,0);
    }
    
    protected void onCloudAction(CloudActivity activity, JSONObject json){
    	activity.openApp(json);
	}
	
	protected HashMap<String, Integer> getIndex(){
    	// create index at first time
    	if(mIndex == null){
    		mIndex = new HashMap<String, Integer>();
    		for(int i=0; i<mListData.length(); i++){
    			App app = new App(mListData.optJSONObject(i));
    			mIndex.put(app.getPackage(), i);
    		}
    	}
    	return mIndex;
	}
	
//    protected class UploadProgressBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
////    		Log.d(Const.APP_NAME, Const.APP_NAME + " ProgressBroadcastReceiver onReceive: " + intent.toString());
//        	if(mIsLoading) return;
//            if(intent.getAction().equals(Const.BROADCAST_UPLOAD_PROGRESS)){
//    			try {
//                	String strPackage = intent.getStringExtra(Const.KEY_PACKAGE);
//                	int index = getIndex().get(strPackage);
//    				JSONObject json = mListData.optJSONObject(index);
//            		int percent = intent.getIntExtra(Const.KEY_PERCENT, 0);
//            		long sizeSent = intent.getLongExtra(Const.KEY_SIZE_TRANSFERRED, 0);
//            		if(percent > 0 ){
//            			json.put(App.STATUS, App.STATUS_UPLOADING);
//            			json.put(Const.KEY_PERCENT, percent);
//            			json.put(Const.KEY_SIZE_TRANSFERRED, sizeSent);
//            			onDataChanged(index);
//            		}
////            		if(progressBar.getProgress()>=100) progressBar.setIndeterminate(true);
//            		String errMsg = intent.getStringExtra(Const.KEY_FAILED);
//            		if(errMsg != null){
////            			json.put(Const.KEY_FAILED, errMsg);
//            			json.put(Const.KEY_FAILED, getString(R.string.err_upload_failed));
//            			json.put(App.STATUS, 0);
//            			onDataChanged(index);
//            		}else{
//	            		boolean finished = intent.getBooleanExtra(Const.KEY_FINISHED, false);
//	            		if(finished){
////							json.put(App.IS_SHARED_BY_ME, true);
////	            			json.put(App.STATUS, 0);
////	            			onDataChanged(index);
//	            			refreshWithoutLoading();
//	            		}
//            		}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//            }
//        }
//    }

    public class RefreshAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_REFRESH_APP)){
            	refreshWithoutLoading();
            }
        }
    }

    public void onActivityResult(CloudActivity activity, int requestCode, int resultCode, Intent data) {
    	if(resultCode == Activity.RESULT_OK && data != null){
    		JSONObject json;
			try {
				json = new JSONObject(data.getStringExtra(Const.KEY_APP));
	    		onCloudAction(activity, json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    }

    @Override
	public long getCacheExpiresIn(){
		return 0; 
	}
}
