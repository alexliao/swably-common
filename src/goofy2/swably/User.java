package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.fragment.User.RefreshUserBroadcastReceiver;
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

public class User extends WithHeaderActivity {
	protected UserHeader header = new UserHeader(this);
	protected RefreshUserBroadcastReceiver mRefreshUserReceiver = new RefreshUserBroadcastReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
    	setContentView(R.layout.user);

        header.setUserFromIntent();
        header.setUserFromCache(header.getUserId());

        registerReceiver(mRefreshUserReceiver, new IntentFilter(Const.BROADCAST_REFRESH_USER));
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();

        Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_USER, header.getUser().toString());
		
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();	
		UserReviewsFragment fragment = new UserReviewsFragment();
		fragment.setArguments(bundle);
		ft.add(R.id.fragment, fragment);
		ft.commit();
    }

    @Override
    public void onDestroy(){
		unregisterReceiver(mRefreshUserReceiver);
    	super.onDestroy();
    }

    public void bind() {
//		TextView tv = (TextView)findViewById(R.id.txtTitle);
//		tv.setText(header.getUser().optString("name"));
    }

    @Override
    public int getMenu(){
    	return R.menu.user_reviews;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getMenu(), menu);

        JSONObject user = header.getUser();
        if(user.optInt("claims_count") <= 0)
        	menu.removeItem(R.id.claimedApps);

		return true;
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.starredApps) {
			startActivity(new Intent(this, UserStarredApps.class).putExtra(Const.KEY_USER, header.getUser().toString()));
	    	return true;
	    }else if (item.getItemId() == R.id.following) {
				startActivity(new Intent(this, UserFollowing.class).putExtra(Const.KEY_USER, header.getUser().toString()));
		    	return true;
	    }else if (item.getItemId() == R.id.followers) {
			startActivity(new Intent(this, UserFollowers.class).putExtra(Const.KEY_USER, header.getUser().toString()));
	    	return true;
	    }else if (item.getItemId() == R.id.claimedApps) {
			startActivity(new Intent(this, UserClaimedApps.class).putExtra(Const.KEY_USER, header.getUser().toString()));
	    	return true;
		}else {
			return super.onOptionsItemSelected(item);
		}
	}

    protected class RefreshUserBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Const.BROADCAST_REFRESH_USER)){
            	String id = intent.getStringExtra(Const.KEY_ID);
        		if(id != null && id.equals(header.getUserId())){
        			String str = loadCache(UserProfile.cacheId(id));
        			if(str != null){
        				try {
							header.setUser(new JSONObject(str));
							bind();
						} catch (JSONException e) {
							e.printStackTrace();
						}
        			}
        		}
        	}
        }
    }
}
