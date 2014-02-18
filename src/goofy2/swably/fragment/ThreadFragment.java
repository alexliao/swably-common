package goofy2.swably.fragment;

import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.CommentsAdapter;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.ThreadCommentsAdapter;

import org.json.JSONObject;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

public abstract class ThreadFragment extends PeopleReviewsFragment{
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
	
	protected void setContent() {
		setContentView(R.layout.height_list_fragment);
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
	protected String getUrl() {
		return super.getUrl() + "&count=10";
	}

	@Override
	protected void refresh() {
		loadingMore.setVisibility(View.VISIBLE);
		refreshWithoutLoading();
	}

}
