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

public abstract class CloudListActivity extends CloudListActivityBase {
	protected PullToRefreshListView mListContainer = null;
	private View viewFooter;
//	protected View footerDivider;
	private View loadingMore;
	private TextView txtMore;
	private TextView txtNoMore;
	private View mListHeader;
	private boolean mIsScrolling = false;
	private boolean mIsDirty = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
	public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
		
        if(mListContainer != null) mListContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	refreshWithoutLoading();
            }
        });
    }
    
    protected AbsListView prepareList(){
    	ListView result;
//      mList=(ListView)findViewById(R.id.list);
        View lv = findViewById(R.id.list);
//        Log.d(Const.APP_NAME, Const.APP_NAME + " list class: " + v1.getClass().toString());
        if(lv.getClass() == PullToRefreshListView.class){
	        mListContainer = (PullToRefreshListView) lv;
	        result = mListContainer.getRefreshableView();
        }else
        	result = (ListView) lv;
        
        mListHeader = getListHeader();
        if(mListHeader != null) result.addHeaderView(mListHeader);
		View vt = getRowTop();
		if(vt != null) result.addHeaderView(vt);
		View vb = getRowBottom();
		if(vb != null) result.addFooterView(vb);
		viewFooter = LayoutInflater.from(this).inflate(R.layout.list_footer, null);
		result.addFooterView(viewFooter);
//		footerDivider = (View) viewFooter.findViewById(R.id.footerDivider);
		loadingMore = (View) viewFooter.findViewById(R.id.loadingMore);
        txtMore = (TextView) viewFooter.findViewById(R.id.txtMore);
        txtNoMore = (TextView) viewFooter.findViewById(R.id.txtNoMore);
        txtMore.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				loadMore();
			}
        });
        
        result.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				onListScrollStateChanged(view, scrollState);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				onListScroll(view, firstVisibleItem, visibleItemCount,  totalItemCount);
//				Log.v(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onScroll");
			}
		});
        
		return result;
    }

	protected void loadMore(){
		String lastId = null;
		if(txtNoMore.getVisibility() != View.VISIBLE && loadingMore.getVisibility() != View.VISIBLE){ 
			try {
				if(mListData.length() > 0)
					lastId = mListData.getJSONObject(mListData.length()-1).getString(getIdName());
				loadingMore.setVisibility(View.VISIBLE);
				txtMore.setVisibility(View.GONE);
				txtNoMore.setVisibility(View.GONE);
				loadList(getUrl(), lastId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    }

	protected void loadedMore(boolean successed){
    	if(mListContainer != null) mListContainer.onRefreshComplete();
		loadingMore.setVisibility(View.GONE);
		txtMore.setVisibility(View.GONE);
		txtNoMore.setVisibility(View.GONE);
		if(successed){
			if(mLastLoaded == 0) txtNoMore.setVisibility(View.VISIBLE);
			else txtNoMore.setVisibility(View.INVISIBLE);
		}else{
    		txtMore.setVisibility(View.VISIBLE);
		}
		if(mAdapter instanceof CloudInplaceActionsAdapter) ((CloudInplaceActionsAdapter) mAdapter).mHelper.hideActionsAnim();
    }
   
	@Override
	protected void refresh(){
		loadingMore.setVisibility(View.VISIBLE);
		super.refresh();
	}
	
	public void onListScrollStateChanged(AbsListView view, int scrollState) {
		Log.v(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onScrollStateChanged: " + scrollState);
		if(scrollState == OnScrollListener.SCROLL_STATE_FLING){ 
			mIsScrolling = true;
		}else{
			mIsScrolling = false;
			if(mIsDirty) refreshListView();
			if(view.getCount() >= getListSize() && view.getLastVisiblePosition() > (view.getCount()-2)){
				Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity try load");
				loadMore();
			}
		}
	}
	
	@Override
	protected void onDataChanged(int item){
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity onDataChanged item:"+item+" mIsScrolling:"+mIsScrolling+" isItemVisible:"+isItemVisible(item));
		mIsDirty = true;
		if(!mIsScrolling){
			if(item < 0 || isItemVisible(item))
				refreshListView();
		}
			
	}

	@Override
	protected void refreshListView(){
		super.refreshListView();
		mIsDirty = false;
	}

	@Override
    protected int getDataPosition(int listPosition){
    	return listPosition - ((ListView)mList).getHeaderViewsCount();
    }

	protected void setAdpater(AbsListView list, BaseAdapter adapter){
		((ListView)list).setAdapter(adapter);
	}
}