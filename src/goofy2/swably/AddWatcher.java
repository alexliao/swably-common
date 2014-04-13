package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.fragment.Back_User.RefreshUserBroadcastReceiver;
import goofy2.swably.fragment.MyMentionedFriendsFragment;
import goofy2.swably.fragment.UserFollowingFragment;
import goofy2.swably.fragment.UserLikedAppsFragment;
import goofy2.swably.fragment.UserReviewsFragment;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
    	disableSliding();
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
        
		final EditText editQuery = (EditText) findViewById(R.id.editQuery);
		editQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					final String query = editQuery.getText().toString().trim();
					if(query.length() > 0){
						Intent i = new Intent(AddWatcher.this, SearchWatcher.class);
						i.putExtra(Const.KEY_REVIEW, mReview.toString());
						i.putExtra(SearchManager.QUERY, query);
						startActivity(i);
					}
					return true;
				}
				return false;
			}
		});
		
//		View btnSearch = findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				EditText editQuery = (EditText) findViewById(R.id.editQuery);
//				final String query = editQuery.getText().toString().trim();
//				if(query.length() > 0){
//					Intent i = new Intent(AddWatcher.this, SearchWatcher.class);
//					i.putExtra(Const.KEY_REVIEW, mReview.toString());
//					i.putExtra(SearchManager.QUERY, query);
//					startActivity(i);
//				}else{
//					editQuery.requestFocus();
//				}
//			}
//		});

        View btnDone = findViewById(R.id.btnDone);
    	btnDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    }

}
