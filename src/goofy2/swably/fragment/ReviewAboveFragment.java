package goofy2.swably.fragment;

import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.CommentsAdapter;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.ThreadCommentsAdapter;

import org.json.JSONObject;

import android.content.IntentFilter;
import android.os.Bundle;

public class ReviewAboveFragment extends ThreadFragment{
	
	@Override
	protected String getAPI() {
		return "/comments/above/" + mReview.optString("id");
	}

    @Override
    public String getCacheId(){
    	return cacheId(mReview.optString("id"));
    }

    static public String cacheId(String reviewId){
    	return ReviewAboveFragment.class.getName()+reviewId;
    }

	
	@Override
	public long getCacheExpiresIn(){
		return 600*1000;
	}
    
}
