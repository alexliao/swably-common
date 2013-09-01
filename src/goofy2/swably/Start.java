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
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

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

public class Start extends WithHeaderActivity {
	private Button btnSignup;
//	protected ListView mList;
//	ArrayList<HashMap<String, Object>> mSignins = new ArrayList<HashMap<String, Object>>();
//	ArrayList<HashMap<String, Object>> mSigninsEx = new ArrayList<HashMap<String, Object>>();
	private FacebookLogin mFacebookLogin = new FacebookLogin();
	private SsoHandler mSsoHandler;
	public static final int REQUEST_CODE_OTHER = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        disableSliding();

    	Button btnSkip = (Button) this.findViewById(R.id.btnSkip);
    	btnSkip.setTypeface(mLightFont);
    	btnSkip.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), Home.class));
				finish();
			}
        });

        
        if(Const.LANG.equals("en")){
	    	View btnTwitter = this.findViewById(R.id.btnTwitter);
	    	btnTwitter.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
		   			signInWIth("twitter");
				}
	    	});
	    	Utils.setTouchAnim(this, btnTwitter);
	    	
	    	View btnFacebook = this.findViewById(R.id.btnFacebook);
//	        if (FacebookApp.mFacebook.isSessionValid()) { // no need to use Facebook session for nappstr has remembered the user. Using the session easy to cause token invalid error.
//			  btnFacebook.setOnClickListener(new OnClickListener(){
//				@Override
//				public void onClick(View v) {
//			//    			signInWIth("facebook");
//			    	onFacebookSessionValid();        	
//				}
//			  });
//	        }else{
	    		
	    		SessionEvents events = new SessionEvents();
	    		events.addAuthListener(new FbAPIsAuthListener());
		        mFacebookLogin.init(btnFacebook, this, Const.AUTHORIZE_ACTIVITY_RESULT_CODE, FacebookApp.mFacebook, FacebookApp.permissions, events);
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

        }else if(Const.LANG.equals("zh")){
	    	View btnSina = this.findViewById(R.id.btnSina);
	    	btnSina.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
//		   			signInWIth("sina");
				    Weibo weibo;
				    weibo = Weibo.getInstance("2808291982", "http://zh.swably.com/connections/accept/sina");
	                mSsoHandler =new SsoHandler(Start.this, weibo);
	                mSsoHandler.authorize( new WeiboAuthDialogListener());
				}
	    	});
	    	Utils.setTouchAnim(this, btnSina);
        }

        
        //prepareUserBar();

//		btnSignup = (Button) this.findViewById(R.id.btnSignup);
//		btnSignup.setOnClickListener(new OnClickListener_btnSignup());
//		mList=(ListView)findViewById(R.id.listSignin);
//
//		prepareList();
//		bindList(mSignins);
//
//		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
//            @Override  
//            public void onItemClick(AdapterView<?> arg0, final View arg1, final int position, long arg3) {
//				onClickItem(position);
//             }  
//		});      
    }

//    private void prepareList(){
//    	String f = getString(R.string.signin_with); 
//        HashMap<String, Object> signin;
////disable local signin
////        signin = new HashMap<String, Object>(); signin.put("logo", R.drawable.icon); signin.put("name", String.format(f, Const.APP_NAME)); signin.put("id", "local");
////        mSignins.add(signin); mSigninsEx.add(signin);
//
////        String lang = getLang();
////		if(lang.equalsIgnoreCase("zh")){
////	        signin = new HashMap<String, Object>(); signin.put("logo", R.drawable.sina); signin.put("name", String.format(f, "新浪微博")); signin.put("id", "sina");
////	        mSignins.add(signin);
////		}else{
////	        signin = new HashMap<String, Object>(); signin.put("logo", R.drawable.facebook); signin.put("name", String.format(f, "Facebook")); signin.put("id", "facebook");
////	        mSignins.add(signin); mSigninsEx.add(signin);
//////	        signin = new HashMap<String, Object>(); signin.put("logo", R.drawable.twitter); signin.put("name", String.format(f, "Twitter")); signin.put("id", "twitter"); 
//////	        mSignins.add(signin); mSigninsEx.add(signin);
//////	        signin = new HashMap<String, Object>(); signin.put("logo", R.drawable.google_plus); signin.put("name", String.format(f, "Google+")); signin.put("id", "plus"); 
//////	        mSignins.add(signin); mSigninsEx.add(signin);
////		}
//        signin = new HashMap<String, Object>(); signin.put("logo", R.drawable.facebook); signin.put("name", String.format(f, "Facebook")); signin.put("id", "facebook");
//        mSignins.add(signin); mSigninsEx.add(signin);
//    }
//    
//    private void onClickItem(int position){
//    	@SuppressWarnings("unchecked")
//		HashMap<String, Object> signin = (HashMap<String, Object>) mList.getAdapter().getItem(position);
//    	String id = (String) signin.get("id");
//    	if(id.equals("local")){
//			finish();
//    		startActivity(new Intent(this, LocalSignin.class));
//    	}else if(id.equals("more")){
//    		bindList(mSigninsEx);
//    	}else if(id.equals("less")){
//    		bindList(mSignins);
//    	}else{
//			finish();
//    		String url = Const.HTTP_PREFIX + "/connections/signin/"+id+"?app_scheme=nappstr";
//			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
////			Intent i = new Intent(this, OAuthSignin.class);
////			i.setData(Uri.parse(url));
////			startActivity(i);
//    	}
//    }
//    
//    private void bindList(ArrayList<HashMap<String, Object> > list){
//		SimpleAdapter sa = new SimpleAdapter(this, list, R.layout.signin_row, new String[] { "logo", "name"}, new int[] { R.id.imgLogo, R.id.txtName});
//		mList.setAdapter(sa);		
//    }
//    
//    private class OnClickListener_btnSignup implements TextView.OnClickListener {
//		@Override
//		public void onClick(View v) {
//			finish();
//			//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Const.API_PREFIX + "account/signup?app_scheme=nappstr&logout=t&" + System.currentTimeMillis() )));
//			startActivity(new Intent(Start.this, Signup.class));
//		}
//		
//	}
    
//    private void signInWIth(String sns_id){
//		String url = Const.HTTP_PREFIX + "/connections/signin/"+sns_id+"?app_scheme=nappstr";
//		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//		finish();
//    }

    private void signInWIth(String sns_id){
		Intent i = new Intent(this, OAuthSignin.class);
		i.setData(Uri.parse(sns_id));
		startActivityForResult(i, REQUEST_CODE_OTHER);
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

//    /*
//     * Request user name, and picture to show on the main screen.
//     */
//    public void requestUserData() {
////        mText.setText("Fetching user name, profile pic...");
//        Bundle params = new Bundle();
//        params.putString("fields", "name, picture");
////        Utility.mAsyncRunner.request("me", params, new UserRequestListener());
//    }


	class WeiboAuthDialogListener implements WeiboAuthListener {
	    public final static int REQUEST_CODE = 32793; // not sure if this code will not change.

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
        	onGetAccessToken("sina", token);
		}

		@Override
		public void onError(WeiboDialogError e) {
        	Log.d("", Const.APP_NAME + " WeiboAuthDialogListener onError: " + e.getMessage());
        	Utils.showToast(Start.this, e.getMessage());
		}

		@Override
		public void onCancel() {
		}

		@Override
		public void onWeiboException(WeiboException e) {
        	Log.d("", Const.APP_NAME + " WeiboAuthDialogListener onWeiboException: " + e.getMessage());
        	Utils.showToast(Start.this, e.getMessage());
		}

	}
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        /*
         * if this is the activity result from authorization flow, do a call
         * back to authorizeCallback Source Tag: login_tag
         */
            case FacebookApp.AUTHORIZE_ACTIVITY_RESULT_CODE: {
            	try{
            		FacebookApp.mFacebook.authorizeCallback(requestCode, resultCode, data);
            	}catch(Exception e){
            		e.printStackTrace();
            	}
                break;
            }
            case Start.REQUEST_CODE_OTHER: {
            	if(data != null){
	                switch (resultCode) {
	                	case OAuthSignin.RESULT_OK: {
	                    	String access_token = data.getStringExtra("access_token");
	                    	String sns_id = data.getStringExtra("sns_id");
	                    	onGetAccessToken(sns_id, access_token);
	                    	break;
	                	}
	                	case OAuthSignin.RESULT_ERROR: {
	                    	String err = data.getStringExtra("error");
//	                    	Utils.showToastLong(this, err);
	                    	Utils.alert(this, err);
	                    	break;
	                	}
	                }            	
            	}
            	break;
            }
            default: {
            	Utils.logV(Start.this, "onActivityResult requestCode: " + requestCode);
            	// assume it's from weibo
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }
    
    private JSONObject signInWithToken(String snsId, String accessToken) throws Exception{
		String strResult = null;
		JSONObject ret = null;
		String url = Const.HTTP_PREFIX + "/connections/accept_access_token/"+snsId+"?format=json&access_token=" + URLEncoder.encode(accessToken) + "&" + Utils.getClientParameters(this);
		HttpGet httpReq = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT);
		httpReq.setParams(httpParameters);
		HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
		strResult = EntityUtils.toString(httpResp.getEntity());
		JSONObject json = new JSONObject(strResult);
		if(httpResp.getStatusLine().getStatusCode() == 200){
			ret = json;
		}else{
			throw new Exception(json.optString("error_message","error"));
		}
		return ret;
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

    private void onGetAccessToken(final String sns_id, final String token){
    	AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
			private String mErr = null;
			private JSONObject mRet = null;
			protected void onPreExecute() {
				try {
					showDialog(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			protected Long doInBackground(Void... params) {
				try {
					mRet = signInWithToken(sns_id, token);
				} catch (Exception e) {
					mErr = e.getMessage();
				}
				return null;
			}
            protected void onPostExecute(Long result) {
				removeDialog(0);
		    	if(mRet != null){
//try {
//	mRet.put("activated", false);
//} catch (JSONException e) {
//	e.printStackTrace();
//}
		    		Intent i = new Intent(Start.this, SignedIn.class);
		    		i.putExtra(Const.KEY_USER, mRet.toString());
		    		startActivity(i);
		    		finish();
		    	}else{
					Utils.alert(Start.this, mErr);
					Log.d(Const.APP_NAME, Const.APP_NAME + " Start onGetAccessToken err: "+mErr);
		    	}
            }
        };
        loadTask.execute();
    }

}
