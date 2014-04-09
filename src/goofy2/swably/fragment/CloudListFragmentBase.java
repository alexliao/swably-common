package goofy2.swably.fragment;

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

import goofy2.swably.CloudActivity;
import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.CloudInplaceActionsAdapter;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.Utils;

import android.annotation.SuppressLint;
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
import android.widget.ResourceCursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import goofy2.swably.CloudActivity.OnClickListener_btnSnap;
import goofy2.swably.R.id;
import goofy2.swably.R.layout;
import goofy2.swably.R.string;
import goofy2.swably.fragment.CloudFragment.ImageMessageBroadcastReceiver;
import goofy2.utils.JSONUtils;
import goofy2.utils.ParamRunnable;
import goofy2.utils.PullToRefreshListView;

public abstract class CloudListFragmentBase extends CloudFragment {
	private int mLayoutId;
	protected AbsListView mList = null;
	protected CloudBaseAdapter mAdapter;
	protected JSONArray mListData = new JSONArray();
	protected int mLastLoaded = -1;
	protected String mData;
	protected HashMap<String, Integer> mLoadingImages = new HashMap<String, Integer>();
	protected boolean mIsLoading = false;
	protected int mFirstVisibleItem = 0;
	protected int mVisibleItemCount = 1000;
	protected Back2TopBroadcastReceiver mBack2TopReceiver = new Back2TopBroadcastReceiver();
	
	ArrayList<AsyncTask<Void, Void, Long>> mLoadTasks = new ArrayList<AsyncTask<Void, Void, Long>>();   

    abstract protected AbsListView prepareList();
    abstract protected void setAdpater(AbsListView list, BaseAdapter adapter);
    abstract void loadedMore(boolean successed);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		if (savedInstanceState == null
//				|| (savedInstanceState != null && savedInstanceState
//						.getString("list") == null)) {
//			loadData();
//		}
        a().registerReceiver(mBack2TopReceiver, new IntentFilter(Const.BROADCAST_BACK2TOP));
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Utils.logV(this, "CloudFragment onCreateView: " + savedInstanceState);
		setContent(); // set mLayoutId in sub class
		View v = inflater.inflate(mLayoutId, container, false);

//		View lv = v.findViewById(R.id.list);
//		if (lv.getClass() == PullToRefreshListView.class) {
//			mListContainer = (PullToRefreshListView) lv;
//			mList = mListContainer.getRefreshableView();
//		} else
//			mList = (ListView) lv;
//		mListHeader = getListHeader();
//		if (mListHeader != null)
//			mList.addHeaderView(mListHeader);
//		View vt = getRowTop();
//		if (vt != null)
//			mList.addHeaderView(vt);
//		viewFooter = LayoutInflater.from(this.getActivity()).inflate(
//				R.layout.list_footer, null);
//		mList.addFooterView(viewFooter);
//		View vb = getRowBottom();
//		if (vb != null)
//			mList.addFooterView(vb);
//		loadingMore = (View) viewFooter.findViewById(R.id.loadingMore);
//		txtMore = (TextView) viewFooter.findViewById(R.id.txtMore);
//		txtNoMore = (TextView) viewFooter.findViewById(R.id.txtNoMore);
//		txtMore.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				loadMore();
//			}
//		});

//		mList.setOnScrollListener(new OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				onListScrollStateChanged(view, scrollState);
//			}
//
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				onListScroll(view, firstVisibleItem, visibleItemCount,
//						totalItemCount);
//			}
//		});

        mList = prepareList();
		mAdapter = getAdapter();
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

    	return v;
	}

    protected int getDataPosition(int listPosition){
    	return listPosition;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
		loadData();
    }
    
//    @Override
//    public void onResume(){
//    	super.onResume();
//    	this.refreshListView();
//    }
    
    @Override
    public void onStop(){
    	super.onStop();
		if(mAdapter instanceof CloudInplaceActionsAdapter) ((CloudInplaceActionsAdapter) mAdapter).mHelper.hideActionsAnim();
    }

    @Override
    public void onDestroy(){
    	for(AsyncTask<Void, Void, Long> task : mLoadTasks){
    		task.cancel(true);
    	}
        a().unregisterReceiver(mBack2TopReceiver);
        super.onDestroy();
    }
    
    protected void loadData(){
    	final Handler handler = new Handler();
		Thread t = new Thread() {
			public void run(){
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} // delay for sliding menu close animation
				final String strCache = loadCache();
				try {
					if (strCache != null) mListData = new JSONArray(strCache);
			    	handler.post(new Runnable(){
						@Override
						public void run() {
//							loadingMore.setVisibility(View.GONE);
							try {
								if (strCache != null) {
//									mAdapter.setData(mListData);
									setData();
									refreshListView();
	//								loadListImagesEx(mListData);
								}
								if (getUrl() != null){
									if(strCache == null || (System.currentTimeMillis() - getCacheAt()) > getCacheExpiresIn()) 
										refresh();
								}
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
			    	});
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
    	};
    	t.setPriority(Thread.MIN_PRIORITY);
		t.start();
    }

//    String mCache = null;
//	protected void loadListFromCache() {
//		AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
//			protected Long doInBackground(Void... params) {
//				Utils.logV(this, "loadListFromCache start");
//				mIsLoading = true;
//				mCache = loadCache();
//				if (mCache != null) {
//					try {
//						mListData = new JSONArray(mCache);
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				mIsLoading = false;
//				return null;
//			}
//
//			protected void onPostExecute(Long result) {
//				Utils.logV(this, "loadListFromCache end");
//				mAdapter.setData(mListData);
//				refreshListView();
//				loadListImagesEx(mListData);
//				if (getUrl() != null){
//					if(mCache == null || (System.currentTimeMillis() - getCacheAt()) > getCacheExpiresIn()) 
//						refresh();
//				}
//			}
//		};
//		loadTask.execute();
//	}
    
//	public void onListScrollStateChanged(AbsListView view, int scrollState) {
//		Log.v(Const.APP_NAME, Const.APP_NAME + " CloudListFragment onScrollStateChanged: "
//				+ scrollState);
//		if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {
//			mIsScrolling = true;
//		} else {
//			mIsScrolling = false;
//			if (mIsDirty)
//				refreshListView();
//			if (view.getCount() >= Const.LIST_SIZE
//					&& view.getLastVisiblePosition() > (view.getCount() - 2)) {
//				Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment try load");
//				loadMore();
//			}
//		}
//	}

	protected void onListScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.v(Const.APP_NAME, Const.APP_NAME + " CloudListFragment onListScroll: "
				+ firstVisibleItem);
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
	}

	public boolean isItemVisible(int position) {
		return position >= mFirstVisibleItem
				&& position <= mFirstVisibleItem + mVisibleItemCount;
	}

	protected void setContent() {
		setContentView(R.layout.list_fragment);
	}

	protected void setContentView(int layoutId) {
		mLayoutId = layoutId;
	}

//	protected void loadMore() {
//		String lastId = null;
//		if (txtNoMore.getVisibility() != View.VISIBLE
//				&& loadingMore.getVisibility() != View.VISIBLE) {
//			try {
//				if (mListData.length() > 0)
//					lastId = mListData.getJSONObject(mListData.length() - 1)
//							.getString(getIdName());
//				loadingMore.setVisibility(View.VISIBLE);
//				txtMore.setVisibility(View.GONE);
//				txtNoMore.setVisibility(View.GONE);
//				loadList(getUrl(), lastId);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//	}

//	protected void loadedMore(boolean successed) {
//		loadingMore.setVisibility(View.GONE);
//		txtMore.setVisibility(View.GONE);
//		txtNoMore.setVisibility(View.GONE);
//		if (successed) {
//			if (mLastLoaded == 0)
//				txtNoMore.setVisibility(View.VISIBLE);
//			else
//				txtNoMore.setVisibility(View.INVISIBLE);
//		} else {
//			txtMore.setVisibility(View.VISIBLE);
//		}
//	}

	protected void loadList(final String url, final String lastId) {
		AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
			private String mErr = null;

			protected Long doInBackground(Void... params) {
				Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment loadList start: " + url);
				mIsLoading = true;
				mErr = loadStream(url, lastId);
				mIsLoading = false;
				return null;
			}

			protected void onPostExecute(Long result) {
				Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment loadList end: " + url);
				if (ca() == null){
					Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment ca()==null");
					return;
				}
				ca().hideLoading();
//				if (mListContainer != null)
//					mListContainer.onRefreshComplete();
				if (mErr == null) {
					// mAdapter.notifyDataSetChanged();
					if (getCacheId() != null)
						cacheData(mListData.toString());
//					mAdapter.setData(mListData);
					setData();
					refreshListView();
					loadedMore(true);
					// FeedHelper.loadUpdateImages(CloudListFragment.this,
					// mListData, mLoadingImages);
					// loadListImages(mListData, mLoadingImages);
//					loadListImagesEx(mListData);

					// if(mList.getClass() == PullToRefreshListView.class){
					// ((PullToRefreshListView) mList).onRefreshComplete();
					// }
				} else {
					// Utils.alert(CloudListFragment.this, mErr);
					Utils.showToast(getActivity(),
							getString(R.string.err_load_failed));
					Log.d("Nappst",
							Const.APP_NAME + " CloudListFragment loadList error: " + mErr);
					loadedMore(false);
//					txtMore.setVisibility(View.VISIBLE);
				}
			}
		};
		mLoadTasks.add(loadTask);
		loadTask.execute();
	}

    protected void setData(){
		mAdapter.setData(mListData);
    }
    
	protected String loadStream(String url, String lastId) {
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment loadStream: " + lastId);
		if (lastId != null)
			url += "&max_id=" + lastId;
		String err = null;
		String strResult = null;
		try {
			HttpGet httpReq = new HttpGet(url);
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					Const.HTTP_TIMEOUT);
			httpReq.setParams(httpParameters);
			HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
			strResult = EntityUtils.toString(httpResp.getEntity());
			int code = httpResp.getStatusLine().getStatusCode();
			if (code == 200) {
				Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity loadStream ok: "
						+ lastId);
				mData = strResult;
				// mListData = JSONUtils.appendArray(mListData,
				// getListArray(strResult));
				JSONArray loaded = getListArray(strResult);
				cacheItems(loaded);
				mLastLoaded = loaded.length();
				if (lastId == null) {
					mLoadingImages.clear();
					mListData = loaded;
				} else {
					mListData = JSONUtils.appendArray(mListData, loaded);
				}
			} else {
				onHttpError(strResult, code);
			}
		} catch (Exception e) {
			// Utils.alertTitle(this, getString(R.string.err_no_network_title),
			// e.getMessage());
			err = e.getMessage();
			Log.e(Const.APP_NAME, Const.APP_NAME + " CloudListFragment loadStream err: " + err);
		}
		return err;
	}

	protected void cacheItems(JSONArray loaded) {
	}

	abstract protected String getUrl();

	protected String onHttpError(String strResult, int code)
			throws JSONException {
		JSONObject json = new JSONObject(strResult);
		String err = json.getString("error_message");
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment loadStream err: " + err);
		return err;
	}


//	public void loadListImagesEx(final JSONArray jsonList) {
//		final ExecutorService threadPool = Executors
//				.newFixedThreadPool(Const.MULITI_DOWNLOADING);
//		threadPool.execute(new Runnable() {
//			public void run() {
//				for (int i = 0; i < jsonList.length(); i++) {
//					JSONObject item;
//					String imageUrl = null;
//					try {
//						item = jsonList.getJSONObject(i);
//						for (int j = 0; j < getImageCount(item); j++) {
//							imageUrl = getImageUrl(item, j);
//							if (imageUrl != null) {
//								ParamRunnable pr = new ParamRunnable() {
//									public void run() {
//										// if(Utils.saveImageToFile(CloudListFragment.this,
//										// (String) param,
//										// Const.HTTP_TIMEOUT_LONG, null))
//										if (saveImage((String) param))
//											if(a() != null) a().sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
//									}
//								};
//								pr.param = imageUrl;
//								threadPool.execute(pr);
//							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//	}

	protected boolean saveImage(String imageUrl) {
		return Utils.saveImageToFile(getActivity(), imageUrl,
				Const.HTTP_TIMEOUT_LONG, null);
	}

	// //load images synch in order
	// public void loadListImages(final JSONArray jsonList, final
	// HashMap<String, Integer> loadingImages){
	// AsyncTask<Void, Integer, Long> loadTask = new AsyncTask<Void, Integer,
	// Long>() {
	// protected Long doInBackground(Void... params) {
	// long ret = 0;
	// for(int i=0; i < jsonList.length() ; i++){
	// JSONObject item;
	// String imageUrl = null;
	// try {
	// item = jsonList.getJSONObject(i);
	// imageUrl = getImageUrl(item);
	// if(FeedHelper.saveImageToFile(CloudListFragment.this, imageUrl,
	// Const.HTTP_TIMEOUT_LONG, loadingImages))
	// publishProgress(1);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// return ret;
	// }
	// protected void onProgressUpdate(Integer... progress) {
	// //if(adapter != null) adapter.notifyDataSetChanged();
	// sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
	// }
	// };
	// loadTask.execute();
	// }

	protected int getImageCount(JSONObject item) throws JSONException {
		return 1;
	}

//	protected abstract String getImageUrl(JSONObject item, int index)
//			throws JSONException;

	protected void refresh() {
		ca().showLoading();
		refreshWithoutLoading();
	}

	protected void refreshWithoutLoading() {
		if(mAdapter instanceof CloudInplaceActionsAdapter) ((CloudInplaceActionsAdapter) mAdapter).mHelper.hideActionsAnim();
		loadList(getUrl(), null);
//		mList.setSelection(0);
	}


	protected void refreshListView() {
		mAdapter.notifyDataSetChanged();
	}

	protected String getIdName() {
		return "id";
	}

	// protected void setFooterDivider(){
	// if(mListData.length() > 0)
	// footerDivider.setVisibility(View.VISIBLE);
	// }

	protected JSONArray getListArray(String result) throws JSONException {
		return new JSONArray(result);
	}

	protected void onClickHeader(){};

	abstract protected CloudBaseAdapter getAdapter();

	protected abstract void onClickItem(int position) throws JSONException;

	// protected abstract void onLongClickItem(int position) throws
	// JSONException;

	protected CharSequence getListTitle() {
		return null;
	}

	protected View getListHeader() {
		// LayoutInflater inflater = LayoutInflater.from(this);
		// View v = inflater.inflate(R.layout.page_title, null);
		// TextView tv = (TextView) v.findViewById(R.id.txtTitle);
		// tv.setText(getListTitle());
		// v.setFocusable(true);
		// return v;
		return null;
	}

	protected View getRowTop() {
		// return LayoutInflater.from(this).inflate(R.layout.list_top, null);
		return null;
	}

	protected View getRowBottom() {
		// return LayoutInflater.from(this).inflate(R.layout.list_bottom, null);
		return null;
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// if(super.onOptionsItemSelected(item)) return true;
	// switch (item.getItemId()) {
	// case R.id.refresh:
	// refresh();
	// return true;
	// default:
	// // Don't toast text when a submenu is clicked
	// if (!item.hasSubMenu()) {
	// Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
	// return true;
	// }
	// break;
	// }
	//
	// return false;
	// }

	// protected String loadCache(){
	// return Utils.getUserPrefString(this, getCacheId(), null);
	// }

	// @Override
	// protected void onSaveInstanceState (Bundle outState){
	// Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment onSaveInstanceState");
	// outState.putString("list", mListData.toString());
	// super.onSaveInstanceState(outState);
	// }
	//
	// @Override
	// protected void onRestoreInstanceState (Bundle savedInstanceState){
	// Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment onRestoreInstanceState");
	// super.onRestoreInstanceState(savedInstanceState);
	// String list = savedInstanceState.getString("list");
	// try {
	// mListData = new JSONArray(list);
	// mAdapter.setData(mListData);
	// mAdapter.notifyDataSetChanged();
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }

//	protected void hideRoundBorder() {
//		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment hideRoundBorder - lenth: "
//				+ mListData.length());
//		if (mListData.length() == 0) {
//			// mList.setVisibility(View.INVISIBLE);
//			View v = v.findViewById(R.id.listTop);
//			if (v != null)
//				v.setVisibility(View.GONE);
//			v = v.findViewById(R.id.listBottom);
//			if (v != null)
//				v.setVisibility(View.GONE);
//		} else {
//			// mList.setVisibility(View.VISIBLE);
//			View v = v.findViewById(R.id.listTop);
//			if (v != null)
//				v.setVisibility(View.VISIBLE);
//			v = v.findViewById(R.id.listBottom);
//			if (v != null)
//				v.setVisibility(View.VISIBLE);
//		}
//	}

//	protected void showList() {
//		if (mListContainer != null)
//			mListContainer.setVisibility(View.VISIBLE);
//		else
//			mList.setVisibility(View.VISIBLE);
//	}
//
//	protected void hideList() {
//		if (mListContainer != null)
//			mListContainer.setVisibility(View.GONE);
//		else
//			mList.setVisibility(View.GONE);
//	}

    protected class Back2TopBroadcastReceiver extends BroadcastReceiver {
    	//private Feeds mUI;

    	//public ImageMessageBroadcastReceiver(Feeds ui){
    	//	mUI = ui;
    	//}
        @SuppressLint("NewApi")
		@Override
        public void onReceive(Context context, Intent intent) {
        	if(mList == null) return;
//			if(Build.VERSION.SDK_INT >= 8)
//				mList.smoothScrollToPosition(0);
//			else
				mList.setSelection(0);
        }
    }

}