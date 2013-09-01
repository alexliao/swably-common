package goofy2.swably;



import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.R;
import goofy2.utils.DownloadImage;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

public class Main extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.createTempDirectory(this);        
        
		checkDns(this);
        
		Timer timer = new Timer();
    	timer.schedule(new TimerTask(){
    		@Override
    		public void run(){
				Utils.clearImageCache();
				
				CacheHelper helper = new CacheHelper(Main.this);
				helper.clearCacheBefore(Const.DATA_CACHE_LIVE_DAYS);
				
		    	//detectStaticHost();
    		}
    	}, 10*1000); // delay execution
		
		navigate();
    }

	private void checkDns(final Context context){
    	long lastCheckTime = Long.parseLong(Utils.getPrefString(this, "refresh_host_time", "0"));
    	if(System.currentTimeMillis() - lastCheckTime > 3600*24*1000){
    		new Thread(new Runnable(){
				@Override
				public void run() {
		    		JSONObject json = Utils.getHost(context);
		    		if(json != null){
		    			Utils.setPrefString(context, "main_host", json.optString("main", null));
		    			Utils.setPrefString(context, "upload_host", json.optString("upload", null));
		    			Utils.setPrefString(context, "refresh_host_time", ""+System.currentTimeMillis());
		    		}
				}
    		}).start();
    	}
	}
    
//    private void detectStaticHost(){
//    	long tb, te, t1 = 0, t2 = 0, r=0;
//    	File f = new File(Const.TMP_FOLDER + "/logo.png");
//    	
//    	for(int i=1; i<=3; i++){
//	    	tb = System.currentTimeMillis();
//	    	try {
//				DownloadImage.toFile(Const.HTTP_PREFIX + "/logo.png" , f, Const.HTTP_TIMEOUT_LONG);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			te = System.currentTimeMillis();
//			t1 = te - tb;
//			Log.i(Const.APP_NAME, Const.APP_NAME + " Main detectStaticHost " + Const.HTTP_PREFIX + " " + t1);
//			
//	    	tb = System.currentTimeMillis();
//	    	try {
//				DownloadImage.toFile(Const.STATIC_HTTP_PREFIX_CN + "/logo.png" , f, Const.HTTP_TIMEOUT_LONG);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			te = System.currentTimeMillis();
//			t2 = te - tb;
//			Log.i(Const.APP_NAME, Const.APP_NAME + " Main detectStaticHost " + Const.STATIC_HTTP_PREFIX_CN + " " + t2);
//			
//			if(t2 < t1) r += 1;
//			else if(t2 > t1) r -= 1;
//    	}
//    	
//		if(r > 0)
//			Utils.setPrefString(this, "static_http_prefix", Const.STATIC_HTTP_PREFIX_CN);
//		else
//			Utils.setPrefString(this, "static_http_prefix", Const.HTTP_PREFIX);
//		
//		Log.i(Const.APP_NAME, Const.APP_NAME + " Main detectStaticHost r=" + r + " select: " + Utils.getStaticHttpPrefix(this));
//		
//    }

//    @Override
//    protected void prepareUserBar(){
//    	super.prepareUserBar();
//    	if(currentUser == null && getSignedIns().length == 0){
//        	header.setVisibility(View.GONE);
//        }else{
//        	header.setVisibility(View.VISIBLE);
//            imgLogo.setVisibility(View.GONE);
//        }
//        if(currentUser == null){
//        	navigationBar.setVisibility(View.GONE);
//		    navBar.setVisibility(View.GONE);
//        	btnSnap.setText(getString(R.string.start));
//        }
//        else{
//        	navigationBar.setVisibility(View.VISIBLE);
//		    navBar.setVisibility(View.VISIBLE);
//        	btnSnap.setText(getString(R.string.snap));
//        }
//    }
    
//    @Override
//    protected void onNewIntent(Intent intent){
//    	super.onNewIntent(intent);
//    	autoSignin(intent);
//    	//setHeader();
//    }

    public void navigate(){
//    	boolean signed = autoSignin(getIntent());
//    	if(signed){
////			startActivity(new Intent(Main.this, SignedIn.class));
//    		String id = Utils.getCurrentUser(Main.this).optString("signup_sns");
//        	String name = (String) Utils.getSnsResource(id, "name");
//    		Intent i = new Intent(Main.this, GuideSnsFriends.class);
//    		i.setData(Uri.parse(id));
//    		i.putExtra("name", name);
//    		startActivity(i);
//    	}else{
	    	//setHeader();
    		if(Utils.getPrefString(this, "terms_accepted", "false").equals("true")){
				Utils.goHome(this);
		    	if(Utils.getCurrentUser(this) != null){
		    		Timer timer = new Timer();
		        	timer.schedule(new TimerTask(){
		        		@Override
		        		public void run(){
		    	            if(Checker.isNoticeOn(Main.this)) startService(new Intent(Main.this, Checker.class));
		    	            // update my status
		    				try {
		    					JSONObject me = Utils.getUserInfo(Main.this, Utils.getCurrentUserId(Main.this));
		    		    		if(me != null) Utils.setCurrentUser(Main.this, me);
		    				} catch (Exception e) {
		    					e.printStackTrace();
		    				}
		        		}
		        	}, 10*1000); // delay execution
		    	}
		    }else{
				startActivity(new Intent(Main.this, Cover.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	    	}
//    	}
		finish();
    }

    private boolean autoSignin(Intent intent){
    	boolean ret = false;
    	Uri data = intent.getData();
    	if(data != null){
	    	String scheme = data.getScheme(); // "http" or Const.APP_NAME
	    	String host = data.getHost(); // 
	    	List<String> params = data.getPathSegments();
	    	String action = params.get(0); // "signin"
	    	String parameters = data.getQuery();
	    	String id = data.getQueryParameter("id");
	    	String username = data.getQueryParameter("username");
	    	String name = data.getQueryParameter("name");
	    	String bio = data.getQueryParameter("bio");
	    	String avatar_mask = data.getQueryParameter("avatar_mask");
	    	String key = data.getQueryParameter("key");
	    	String connections = data.getQueryParameter("connections");
	    	String signup_sns = data.getQueryParameter("signup_sns");
	    	if(id != null && username != null && key != null){
	    		try {
	    			JSONObject user = new JSONObject();
					user.put("id", id);
		    		user.put("username", username);
		    		user.put("name", name);
		    		user.put("bio", bio);
		    		user.put("avatar_mask", avatar_mask);
		    		user.put("key", key);
		    		user.put("connections", connections);
		    		user.put("signup_sns", signup_sns);
		    		Utils.setCurrentUser(Main.this, user);
					Utils.saveSignedIn(Main.this, username, null);
					ret = true;
				} catch (JSONException e) {
					Log.e(Const.APP_NAME, Const.APP_NAME + " Main onStart:" + e.getMessage());
				}
	    	}
	    	intent.setData(null);
    	}
    	return ret;
    }
    
//    private void setHeader(){
//    	if(currentUser == null && getSignedIns().length == 0){
//        	//slogon.setVisibility(View.VISIBLE);
//        	header.setVisibility(View.GONE);
//        }else{
//        	//slogon.setVisibility(View.GONE);
//        	header.setVisibility(View.VISIBLE);
//            imgLogo.setVisibility(View.GONE);
//        }
//        if(currentUser == null){
//		    navBar.setVisibility(View.GONE);
//        	btnSnap.setVisibility(View.GONE);
//        	btnStart.setVisibility(View.VISIBLE);
//        }
//        else{
//		    navBar.setVisibility(View.VISIBLE);
//        	btnSnap.setVisibility(View.VISIBLE);
//        	btnStart.setVisibility(View.GONE);
//        }
//    }
    
//    @Override
//    public void onResume(){
//    	super.onResume();
//    }
    
    
//    private class OnClickListener_btnHome implements Button.OnClickListener {
//		@Override
//		public void onClick(View v) {
//			//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Const.API_PREFIX + "public?" + getLoginParameters() + " &" + System.currentTimeMillis())));
//			startActivity(new Intent(Main.this, PublicStream.class));
//		}
//		
//	}

}