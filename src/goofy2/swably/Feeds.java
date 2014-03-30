package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.fragment.Back_User.RefreshUserBroadcastReceiver;
import goofy2.swably.fragment.FeedsFragment;
import goofy2.swably.fragment.UserReviewsFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Feeds extends WithHeaderActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
    	setContentView(R.layout.feeds);
    	redirectAnonymous(true);
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        
    	JSONObject user = Utils.getCurrentUser(this);
    	
        Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_USER, user.toString());
		bundle.putInt("lastReadAt", FeedsFragment.getLastReadTime(this));
		
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();	
		FeedsFragment fragment = new FeedsFragment();
		fragment.setArguments(bundle);
		ft.add(R.id.fragment, fragment);
		ft.commit();
    }

}
