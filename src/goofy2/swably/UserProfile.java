package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.utils.ParamRunnable;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class UserProfile extends WithHeaderActivity {
//	protected UserHeader header = new UserHeader(this);
//	private View btnFollow;
//	private View btnUnfollow;
//	private Button btnReviews;
//	private View btnFollowing;
//	private View btnFollowers;
//	protected Button btnAdd;
//	private TextView txtFollowingCount;
//	private TextView txtFollowersCount;
//	private View imgFollowing;
//	
//	
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        setContentView(R.layout.user_profile);
//        super.onCreate(savedInstanceState);
//    	header.setUserFromIntent();
//
//        btnFollow = this.findViewById(R.id.btnFollow);
//        btnFollow.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
////				Utils.alert(UserProfile.this, "Not designed yet");
//		        if(redirectAnonymous(false)) return;
//				showDialog(0);
//				Utils.follow(UserProfile.this, header.getUserId(), header.getUser().optString("name"), true, new ParamRunnable(){
//					public void run(){
//						header.setUser((JSONObject) param);
//						bind();
//						removeDialog(0);
//					}
//				}, false);
//			}
//        });
//        btnUnfollow = this.findViewById(R.id.btnUnfollow);
//        btnUnfollow.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				showDialog(0);
//				Utils.follow(UserProfile.this, header.getUserId(), header.getUser().optString("name"), false, new ParamRunnable(){
//					public void run(){
//						header.setUser((JSONObject) param);
//						bind();
//						removeDialog(0);
//					}
//				}, false);
//			}
//        });
////        btnEdit = (ImageButton) this.findViewById(R.id.btnEdit);
////        btnEdit.setOnClickListener(new OnClickListener(){
////			@Override
////			public void onClick(View arg0) {
////				Utils.alert(UserProfile.this, "Not designed yet");
////			}
////        });
//
//        btnReviews = (Button) this.findViewById(R.id.btnReviews);
//        btnReviews.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				openUserReviews(UserProfile.this, header.getUser());
//			}
//        });
//        btnFollowing = this.findViewById(R.id.btnFollowing);
//        btnFollowing.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				startActivity(new Intent(UserProfile.this, UserFollowing.class).putExtra(Const.KEY_USER, header.getUser().toString()));
//			}
//        });
//        btnFollowers = this.findViewById(R.id.btnFollowers);
//        btnFollowers.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				startActivity(new Intent(UserProfile.this, UserFollowers.class).putExtra(Const.KEY_USER, header.getUser().toString()));
//			}
//        });
//	    btnAdd = (Button) this.findViewById(R.id.btnAdd);
//	    btnAdd.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(UserProfile.this, LocalApps.class));
//			}
//		});
//        txtFollowingCount = (TextView) this.findViewById(R.id.txtFollowingCount);
//        txtFollowersCount = (TextView) this.findViewById(R.id.txtFollowersCount);
//        imgFollowing = this.findViewById(R.id.imgFollowing);
//        
//		String str = loadCache();
//		if(str != null){
//			try {
//				header.setUser(new JSONObject(str));
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		
//        bind();
//        loadUser();
//    }
//
//    private void bind(){
//    	header.bindView(String.format(getString(R.string.about_user), header.getUser().optString("name")));
//    	TextView tv;
//    	tv = (TextView) findViewById(R.id.txtBio);
//    	tv.setText(header.getUser().optString("bio"));
//    	if(tv.getText().equals("null")) tv.setText("");
////    	tv = (TextView) findViewById(R.id.txtReviewsCount);
////    	tv.setText(header.getUser().optString("reviews_count"));
////    	tv = (TextView) findViewById(R.id.txtFollowingCount);
////    	tv.setText(header.getUser().optString("friends_count"));
////    	tv = (TextView) findViewById(R.id.txtFollowersCount);
////    	tv.setText(header.getUser().optString("followers_count"));
////    	btnReviews.setText(String.format(getString(R.string.reviews_count), header.getUser().optInt("reviews_count")));
//    	txtFollowingCount.setText(header.getUser().optString("friends_count"));
//    	txtFollowersCount.setText(header.getUser().optString("followers_count"));
//    	
//    	if(header.isMe()){
//    		//btnEdit.setVisibility(View.VISIBLE);
//    		btnFollow.setVisibility(View.GONE);
//    		btnUnfollow.setVisibility(View.GONE);
//    		btnAdd.setVisibility(View.VISIBLE);
//    		imgFollowing.setVisibility(View.GONE);
////        	tv = (TextView) findViewById(R.id.txtAppsCount);
////        	tv.setText(""+(new AppHelper(this).getAppCount()));
////        	btnAdd.setText(String.format(getString(R.string.my_applist_count), (new AppHelper(this).getAppCount())));
//    	}else{
//    		//btnEdit.setVisibility(View.GONE);
//	    	if(header.isFollowed()){
//	    		btnFollow.setVisibility(View.GONE);
//	    		btnUnfollow.setVisibility(View.VISIBLE);
//	    		imgFollowing.setVisibility(View.VISIBLE);
//	    	}else{
//	    		btnFollow.setVisibility(View.VISIBLE);
//	    		btnUnfollow.setVisibility(View.GONE);
//	    		imgFollowing.setVisibility(View.GONE);
//	    	}
//	    	btnAdd.setVisibility(View.GONE);
//    	}
//    	
//    }
//
//    private void loadUser(){
//    	AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
//			private String mErr = null;
//			private JSONObject mRet = null;
//			protected void onPreExecute() {
//				showLoading();
//			}
//			protected Long doInBackground(Void... params) {
//				try {
//					mRet = Utils.getUserInfo(UserProfile.this, header.getUserId());
//				} catch (Exception e) {
//					mErr = e.getMessage();
//				}
//				return null;
//			}
//            protected void onPostExecute(Long result) {
//		    	if(mRet != null){
//		    		header.setUser(mRet);
//		    		bind(); 
//		    		cacheData(mRet.toString());
//		    	}
//		    	hideLoading();
//            }
//        };
//        loadTask.execute();
//    }
//
////	  @Override
////	  public boolean onCreateOptionsMenu(Menu menu) {
////	      mMenu = menu;
////	      // Inflate the currently selected menu XML resource.
////	      MenuInflater inflater = getMenuInflater();
////	      inflater.inflate(R.menu.common, menu);
////	      //setNoticeMenu();        		
////	      return true;
////	  }
//    @Override
//	protected void onDataChanged(int item){
//		bind();
//	}
//    
//    @Override
//    protected String getCacheId(){
//    	return cacheId(header.getUserId());
//    }
    
    static public String cacheId(String userId){
    	return UserProfile.class.getName()+userId;
    }
}
