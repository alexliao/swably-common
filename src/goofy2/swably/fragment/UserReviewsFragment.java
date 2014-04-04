package goofy2.swably.fragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.CommentsAdapter;
import goofy2.swably.Const;
import goofy2.swably.FollowBtn;
import goofy2.swably.R;
import goofy2.swably.UserHeader;
import goofy2.swably.UserProfile;
import goofy2.swably.UserUploadedApps;
import goofy2.utils.AsyncImageLoader;
import goofy2.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class UserReviewsFragment extends CloudCommentsFragment {
	protected UserHeader header;
	protected FollowBtn followBtn;
//	protected FollowBar followBar = new FollowBar(this, header);
	protected String mUserCacheId;
	ExecutorService mLoadImageThreadPool = Executors.newFixedThreadPool(3); // 3 is the count of recent uploadees

    @Override
	public void onCreate(Bundle savedInstanceState) {
    	header = new UserHeader(ca());
        header.setUserFromBundle(getArguments());
		super.onCreate(savedInstanceState);
		mUserCacheId = UserProfile.cacheId(header.getUserId());
		String str = ca().loadCache(mUserCacheId);
		if(str != null){
			try {
				header.setUser(new JSONObject(str));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
		followBtn = new FollowBtn(ca(), header, v);
        followBtn.init(null);
        bind(v);
        return v;
    }
    
    protected void bind(View v){
    	if(v == null) return; 
		header.bindUserHeader(v, false);
//		followBar.bind();
		followBtn.bind();
		
		View viewUploadees = v.findViewById(R.id.viewUploadees);
		viewUploadees.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(a(), UserUploadedApps.class).putExtra(Const.KEY_USER, header.getUser().toString()));
			}
		});
		
		JSONArray uploadees = header.getUser().optJSONArray("recent_uploadees");
		if(uploadees != null){
			for(int i=0; i<uploadees.length(); i++){
				JSONObject app = uploadees.optJSONObject(i);
//				Log.d("", "uploadee: " + "uploadee"+(i+1));
				ImageView iv = (ImageView) v.findViewWithTag("uploadee"+(i+1));
				if(iv != null){
//					Log.d("", "uploadee tag: " + iv.getTag());
					iv.setVisibility(View.VISIBLE);
					if(!app.isNull("icon")){
						String url = app.optString("icon", "");
						new AsyncImageLoader(a(), iv, 0).setThreadPool(mLoadImageThreadPool).loadUrl(url);
					}
				}
			}
		}
		
	}
	
	@Override
	protected CloudBaseAdapter getAdapter() {
//		return new CommentsAdapter(ca(), mListData, mLoadingImages, false, false);
		return new CommentsAdapter(ca(), mListData, mLoadingImages);
	}

	@Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + "/users/reviews/" + header.getUserId() + "?format=json&"+ca().getLoginParameters()+"&"+ca().getClientParameters();
	}

	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		JSONArray ret = (new JSONObject(result)).getJSONArray("reviews"); 
		return ret;		
	}

	@Override
	protected void loadedMore(boolean succeeded){
		super.loadedMore(succeeded);
		try {
			if(mData != null){
				// cache user for following status
				JSONObject user = new JSONObject(mData).optJSONObject("user");
				header.setUser(user);
				ca().cacheData(user.toString(), mUserCacheId);
				bind(getView());
				Intent i = new Intent(Const.BROADCAST_REFRESH_USER);
				i.putExtra(Const.KEY_ID, header.getUserId());
				a().sendBroadcast(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	

	@Override
	protected void onClickHeader() {
	}

	@Override
	protected String getIdName(){
		return "id";
	}

    @Override
    public String getCacheId(){
    	return this.getClass().getName()+header.getUserId();
    }

    @Override
	protected void onDataChanged(int item){
    	super.onDataChanged(item);
		bind(getView());
	}

	@Override
	protected View getListHeader() {
        LayoutInflater inflater = LayoutInflater.from(a());
        View v = inflater.inflate(R.layout.user_header2, null);
		v.setFocusable(true);
		return v;
	}

}
