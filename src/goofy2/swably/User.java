package goofy2.swably;

import java.util.List;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class User extends WithHeaderActivity {
	String mId = null;
	protected UserHeader header = new UserHeader(this);
	protected RefreshUserBroadcastReceiver mRefreshUserReceiver = new RefreshUserBroadcastReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        mId = getIdFromUrl(i);
    	if(mId != null){
    		JSONObject json = new JSONObject();
    		try {
				json.put("id", mId);
	    		String str = loadCache(User.cacheId(mId));
	    		if(str != null){
	    			i.putExtra(Const.KEY_USER, str);
	    		}else{
		    		i.putExtra(Const.KEY_USER, json.toString());
	    		}
			} catch (JSONException e) {
				e.printStackTrace();
			}
    		FeedsFragment.setAllRead(this); // in case user tap the notification of single feed
    	}
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
    	setContentView(R.layout.user);

        header.setUserFromIntent();
        header.setUserFromCache(header.getUserId());

        registerReceiver(mRefreshUserReceiver, new IntentFilter(Const.BROADCAST_REFRESH_USER));
        
    }
    private void loadList() {
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
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();

        if(header.getUser() != null){
        	loadList();
        }else if(mId != null){
    		loadUser(mId);
        }
        
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
//	    }else if (item.getItemId() == R.id.starredPosts) {
//				startActivity(new Intent(this, UserStarredPosts.class).putExtra(Const.KEY_USER, header.getUser().toString()));
//		    	return true;
//	    }else if (item.getItemId() == R.id.following) {
//				startActivity(new Intent(this, UserFollowing.class).putExtra(Const.KEY_USER, header.getUser().toString()));
//		    	return true;
//	    }else if (item.getItemId() == R.id.followers) {
//			startActivity(new Intent(this, UserFollowers.class).putExtra(Const.KEY_USER, header.getUser().toString()));
//	    	return true;
	    }else if (item.getItemId() == R.id.claimedApps) {
			startActivity(new Intent(this, UserClaimedApps.class).putExtra(Const.KEY_USER, header.getUser().toString()));
	    	return true;
		}else {
			return super.onOptionsItemSelected(item);
		}
	}

    private String getIdFromUrl(Intent intent){
    	String ret = null;
    	Uri data = intent.getData();
    	if(data != null){
	    	List<String> params = data.getPathSegments();
	    	//String action = params.get(0); // "u"
	    	if("http".equalsIgnoreCase(data.getScheme())) ret = params.get(1);
    	}
    	return ret;
    }
    
    static public String cacheId(String userId){
    	return User.class.getName()+userId;
    }

    private void loadUser(final String id){
    	AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
			private String mErr = null;
			private JSONObject mRet = null;
			protected void onPreExecute() {
				showLoading();
			}
			protected Long doInBackground(Void... params) {
				try {
					mRet = Utils.getUserInfo(User.this, id);
				} catch (Exception e) {
					mErr = e.getMessage();
				}
				return null;
			}
            protected void onPostExecute(Long result) {
		    	if(mRet != null){
		    		header.setUser(mRet);
		        	loadList();
		    		bind(); 
		    		cacheData(mRet.toString());
		    	}
		    	if(mErr != null){
		    		Utils.showToastLong(User.this, mErr);
		    	}
		    	hideLoading();
            }
        };
        loadTask.execute();
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
