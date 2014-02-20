package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.data.App;
import goofy2.swably.fragment.ReviewAboveFragment;
import goofy2.swably.fragment.ReviewBelowFragment;
import goofy2.swably.fragment.ReviewProfileFragment;
import goofy2.utils.AsyncImageLoader;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class ReviewProfile extends WithHeaderActivity {
	JSONObject mReview;
	protected UserHeader header = new UserHeader(this);
	protected AppHeader appHeader = new AppHeader(this);
	protected AppActionHelper appActionHelper = new AppActionHelper(this, appHeader);  
	protected ReviewActionHelper reviewActionHelper;  
//	protected AppTribtn tribtn = new AppTribtn(); 
	JSONObject mInreplyto;
	String mId;
	private View btnUser;
	private View btnRetweet;
	private View btnReply;
	private View btnShareReview;
//	private View btnDelete;
	private ViewGroup viewReview;
	private ViewGroup viewUser;
	private ViewGroup viewApp;
	private ViewGroup viewContent;
	private View viewInreplyto;
	private View viewLoadingInreplyto;
	private ViewGroup viewBelow;
	private ViewGroup viewList;
	 
	static int POSITION_USER = 0;
	static int POSITION_CONTENT = 1;
	static int POSITION_APP = 2;
	InplaceActionsHelper mHelper = new InplaceActionsHelper(this);
	View btnTriangleUser;
	View btnTriangleApp;
	View btnTriangleContent;
	protected FollowBtn followBtn;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        mId = getIdFromUrl(i);
//    	if(id != null){
//    		JSONObject json = new JSONObject();
//    		try {
//				json.put("id", id);
//	    		i.putExtra(Const.KEY_REVIEW, json.toString());
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//    	}
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.review_profile);
        
        final String str = i.getStringExtra(Const.KEY_REVIEW);
        if(str != null){
	        try {
	        	mReview = new JSONObject(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        // load cache here for refreshing dig status
		String strCache = loadCache(getCacheId(mId == null ? mReview.optString("id") : mId)); 
		if(strCache != null){
			try {
				mReview = new JSONObject(strCache);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        
        viewList = (ViewGroup) this.findViewById(R.id.viewList);
        if(mReview != null){
        	loadList();
        }else if(mId != null){
    		loadReview(mId);
        }
    }

    void loadList(){
		Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_REVIEW, mReview.toString());
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		ReviewProfileFragment fragment = new ReviewProfileFragment();
		fragment.setArguments(bundle);
		fragmentTransaction.add(R.id.viewList, fragment);
		fragmentTransaction.commit();
    }

	@Override
    public void onStart(){
    	super.onStart();
        if(mReview != null){
	        bind();
        }        
    }

    protected void bind(){
		hideLoading();

        header.setUser(mReview.optJSONObject("user"));
        header.setUserFromCache(header.getUserId());
        if(!header.isMe()){
    		btnOptionsMenu = findViewById(R.id.btnOptionsMenu);
        	btnOptionsMenu.setVisibility(View.INVISIBLE);
        }

        if(mReview.optJSONObject("app") != null){
	//		tribtn.setStatus(new App(mReview.optJSONObject("app")));
			appHeader.setApp(new App(mReview.optJSONObject("app")));
			appHeader.setAppFromCache(appHeader.getAppId());
		
			final View bottomBar = findViewById(R.id.bottomBar);
	        appActionHelper.init(bottomBar, null);
	        appActionHelper.bind();

        }else{
     	   TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
     	   txtTitle.setText(getString(R.string.a_request));
        }
        
        reviewActionHelper = new ReviewActionHelper(this, mReview);
        reviewActionHelper.init(findViewById(R.id.viewBody));
		reviewActionHelper.bind();
		
    }
    
    private String getIdFromUrl(Intent intent){
    	String ret = null;
    	Uri data = intent.getData();
    	if(data != null){
	    	List<String> params = data.getPathSegments();
	    	//String action = params.get(0); // "r"
	    	if("http".equalsIgnoreCase(data.getScheme())) ret = params.get(1);
    	}
    	return ret;
    }

    private void loadReview(final String id){
    	AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
			private String mErr = null;
			private JSONObject mRet = null;
			protected void onPreExecute() {
				showLoading();
			}
			protected Long doInBackground(Void... params) {
				try {
					mRet = Utils.getReviewInfo(ReviewProfile.this, id);
				} catch (Exception e) {
					mErr = e.getMessage();
				}
				return null;
			}
            protected void onPostExecute(Long result) {
		    	if(mRet != null){
		    		mReview = mRet;
		        	loadList();
		    		bind(); 
		    		cacheData(mRet.toString());
		    	}
		    	if(mErr != null){
		    		Utils.showToastLong(ReviewProfile.this, mErr);
		    	}
		    	hideLoading();
            }
        };
        loadTask.execute();
    }

    static public String cacheId(String id){
//    	if(app == null) return null; // in case opened from share link
    	return ReviewProfile.class.getName()+id;
    }
    
    @Override
    public String getCacheId(){
    	return getCacheId(mId);
    }

    public String getCacheId(String id){
    	return cacheId(id);
    }

    @Override
    public void showLoading(){
    	if(mId != null) showDialog(0);
    }
    @Override
    public void hideLoading(){
    	super.hideLoading();
    	if(mId != null) removeDialog(0);
    }

    protected int getMenu(){
    	return header.isMe()? R.menu.my_review : R.menu.review;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//	    if (item.getItemId() == R.id.share) {
////	    	sendOutReview(mReview);
//	    	return true;
//	    }else if (item.getItemId() == R.id.delete) {
	    if (item.getItemId() == R.id.delete) {
			confirm(getString(R.string.delete_review),  new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which)
				{
//					if(deleteReviewInWeb(mReview.optString("id"))){
					new Thread() {
						public void run(){
							deleteReviewInWeb(mReview.optString("id"));
						}
					}.start();
					Animation anim = AnimationUtils.loadAnimation(ReviewProfile.this, R.anim.shrink_out_to_up_right);
					anim.setDuration(1000);
					anim.setFillAfter(true);
					anim.setAnimationListener(new AnimationListener(){
						@Override
						public void onAnimationEnd(Animation animation) {
							finish();
						}
						@Override
						public void onAnimationRepeat(Animation animation) {}
						@Override
						public void onAnimationStart(Animation animation) {}
					});
					viewReview.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
					viewReview.startAnimation(anim);
					Intent intent = new Intent(Const.BROADCAST_REVIEW_DELETED);
					intent.putExtra(Const.KEY_ID, mReview.optString("id"));
					sendBroadcast(intent);
					clearCache();
//					finish();
//			    	overridePendingTransition(R.anim.grow_fade_in_center, R.anim.shrink_out_to_bottom_right);
				}       
			});
	    	return true;
		}else {
			return super.onOptionsItemSelected(item);
		}
	}
}
