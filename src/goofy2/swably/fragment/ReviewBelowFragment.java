package goofy2.swably.fragment;

import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.CommentsAdapter;
import goofy2.swably.Const;
import goofy2.swably.ThreadCommentsAdapter;

import org.json.JSONObject;

import android.content.IntentFilter;
import android.os.Bundle;

public class ReviewBelowFragment extends PeopleReviewsFragment{
	JSONObject mReview;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	Bundle bundle = getArguments();
        String str = bundle.getString(Const.KEY_REVIEW);
        if(str != null){
	        try {
	        	mReview = new JSONObject(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
   }
	
	@Override
	protected CloudBaseAdapter getAdapter() {
		return new ThreadCommentsAdapter(getCloudActivity(), mListData, mLoadingImages);
	}

    @Override
	protected String getPageTitle() {
		return null;
	}

	@Override
	protected String getAPI() {
		return "/comments/below/" + mReview.optString("id");
	}

    @Override
    public String getCacheId(){
    	return cacheId(mReview.optString("id"));
    }

    static public String cacheId(String reviewId){
    	return ReviewBelowFragment.class.getName()+reviewId;
    }

	
	@Override
	public long getCacheExpiresIn(){
		return 60*1000;
	}
    
}
