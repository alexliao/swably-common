package goofy2.swably.fragment;

import goofy2.swably.AppHeader;
import goofy2.swably.AppHistoryAdapter;
import goofy2.swably.AppProfile;
import goofy2.swably.AppTagsAdapter;
import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.data.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AppTagsFragment extends CloudListFragment {
	protected AppHeader header;
	protected String mAppCacheId;
	boolean needRefresh = false;
	
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
//		header.bindAppHeader(v);
//		TextView tv = (TextView)v.findViewById(R.id.txtReviewsCount);
//		if(tv != null){
//			int count = header.getApp().getUploadsCount();
//			tv.setText(String.format(a().getString(R.string.uploads_count), count));
//			tv.setTypeface(ca().mLightFont);
//		}
		
    }
	
    protected void setContent(){
	    setContentView(R.layout.list_fragment);
    }

	@Override
	protected CloudBaseAdapter getAdapter() {
		return new AppTagsAdapter(ca(), mListData, mLoadingImages);
	}

	@Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + "/apps/tags/" + header.getApp().getCloudId() + "?format=json&"+ca().getLoginParameters()+"&"+ca().getClientParameters();
	}

	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		return new JSONArray(result);		
	}


//	@Override
//	protected void loadedMore(boolean succeeded){
//		super.loadedMore(succeeded);
//		try {
//			if(mData != null){
//				App app = new App(new JSONObject(mData).optJSONObject("app"));
//				ca().cacheData(app.getJSON().toString(), AppProfile.cacheId(app.getCloudId()));
//				header.setApp(app);
//				bind(getView());
////				mCallback.onRefresh(app);
//				Intent i = new Intent(Const.BROADCAST_REFRESH_APP);
//				i.putExtra(Const.KEY_ID, app.getCloudId());
//				a().sendBroadcast(i);
//				
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}	

//	@Override
//	protected View getListHeader() {
//        LayoutInflater inflater = LayoutInflater.from(a());
//        View v = inflater.inflate(R.layout.app_info2, null);
//		return v;
//	}


    @Override
    public String getCacheId(){
    	return cacheId(header.getAppId());
    }

    static public String cacheId(String appId){
    	return AppTagsFragment.class.getName()+appId;
    }

    @Override
	protected void onDataChanged(int item) {
    	needRefresh = true;
	}

    @Override
	public void onStart() {
    	super.onStart();
    	if(needRefresh) this.refresh();
    	needRefresh = false;
    }

	@Override
	protected void onClickItem(int position) throws JSONException {
		// TODO Auto-generated method stub
		
	}

}
