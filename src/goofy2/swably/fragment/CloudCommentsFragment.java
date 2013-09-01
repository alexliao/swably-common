package goofy2.swably.fragment;

import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.CommentsAdapter;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.ReviewProfile;
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

public abstract class CloudCommentsFragment extends CloudListFragment {
	protected ReviewDeletedBroadcastReceiver mReviewDeletedReceiver = new ReviewDeletedBroadcastReceiver();
	protected ReviewAddedBroadcastReceiver mReviewAddedReceiver = new ReviewAddedBroadcastReceiver();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    
//    //for top padding
//    @Override
//	protected View getListHeader() {
////		TextView tv = new TextView(this);
////		tv.setLayoutParams(new LayoutParams(100, 7));
////		return tv;
//      LayoutInflater inflater = LayoutInflater.from(this);
//      View v = inflater.inflate(R.layout.stream_margin, null);
//      return v;
//	}
	@Override
	protected void loadedMore(boolean succeeded){
//		if(mListData.length() > 0)
//			footerDivider.setVisibility(View.VISIBLE);
		super.loadedMore(succeeded);
	}	
	
    protected int getImageCount(JSONObject item) throws JSONException{
    	return 2;
    }
//	@Override
//	protected String getImageUrl(JSONObject item, int index) throws JSONException {
//		String ret = null;
//		if(index==0){
//			JSONObject user = item.getJSONObject("user");
//			String mask = user.optString("avatar_mask", "");
//			ret = mask.replace("[size]", "bi");
//		}else if(index==1){
//			App app = new App(item.optJSONObject("app"));
//			if(app.getJSON() != null) ret = app.getIcon();
//		}
//		
//		return ret;
//	}


	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		return new JSONArray(result);		
	}


	@Override
	protected CloudBaseAdapter getAdapter() {
		return new CommentsAdapter(getCloudActivity(), mListData, mLoadingImages);
	}

//	protected void onClickItem(int position) throws JSONException {
//    	JSONObject user = mListData.getJSONObject(position-1);
//		openUser(user);
//	}
	 
	protected void onClickItem(final int position) throws JSONException {
    	final JSONObject json = mListData.getJSONObject(position);
		Intent i = new Intent(getActivity(), ReviewProfile.class);
		i.putExtra(Const.KEY_REVIEW, json.toString());
		startActivity(i);
//		this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

//	protected void onClickItem(final int position) throws JSONException {
//    	final JSONObject review = mListData.getJSONObject(position);
//    	JSONObject user = review.getJSONObject("user");
//    	int menu;
//    	if(user.optString("id").equalsIgnoreCase(Utils.getCurrentUserId(this)))
//    		menu = R.array.review_menu_me;
//    	else
//    		menu = R.array.review_menu;
//		final String[] menuItems = getResources().getStringArray(menu);
//    		
//		Builder ad = new AlertDialog.Builder(this);
//		ad.setTitle(getString(R.string.options));
//		ad.setItems(menuItems, new DialogInterface.OnClickListener(){
//			public void onClick(DialogInterface dialog, int whichButton){
//				onPopupClick(position, whichButton, menuItems, review);				
//			}
//		});
//		ad.show();
//	
//	}

//	protected void onPopupClick(final int position, int whichButton, String[] menuItems, JSONObject review){
//		String name = menuItems[whichButton];
//    	App app = new App(review.optJSONObject("app"));
//    	final String reviewId = review.optString("id");
//		if(name.equals(getString(R.string.reply))){
//			//reply_dialog(updateId, mask).show();
//			Intent i = new Intent(getActivity(), PostReview.class);
//			i.putExtra(Const.KEY_REVIEW, review.toString());
//			startActivity(i);
//		}else if(name.equals(getString(R.string.send_out))){
//			getCloudActivity().share(review.optString("content"), app.getName(), app.getCloudId());
//		}else if(name.equals(getString(R.string.delete))){
//			getCloudActivity().confirm(getString(R.string.delete_review),  new DialogInterface.OnClickListener(){
//				public void onClick(DialogInterface dialog, int which)
//				{
//					if(getCloudActivity().deleteReviewInWeb(reviewId)){
//						mListData = JSONUtils.arrayDelete(mListData, position);
//						mAdapter.setData(mListData);
//						mAdapter.notifyDataSetChanged();
//					}
//				}       
//			});
//		}else if(name.equals(getString(R.string.cancel))){
//		}
//	}

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        mMenu = menu;
//        // Inflate the currently selected menu XML resource.
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.refresh, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	if(super.onOptionsItemSelected(item)) return true;
//        switch (item.getItemId()) {
//            case R.id.refresh:
//            	refresh();
//                return true;
//            default:
//                // Don't toast text when a submenu is clicked
//                if (!item.hasSubMenu()) {
//                    Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                break;
//        }
//        
//        return false;
//    }

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

//    @Override
//    protected void cacheItems(final JSONArray loaded) {
//		new Thread() {
//			public void run(){
//				// very slow, almost 3 second per item
//		    	Log.v(Const.APP_NAME, Const.APP_NAME + " cacheItems start: "+loaded.length());
//				try {
//			    	for(int i=0; i<loaded.length(); i++){
//						JSONObject item = loaded.getJSONObject(i);
//						cacheData(item.toString(), ReviewProfile.cacheId(item.optString("id")));	
//			    	}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//		    	Log.v(Const.APP_NAME, Const.APP_NAME + " cacheItems end");
//			}
//		}.start();
//	}

}
