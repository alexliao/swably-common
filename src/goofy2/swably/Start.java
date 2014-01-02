package goofy2.swably;


import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import goofy2.swably.facebook.*;
import goofy2.swably.facebook.SessionEvents.AuthListener;
import goofy2.swably.facebook.SessionEvents.LogoutListener;
import goofy2.utils.ViewWrapper;

public class Start extends StartBase {
	private FacebookLogin mFacebookLogin = new FacebookLogin();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	    	View btnTwitter = this.findViewById(R.id.btnTwitter);
	    	btnTwitter.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
		   			signInWIth("twitter");
				}
	    	});
	    	Utils.setTouchAnim(this, btnTwitter);
	    	
	    	View btnPublic2 = this.findViewById(R.id.btnPublic2);
//	        if (FacebookApp.mFacebook.isSessionValid()) { // no need to use Facebook session for nappstr has remembered the user. Using the session easy to cause token invalid error.
//			  btnPublic2.setOnClickListener(new OnClickListener(){
//				@Override
//				public void onClick(View v) {
//			//    			signInWIth("facebook");
//			    	onFacebookSessionValid();        	
//				}
//			  });
//	        }else{
	    		
	    		SessionEvents events = new SessionEvents();
	    		events.addAuthListener(new FbAPIsAuthListener());
		        mFacebookLogin.init(btnPublic2, this, Const.AUTHORIZE_ACTIVITY_RESULT_CODE, FacebookApp.mFacebook, FacebookApp.permissions, events);
//	        }
	    	View btnPlus = this.findViewById(R.id.btnPlus);
	    	btnPlus.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Utils.confirm(Start.this, null, "This entry is not for new Swably user. Are you sure you have ever signed in Swably with Google+?", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
				   			signInWIth("plus");
						}
					});
				}
	    	});

	    	View btn4OldUser = this.findViewById(R.id.btn4OldUser);
	    	btn4OldUser.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
			    	ObjectAnimator anim;
			    	View view4OldUser = findViewById(R.id.view4OldUser);
					(new ViewWrapper(view4OldUser)).setHeight(LayoutParams.WRAP_CONTENT);
					view4OldUser.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
					int targetHeight = view4OldUser.getMeasuredHeight();
			    	if(view4OldUser.getHeight() == 0){
						anim = ObjectAnimator.ofInt(new ViewWrapper(view4OldUser), "Height", 0, targetHeight);
			    	}else{
						anim = ObjectAnimator.ofInt(new ViewWrapper(view4OldUser), "Height", targetHeight, 0);
			    	}
					anim.setDuration(getResources().getInteger(R.integer.config_mediumAnimTime));
					anim.start();
				}
	    	});


        
    }

    public class FbAPIsAuthListener implements AuthListener {

        @Override
        public void onAuthSucceed() {
        	Log.d("", Const.APP_NAME + " FbAPIsAuthListener onAuthSucceed. AccessToken: "+FacebookApp.mFacebook.getAccessToken() + " Expire at: "+FacebookApp.mFacebook.getAccessExpires()+" after: " + (FacebookApp.mFacebook.getAccessExpires()-System.currentTimeMillis())/1000/3600 + " hours");
            SessionStore.save(FacebookApp.mFacebook, Start.this);
        	onGetAccessToken("facebook", FacebookApp.mFacebook.getAccessToken());
        }

        @Override
        public void onAuthFail(String error) {
//            mText.setText("Login Failed: " + error);
        	Log.d("", Const.APP_NAME + " FbAPIsAuthListener onAuthFail: " + error);
        	Utils.showToast(Start.this, error);
        }
    }

//    /*
//     * The Callback for notifying the application when log out starts and
//     * finishes.
//     */
//    public class FbAPIsLogoutListener implements LogoutListener {
//        @Override
//        public void onLogoutBegin() {
////            mText.setText("Logging out...");
//        	Log.d("", Const.APP_NAME + " FbAPIsLogoutListener onLogoutBegin");
//        }
//
//        @Override
//        public void onLogoutFinish() {
////            mText.setText("You have logged out! ");
////            mUserPic.setImageBitmap(null);
//        	Log.d("", Const.APP_NAME + " FbAPIsLogoutListener onLogoutFinish");
//        }
//    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == Start.REQUEST_CODE_OTHER)
    		super.onActivityResult(requestCode, resultCode, data);
    	else{
        	Utils.logV(Start.this, "onActivityResult requestCode: " + requestCode);
        	if(requestCode == FacebookApp.AUTHORIZE_ACTIVITY_RESULT_CODE){
				try{
					FacebookApp.mFacebook.authorizeCallback(requestCode, resultCode, data);
				}catch(Exception e){
					e.printStackTrace();
				}
        	}
        }
    }
    
//    private void onFacebookSessionValid(){
//    	AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
//			private String mErr = null;
//			private JSONObject mRet = null;
//			protected void onPreExecute() {
//				try {
//					showDialog(0);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			protected Long doInBackground(Void... params) {
//				try {
//					mRet = signInWithToken("facebook", FacebookApp.mFacebook.getAccessToken());
//				} catch (Exception e) {
//					mErr = e.getMessage();
//				}
//				return null;
//			}
//            protected void onPostExecute(Long result) {
//				removeDialog(0);
//		    	if(mRet != null){
////try {
////	mRet.put("activated", false);
////} catch (JSONException e) {
////	e.printStackTrace();
////}
//		    		Intent i = new Intent(Start.this, SignedIn.class);
//		    		i.putExtra(Const.KEY_USER, mRet.toString());
//		    		startActivity(i);
//		    		finish();
//		    	}else{
//					Utils.alert(Start.this, mErr);
//		    	}
//            }
//        };
//        loadTask.execute();
//    }


}
