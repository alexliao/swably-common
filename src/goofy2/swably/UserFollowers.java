package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.fragment.User.RefreshUserBroadcastReceiver;
import goofy2.swably.fragment.UserFollowersFragment;
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

public class UserFollowers extends WithHeaderActivity {
	protected UserHeader header = new UserHeader(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
    	setContentView(R.layout.user_followers);

        header.setUserFromIntent();
        header.setUserFromCache(header.getUserId());
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();

        Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_USER, header.getUser().toString());
		
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();	
		UserFollowersFragment fragment = new UserFollowersFragment();
		fragment.setArguments(bundle);
		ft.add(R.id.fragment, fragment);
		ft.commit();
    }

    public void bind() {
//		TextView tv = (TextView)findViewById(R.id.txtTitle);
//		tv.setText(header.getUser().optString("name"));
    }

}
