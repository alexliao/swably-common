package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.fragment.Back_User.RefreshUserBroadcastReceiver;
import goofy2.swably.fragment.MyMentionedFriendsFragment;
import goofy2.swably.fragment.UserFollowingFragment;
import goofy2.swably.fragment.UserLikedAppsFragment;
import goofy2.swably.fragment.UserReviewsFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AddWatcher extends WithHeaderActivity {
	protected UserHeader header = new UserHeader(this);
	JSONObject mReview;
	static final int REQUEST_CODE = 8385;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	JSONObject user = Utils.getCurrentUser(this);
		Intent i = getIntent();
		i.putExtra(Const.KEY_USER, user.toString());
		try {
			mReview = new JSONObject(i.getStringExtra(Const.KEY_REVIEW));
		} catch (JSONException e) {
			e.printStackTrace();
		}
        super.onCreate(savedInstanceState);
//    	enableSlidingMenu();
    	setContentView(R.layout.add_watcher);

        header.setUserFromIntent();
        header.setUserFromCache(header.getUserId());
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();

        Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_USER, header.getUser().toString());
		bundle.putString(Const.KEY_REVIEW, mReview.toString());
		
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();	
		MyMentionedFriendsFragment fragment = new MyMentionedFriendsFragment();
		fragment.setArguments(bundle);
		ft.add(R.id.fragment, fragment);
		ft.commit();
    }

    public void bind() {
//		TextView tv = (TextView)findViewById(R.id.txtTitle);
//		tv.setText(header.getUser().optString("name"));
        
    	View btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(AddWatcher.this, SearchWatcher.class));
			}
		});

        View btnDone = findViewById(R.id.btnDone);
    	btnDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    }

}
