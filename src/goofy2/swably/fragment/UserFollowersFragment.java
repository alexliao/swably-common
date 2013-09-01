package goofy2.swably.fragment;

import goofy2.swably.Const;
import goofy2.swably.Utils;
import goofy2.utils.JSONUtils;

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


public class UserFollowersFragment extends UserUsersFragment {
	protected FollowDeletedBroadcastReceiver mFollowDeletedReceiver = new FollowDeletedBroadcastReceiver();
	protected FollowAddedBroadcastReceiver mFollowAddedReceiver = new FollowAddedBroadcastReceiver();

	@Override
	protected void loadedMore(boolean succeeded){
		super.loadedMore(succeeded);
		if(succeeded && header.isMe())
			Utils.setUnreadFollowsCount(a(), 0);
	}	

	@Override
	protected String getAPI() {
		return "/users/followers/";
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	a().registerReceiver(mFollowDeletedReceiver, new IntentFilter(Const.BROADCAST_FOLLOW_DELETED));
    	a().registerReceiver(mFollowAddedReceiver, new IntentFilter(Const.BROADCAST_FOLLOW_ADDED));
   }
    @Override
    public void onDestroy(){
    	a().unregisterReceiver(mFollowAddedReceiver);
    	a().unregisterReceiver(mFollowDeletedReceiver);
    	super.onDestroy();
    }

    protected class FollowAddedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_FOLLOW_ADDED)){
				String userId = intent.getStringExtra(Const.KEY_ID);
				if(userId.equals(header.getUserId())){
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

    protected class FollowDeletedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_FOLLOW_DELETED)){
				String userId = intent.getStringExtra(Const.KEY_ID);
				if(userId.equals(header.getUserId())){
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
