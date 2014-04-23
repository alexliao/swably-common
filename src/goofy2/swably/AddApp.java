package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.LocalApps.CacheProgressBroadcastReceiver;
import goofy2.swably.fragment.LocalAppsFragment;
import goofy2.swably.fragment.MyMentionedFriendsFragment;
import goofy2.swably.fragment.OldLocalAppsFragment;
import goofy2.swably.fragment.ShuffleAppsFragment;
import goofy2.swably.fragment.SystemAppsFragment;
import goofy2.swably.fragment.UserLikedAppsFragment;
import goofy2.swably.fragment.UserRecentReviewedAppsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

public class AddApp extends TabStripActivity
	implements LocalAppsFragment.OnClickListener
{
	private ImageButton btnRefresh;
//	protected View btnSearch;
//	String mImagePath = null;
	private View viewProgress;
	private ProgressBar progressBar;
	private TextView txtSizeSent;
	protected CacheProgressBroadcastReceiver mCacheProgressReceiver = new CacheProgressBroadcastReceiver();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
    	super.onCreate(savedInstanceState);
        if(redirectAnonymous()) return;
        
        disableSliding();
        setContent();
        
    	viewProgress = this.findViewById(R.id.viewProgress);
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        txtSizeSent = (TextView) this.findViewById(R.id.txtSizeSent);

    	registerReceiver(mCacheProgressReceiver, new IntentFilter(Const.BROADCAST_CACHE_APPS_PROGRESS));

    	View btnRefresh = findViewById(R.id.btnRefresh);
    	btnRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				sendBroadcast(new Intent(Const.BROADCAST_START_CACHE_APP));
				refreshDb();
			}
		});

    	if(getIntent().getStringExtra(Const.KEY_REVIEW) == null){ 
        	findViewById(R.id.viewBottomBar).setVisibility(View.VISIBLE);
	        View btnRequest = findViewById(R.id.btnRequest);
	        btnRequest.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
//					Intent i = new Intent(AddApp.this, PostReview.class);
//					i.putExtra("image", getIntent().getStringExtra("image"));
//					i.putExtra(Const.KEY_REVIEW, getIntent().getStringExtra(Const.KEY_REVIEW));
//					i.putExtra("content", getIntent().getStringExtra("content"));
					startActivity(getReviewIntent(null));
					finish();
				}
	        });
        }else{ // reply can not be question
        	findViewById(R.id.viewBottomBar).setVisibility(View.GONE);
        }
        
        // get picture from ACTION_SEND intent
        Uri imageUri = null;
        Intent it = getIntent();
    	Bundle bd = it.getExtras();
    	if(bd != null){
    		Object o = bd.get(Intent.EXTRA_STREAM);
    		imageUri = (Uri)o;
    	}
        if(imageUri != null){
        	it.putExtra("image", getRealPathFromURI(imageUri));
        }
    }

    void setContent(){
        setContentView(R.layout.add_app);
    }
    
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        btnSearch = findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onSearchRequested();
//			}
//		});
//        
        mPagerAdapter.addTab("system", getString(R.string.system_apps), SystemAppsFragment.class, null);
        mPagerAdapter.addTab("installed", getString(R.string.installed_apps), LocalAppsFragment.class, null);
		Bundle args = new Bundle();
		args.putString(Const.KEY_USER, Utils.getCurrentUser(this).toString());
        mPagerAdapter.addTab("swably", getString(R.string.swably_apps), UserRecentReviewedAppsFragment.class, args);

		mViewPager.setCurrentItem(1);
    }

    @Override
    public void onDestroy(){
    	try{
    		unregisterReceiver(mCacheProgressReceiver);
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	super.onDestroy();
    }

//    @Override
//	protected void onCloudAction(JSONObject json){
//
//        Intent i = new Intent(AddApp.this, PostReview.class);
//		i.putExtra(Const.KEY_APP, json.toString());
//		i.putExtra("image", mImagePath);
//		i.putExtra(Const.KEY_REVIEW, getIntent().getStringExtra(Const.KEY_REVIEW));
//		i.putExtra("content", getIntent().getStringExtra("content"));
//		startActivity(i);
//		finish();
//    }

    private String getRealPathFromURI(Uri uri){
    	if("content".equalsIgnoreCase(uri.getScheme())){
    		return getRealPathFromMediaURI(uri);
    	}else{
    		return uri.getPath();
    	}
    }
    
    public String getRealPathFromMediaURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    	LocalAppsFragment frag = (LocalAppsFragment) mPagerAdapter.getItem(0);
//    	if(frag != null) frag.onActivityResult(this, requestCode, resultCode, data);
    	
    	if(resultCode == Activity.RESULT_OK && data != null){
    		JSONObject json;
			try {
				json = new JSONObject(data.getStringExtra(Const.KEY_APP));
				ReviewApp(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    	
    }

	@Override
	public void onClick(JSONObject json) {
		ReviewApp(json);
	}

	void ReviewApp(JSONObject json){
		startActivity(getReviewIntent(json));
		finish();
	}
	
	Intent getReviewIntent(JSONObject json){
        Intent i = new Intent(AddApp.this, PostReview.class);
		if(json != null) i.putExtra(Const.KEY_APP, json.toString());
		i.putExtra("image", getIntent().getStringExtra("image"));
		i.putExtra(Const.KEY_REVIEW, getIntent().getStringExtra(Const.KEY_REVIEW));
		i.putExtra("content", getIntent().getStringExtra("content"));
		return i;
	}
    public class CacheProgressBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_CACHE_APPS_PROGRESS)){
            	int count = intent.getIntExtra(Const.KEY_COUNT, 0);
            	int total = intent.getIntExtra(Const.KEY_TOTAL, 1);
        		boolean finished = intent.getBooleanExtra(Const.KEY_FINISHED, false);
        		boolean loading = intent.getBooleanExtra(Const.KEY_LOADING, false);
        		boolean loaded = intent.getBooleanExtra(Const.KEY_LOADED, false);
        		
        		if(loading){
        			viewProgress.setVisibility(View.VISIBLE);
        		}else if(loaded){
        			sendBroadcast(new Intent(Const.BROADCAST_REFRESH_APP));
        			if(progressBar.isIndeterminate()){
            			viewProgress.setVisibility(View.GONE);
        			}
        		}else if(finished){
        			sendBroadcast(new Intent(Const.BROADCAST_REFRESH_APP));
        			viewProgress.setVisibility(View.GONE);
        		}else{
        			viewProgress.setVisibility(View.VISIBLE);
        			int percent = count*100/total;
        			if(percent >= 100){
	        			progressBar.setIndeterminate(true);
	            		txtSizeSent.setText(getString(R.string.refreshing_app_status));
        			}else{
	        			progressBar.setIndeterminate(false);
	        			progressBar.setProgress(percent);
	            		txtSizeSent.setText(String.format(getString(R.string.caching_progress), count, total));
        			}
        		}
            }
        }
    }

    
	protected void refreshDb(){
		if(Utils.isCaching) return;
		new Thread() {
			public void run(){
				Utils.cacheMyApps(AddApp.this);
			}
		}.start();
	}
    
}
