package goofy2.swably;


import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import goofy2.swably.LocalAppsFragment.RefreshAppBroadcastReceiver;
import goofy2.swably.data.App;

public abstract class CloudWithLocalAppsListActivity extends CloudGridActivity {
//	protected ProgressBroadcastReceiver mProgressReceiver = new ProgressBroadcastReceiver();
	protected RefreshAppBroadcastReceiver mRefreshAppProgressReceiver = new RefreshAppBroadcastReceiver();
	private HashMap<String, Integer> mIndex = null;
   	Cursor cursor;
	SQLiteDatabase db;

	//protected JSONArray mMyApps = new JSONArray();
	UploadingApp.UploaderExServiceConnection mConnection;
	
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
    	AppHelper helper = new AppHelper(this);
    	db = helper.getHelper().getReadableDatabase();
    	super.onPostCreate(savedInstanceState);

//        registerReceiver(mProgressReceiver, new IntentFilter(Const.BROADCAST_UPLOAD_PROGRESS));
        registerReceiver(mRefreshAppProgressReceiver, new IntentFilter(Const.BROADCAST_REFRESH_APP));
        Intent intent = new Intent(this, UploaderEx.class);
        mConnection = new UploadingApp.UploaderExServiceConnection();
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy(){
    	try{
    		if(cursor != null) cursor.close();
    		Utils.closeDB(db);
        	if(mConnection != null && mConnection.getService() != null) unbindService(mConnection);
//    		unregisterReceiver(mProgressReceiver);
    		unregisterReceiver(mRefreshAppProgressReceiver);
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	super.onDestroy();
    }
    
    @Override 
    public void onResume(){
    	super.onResume();
//    	if(mAdapter == null) return;
//		setStatus();
//		mAdapter.setData(mListData);
//		onDataChanged(-1);
    }

//    private void setStatus(){
//        UploaderEx service = mConnection.getService();
//        if(service != null){
//        	for(int i=0; i<mListData.length(); i++){
//				try {
//	        		JSONObject json = mListData.getJSONObject(i);
//	        		if(service.isUploading(App.PACKAGE))
//						json.put(App.STATUS, App.STATUS_UPLOADING);
//					else
//						json.put(App.STATUS, null);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//        	}
//        }
//    }
    
	@Override
	protected View getListHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        mMenu = menu;
//        // Inflate the currently selected menu XML resource.
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.refresh, menu);
//        //menu.add(Utils.getCurrentUserId(this)+Utils.getCurrentUser(this).optString("name"));
//        //setNoticeMenu();        		
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	switch (item.getItemId()) {
//            case R.id.refresh:
//            	refreshDb();
//                return true;
//            default:
//            	if(super.onOptionsItemSelected(item)) return true;
//                break;
//        }
//        
//        return false;
//    }
    

	@Override
	protected CharSequence getListTitle() {
		// TODO Auto-generated method stub
		return null;
	}

//    @Override
//	protected String getImageUrl(JSONObject item, int index) throws JSONException {
//		return item.getString(App.ICON);
//	}

	@Override
	protected CloudBaseAdapter getAdapter() {
		return new CloudWithLocalAppsAdapter(this, mListData, mLoadingImages, true);
	}

	@Override
    protected void setData(){
		((CloudWithLocalAppsAdapter) mAdapter).setData(cursor);
    }
	
	protected void onClickItem(int position) throws JSONException {
//		if(Utils.isCaching) return;
//    	JSONObject json = mListData.optJSONObject(position);
		JSONObject json = (JSONObject) ((CloudWithLocalAppsAdapter) mAdapter).getItem(position);
    	App app = new App(json);
//		if(app.getStatus() == App.STATUS_UPLOADING){
		if(isUploading(app)){
			Intent i = new Intent(CloudWithLocalAppsListActivity.this, UploadingApp.class);
			i.setData(Uri.parse(app.getPackage()));
			i.putExtra(Const.KEY_APP, json.toString());
			i.putExtra(Const.KEY_PERCENT, getProgress(app));
			startActivity(i);
		}else{
	        if(!app.isInCloud()){
	        	if(Utils.HttpTest(this)) checkStatus(app);
	        }else if(app.isLocalNew(this)){
				onNotUploaded(app);
	        }else{
	        	 onCloudAction(app.getJSON());
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
				showDialog(0);
			}
			protected Long doInBackground(Void... params) {
				try {
					mRet = Utils.getAppStatus(CloudWithLocalAppsListActivity.this, app, false);
				} catch (Exception e) {
					mErr = e.getMessage();
				}
				return null;
			}
            protected void onPostExecute(Long result) {
		    	removeDialog(0);
		    	if(mRet != null){
					App cloudApp = new App(mRet);
					if(cloudApp.getVersionCode() >= app.getVersionCode()){
						AppHelper helper = new AppHelper(CloudWithLocalAppsListActivity.this);
						cloudApp.mergeLocalApp(app);
						helper.updateOrAddApp(cloudApp);
						//updateStatus(mCloudApp.getCloudId());
//							notifyList();
						onCloudAction(cloudApp.getJSON());
					}else{
						onNotUploaded(app);
					}
		    	}
		    	if(mErr != null) Utils.showToastLong(CloudWithLocalAppsListActivity.this, mErr);
            }
        };
        loadTask.execute();
    }
    
    protected void onNotUploaded(App app){
		Intent i = new Intent(CloudWithLocalAppsListActivity.this, UploadApp.class);
		i.putExtra(Const.KEY_APP, app.getJSON().toString());
		startActivityForResult(i,0);
    }
    
    protected void onCloudAction(JSONObject json){
		openApp(json);
	}
//	protected void sendOutReview(JSONObject json){
//		try {
//	    	App app = new App(json);
//	    	if(app.getStatus() != App.STATUS_UPLOADING){
//	    		json.put(Const.KEY_FAILED, null);
//		    	json.put(App.STATUS, App.STATUS_UPLOADING);
//				json.put(Const.KEY_PERCENT, 0);
//				json.put(Const.KEY_SIZE_TRANSFERRED, 0);
//		    	Intent i = new Intent(CloudLocalAppsActivity.this, Uploader.class);
//				i.setData(Uri.parse(app.getPackage()));
//				startService(i);
//	    	}
//	    	mAdapter.notifyDataSetChanged();
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
	
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
	
//    protected class ProgressBroadcastReceiver extends BroadcastReceiver {
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
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//            }
//        }
//    }

//    private class OnClickListener_btnShare implements Button.OnClickListener {
//		@Override
//		public void onClick(View v) {
//			int count = 0;
//			for(int i=0; i<mListData.length(); i++){
//				JSONObject json = mListData.optJSONObject(i);
//				App app = new App(json);
//				if(!app.isSharedByMe() && json.optBoolean(Const.KEY_CHECKED, true)){
//					count ++;
//					share(json);
//					viewShare.setVisibility(View.GONE);
//					btnNext.setVisibility(View.VISIBLE);
//				}
//			}
//			if(count == 0)
//				Utils.showToast(CloudLocalAppsActivity.this, getString(R.string.not_select_app));
//		}
//		
//	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == RESULT_OK && data != null){
    		JSONObject json;
			try {
				json = new JSONObject(data.getStringExtra(Const.KEY_APP));
	    		onCloudAction(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    }

    protected class RefreshAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_REFRESH_APP)){
            	refreshWithoutLoading();
            }
        }
    }

}
