package goofy2.swably.fragment;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import goofy2.swably.Const;
import goofy2.swably.Utils;
import goofy2.swably.data.App;
import goofy2.swably.fragment.CloudCommentsFragment.ReviewAddedBroadcastReceiver;
import goofy2.swably.fragment.CloudCommentsFragment.ReviewDeletedBroadcastReceiver;
import goofy2.utils.JSONUtils;

public class AppLikedByUsersFragment extends AppUsersFragment {
	protected StarDeletedBroadcastReceiver mStarDeletedReceiver = new StarDeletedBroadcastReceiver();
	protected StarAddedBroadcastReceiver mStarAddedReceiver = new StarAddedBroadcastReceiver();

	@Override
	protected String getAPI() {
		return "/apps/liked_by_users/";
	}

	@Override
	protected String getIdName(){
		return "like_id";
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	a().registerReceiver(mStarDeletedReceiver, new IntentFilter(Const.BROADCAST_STAR_DELETED));
    	a().registerReceiver(mStarAddedReceiver, new IntentFilter(Const.BROADCAST_STAR_ADDED));
   }
    @Override
    public void onDestroy(){
    	a().unregisterReceiver(mStarAddedReceiver);
    	a().unregisterReceiver(mStarDeletedReceiver);
    	super.onDestroy();
    }

    protected class StarAddedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_STAR_ADDED)){
				String appId = intent.getStringExtra(Const.KEY_ID);
				if(appId.equals(header.getAppId())){
	            	JSONObject user = Utils.getCurrentUser(context);
	            	JSONArray arr = new JSONArray();
	            	arr.put(user);
	            	mListData = JSONUtils.appendArray(arr, mListData);
					mAdapter.setData(mListData);
					onDataChanged(-1);
					clearCache();
				}
            }
        }
    }

    protected class StarDeletedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_STAR_DELETED)){
				String appId = intent.getStringExtra(Const.KEY_ID);
				if(appId.equals(header.getAppId())){
	            	String id = Utils.getCurrentUserId(context);
	            	for(int i=0; i<mListData.length(); i++){
	            		if(mListData.optJSONObject(i).optString("id").equals(id)){
	            			mListData = JSONUtils.arrayDelete(mListData, i);
	        				mAdapter.setData(mListData);
//		        				mAdapter.notifyDataSetChanged();
							onDataChanged(-1);
							clearCache();
	            			break;
	            		}
	            	}
				}
			}
        }
    }

}
