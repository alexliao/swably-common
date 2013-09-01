package goofy2.swably.fragment;

import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.Utils;
import goofy2.swably.R.string;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;


public class MyFollowingReviewsFragment extends PeopleReviewsFragment{
    
//	@Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//		View btnMenu = findViewById(R.id.btnMenu);
//		btnMenu.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(MyFollowingReviews.this, MainMenu.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			}
//		});
//		View btnAdd = findViewById(R.id.btnAdd);
//		btnAdd.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(MyFollowingReviews.this, SelectLocalAppToReview.class));
//			}
//		});
//	}
	
    @Override
    public void onResume(){
    	Log.d("", Const.APP_NAME + " test");
    	super.onResume();
        if(getCloudActivity().redirectAnonymous()) return;
    }

//	@Override
//    public void onStart(){
//    	super.onStart();
//        if(redirectAnonymous()) return;
//    }
    
    @Override
	protected void loadedMore(boolean succeeded){
		super.loadedMore(succeeded);
		if(succeeded)
			Utils.setUnreadReviewsCount(getActivity(), 0);
	}	

	@Override
	protected String getPageTitle() {
		return getString(R.string.following_reviews);
	}

	@Override
	protected String getAPI() {
		return "/comments/my_following";
	}

}
