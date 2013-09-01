package goofy2.swably;

//import eu.erikw.PullToRefreshListView.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import goofy2.swably.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import goofy2.swably.CloudActivity.OnClickListener_btnSnap;
import goofy2.utils.JSONUtils;
import goofy2.utils.ParamRunnable;
import goofy2.utils.PullToRefreshListView;

public abstract class CloudListActivityBase extends WithHeaderActivity {
	protected AbsListView mList;
	protected CloudBaseAdapter mAdapter;
	protected JSONArray mListData = new JSONArray();
	protected int mLastLoaded = -1;
	protected String mData;
	protected HashMap<String, Integer> mLoadingImages = new HashMap<String, Integer>();
	protected boolean mIsLoading = false;
	protected int mFirstVisibleItem = 0;
	protected int mVisibleItemCount = 1000;
//	protected View mListHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onCreate");
        super.onCreate(savedInstanceState);
        setContent();
    }

    @Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
    
        mList = prepareList();
        
		//registerForContextMenu(mList); 
        
        
//        // Create a progress bar to display while the list loads
//        ProgressBar progressBar = new ProgressBar(this);
//        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
//                LayoutParams.WRAP_CONTENT));
//        progressBar.setIndeterminate(true);
//        progressBar.setVisibility(View.GONE);  
//        ((ViewGroup)mList.getParent()).addView(progressBar);  
//        mList.setEmptyView(progressBar);
        
		mAdapter = getAdapter();
		//showDialog(0);
		//loadList(getUrl());
		if(savedInstanceState == null || (savedInstanceState != null && savedInstanceState.getString("list") == null)){
			final String strCache = loadCache();
			if(strCache != null){
				try {
					mListData = new JSONArray(strCache);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				setData();
//				loadingMore.setVisibility(View.GONE);
				hideRoundBorder();
			}
			if (getUrl() != null){
				if(strCache == null || (System.currentTimeMillis() - getCacheAt()) > getCacheExpiresIn()) 
					refresh();
			}
		}

		setAdpater(mList, mAdapter);
		
		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
            @Override  
            public void onItemClick(AdapterView<?> arg0, final View arg1, int position, long arg3) {
				try {
					onClickItem(getDataPosition(position));
				} catch (Exception e) {
					e.printStackTrace();
				}
             }  
		});      

//		if(mList.getClass() == PullToRefreshListView.class){
//	        ((PullToRefreshListView) mList).setOnRefreshListener(new OnRefreshListener() {
//	            @Override
//	            public void onRefresh() {
//	                refresh();
//	            }
//	        });
//		}
		
//		mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {  
//            @Override  
//            public boolean onItemLongClick(AdapterView<?> arg0, final View arg1, final int position, long arg3) {
// 				try {
// 	            	onLongClickItem(position);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				return true;
//            }
//        });
	
//        View tv = (TextView) findViewById(R.id.txtTitle);
//        if(tv != null){
//        	tv.setOnClickListener(new OnClickListener(){
//				@Override
//				public void onClick(View arg0) {
//					mList.setSelection(0);
//				}
//		    });
//        }

//        AnimationSet set = new AnimationSet(true);
//
//        Animation animation = new AlphaAnimation(0.0f, 1.0f);
//        animation.setDuration(50);
//        set.addAnimation(animation);
//
//        animation = new TranslateAnimation(
//            Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f,
//            Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f
//        );
//        animation.setDuration(100);
//        set.addAnimation(animation);
//
//        LayoutAnimationController controller =
//                new LayoutAnimationController(set, 0.5f);
//        mList.setLayoutAnimation(controller);
    
    
    }

    abstract protected void setAdpater(AbsListView list, BaseAdapter adapter);
    
    abstract protected AbsListView prepareList();
    
    protected int getDataPosition(int listPosition){
    	return listPosition;
    }
    
	public long getCacheExpiresIn(){
		return Const.DEFAULT_CACHE_EXPIRES_IN;
	}

	protected int getListSize(){
		return Const.LIST_SIZE;
	}
	
    protected void onListScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.v(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onListScroll: " + firstVisibleItem);
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
	}

    public boolean isItemVisible(int position){
    	return position >= mFirstVisibleItem && position <= mFirstVisibleItem + mVisibleItemCount;
    }
    
	abstract protected void setContent();
    
    protected void loadList(final String url, final String lastId){
//		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity loadList: " + url);
    	Utils.logV(this, "loadList: "+url);
    	AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
			private String mErr = null;
			protected Long doInBackground(Void... params) {
				mIsLoading = true;
				mErr = loadStream(url, lastId);
				mIsLoading = false;
				return null;
			}
            protected void onPostExecute(Long result) {
		    	hideLoading();
		    	if(mErr == null){
		    		//mAdapter.notifyDataSetChanged();
		        	if(getCacheId() != null)
		        		cacheData(mListData.toString(), getCacheId());
					setData();
		    		onDataChanged(-1);
					loadedMore(true);
					//FeedHelper.loadUpdateImages(CloudListActivity.this, mListData, mLoadingImages);
					//loadListImages(mListData, mLoadingImages);
//					loadListImagesEx(mListData);
					
//					if(mList.getClass() == PullToRefreshListView.class){
//						((PullToRefreshListView) mList).onRefreshComplete();
//					}
		    	}
		    	else{
		    		//Utils.alert(CloudListActivity.this, mErr);
		    		Utils.showToastLong(CloudListActivityBase.this, getString(R.string.err_load_failed));
		    		Log.d("Nappst", Const.APP_NAME + " CloudListActivity loadList error: "+mErr);
					loadedMore(false);
		    	}
            }
        };
        loadTask.execute();
    }
    
    abstract void loadedMore(boolean successed);
    
    protected void setData(){
		mAdapter.setData(mListData);
    }
    
	protected String loadStream(String url, String lastId) {
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity loadStream: " + lastId);
    	if(lastId != null)	url += "&max_id=" + lastId;
		String err = null;
		String strResult = null;
		try{
			HttpGet httpReq = new HttpGet(url);
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT);
			httpReq.setParams(httpParameters);
			HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
			strResult = EntityUtils.toString(httpResp.getEntity());
			int code = httpResp.getStatusLine().getStatusCode(); 
			if( code == 200){
				Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity loadStream ok: " + lastId);
				mData = strResult;
				//mListData = JSONUtils.appendArray(mListData, getListArray(strResult));
				JSONArray loaded = getListArray(strResult);
				cacheItems(loaded);
				mLastLoaded = loaded.length();
		    	if(lastId == null){
		    		mLoadingImages.clear();
		    		mListData = loaded;
		    	}else{
					mListData = JSONUtils.appendArray(mListData, loaded);
		    	}
			}else{
				onHttpError(strResult, code);
			}
		}catch (Exception e){
//	    	Utils.alertTitle(this, getString(R.string.err_no_network_title), e.getMessage());
			err = e.getMessage();
			Log.e(Const.APP_NAME, Const.APP_NAME + " CloudListActivity loadStream err: " + err);
		}
		return err;
	}
    
    protected void cacheItems(JSONArray loaded) {
	}

	abstract protected String getUrl();
    
    protected String onHttpError(String strResult, int code) throws JSONException{
		JSONObject json = new JSONObject(strResult);
		String err = json.getString("error_message");
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity loadStream err: " + err);
		return err;
    }
    
    @Override
    public void onDestroy(){
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onDestroy");
    	super.onDestroy();
    }

    @Override 
    public void onStart(){
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onStart");
    	super.onStart();
    	//redirectAnonymous(null);    	
    }
    
    @Override 
    public void onResume(){
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onResume");
    	super.onResume();
    }
    @Override 
    public void onPause(){
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onPause");
    	super.onPause();
    }
    @Override 
    public void onStop(){
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onStop");
    	super.onStop();
		if(mAdapter instanceof CloudInplaceActionsAdapter) ((CloudInplaceActionsAdapter) mAdapter).mHelper.hideActionsAnim();
    }
    @Override 
    public void onRestart(){
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onRestart");
    	super.onRestart();
    }

//    public void loadListImagesEx(final JSONArray jsonList){
//    	final ExecutorService threadPool = Executors.newFixedThreadPool(Const.MULITI_DOWNLOADING);
//        threadPool.execute(new Runnable(){
//        	public void run() {
//				for(int i=0; i < jsonList.length() ; i++){
//					JSONObject item;
//					String imageUrl = null;
//					try {
//						item = jsonList.getJSONObject(i);
//						for(int j=0; j<getImageCount(item); j++){
//							imageUrl = getImageUrl(item, j);
//							if(imageUrl != null){
//								ParamRunnable pr = new ParamRunnable(){
//						        	public void run() {
//	//									if(Utils.saveImageToFile(CloudListActivity.this, (String) param, Const.HTTP_TIMEOUT_LONG, null))
//										if(saveImage((String) param))
//											sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
//						        	}
//								};
//								pr.param = imageUrl;
//								threadPool.execute(pr);
//							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//        	}
//        });  
//    }
    
//    protected boolean saveImage(String imageUrl){
//    	return Utils.saveImageToFile(CloudListActivity.this, imageUrl, Const.HTTP_TIMEOUT_LONG, null);    	
//    }
//	//load images synch in order
//    public void loadListImages(final JSONArray jsonList, final HashMap<String, Integer> loadingImages){
//		AsyncTask<Void, Integer, Long> loadTask = new AsyncTask<Void, Integer, Long>() {
//			protected Long doInBackground(Void... params) {
//				long ret = 0;
//				for(int i=0; i < jsonList.length() ; i++){
//					JSONObject item;
//					String imageUrl = null;
//					try {
//						item = jsonList.getJSONObject(i);
//						imageUrl = getImageUrl(item);
//						if(FeedHelper.saveImageToFile(CloudListActivity.this, imageUrl, Const.HTTP_TIMEOUT_LONG, loadingImages))
//							publishProgress(1);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				return ret;
//			}
//			protected void onProgressUpdate(Integer... progress) {
//            	//if(adapter != null) adapter.notifyDataSetChanged();
//				sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
//			}
//		};
//		loadTask.execute();
//	}
    
    protected int getImageCount(JSONObject item) throws JSONException{
    	return 1;
    }
//    protected abstract String getImageUrl(JSONObject item, int index) throws JSONException;

	protected void refresh(){
		showLoading();
		refreshWithoutLoading();
	}
	
	protected void refreshWithoutLoading(){
		loadList(getUrl(), null);
		if(mAdapter instanceof CloudInplaceActionsAdapter) ((CloudInplaceActionsAdapter) mAdapter).mHelper.hideActionsAnim();
//		mList.setSelection(0);
	}

	protected void refreshListView(){
		mAdapter.notifyDataSetChanged();
		hideRoundBorder();
		//Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity refreshListView");
	}
	
	protected String getIdName(){
		return "id";
	}

//	protected void setFooterDivider(){
//		if(mListData.length() > 0)
//			footerDivider.setVisibility(View.VISIBLE);
//	}

	protected JSONArray getListArray(String result) throws JSONException {
		return new JSONArray(result);		
	}

	protected void onClickHeader(){	};
	
	abstract protected CloudBaseAdapter getAdapter();

    protected abstract void onClickItem(int position) throws JSONException;

//    protected abstract void onLongClickItem(int position) throws JSONException;

	protected CharSequence getListTitle() {
		return null;
	}
	
	protected View getListHeader() {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View v = inflater.inflate(R.layout.page_title, null);
//		TextView tv = (TextView) v.findViewById(R.id.txtTitle);
//		tv.setText(getListTitle());
//		v.setFocusable(true);
//		return v;
		return null;
	}

	protected View getRowTop() {
//		return LayoutInflater.from(this).inflate(R.layout.list_top, null);
		return null;
	}
	protected View getRowBottom() {
//		return LayoutInflater.from(this).inflate(R.layout.list_bottom, null);
		return null;
	}
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	if(super.onOptionsItemSelected(item)) return true;
//        switch (item.getItemId()) {
//            case R.id.refresh:
//            	refresh();
//                return true;
//            default:
//                // Don't toast text when a submenu is clicked
//                if (!item.hasSubMenu()) {
//                    Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                break;
//        }
//        
//        return false;
//    }
    
    public String getCacheId(){
    	return this.getClass().getName();
    }
//    protected String loadCache(){
//    	return Utils.getUserPrefString(this, getCacheId(), null);
//    }
    
//    @Override
//    protected void onSaveInstanceState (Bundle outState){
//		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onSaveInstanceState");
//		outState.putString("list", mListData.toString());
//    	super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState (Bundle savedInstanceState){
//		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onRestoreInstanceState");
//    	super.onRestoreInstanceState(savedInstanceState);
//    	String list = savedInstanceState.getString("list");
//    	try {
//			mListData = new JSONArray(list);
//			mAdapter.setData(mListData);
//			mAdapter.notifyDataSetChanged();
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//    }
    
    protected void hideRoundBorder(){
//    	Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity hideRoundBorder - lenth: " + mListData.length());
//    	if(mListData.length() == 0){
////    		mList.setVisibility(View.INVISIBLE);
//    		View v = this.findViewById(R.id.listTop);
//    		if(v != null) v.setVisibility(View.GONE);
//    		v = this.findViewById(R.id.listBottom);
//    		if(v != null) v.setVisibility(View.GONE);
//    	}else{
////    		mList.setVisibility(View.VISIBLE);
//    		View v = this.findViewById(R.id.listTop);
//    		if(v != null) v.setVisibility(View.VISIBLE);
//    		v = this.findViewById(R.id.listBottom);
//    		if(v != null) v.setVisibility(View.VISIBLE);
//    	}
    }
    
//    protected void showList(){
//    	if(mListContainer != null) mListContainer.setVisibility(View.VISIBLE);
//    	else mList.setVisibility(View.VISIBLE);
//    }
//    protected void hideList(){
//    	if(mListContainer != null) mListContainer.setVisibility(View.GONE);
//    	else mList.setVisibility(View.GONE);
//    }
}