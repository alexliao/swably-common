package goofy2.swably;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nineoldandroids.animation.ObjectAnimator;

import goofy2.swably.About;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.SelectLocalAppToReview;
import goofy2.swably.TabStripActivity;
import goofy2.swably.Utils;
import goofy2.swably.fragment.MyFollowingReviewsFragment;
import goofy2.swably.fragment.MyReviewsFragment;
import goofy2.swably.fragment.PublicReviewsFragment;
import goofy2.utils.ViewWrapper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

public class PublicReviews extends PeopleReviews {
	View viewEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		TextView tv = (TextView) findViewById(R.id.txtSubmitEmail);
		tv.setTypeface(mNormalFont);
		
		viewEmail = findViewById(R.id.viewEmail);
		View btnCloseEmail = findViewById(R.id.btnCloseEmail);
		btnCloseEmail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.pushUp(PublicReviews.this, viewEmail);
			}
		});
		if(Utils.getCurrentUser(this) != null && Utils.isEmpty(Utils.getCurrentUser(this).optString("email"))){
			viewEmail.setVisibility(View.VISIBLE);
			View btnSubmitEmail = findViewById(R.id.btnSubmitEmail);
			btnSubmitEmail.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					EditText editEmail = (EditText) findViewById(R.id.editEmail);
					final String email = editEmail.getText().toString().trim();
					if(email.length() > 0){
						if(!Utils.isValidEmail(email)){
							Utils.showToast(PublicReviews.this, getString(R.string.err_invalid_email));
							editEmail.requestFocus();
						}else{
							Utils.pushUp(PublicReviews.this, viewEmail);
							new Thread(new Runnable(){
								@Override
								public void run() {
									try {
										JSONObject user = Api.changeEmail(getApplicationContext(), email);
										Utils.setCurrentUser(getApplicationContext(), user);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}).start();
						}
						
					}else{
						editEmail.requestFocus();
					}
					
				}
			});
			Utils.pullDown(this, viewEmail);
		}
		
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    }
	
	void setContent(){
        setContentView(R.layout.public_reviews);
    }

//  @Override
//  public int getMenu() {
////      return R.menu.home;
//      return 0;
//  }
//
//    @Override
//	public boolean onOptionsItemSelected(MenuItem item) {
////	    if (item.getItemId() == R.id.home) {
////	    	Utils.goHome(this);
////	    	return true;
//		if (item.getItemId() == R.id.menu_my_following_reviews) {
//			startActivity(new Intent(this, MyFollowingReviews.class));
//			return true;
//		}else {
//			return super.onOptionsItemSelected(item);
//		}
//	}
    
}
