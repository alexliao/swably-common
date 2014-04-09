package goofy2.swably.fragment;

import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.CommentsAdapter;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.ReviewProfileAdapter;
import goofy2.swably.ThreadCommentsAdapter;
import goofy2.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class ReviewProfileFragment extends PeopleReviewsFragment{
	JSONObject mReview;
	boolean needRefresh = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Bundle bundle = getArguments();
        String str = bundle.getString(Const.KEY_REVIEW);
        try {
        	mReview = new JSONObject(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	super.onCreate(savedInstanceState);
    }
	
	@Override
	protected View getRowBottom() {
		return LayoutInflater.from(a()).inflate(R.layout.thread_placeholder, null);
	}

//	@Override
//	protected void loadedMore(boolean successed) {
//    	super.loadedMore(successed);
//    	if(mListData.length() > 1){ // has thread
//    		mList.setSelectionFromTop(getCurrentReviewPosition(), 10);
//    	}
//    }

	@Override
    protected void loadMore(){
		// disable auto load
	}

	@Override
	protected void refreshListView() {
		super.refreshListView();
    	if(mListData.length() > 1){ // has thread
			((ListView) mList).setSelectionFromTop(getCurrentReviewPosition(), 20);
//			mList.setSelection(getCurrentReviewPosition());
		}
	}

	int getCurrentReviewPosition(){
    	int ret = 1;
    	for(int i=0; i<mListData.length(); i++){
    		if(mReview.optInt("id") == mListData.optJSONObject(i).optInt("id")){
    			ret = i+1;
    			break;
    		}
    	}
    	return ret;
    }
    
    @Override
	protected CloudBaseAdapter getAdapter() {
		return new ReviewProfileAdapter(getCloudActivity(), mListData, mLoadingImages, mReview);
	}
    
    @Override
	protected String getPageTitle() {
		return null;
	}

	@Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + getAPI() + "?format=json&count=100&"+getCloudActivity().getLoginParameters()+"&"+getCloudActivity().getClientParameters();
	}

	@Override
	protected String getAPI() {
		return "/comments/thread/" + mReview.optString("id");
	}

    @Override
    public String getCacheId(){
    	return cacheId(mReview.optString("id"));
    }

    static public String cacheId(String reviewId){
    	return ReviewProfileFragment.class.getName()+reviewId;
    }

	
	@Override
	public long getCacheExpiresIn(){
		return 600*1000;
	}
    
	@Override
	protected JSONArray addNewReviewToList(JSONArray list, JSONArray newReviews){
		return JSONUtils.appendArray(list, newReviews);
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

}
