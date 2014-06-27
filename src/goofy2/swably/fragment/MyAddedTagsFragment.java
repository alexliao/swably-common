package goofy2.swably.fragment;

import goofy2.swably.AddedTagsAdapter;
import goofy2.swably.AppHeader;
import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.fragment.CloudCommentsFragment.ReviewAddedBroadcastReceiver;
import goofy2.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;


public class MyAddedTagsFragment extends CloudListFragment {
	protected AppHeader header;
	boolean needRefresh = false;
	protected TagAddedBroadcastReceiver mTagAddedReceiver = new TagAddedBroadcastReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	header = new AppHeader(ca());
        header.setAppFromBundle(getArguments());
		super.onCreate(savedInstanceState);
    	getActivity().registerReceiver(mTagAddedReceiver, new IntentFilter(Const.BROADCAST_TAG_ADDED));
    }

    
    @Override
    public void onDestroy(){
    	getActivity().unregisterReceiver(mTagAddedReceiver);
    	super.onDestroy();
    }

    @Override
    protected void loadMore(){
		// disable auto load
	}

    @Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + "/apps/my_added_tags/" + header.getApp().getCloudId() + ".json?" + ca().getLoginParameters();
	}

	@Override
	protected CloudBaseAdapter getAdapter() {
		return new AddedTagsAdapter(ca(), mListData, mLoadingImages, header.getApp());
	}

	@Override
	protected void onClickItem(final int position) throws JSONException {
	}

    @Override
    public String getCacheId(){
    	return cacheId(header.getAppId());
    }

    static public String cacheId(String appId){
    	return MyAddedTagsFragment.class.getName()+appId;
    }

//    @Override
//	protected void onDataChanged(int item) {
//    	needRefresh = true;
//	}
//
//    @Override
//	public void onStart() {
//    	super.onStart();
//    	if(needRefresh) this.refresh();
//    	needRefresh = false;
//    }

//	@Override
//	protected String getAPI() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//

    public class TagAddedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_TAG_ADDED)){
            	JSONObject tag;
				try {
					tag = new JSONObject(intent.getStringExtra(Const.KEY_TAG));
	            	JSONArray arr = new JSONArray();
	            	arr.put(tag);
	            	mListData = JSONUtils.appendArray(arr, mListData);
					mAdapter.setData(mListData);
//					mAdapter.notifyDataSetChanged();
					onDataChanged(-1);
					clearCache();
				} catch (JSONException e) {
					e.printStackTrace();
				}
            }
        }
    }

}
