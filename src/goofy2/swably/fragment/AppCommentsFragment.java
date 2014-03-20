package goofy2.swably.fragment;

import goofy2.swably.AppAbout;
import goofy2.swably.AppActionHelper;
import goofy2.swably.AppHeader;
import goofy2.swably.AppHelper;
import goofy2.swably.AppHistory;
import goofy2.swably.AppProfile;
import goofy2.swably.AppUploaders;
import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.CommentsAdapter;
import goofy2.swably.Const;
import goofy2.swably.PostReview;
import goofy2.swably.R;
import goofy2.swably.Utils;
import goofy2.swably.CloudCommentsActivity.ReviewAddedBroadcastReceiver;
import goofy2.swably.R.id;
import goofy2.swably.R.layout;
import goofy2.swably.R.string;
import goofy2.swably.SnsFriendsFragment.OnInviteListener;
import goofy2.swably.data.App;
import goofy2.swably.fragment.AppAboutFragment.RefreshAppBroadcastReceiver;
import goofy2.utils.AsyncImageLoader;
import goofy2.utils.JSONUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class AppCommentsFragment extends CloudCommentsFragment {
//	OnRefreshListener mCallback;
	OnAboutListener mAboutListener;
	
	protected AppHeader header;
//	protected LikeBar likeBar = new LikeBar(this, header);
	protected String mAppCacheId;
//	protected HoverBar hoverBar = new HoverBar();
	protected RefreshAppBroadcastReceiver mRefreshAppReceiver = new RefreshAppBroadcastReceiver();
	protected AppActionHelper actionHelper;  
	ExecutorService mLoadImageThreadPool = Executors.newFixedThreadPool(3); // 3 is the count of recent uploaders
	
    // Container Activity must implement this interface
    public interface OnAboutListener {
        public void onAbout();
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
    	header = new AppHeader(ca());
        header.setAppFromBundle(getArguments());
		super.onCreate(savedInstanceState);
//		mAppCacheId = AppProfile.cacheId(header.getAppId());
//		String str = ca().loadCache(mAppCacheId);
//		if(str != null){
//			try {
//				header.setApp(new App(new JSONObject(str)));
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
		a().registerReceiver(mRefreshAppReceiver, new IntentFilter(Const.BROADCAST_REFRESH_APP));
		actionHelper = new AppActionHelper(ca(), header);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mAboutListener = (OnAboutListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAboutListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        bind(v);
        return v;
    }

    @Override
    public void onDestroy(){
		a().unregisterReceiver(mRefreshAppReceiver);
    	super.onDestroy();
    }

    protected void bind(View v){
    	if(v == null) return; 
		header.bindAppHeader(v);
		
		View viewUploaders = v.findViewById(R.id.viewUploaders);
		viewUploaders.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(a(), AppHistory.class).putExtra(Const.KEY_APP, header.getApp().getJSON().toString()));
			}
		});
		
		JSONArray uploaders = header.getApp().getJSON().optJSONArray("recent_uploaders");
		if(uploaders != null){
			for(int i=0; i<uploaders.length(); i++){
				JSONObject user = uploaders.optJSONObject(i);
//				Log.d("", "uploader: " + "uploader"+(i+1));
				ImageView iv = (ImageView) v.findViewWithTag("uploader"+(i+1));
				if(iv != null){
//					Log.d("", "uploader tag: " + iv.getTag());
					iv.setVisibility(View.VISIBLE);
					if(!user.isNull("avatar_mask")){
						String mask = user.optString("avatar_mask", "");
						String url = mask.replace("[size]", "sq");
	//					iv.setImageResource(R.drawable.noname);
						new AsyncImageLoader(a(), iv, 0).setThreadPool(mLoadImageThreadPool).loadUrl(url);
					}
				}
			}
		}

//		TextView tv = (TextView) v.findViewById(R.id.txtEmpty);
//		tv.setText(String.format(getString(R.string.no_review_for_app), header.getApp().getName()));

		View btnAdd = v.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(a(), PostReview.class);
				i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
				startActivity(i);
			}
		});
		
		actionHelper.init(v);
		actionHelper.bind();
    }
	
    protected void setContent(){
	    setContentView(R.layout.app_reviews_fragment);
    }

	@Override
	protected CloudBaseAdapter getAdapter() {
		return new CommentsAdapter(ca(), mListData, mLoadingImages, false, false);
	}

	@Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + "/apps/all_comments/" + header.getApp().getCloudId() + "?format=json&"+ca().getLoginParameters()+"&"+ca().getClientParameters();
	}

	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		JSONArray ret = (new JSONObject(result)).getJSONArray("reviews"); 
		return ret;		
	}

	@Override
	protected void onClickHeader() {
		mAboutListener.onAbout();
	}

	@Override
	protected void loadedMore(boolean succeeded){
		super.loadedMore(succeeded);
		try {
			if(mData != null){
				App app = new App(new JSONObject(mData).optJSONObject("app"));
				ca().cacheData(app.getJSON().toString(), AppProfile.cacheId(app.getCloudId()));
				header.setApp(app);
				bind(getView());
//				mCallback.onRefresh(app);
				Intent i = new Intent(Const.BROADCAST_REFRESH_APP);
				i.putExtra(Const.KEY_ID, app.getCloudId());
				a().sendBroadcast(i);
				
			}
			setViewStatus(getView(), mListData.length()==0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	

	@Override
	protected View getListHeader() {
        LayoutInflater inflater = LayoutInflater.from(a());
        View v = inflater.inflate(R.layout.app_header2, null);
//        v.setFocusable(true);
		return v;
	}

	@Override
	protected String getIdName(){
		return "id";
	}

    @Override
    public String getCacheId(){
    	return cacheId(header.getAppId());
    }

    static public String cacheId(String appId){
//    	if(app == null) return null; // in case opened from share link
    	return AppCommentsFragment.class.getName()+appId;
    }

    @Override
	protected void onDataChanged(int item){
    	super.onDataChanged(item);
		bind(getView());
		setViewStatus(getView(), mListData.length()==0);
	}

    protected void setViewStatus(View v, boolean isEmpty){
		View viewEmpty = v.findViewById(R.id.viewEmpty);
    	if(isEmpty){
//    		hideList();
    		viewEmpty.setVisibility(View.VISIBLE);
    	}else{
//    		showList();
    		viewEmpty.setVisibility(View.GONE);
    	}
    }

//    public interface OnRefreshListener {
//        public void onRefresh(App newApp);
//    }

    protected class RefreshAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_REFRESH_APP)){
            	String pkg = intent.getStringExtra(Const.KEY_PACKAGE);
            	if(pkg != null && pkg.equalsIgnoreCase(header.getApp().getPackage())){
        			AppHelper helper = new AppHelper(a());
        			goofy2.swably.data.App app = helper.getApp(pkg);
            		if(app != null) header.setApp(app);
            		bind(getView());
            	}else{
                	String id = intent.getStringExtra(Const.KEY_ID);
            		if(id != null && id.equals(header.getApp().getCloudId())){
            			String str = ca().loadCache(AppProfile.cacheId(id));
            			if(str != null){
            				try {
								header.setApp(new goofy2.swably.data.App(new JSONObject(str)));
								bind(getView());
							} catch (JSONException e) {
								e.printStackTrace();
							}
            			}
            		}
            	}
            }
        }
    }

    @Override
    public void onStart (){
		Utils.logV(this, "AppCommntsFragment onStart getViewBack(): " + getView().findViewById(R.id.viewBack).getMeasuredHeight());
    	super.onStart();
    }

}
