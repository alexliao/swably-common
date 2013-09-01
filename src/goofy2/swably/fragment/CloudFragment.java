package goofy2.swably.fragment;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;

import goofy2.swably.CloudActivity;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.Utils;
import goofy2.swably.CloudActivity.ImageMessageBroadcastReceiver;
import goofy2.swably.facebook.FacebookApp;
import goofy2.utils.JSONUtils;
import goofy2.utils.ParamRunnable;
import goofy2.utils.Rotate3dAnimation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CloudFragment extends Fragment {
	protected ImageMessageBroadcastReceiver mImageLoadedReceiver = new ImageMessageBroadcastReceiver();

	// convienent methods
	public Activity a(){
		return getActivity();
	}
	public CloudActivity ca(){
		return getCloudActivity();
	}
	
    @Override
    public void onAttach(Activity activity) {
		Utils.logV(this, "CloudFragment onAttach: " + activity);
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
		Utils.logV(this, "CloudFragment onCreate: " + savedInstanceState);
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(mImageLoadedReceiver, new IntentFilter(CloudActivity.IMAGE_LOADED));

    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Utils.logV(this, "CloudFragment onCreateView: " + savedInstanceState);
		return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
		Utils.logV(this, "CloudFragment onActivityCreated: " + savedInstanceState);
		super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onStart (){
		Utils.logV(this, "CloudFragment onStart getView(): " + getView());
    	super.onStart();
    }

    @Override
    public void onResume (){
		Utils.logV(this, "CloudFragment onResume");
    	super.onResume();
    }

    @Override
    public void onPause (){
		Utils.logV(this, "CloudFragment onPause");
    	super.onPause();
    }

    @Override
    public void onStop (){
		Utils.logV(this, "CloudFragment onStop");
    	super.onStop();
    }

    @Override
    public void onDestroyView(){
		Utils.logV(this, "CloudFragment onDestroyView");
    	super.onDestroyView();
    }

    @Override
    public void onDestroy(){
		Utils.logV(this, "CloudFragment onDestroy");
    	getActivity().unregisterReceiver(mImageLoadedReceiver);
    	super.onDestroy();
    }

    @Override
    public void onDetach(){
		Utils.logV(this, "CloudFragment onDetach");
    	super.onDestroyView();
    }

    public CloudActivity getCloudActivity(){
		return (CloudActivity)(getActivity());
	}

    protected class ImageMessageBroadcastReceiver extends BroadcastReceiver {
    	//private Feeds mUI;

    	//public ImageMessageBroadcastReceiver(Feeds ui){
    	//	mUI = ui;
    	//}
        @Override
        public void onReceive(Context context, Intent intent) {
    		Log.d(Const.APP_NAME, Const.APP_NAME + " ImageMessageBroadcastReceiver onReceive: " + intent.toString());
            if(intent.getAction().equals(CloudActivity.IMAGE_LOADED)){
            	onDataChanged(-1);
            }
        }
    }
	
    // override it in subclass
	protected void onDataChanged(int item) {}
	
    public String loadCache(){
    	return Utils.loadCache(a(), getCacheId());
    }
    public void cacheData(String data){
    	Utils.cacheData(a(), data, getCacheId());
    }
    public void clearCache(){
    	Utils.clearCache(a(), getCacheId());
    }
    public String getCacheId(){
    	return this.getClass().getName();
    }
	public long getCacheExpiresIn(){
		return Const.DEFAULT_CACHE_EXPIRES_IN;
	}
    public long getCacheAt(){
    	return Utils.getCacheAt(a(), getCacheId());
    }
	
	
//	public static final String UPDATE = "goofy2.swably.CloudActivity.UPDATE";
//	public static final String FOLLOWED = "goofy2.swably.CloudActivity.FOLLOWED";
//	public static final String AUTO_REFRESH = "goofy2.swably.CloudActivity.AUTO_REFRESH";
//	public static final String IMAGE_LOADED = "goofy2.swably.IMAGE_LOADED";
//	public static final String RESTART = "goofy2.swably.RESTART";
////	protected NavBar navbar = new NavBar();
//	protected ImageMessageBroadcastReceiver mImageLoadedReceiver = new ImageMessageBroadcastReceiver();
//	protected RestartBroadcastReceiver mRestartReceiver = new RestartBroadcastReceiver();
//	protected Menu mMenu;
//	protected View viewLoading;
////	protected Typeface FONT_NEUROPOL;
//	protected Typeface FONT_VEGUR;
//	protected Typeface mHeaderFont;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        FONT_NEUROPOL = Typeface.createFromAsset(getAssets(), "NEUROPOL.ttf");
//        FONT_VEGUR = Typeface.createFromAsset(getAssets(), "Vegur-M 0602.otf");
//        mHeaderFont = FONT_VEGUR;
//        //requestWindowFeature(Window.FEATURE_NO_TITLE);
//		registerReceiver(mImageLoadedReceiver, new IntentFilter(IMAGE_LOADED));
//		registerReceiver(mRestartReceiver, new IntentFilter(RESTART));
//		viewLoading = findViewById(R.id.loading);
//		
//		if(FacebookApp.mFacebook == null) FacebookApp.init(this);
//
//    }
//    
//    @Override
//    public void onDestroy(){
//		unregisterReceiver(mImageLoadedReceiver);
//		unregisterReceiver(mRestartReceiver);
//    	super.onDestroy();
//    }
//
//    @Override
//    public void onStart(){
//    	super.onStart();
////        navbar.init(this);
//    }
//
//    public boolean redirectAnonymous(){
//    	return redirectAnonymous(true, null);
//    }
//    public boolean redirectAnonymous(boolean finish){
//    	return redirectAnonymous(finish, null);
//    }
//    public boolean redirectAnonymous(boolean finish, Uri data){
//    	boolean ret = false;
//		if(Utils.getCurrentUser(this) == null){
//			if(finish) finish();
////			startActivityForResult(new Intent(this, Signup.class), 0);
//			Intent i = new Intent(this, Cover.class);
//			i.setData(data);
//			startActivity(i);
//			ret = true;
//		}
//		return ret;
//    }
//
////    protected void tryCacheApps(){
////    	if(Utils.isCaching) return;
////		new Thread() {
////			public void run(){
////				PackageManager pm = getPackageManager();
////				List<PackageInfo> pckInfos = pm.getInstalledPackages(0);
////				int total = 0;
////				for(int i=0; i<pckInfos.size(); i++){
////					PackageInfo info = pckInfos.get(i);
////		        	if(!info.applicationInfo.sourceDir.matches("/system.*")){
////		        		total ++;
////					}
////				}
////		    	if(new AppHelper(CloudFragment.this).getAppCount() != total){
////		    		Utils.cacheMyApps(CloudFragment.this);
////				}
////			}
////		}.start();
////    }
//	
////	public void setCurrentUser(JSONObject user){
////		currentUser = user;
////		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
////		SharedPreferences.Editor editor = pref.edit();
////		editor.putString("currentUser", user.toString());
////		editor.commit();
//
//// no need to do this because app status changed to if it's on cloud which is no longer relative to user account.		
////		new Thread() {
////			public void run(){
////				Utils.cacheAppsStatus(CloudActivity.this, new AppHelper(CloudActivity.this).getApps());
////			}
////		}.start();
////	}
//
////	protected void saveSignedIn(String username, JSONObject user){
////		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
////		SharedPreferences.Editor editor = pref.edit();
////		String usernames = pref.getString("usernames", "");
////		// save and make current user the first order.
////		String[] names = getSignedIns();
////		String ret = username;
////    	for(int i=0; i<names.length; i++){
////    		if(!names[i].equalsIgnoreCase(username)){
////    			ret +=  Const.USERNAME_SPLITOR + names[i];
////    		}
////    	}		
////		//usernames = usernames.replaceAll(username, ""); //it has problem
////		//usernames = username + Const.USERNAME_SPLITOR + usernames;
////		//usernames = usernames.replaceAll(Const.USERNAME_SPLITOR+"+", Const.USERNAME_SPLITOR);
////		//usernames = usernames.replaceAll("^"+Const.USERNAME_SPLITOR, "");
////		//usernames = usernames.replaceAll(Const.USERNAME_SPLITOR+"$", "");
////		editor.putString("usernames", ret);
////		editor.putString(Const.USERNAME_PREFIX+username, user.toString());
////		editor.commit();
////	}
//	
//	public void setCurrentUser(JSONObject user){
//		Utils.setCurrentUser(this, user);
//	}
//    
//    protected void saveSignedIn(String username, String password){
//		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
//		SharedPreferences.Editor editor = pref.edit();
//		String usernames = pref.getString("usernames", "");
//		// save and make current user the first order.
//		String[] names = getSignedIns();
//		String ret = username;
//    	for(int i=0; i<names.length; i++){
//    		if(!names[i].equalsIgnoreCase(username)){
//    			ret +=  Const.USERNAME_SPLITOR + names[i];
//    		}
//    	}		
//		//usernames = usernames.replaceAll(username, ""); //it has problem
//		//usernames = username + Const.USERNAME_SPLITOR + usernames;
//		//usernames = usernames.replaceAll(Const.USERNAME_SPLITOR+"+", Const.USERNAME_SPLITOR);
//		//usernames = usernames.replaceAll("^"+Const.USERNAME_SPLITOR, "");
//		//usernames = usernames.replaceAll(Const.USERNAME_SPLITOR+"$", "");
//		editor.putString("usernames", ret);
//		if(password != null)
//			editor.putString(Const.USERNAME_PREFIX+username, password);
//		editor.commit();
//	}
//	protected String[] getSignedIns(){
//		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
//		String usernames = pref.getString("usernames", "");
//		if(usernames.equals(""))
//			return new String[0]; 
//		else
//			return usernames.split(Const.USERNAME_SPLITOR);
//	}
//
//	protected String getPassword(String username){
//		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
//		String password = pref.getString(Const.USERNAME_PREFIX+username, "");
//		return password;
//	}
//
////	protected JSONObject getUser(String username){
////		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
////		String str = pref.getString(Const.USERNAME_PREFIX+username, "");
////		JSONObject user = null;
////		try {
////			user = new JSONObject(str);
////		} catch (JSONException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} 
////		return user;
////	}
//
////	protected void clearSignedIns(){
////		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
////		SharedPreferences.Editor editor = pref.edit();
////		try {
////			JSONObject currentUser = Utils.getCurrentUser(this);
////			editor.putString("usernames", currentUser == null ? "" : currentUser.getString("username"));
////		} catch (JSONException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////		editor.commit();
////		this.startActivity(getIntent());
////	}
//
////	public String getAppUrl(String appId, String displayName){
////		return Const.HTTP_PREFIX + "/a/" + appId;
////	}
//
////	protected String getLoginParameters(){
////		return Utils.getLoginParameters(this);	
////	}
////	
////	protected String getClientParameters(){
////		return Utils.getClientParameters(this);
////	}
//
//	public String getSiteUrl(){
//    	return Const.HTTP_PREFIX + "/?" + getLoginParameters() + " &" + System.currentTimeMillis();
//    }
//	
//	public void startSite(){
//		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getSiteUrl())));
//	}
//
//	protected Uri getPicUri(Intent it){
//    	if(it == null) it = new Intent();
//        Uri picUri = null;
//        picUri = it.getData();
//        if(picUri == null){
//        	Bundle bd = it.getExtras();
//        	if(bd != null){
//        		Object o = bd.get(Intent.EXTRA_STREAM);
//        		picUri = (Uri)o;
//        	}
//        }
//        return picUri;
//	}
//	public boolean createTempDirectory() {
//	    File tempdir = new File(Const.TMP_FOLDER);
//	    if (!tempdir.exists()) {
//	        if (!tempdir.mkdirs()) {
//	        	Utils.showToast(this, getString(R.string.err_no_SD));
//	            //Log.d(Const.APP_NAME, "Cannot create directory: " + Const.TMP_FOLDER);
//	            return false;
//	        }
//	    }
//	    return true;
//	}
//
//	
//	public boolean HttpPrompt()
//	{ 
//		boolean ret = true;
//	    if( !Utils.isNetworkAvailable(this) ){
//	    	ret = false;
//	    	Utils.showToast(this, getString(R.string.err_no_network_message));
//	    }
//	    return ret;
//	}	
//	
//	class OnClickListener_imgLogo implements ImageView.OnClickListener {
//    	
//		@Override
//		public void onClick(View v) {
////    		startSite();         
////	    	if(currentUser != null){
////	    		SharedPreferences pref = getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
////	    		SharedPreferences.Editor editor = pref.edit();
////	    		// save and make current user the first order.
////	    		editor.putBoolean("tappedLogo", true);
////	    		editor.commit();
////	    	}
////			startActivity(new Intent(CloudActivity.this, Main.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//			goHome();
//		}
//    }
//
//    protected class OnClickListener_btnSnap implements Button.OnClickListener {
//		@Override
//		public void onClick(View v) {
////			startActivity(new Intent(CloudActivity.this, Shooter.class));
//		}
//		
//	}
//
//    @Override
//    protected Dialog onCreateDialog(int id) {
//        ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage(getString(R.string.loading));
//        dialog.setIndeterminate(true);
//        //dialog.setCancelable(false);
//        //dialog.setCanceledOnTouchOutside(true);
//        return dialog;
//    }
//
//    public void openMe(){
//    	JSONObject user = Utils.getCurrentUser(this);
////    	openUser(user);
//		Intent i = new Intent(this, UserAllReviews.class);
//		i.putExtra(Const.KEY_USER, user.toString());
//		i.putExtra(UserReviews.EXPAND_INFO, true);
//		startActivity(i);
//    }
//
//    
////    protected void openApp(JSONObject app){
////		Intent i = new Intent(this, ReviewLocalApp.class);
////		i.putExtra(Const.KEY_APP, app.toString());
////		startActivity(i);
////	}
////
////    protected void openApp(String packageName){
////		Intent i = new Intent(this, ReviewLocalApp.class);
////		i.setData(Uri.parse(packageName));
////		startActivity(i);
////	}
//
//    protected void openUser(JSONObject user){
//    	openUser(this, user);
//    }
//    static public void openUser(Context context, JSONObject user){
//    	openUserReviews(context, user);
//    }
//
//    protected void openUserProfile(JSONObject user){
//    	openUserProfile(this, user);
//    }
//    static public void openUserProfile(Context context, JSONObject user){
//		Intent i = new Intent(context, UserProfile.class);
//		i.putExtra(Const.KEY_USER, user.toString());
//		context.startActivity(i);
//    }
//
//    protected void openUserReviews(JSONObject user){
//    	openUserReviews(this, user);
//    }
//    static public void openUserReviews(Context context, JSONObject user){
//		Intent i = new Intent(context, UserAllReviews.class);
//		i.putExtra(Const.KEY_USER, user.toString());
//		context.startActivity(i);
//    }
//    
//    protected void openApp(JSONObject app){
//    	openApp(this, app);
//    }
//    static public void openApp(Context context, JSONObject app){
//    	openAppReviews(context, app);
//    }
//    protected void openAppProfile(JSONObject app){
//    	openAppProfile(this, app);
//    }
//    static public void openAppProfile(Context context, JSONObject app){
//		Intent i = new Intent(context, AppProfile.class);
//		i.putExtra(Const.KEY_APP, app.toString());
//		context.startActivity(i);
//    }
//
//    protected void openAppReviews(JSONObject app){
//    	openAppReviews(this, app);
//    }
//    static public void openAppReviews(Context context, JSONObject app){
//		Intent i = new Intent(context, AppAllComments.class);
//		i.putExtra(Const.KEY_APP, app.toString());
//		context.startActivity(i);
//    }
//
//    protected void sendOutApp(App app){
//    	share("", app.getName(), Const.HTTP_PREFIX+"/a/"+app.getCloudId());
//    }
//
//    protected void sendOutReview(JSONObject review){
//    	App app = new App(review.optJSONObject("app"));
//    	share(review.optString("content")+" -- @"+review.optJSONObject("user").optString("screen_name"), app.getName(), Const.HTTP_PREFIX+"/r/"+review.optString("id"));
//    }
//
//    protected void share(String review, String name, String url){
//		try {
//			String content = "#" + name + " " + url + " " + review;
//	        Intent intent = new Intent(Intent.ACTION_SEND);
//	        intent.setType("text/plain");
//	        intent.putExtra(Intent.EXTRA_TEXT, content);
//	        intent.putExtra(Intent.EXTRA_SUBJECT, name);
//	    	Intent i = Intent.createChooser(intent, getString(R.string.tell_friends_via));
//	    	startActivity(i);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    }
//
////    public JSONObject doReview(String appId, String content, String inReplyToId, String sync_sns){
////		JSONObject ret = null ;
////    	String actionURL = Const.HTTP_PREFIX+"/comments/create?format=json&"+getLoginParameters()+"&"+getClientParameters();
////		//post form
////		HttpPost httpReq = new HttpPost(actionURL);
////		List <NameValuePair> params = new ArrayList <NameValuePair>();
////		params.add(new BasicNameValuePair("app_id", appId));
////		params.add(new BasicNameValuePair("content", content));
////		params.add(new BasicNameValuePair("in_reply_to_id", inReplyToId));
////		params.add(new BasicNameValuePair("sync_sns", sync_sns));
////		try {
////			httpReq.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
////			HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
////			String strResult = null;
////			JSONObject json = null;
////			strResult = EntityUtils.toString(httpResp.getEntity());
////			json = new JSONObject(strResult);
////			if(httpResp.getStatusLine().getStatusCode() == 200){
////				ret = json;
////			}
////		} catch (Exception e1) {
////			e1.printStackTrace();
////		}
////		return ret;
////    }
//
//    protected class ImageMessageBroadcastReceiver extends BroadcastReceiver {
//    	//private Feeds mUI;
//
//    	//public ImageMessageBroadcastReceiver(Feeds ui){
//    	//	mUI = ui;
//    	//}
//        @Override
//        public void onReceive(Context context, Intent intent) {
//    		Log.d(Const.APP_NAME, Const.APP_NAME + " ImageMessageBroadcastReceiver onReceive: " + intent.toString());
//            if(intent.getAction().equals(IMAGE_LOADED)){
//            	onDataChanged(-1);
//            }
//        }
//    }
//	
//    protected class RestartBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//    		Log.d(Const.APP_NAME, Const.APP_NAME + " RestartBroadcastReceiver onReceive: " + intent.toString());
//            if(intent.getAction().equals(RESTART)){
//            	finish();
//            	startActivity(CloudFragment.this.getIntent());
//            }
//        }
//    }
//
//
////	public void confirm(String title, DialogInterface.OnClickListener listener)
////	{ 
////	      AlertDialog.Builder builders = new AlertDialog.Builder(this);
////	      builders.setTitle(title);
////	      builders.setMessage(getString(R.string.confirm_prompt));
////	      builders.setPositiveButton(getString(R.string.ok),  listener);
////	      builders.setNegativeButton(getString(R.string.cancel), null);
////	      builders.show();
////    }
//	
////	public void confirm_content(String content, DialogInterface.OnClickListener listener)
////	{ 
////	      AlertDialog.Builder builders = new AlertDialog.Builder(this);
////	      builders.setMessage(content);
////	      builders.setPositiveButton(getString(R.string.ok),  listener);
////	      builders.setNegativeButton(getString(R.string.cancel), null);
////	      builders.show();
////    }
//
//	//	public void setNoticeMenu(){
////        if(Checker.isNoticeOn(this)){
////        	mMenu.findItem(R.id.notice_on).setVisible(false);
////       		mMenu.findItem(R.id.notice_off).setVisible(true);
////        }else{
////        	mMenu.findItem(R.id.notice_on).setVisible(true);
////       		mMenu.findItem(R.id.notice_off).setVisible(false);
////        }
////	}
//	
//
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        mMenu = menu;
////        // Inflate the currently selected menu XML resource.
////        MenuInflater inflater = getMenuInflater();
////        inflater.inflate(R.menu.common, menu);
////        //setNoticeMenu();        		
////        return true;
////    }
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
////    	String prompt;
////        switch (item.getItemId()) {
//////	        case R.id.settings:
//////				startActivity(new Intent(CloudActivity.this, CloudSettings.class));
//////	            return true;
//////	        case R.id.sign_out:
//////	        	signout();
//////	            return true;
//////            case R.id.about:
//////    			startActivity(new Intent(CloudActivity.this, About.class));
//////                return true;
////            case R.id.home:
////    			goHome();
////                return true;
////            default:
////                return false;
////        }
////    }
//
//    protected void signout(){
//		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_APPEND);
//		SharedPreferences.Editor editor = pref.edit();
//		editor.remove("currentUser");
//		editor.commit();
//		
//		FacebookApp.logout(this);
//		
//    	//finish();
////		if(this.getClass().equals(Main.class)) finish();
////		Intent i = new Intent(this, Main.class);
////		i.setData(getIntent().getData());
////		startActivity(i);
//
////		Intent i = new Intent(this, Main.class);
////		startActivity(i);
//    	finish();
//    }
//
////    protected void notifyNewVersion(){
////    	try{
////			String s = Utils.getPrefString(CloudFragment.this, "version_changes", null);
////			JSONArray changes = new JSONArray(s);
////			Log.d(Const.APP_NAME, Const.APP_NAME + " CloudActitivy get " + changes.length() + " new version");
////			if(changes.length() > 0){
////				int newVersion = changes.getJSONObject(0).getInt("code");
////				String versionName = ""+(newVersion/1000.0);
////				String text = String.format(getString(R.string.not_up2date), versionName);
////				NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
////				Notification notification = Utils.getDefaultNotification(text);
////				Intent i = new Intent(this, About.class);
////				PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, 0);
////				notification.setLatestEventInfo(this, getString(R.string.cloud_update), text, launchIntent);
////				
////				nm.notify(100, notification);
////			}
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////    }
//
//    // override it in subclass
//	protected void onDataChanged(int item) {}
//    
////    protected void bindAppHeader(View header, final App app){
////		TextView tv;
////
////		tv = (TextView)header.findViewById(R.id.txtName);
////		tv.setText(app.getName());
////
////		if(app.getVersionName() != ""){
////			tv = (TextView)header.findViewById(R.id.txtVersion);
////			long size = app.getCloudSize();
////			if(size == 0) size = app.getSize();
////			tv.setText(String.format(getString(R.string.version_size), app.getVersionName(), size/1048576.0));
////		}
////		//		tv = (TextView)findViewById(R.id.txtVersion);
//////		tv.setText(String.format(getString(R.string.app_version), app.getVersionName()));
//////
//////		tv = (TextView)findViewById(R.id.txtSize);
//////		tv.setText(String.format(getString(R.string.app_size), app.getSize()/1048576.0));
////
////		ImageView iv = (ImageView)header.findViewById(R.id.icon);
////		Bitmap bm = null;
////		String url = app.getIcon();
////		bm = Utils.getImageFromFile(this, url); // file store 
////		if(bm == null)  Utils.asyncLoadImage(this, 0, url, null);
////		iv.setImageBitmap(bm);
////		
////    }
//    
////    protected void bindUserHeader(View header, JSONObject user){
////		TextView tv;
////		tv = (TextView)header.findViewById(R.id.txtName);
////		tv.setText(user.optString("name"));
////
//////		tv = (TextView)header.findViewById(R.id.txtTitle);
//////		if(title == null)
//////			tv.setVisibility(View.GONE);
//////		else
//////			tv.setText(title);
////
////		ImageView iv = (ImageView) header.findViewById(R.id.icon);
////		if(!user.isNull("avatar_mask")){
////			String mask = user.optString("avatar_mask", "");
////			String url = mask.replace("[size]", "bi");
////			Bitmap bm = Utils.getImageFromFile(this, url); 
////			if(bm == null) Utils.asyncLoadImage(this, 0, url, null);
////			if(bm != null) iv.setImageBitmap(bm);
////		}
////    }
//
//    protected void batch_follow(String ids) throws Exception{
//    	JSONObject ret = null;
//    	String actionURL = Const.HTTP_PREFIX+"/relationships/batch_follow?format=json&"+getLoginParameters()+"&ids="+ids;
//		HttpPost httpReq = new HttpPost(actionURL);
//		HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
//    }
//
////    protected boolean deleteReviewInWeb(String id){
////    	boolean ret = false;
////    	String actionURL = Const.HTTP_PREFIX + "/comments/delete/" + id + "?format=json&" + getLoginParameters();
////
////    	HttpPost httpReq = new HttpPost(actionURL);
////		try{
////			HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
////			if(httpResp.getStatusLine().getStatusCode() == 200){
////				ret = true;
////			}
////		}catch (Exception e){
////			Log.e(Const.APP_NAME, Const.APP_NAME + " delete comment error: " + e.getMessage());
////		}
////		return ret;
////    }
//
//    public String getLang(){
//        String lang = "en";
//		Locale lc = Locale.getDefault();
//		lang = lc.getLanguage(); 
//		return lang;
//    }
//
//    
//    protected void refresh(){
//    	finish();
//    	startActivity(CloudFragment.this.getIntent());
//    }
//
//    protected void showLoading(){
//    	if(viewLoading != null)
//    		viewLoading.setVisibility(View.VISIBLE);
//    }
//    protected void hideLoading(){
//    	if(viewLoading != null)
//    		viewLoading.setVisibility(View.GONE);
//    }
////    
//    protected String getCacheId(){
//    	return this.getClass().getName();
//    }
//    protected void cacheData(String data){
//    	cacheData(data, getCacheId());
//    }
//    protected void cacheData(String data, String cacheId){
//    	if(cacheId != null){
////    		Utils.setUserPrefString(this, cacheId, data);
//			CacheHelper helper = new CacheHelper(this.getActivity());
//			helper.cacheData(data, cacheId);
//    	}
//    }
//    protected String loadCache(){
//    	return loadCache(getCacheId());
//    }
//    protected String loadCache(String cacheId){
//    	if(cacheId != null){
////    		return Utils.getUserPrefString(this, cacheId, null);
//			CacheHelper helper = new CacheHelper(this.getActivity());
//    		return helper.loadCache(cacheId);
//    	}else
//    		return null;
//    }
//    protected void clearCache(){
//    	clearCache(getCacheId());
//    }
//    protected void clearCache(String cacheId){
//    	if(cacheId != null){
////    		Utils.setUserPrefString(this, cacheId, null);
//			CacheHelper helper = new CacheHelper(this.getActivity());
//			helper.clearCache(cacheId);
//    	}
//    }
//    
//    public class OnClickListener_btnInvite implements Button.OnClickListener {
//		@Override
//		public void onClick(View v) {
//			String content = String.format(getString(R.string.invite_content), Utils.getCurrentUser(CloudFragment.this).optString("name"));
//			try {
//		        Intent intent = new Intent(Intent.ACTION_SEND);
//		        intent.setType("text/plain");
//		        intent.putExtra(Intent.EXTRA_TEXT, content);
//		        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invite_subject));
//		    	Intent i = Intent.createChooser(intent, getString(R.string.tell_friends_via));
//		    	startActivity(i);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		
//	}
//    
//	public void goHome(){
////		startActivity(new Intent(this, MyFollowingReviews.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
////		startActivity(new Intent(this, PublicReviews.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//		startActivity(new Intent(this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//	}
//
//    public void flipView(View view) {
//    	float start = 0;
//    	float end = 360;
//        // Find the center of the container
//        final float centerX = view.getWidth() / 2.0f;
//        final float centerY = view.getHeight() / 2.0f;
//
//        // Create a new 3D rotation with the supplied parameter
//        // The animation listener is used to trigger the next animation
//        final Rotate3dAnimation rotation =
//                new Rotate3dAnimation(start, end, centerX, centerY, 0.0f, true);
//        rotation.setDuration(1000);
//        rotation.setFillAfter(true);
//        rotation.setInterpolator(new AccelerateInterpolator());
////        rotation.setAnimationListener(new DisplayNextView(position));
//
//        view.startAnimation(rotation);
//    }
//
//    public void flipView(final View front, final View back, final Runnable callback) {
//    	final long duration = 1000;
//
//		front.setVisibility(View.VISIBLE);
//		back.setVisibility(View.GONE);
//        float centerX = front.getWidth() / 2.0f;
//        float centerY = front.getHeight() / 2.0f;
//        Rotate3dAnimation rota1 = new Rotate3dAnimation(0, 90, centerX, centerY, 0.0f, false);
//        rota1.setDuration(duration/2);
//        rota1.setInterpolator(new AccelerateInterpolator());
//        rota1.setAnimationListener(new AnimationListener(){
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				front.setVisibility(View.GONE);
//				back.setVisibility(View.VISIBLE); 
//			}
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//			@Override
//			public void onAnimationStart(Animation animation) {}
//		});
//        front.startAnimation(rota1);
//
////        centerX = back.getWidth() / 2.0f;
////        centerY = back.getHeight() / 2.0f;
//        Rotate3dAnimation rota2 = new Rotate3dAnimation(270, 360, centerX, centerY, 0.0f, false);
//        rota2.setDuration(duration/2);
//        rota2.setInterpolator(new DecelerateInterpolator());
//        rota2.setStartOffset(duration/2);
//        rota2.setAnimationListener(new AnimationListener(){
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				if(callback != null) callback.run();
//			}
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//			@Override
//			public void onAnimationStart(Animation animation) {}
//		});
//        back.startAnimation(rota2);
//
////		front.setVisibility(View.VISIBLE);
////		back.setVisibility(View.GONE);
////		
////		animate(front).setDuration(duration/2).rotationY(90).setListener(new AnimatorListener(){
////			@Override
////			public void onAnimationStart(Animator animation) {
////			}
////			@Override
////			public void onAnimationEnd(Animator animation) {
////				front.setVisibility(View.GONE);
////				animate(front).setDuration(1).rotationY(360);
////			}
////			@Override
////			public void onAnimationCancel(Animator animation) {
////			}
////			@Override
////			public void onAnimationRepeat(Animator animation) {
////			}
////		});
//////		animate(back).setDuration(duration/2).rotationY(270); // prepare for later
////		animate(back).setDuration(duration/2).rotationY(270).setListener(new AnimatorListener(){
////			@Override
////			public void onAnimationStart(Animator animation) {
////			}
////			@Override
////			public void onAnimationEnd(Animator animation) {
////				back.setVisibility(View.VISIBLE);
////				animate(back).setDuration(duration/2).rotationY(360).setListener(new AnimatorListener(){
////					@Override
////					public void onAnimationStart(Animator animation) {
////					}
////					@Override
////					public void onAnimationEnd(Animator animation) {
////						if(callback != null) callback.run();
////					}
////					@Override
////					public void onAnimationCancel(Animator animation) {
////					}
////					@Override
////					public void onAnimationRepeat(Animator animation) {
////					}
////				});
////			}
////			@Override
////			public void onAnimationCancel(Animator animation) {
////			}
////			@Override
////			public void onAnimationRepeat(Animator animation) {
////			}
////		});
////	    
//////		animate(back).setDuration(duration/2).setStartDelay(duration).rotationY(360).setListener(new AnimatorListener(){
//////			@Override
//////			public void onAnimationStart(Animator animation) {
//////			}
//////			@Override
//////			public void onAnimationEnd(Animator animation) {
//////				if(callback != null) callback.run();
//////			}
//////			@Override
//////			public void onAnimationCancel(Animator animation) {
//////			}
//////			@Override
//////			public void onAnimationRepeat(Animator animation) {
//////			}
//////		});
//////    
//    }
}

