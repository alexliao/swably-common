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

public class StartBase extends WithHeaderActivity {
	private Button btnSignup;
//	protected ListView mList;
//	ArrayList<HashMap<String, Object>> mSignins = new ArrayList<HashMap<String, Object>>();
//	ArrayList<HashMap<String, Object>> mSigninsEx = new ArrayList<HashMap<String, Object>>();
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

    protected void signInWIth(String sns_id){
		Intent i = new Intent(this, OAuthSignin.class);
		i.setData(Uri.parse(sns_id));
		startActivityForResult(i, REQUEST_CODE_OTHER);
    }


//    /*
//     * Request user name, and picture to show on the main screen.
//     */
//    public void requestUserData() {
////        mText.setText("Fetching user name, profile pic...");
//        Bundle params = new Bundle();
//        params.putString("fields", "name, picture");
////        Utility.mAsyncRunner.request("me", params, new UserRequestListener());
//    }


    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case StartBase.REQUEST_CODE_OTHER: {
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
            	Utils.logV(StartBase.this, "onActivityResult requestCode: " + requestCode);
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

    protected void onGetAccessToken(final String sns_id, final String token){
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
//		    		Intent i = new Intent(StartBase.this, SignedIn.class);
//		    		i.putExtra(Const.KEY_USER, mRet.toString());
//		    		startActivity(i);
		    		
		    		setCurrentUser(mRet);
		    		saveSignedIn(mRet.optString("name"), null);
					sendBroadcast(new Intent(Const.BROADCAST_FINISH));
//		    		Utils.goHome(StartBase.this);
			    	startActivity(new Intent(StartBase.this, GuideSnsFriends.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		    		finish();
		    	}else{
					Utils.alert(StartBase.this, mErr);
					Log.d(Const.APP_NAME, Const.APP_NAME + " Start onGetAccessToken err: "+mErr);
		    	}
            }
        };
        loadTask.execute();
    }

}
