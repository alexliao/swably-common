package goofy2.swably.fragment;

import goofy2.swably.AppAbout;
import goofy2.swably.AppActionHelper;
import goofy2.swably.AppHeader;
import goofy2.swably.AppHelper;
import goofy2.swably.AppHistoryAdapter;
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


public class AppHistoryFragment extends CloudListFragment {
//	OnRefreshListener mCallback;
	
	protected AppHeader header;
//	protected LikeBar likeBar = new LikeBar(this, header);
	protected String mAppCacheId;
//	protected HoverBar hoverBar = new HoverBar();
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	header = new AppHeader(ca());
        header.setAppFromBundle(getArguments());
		super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        bind(v);
        return v;
    }

    @Override
    public void onDestroy(){
    	super.onDestroy();
    }

    protected void bind(View v){
    	if(v == null) return; 
		header.bindAppHeader(v);
		TextView tv = (TextView)v.findViewById(R.id.txtReviewsCount);
		if(tv != null){
			int count = header.getApp().getUploadsCount();
			tv.setText(String.format(a().getString(R.string.uploads_count), count));
			tv.setTypeface(ca().mLightFont);
		}
		
    }
	
    protected void setContent(){
	    setContentView(R.layout.list_fragment);
    }

	@Override
	protected CloudBaseAdapter getAdapter() {
		return new AppHistoryAdapter(ca(), mListData, mLoadingImages);
	}

	@Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + "/apps/uploaders/" + header.getApp().getCloudId() + "?format=json&"+ca().getLoginParameters()+"&"+ca().getClientParameters();
	}

	@Override
	protected String getIdName(){
		return "share_id";
	}

	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		return new JSONArray(result);		
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	

	@Override
	protected View getListHeader() {
        LayoutInflater inflater = LayoutInflater.from(a());
        View v = inflater.inflate(R.layout.app_info2, null);
		return v;
	}


    @Override
    public String getCacheId(){
    	return cacheId(header.getAppId());
    }

    static public String cacheId(String appId){
    	return AppHistoryFragment.class.getName()+appId;
    }

    @Override
	protected void onDataChanged(int item){
    	super.onDataChanged(item);
		bind(getView());
	}

    @Override
    public void onStart (){
    	super.onStart();
    }

	@Override
	protected void onClickItem(int position) throws JSONException {
		// TODO Auto-generated method stub
		
	}

}
