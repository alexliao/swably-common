package goofy2.swably.fragment;

import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.Const;
import goofy2.swably.MentionedFriendsAdapter;
import goofy2.swably.UsersAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class MyMentionedFriendsFragment extends UserUsersFragment {
	JSONObject mReview;

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
	protected String getAPI() {
		return "/users/mentioned_friends/";
	}

	@Override
	protected CloudBaseAdapter getAdapter() {
		return new MentionedFriendsAdapter(ca(), mListData, mLoadingImages, mReview);
	}

	@Override
	protected void onClickItem(final int position) throws JSONException {

    	JSONObject user = mListData.getJSONObject(position);
//		ca().openUser(user);
		
	}

}
