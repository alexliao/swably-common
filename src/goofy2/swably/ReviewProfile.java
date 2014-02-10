package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.data.App;
import goofy2.utils.AsyncImageLoader;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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
        viewReview = (ViewGroup) this.findViewById(R.id.viewReview);
        viewUser = (ViewGroup) this.findViewById(R.id.viewUser);
        viewApp = (ViewGroup) this.findViewById(R.id.viewApp);
        viewContent = (ViewGroup) this.findViewById(R.id.viewContent);
        viewInreplyto = this.findViewById(R.id.viewInreplyto);
//        viewLoadingInreplyto = this.findViewById(R.id.viewLoadingInreplyto);
        btnUser = this.findViewById(R.id.avatar);
//        btnReply = this.findViewById(R.id.btnReply);
//        btnRetweet = this.findViewById(R.id.btnRetweet);
//        btnDelete = this.findViewById(R.id.btnDelete);
//        btnShareReview = this.findViewById(R.id.btnShareReview);
        
        btnUser.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				openUser(header.getUser());
			}
        });
//        btnReply.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
////				Intent i = new Intent(ReviewProfile.this, PostReview.class);
////				i.putExtra(Const.KEY_REVIEW, mReview.toString());
//////				i.putExtra("sync_sns", Utils.getCurrentUser(ReviewProfile.this).optString("signup_sns"));
////				i.putExtra("content", "@"+mReview.optJSONObject("user").optString("screen_name")+" ");
////				startActivity(i);
//				replyReview(mReview);
//			}
//        });
//        btnRetweet.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				Intent i = new Intent(ReviewProfile.this, PostReview.class);
//				i.putExtra(Const.KEY_REVIEW, mReview.toString());
////				i.putExtra("sync_sns", Utils.getCurrentUser(ReviewProfile.this).optString("signup_sns"));
//				i.putExtra("content", "\"@"+mReview.optJSONObject("user").optString("screen_name")+": "+mReview.optString("content")+"\"");
//				startActivity(i);
//			}
//        });
//        btnDelete.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				confirm(getString(R.string.delete_review),  new DialogInterface.OnClickListener(){
//					public void onClick(DialogInterface dialog, int which)
//					{
////						if(deleteReviewInWeb(mReview.optString("id"))){
//						new Thread() {
//							public void run(){
//								deleteReviewInWeb(mReview.optString("id"));
//							}
//						}.start();
//						Animation anim = AnimationUtils.loadAnimation(ReviewProfile.this, R.anim.shrink_out_to_bottom);
//						anim.setDuration(1000);
//						anim.setFillAfter(true);
//						anim.setAnimationListener(new AnimationListener(){
//							@Override
//							public void onAnimationEnd(Animation animation) {
//								finish();
//							}
//							@Override
//							public void onAnimationRepeat(Animation animation) {}
//							@Override
//							public void onAnimationStart(Animation animation) {}
//						});
//						viewReview.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
//						viewReview.startAnimation(anim);
//						Intent intent = new Intent(Const.BROADCAST_REVIEW_DELETED);
//						intent.putExtra(Const.KEY_ID, mReview.optString("id"));
//						sendBroadcast(intent);
//						clearCache();
////						finish();
////				    	overridePendingTransition(R.anim.grow_fade_in_center, R.anim.shrink_out_to_bottom_right);
//					}       
//				});
//			}
//        });
//        btnShareReview.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				Utils.shareReview(ReviewProfile.this, mReview);
//			}
//
//       });

        View btnCopy = findViewById(R.id.txtContent);
        btnCopy.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				cbm.setText(mReview.optString("content"));		
				Utils.showToast(ReviewProfile.this, getString(R.string.copied));
//				mHelper.hideActionsAnim();
			}
        });
//        View btnShareContent = findViewById(R.id.btnShareContent);
//        btnShareContent.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//		    	share(getString(R.string.tell_friends_via), mReview.optString("content"));
//				mHelper.hideActionsAnim();
//			}
//	    });
        
        final View inplacePanelUser = findViewById(R.id.inplacePanelUser);
		mHelper.hideActions(inplacePanelUser);
        btnTriangleUser = this.findViewById(R.id.btnTriangleUser);
        btnTriangleUser.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				boolean isOpened = (mHelper.mLastExpandedPosition == POSITION_USER);  
				mHelper.hideActionsAnim();
				if(!isOpened){
					mHelper.showActionsAnim(inplacePanelUser, POSITION_USER);
				}
			}
        });
        
//        followBtn = new FollowBtn(this, header, viewUser);
//		followBtn.init(new Runnable(){
//			@Override
//			public void run() {
//				mHelper.hideActionsAnim();
//			}
//		});
        

        final View inplacePanelContent = findViewById(R.id.inplacePanelContent);
		mHelper.hideActions(inplacePanelContent);
        btnTriangleContent = this.findViewById(R.id.btnTriangleContent);
        btnTriangleContent.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				boolean isOpened = (mHelper.mLastExpandedPosition == POSITION_CONTENT);  
				mHelper.hideActionsAnim();
				if(!isOpened){
					mHelper.showActionsAnim(inplacePanelContent, POSITION_CONTENT);
				}
			}
        });
		
        btnTriangleApp = this.findViewById(R.id.btnTriangleApp);
		final View inplacePanelApp = findViewById(R.id.inplacePanelApp);
		mHelper.hideActions(inplacePanelApp);
        
        if(mId != null)
    		loadReview(mId);
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
//		viewBody.setVisibility(View.VISIBLE);
//		tribtn.init(this, new App(mReview.optJSONObject("app")));

        header.setUser(mReview.optJSONObject("user"));
        header.setUserFromCache(header.getUserId());
        if(header.isMe())
        	btnTriangleUser.setVisibility(View.GONE);
        else{
    		btnOptionsMenu = findViewById(R.id.btnOptionsMenu);
        	btnOptionsMenu.setVisibility(View.INVISIBLE);
//        	followBtn.bind();
        }
        bindReview();

        if(mReview.optJSONObject("app") != null){
	//		tribtn.setStatus(new App(mReview.optJSONObject("app")));
			appHeader.setApp(new App(mReview.optJSONObject("app")));
			appHeader.setAppFromCache(appHeader.getAppId());
		
//			final View inplacePanelApp = findViewById(R.id.inplacePanelApp);
//	        btnTriangleApp.setOnClickListener(new OnClickListener(){
//				@Override
//				public void onClick(View arg0) {
//					boolean isOpened = (mHelper.mLastExpandedPosition == POSITION_APP);  
//					mHelper.hideActionsAnim();
//					if(!isOpened){
//						mHelper.showActionsAnim(inplacePanelApp, POSITION_APP);
//					}
//				}
//	        });
//
//	        appActionHelper.init(inplacePanelApp, new Runnable(){
//				@Override
//				public void run() {
//					mHelper.hideActionsAnim();
//				}
//			});
//
//			// hide some buttons
//	        inplacePanelApp.findViewById(R.id.btnShare).setVisibility(View.GONE);
//	        inplacePanelApp.findViewById(R.id.btnReview).setVisibility(View.GONE);

			final View bottomBar = findViewById(R.id.bottomBar);
	        appActionHelper.init(bottomBar, null);
	        appActionHelper.bind();

        }
        
        reviewActionHelper = new ReviewActionHelper(this, mReview);
        reviewActionHelper.init(findViewById(R.id.viewBody));
		reviewActionHelper.bind();
		
//		if(mReview.optJSONObject("user").optString("id").equalsIgnoreCase(Utils.getCurrentUserId(this))){
//    		btnDelete.setVisibility(View.VISIBLE);
////    		btnRetweet.setVisibility(View.GONE);
//    	}else{
//    		btnDelete.setVisibility(View.GONE);
////    		btnRetweet.setVisibility(View.VISIBLE);
//    	}
		
        if(mReview.optJSONObject("app") != null){
       }

//    	String InreplytoId = mReview.optString("in_reply_to_id", null);
//        if(mInreplyto == null && InreplytoId != null){
//    		String strCache = loadCache(InreplytoId);
//    		if(strCache != null){
//    			try {
//    				mInreplyto = new JSONObject(strCache);
//    				bindInreplyto(mInreplyto);
//    			} catch (JSONException e) {
//    				e.printStackTrace();
//    			}
//    		}
//    		loadInreplyto(InreplytoId);
//        }
    }
//	protected void bindIcons() {
//		String id = Utils.getCurrentUser(this).optString("signup_sns");
//    	Drawable d;
//    	d = this.getResources().getDrawable((Integer) Utils.getSnsResource(id, "btnReply"));
//    	d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
//		btnReply.setCompoundDrawables(d, null, null, null);
//    	d = this.getResources().getDrawable((Integer) Utils.getSnsResource(id, "btnRetweet"));
//    	d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
//    	btnRetweet.setCompoundDrawables(d, null, null, null);
//    	d = this.getResources().getDrawable((Integer) Utils.getSnsResource(id, "btnDelete"));
//    	d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
//    	btnDelete.setCompoundDrawables(d, null, null, null);
//	}
    
	protected void bindReview() {
		try {
			ImageView iv;
			TextView tv;
			Bitmap bm = null;
			String url = null;
			
			View viewUser = findViewById(R.id.viewUser);

			tv = (TextView)findViewById(R.id.txtUserName);
			tv.setText(header.getUser().optString("name"));
			tv.setTypeface(mBoldFont);

			if(!header.getUser().isNull("avatar_mask")){
				String mask = header.getUser().optString("avatar_mask", "");
				url = mask.replace("[size]", "bi");
//				bm = Utils.getImageFromFile(this, url); 
			}
			iv = (ImageView)findViewById(R.id.avatar);
//			if(bm == null)  Utils.asyncLoadImage(this, 0, url, null);
//			if(bm != null) iv.setImageBitmap(bm);
			iv.setImageResource(R.drawable.noname);
			new AsyncImageLoader(this, iv, 0).loadUrl(url);

			viewUser.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					openUser(header.getUser());
				}
				
			});
			
			String str;
			View viewContent = findViewById(R.id.viewContent);
			
			tv = (TextView) findViewById(R.id.txtContent);
			str = mReview.optString("content");
//			if(str.equals("")){
//				tv.setVisibility(View.GONE);
//			}else{
//				tv.setVisibility(View.VISIBLE);
				tv.setText(str);
				tv.setTypeface(mLightFont);
//			}

			double dTime = mReview.getDouble("created_at");
			String time = Utils.formatTimeDistance(this, new Date((long) (dTime*1000)));
			tv = (TextView) findViewById(R.id.txtTime);
			tv.setText(time);
			tv.setTypeface(mLightFont);

			str = mReview.optString("model");
			if(str != null){
				tv = (TextView) findViewById(R.id.txtModel);
				tv.setText(str);
				tv.setTypeface(mLightFont);
			}

			final App app = new App(mReview.optJSONObject("app"));
			View viewApp = findViewById(R.id.viewApp);
//			View dividerApp = findViewById(R.id.dividerApp);
			View inplacePanelApp = findViewById(R.id.inplacePanelApp);
			View dividerApp = findViewById(R.id.dividerApp);
			if(app.getJSON() == null){
				viewApp.setVisibility(View.GONE);
				btnTriangleApp.setVisibility(View.GONE);
				inplacePanelApp.setVisibility(View.GONE);
				dividerApp.setVisibility(View.GONE);
			}else{
				viewApp.setVisibility(View.VISIBLE);
//				btnTriangleApp.setVisibility(View.VISIBLE);
				inplacePanelApp.setVisibility(View.VISIBLE);
				dividerApp.setVisibility(View.VISIBLE);
				iv = (ImageView) findViewById(R.id.icon);
				if(app.getIcon() != null){
					url = app.getIcon();
//					bm = Utils.getImageFromFile(this, url); 
//					if(bm == null) Utils.asyncLoadImage(this, 0, url, null);
//					if(bm != null) iv.setImageBitmap(bm);
//					else iv.setImageResource(R.drawable.noimage);
					new AsyncImageLoader(this, iv, 1).loadUrl(url);
				}
				tv = (TextView) findViewById(R.id.txtAppName);
				tv.setText(app.getName());
				tv.setTypeface(mBoldFont);
//				tv = (TextView) findViewById(R.id.txtAppVersion);
//				tv.setText(app.getVersionName());
//				tv.setTypeface(mNormalFont);
				tv = (TextView) findViewById(R.id.txtAppSize);
				tv.setText(String.format(getString(R.string.app_size_short), app.getCloudSize()/1048576.0));
				tv.setTypeface(mLightFont);
	
				viewApp.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						openApp(app.getJSON());
					}
					
				});
			}
			if(getInreplytoUser(mReview) != null) bindInreplyto();
			
			final ImageView imgThumbnail = (ImageView)findViewById(R.id.imgThumbnail);
			final ImageView imgImage = (ImageView)findViewById(R.id.imgImage);
			final View loadingImage = findViewById(R.id.loadingImage);
			String imageUrl = mReview.optString("image", null);
			if(imageUrl != null){
				imgThumbnail.setVisibility(View.VISIBLE);
				imgImage.setVisibility(View.VISIBLE);
				loadingImage.setVisibility(View.VISIBLE);

				// disable thumbnail for now
//					String thumbnailUrl = mReview.optString("thumbnail", null);
//					new AsyncImageLoader(this, imgThumbnail, 1, null).loadUrl(thumbnailUrl);
				new AsyncImageLoader(this, imgImage, 1).setRequestSize(Const.SCREEN_WIDTH, Const.SCREEN_HEIGHT).setCallback(new Runnable(){
					@Override
					public void run() {
						loadingImage.setVisibility(View.GONE);
					}
				})
				.loadUrl(imageUrl);

				imgImage.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						if(imgImage.getDrawable() != null){ // image saved
							String imageUrl = mReview.optString("image", null);
							Intent intent = new Intent("android.intent.action.VIEW");  
						    intent.addCategory("android.intent.category.DEFAULT");  
						    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
						    Uri uri = Uri.fromFile(new File(Utils.getImageFileName(imageUrl)));  
						    intent.setDataAndType(uri, "image/*");
						    startActivity(intent);
						}
					}
				});
			}else{
				imgThumbnail.setVisibility(View.VISIBLE);
				imgImage.setVisibility(View.GONE);
				loadingImage.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	protected void bindInreplyto() {
		viewInreplyto.setVisibility(View.VISIBLE);
		try {
			String str;
			ImageView iv;
			TextView tv;
			JSONObject user = getInreplytoUser(mReview);
			
			tv = (TextView) findViewById(R.id.txtInreplyto);
			tv.setTypeface(mLightFont);
			tv = (TextView) findViewById(R.id.txtInreplytoName);
			str = user.optString("name");
			tv.setText(str);
			tv.setTypeface(mLightFont);
//
//			Bitmap bm = null;
//			String url = null;
//			if(!user.isNull("avatar_mask")){
//				String mask = user.optString("avatar_mask", "");
//				url = mask.replace("[size]", "bi");
//				bm = Utils.getImageFromFile(this, url); 
//			}
//			if(bm == null)  Utils.asyncLoadImage(this, 0, url, null);
//			iv = (ImageView) findViewById(R.id.imgInreplyto);
//			iv.setImageBitmap(bm);
//
			viewInreplyto.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent i = new Intent(ReviewProfile.this, ReviewProfile.class);
					i.setData(Uri.parse(Const.HTTP_PREFIX+"/r/"+mReview.optString("in_reply_to_id")));
					startActivity(i);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONObject getInreplytoUser(JSONObject review){
		String str = review.optString("in_reply_to_user", "");
		JSONObject ret = null;
		if(!Utils.isEmpty(str))
			try {
				ret = new JSONObject(str);
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		return ret;
	}

	//	  @Override
//	  public boolean onCreateOptionsMenu(Menu menu) {
//	      mMenu = menu;
//	      // Inflate the currently selected menu XML resource.
//	      MenuInflater inflater = getMenuInflater();
//	      inflater.inflate(R.menu.common, menu);
//	      //setNoticeMenu();        		
//	      return true;
//	  }
//    @Override
//	protected void onDataChanged(int item){
//		bind();
//	}

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

//    private void loadInreplyto(final String id){
//    	AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
//			private String mErr = null;
//			private JSONObject mRet = null;
//			protected void onPreExecute() {
//				viewLoadingInreplyto.setVisibility(View.VISIBLE);
//			}
//			protected Long doInBackground(Void... params) {
//				try {
//					mRet = Utils.getReviewInfo(ReviewProfile.this, id);
////					Log.d("", Const.APP_NAME + " loadInreplyto: "+mRet.toString());
//				} catch (Exception e) {
//					mErr = e.getMessage();
//					Log.d("", Const.APP_NAME + " loadInreplyto error: "+mErr);
//					clearCache(id);
//				}
//				return null;
//			}
//            protected void onPostExecute(Long result) {
//		    	if(mRet != null){
//		    		mInreplyto = mRet;
//		    		bindInreplyto(mInreplyto); 
//		    		cacheData(mRet.toString(), mInreplyto.optString("id"));
//		    	}else viewInreplyto.setVisibility(View.GONE);
//				viewLoadingInreplyto.setVisibility(View.GONE);
//            }
//        };
//        loadTask.execute();
//    }

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
