package goofy2.swably.fragment;

import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.CommentsAdapter;
import goofy2.swably.Const;
import goofy2.swably.FeedsAdapter;
import goofy2.swably.R;
import goofy2.swably.ReviewProfile;
import goofy2.swably.User;
import goofy2.swably.UserHeader;
import goofy2.swably.LocalApps.CacheProgressBroadcastReceiver;
import goofy2.swably.data.App;
import goofy2.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FeedsFragment extends CloudListFragment {
	protected UserHeader header;
	protected ReviewDeletedBroadcastReceiver mReviewDeletedReceiver = new ReviewDeletedBroadcastReceiver();
	protected ReviewAddedBroadcastReceiver mReviewAddedReceiver = new ReviewAddedBroadcastReceiver();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	header = new UserHeader(ca());
        header.setUserFromBundle(getArguments());
    	super.onCreate(savedInstanceState);
    	getActivity().registerReceiver(mReviewDeletedReceiver, new IntentFilter(Const.BROADCAST_REVIEW_DELETED));
    	getActivity().registerReceiver(mReviewAddedReceiver, new IntentFilter(Const.BROADCAST_REVIEW_ADDED));
   }
    @Override
    public void onDestroy(){
    	getActivity().unregisterReceiver(mReviewAddedReceiver);
    	getActivity().unregisterReceiver(mReviewDeletedReceiver);
    	super.onDestroy();
    }
    
	@Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + "/feeds/list/" + header.getUserId() + "?format=json&"+ca().getLoginParameters()+"&"+ca().getClientParameters();
	}

	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		JSONArray ret = (new JSONObject(result)).getJSONArray("feeds"); 
		return ret;		
	}

	@Override
	protected CloudBaseAdapter getAdapter() {
		return new FeedsAdapter(getCloudActivity(), mListData, mLoadingImages);
	}

	protected void onClickItem(final int position) throws JSONException {
    	final JSONObject json = mListData.getJSONObject(position);
    	String type = json.optString("object_type");
    	String id = json.optString("object_id");
    	
    	String http = "http://"+a().getString(R.string.host1);
    	if(type.equalsIgnoreCase("review")){
    		Intent i = new Intent(getActivity(), ReviewProfile.class);
    		i.setData(Uri.parse(http+"/r/"+id));
    		startActivity(i);
    	}else if(type.equalsIgnoreCase("app")){
    		Intent i = new Intent(getActivity(), App.class);
    		i.setData(Uri.parse(http+"/a/"+id));
    		startActivity(i);
    	}else if(type.equalsIgnoreCase("user")){
    		Intent i = new Intent(getActivity(), User.class);
    		i.setData(Uri.parse(http+"/u/"+id));
    		startActivity(i);
    	}
	}

	public class ReviewDeletedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_REVIEW_DELETED)){
            	String id = intent.getStringExtra(Const.KEY_ID);
            	for(int i=0; i<mListData.length(); i++){
            		if(mListData.optJSONObject(i).optString("id").equals(id)){
            			mListData = JSONUtils.arrayDelete(mListData, i);
        				mAdapter.setData(mListData);
        				mAdapter.notifyDataSetChanged();
						clearCache();
            			break;
            		}
            	}
            }
        }
    }
    
    public class ReviewAddedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_REVIEW_ADDED)){
            	JSONObject review;
				try {
					review = new JSONObject(intent.getStringExtra(Const.KEY_REVIEW));
	            	JSONArray arr = new JSONArray();
	            	arr.put(review);
	            	mListData = addNewReviewToList(mListData, arr);
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


	protected JSONArray addNewReviewToList(JSONArray list, JSONArray newReviews){
		return JSONUtils.appendArray(newReviews, list);
	}

}
