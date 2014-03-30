package goofy2.swably;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nineoldandroids.animation.ObjectAnimator;

import goofy2.swably.R;
import goofy2.swably.data.App;
import goofy2.swably.facebook.FacebookApp;
import goofy2.utils.DownloadImage;
import goofy2.utils.ParamRunnable;
import goofy2.utils.ViewWrapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class Utils {
	static public boolean isCaching = false;

	/**
	   * 检测网络是否存在
	   */
	public static boolean HttpTest(final Context context)
	{ 
		boolean ret = true;
	    if( !Utils.isNetworkAvailable(context) ){
	    	ret = false;
	      AlertDialog.Builder builders = new AlertDialog.Builder(context);
	      builders.setTitle(context.getString(R.string.err_no_network_title));
	      builders.setMessage(context.getString(R.string.err_no_network_message));
	      //LayoutInflater _inflater = LayoutInflater.from(mActivity);
	      //View convertView = _inflater.inflate(R.layout.error,null);
	      //builders.setView(convertView);
	      builders.setPositiveButton(context.getString(R.string.settings),  new DialogInterface.OnClickListener(){
		      public void onClick(DialogInterface dialog, int which)
		      {
		    	  context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); 		      
		      }       
	      });
	      builders.setNegativeButton(context.getString(R.string.cancel), null);
	      builders.show();
	    }
	    return ret;
	}	

	public final static void alertTitle(Context context, String title, String msg){
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(msg);
		ad.show();
	}

	public final static void alert(Context context, String msg){
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		//ad.setTitle("");
		ad.setMessage(msg);
		ad.show();
	}

	public final static void showToast(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public final static void showToastLong(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	/** 
	     * 检测网络是否连接（注：需要在配置文件即AndroidManifest.xml加入权限） 
	     *  
	     * @param context 
	     * @return true : 网络连接成功 
	     * @return false : 网络连接失败 
	* */  
	public static boolean isNetworkAvailable(Context context) {  
	    // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）  
	   ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	   if (connectivity != null) {  
	       // 获取网络连接管理的对象  
		   NetworkInfo info = connectivity.getActiveNetworkInfo();  
		   if (info != null) {  
		       // 判断当前网络是否已经连接
//	           if (info.getState() == NetworkInfo.State.CONNECTED) {  
//	               return true;  
//	           }  
			   return info.isAvailable();
	       }  
		}
	   return false;  
	}
	
	public static void setCurrentUser(Context context, JSONObject user){
		Log.d(Const.APP_NAME, Const.APP_NAME + " Utils.setCurrentUser");
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS, Activity.MODE_APPEND);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("currentUser", user.toString());
		editor.commit();
	}

	public static JSONObject getCurrentUser(Context context){
		JSONObject user = null;
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS, Activity.MODE_APPEND);
		String str = pref.getString("currentUser", null);
		if(str != null){
			try {
				user = new JSONObject(str);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	public static String getCurrentUserId(Context context){
		String ret = "";
		try {
			JSONObject user = getCurrentUser(context);
			if(user != null) ret = user.getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	public static String getCurrentUserUsername(Context context){
		String ret = null;
		try {
			ret = getCurrentUser(context).getString("username");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static String getCurrentUserKey(Context context){
		String ret = null;
		try {
			ret = getCurrentUser(context).getString("key");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static Notification getDefaultNotification(String text){
		int icon = R.drawable.icon_noti;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, text, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		//notification.defaults = Notification.DEFAULT_SOUND;
		return notification;
	}

	public static String getUserPrefString(Context context, String key, String defaultValue){
		String user_id = getCurrentUserId(context);
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS+user_id, Activity.MODE_PRIVATE);
		return pref.getString(key, defaultValue);
	}

	public static void setUserPrefString(Context context, String key, String value){
		String user_id = getCurrentUserId(context);
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS+user_id, Activity.MODE_APPEND);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getPrefString(Context context, String key, String defValue){
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		return pref.getString(key, defValue);
	}

	protected static void setPrefString(Context context, String key, String value){
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getStaticHttpPrefix(Context context){
		return getPrefString(context, "static_http_prefix", Const.HTTP_PREFIX);
	}
	
	public static String formatTime(Date time){
        DateFormat formatter;
        Date now = new Date();
        if(time.getDate() == now.getDate())
        	//formatter = new SimpleDateFormat(todayFormat);
        	formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        else
        	//formatter = new SimpleDateFormat();
//        	formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        	formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
        
		return formatter.format(time);
	}
	
	public static void clearImageCache(){
		try{
			File dir = new File(Const.TMP_FOLDER);
			File[] files = dir.listFiles();
			//ArrayList<File> arrFiles = new ArrayList<File>();
			if(files.length > Const.IMAGE_CACHE_MAX_SIZE){
				Arrays.sort(files, new CompareCacheFile());
				int delNum = files.length - Const.IMAGE_CACHE_MIN_SIZE; 
				Log.v(Const.APP_NAME, Const.APP_NAME + " clearImageCache will clear " + delNum +" files");
				for(int i=0; i < delNum; i++){ 
					Log.v(Const.APP_NAME, Const.APP_NAME + " clearImageCache: " + files[i].getName());
					files[i].delete();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static class CompareCacheFile implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            return (int) (file1.lastModified()-file2.lastModified());
        }
    }
	
	static public void closeDB(SQLiteDatabase db){
//		can't close db here because the SqliteOpenHelper is now singleton. db will automatically closed when app is terminated
//		try{
//			db.close();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
	}
	
	static public String getError(String strResult){
		String ret = null;
		JSONObject json;
		try {
			json = new JSONObject(strResult);
			ret = json.getString("error_code")+": "+json.getString("error_message");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	static public String getLoginParameters(Context context){
		String ret = "t";
		if(Utils.getCurrentUser(context)!=null)
			ret = "user_id=" + Utils.getCurrentUserId(context) + "&user_key=" + Utils.getCurrentUserKey(context);
		return ret;	
	}


//	static public String getUserIdInCloud(Context context) {
//		if(Utils.getCurrentUser(context) == null){
//			cacheAppsStatus(context, new AppHelper(context).getApps());
//		}
//		String ret = Utils.getCurrentUserId(context);
//		return ret;
//	}

	static public void cacheMyApps(Context context) {
		Log.v(Const.APP_NAME, Const.APP_NAME + " caching apps...");
		try{
			isCaching = true;
			JSONArray ret = new JSONArray();
			PackageManager pm = context.getPackageManager();
			List<PackageInfo> pckInfos = pm.getInstalledPackages(PackageManager.GET_META_DATA | PackageManager.GET_SIGNATURES);
			AppHelper helper = new AppHelper(context);
			helper.clearAll();
			SQLiteDatabase db = helper.getHelper().getWritableDatabase();
			int count = 0;
			int total = getShareableAppCount(context);
			for(int i=0; i<pckInfos.size(); i++){
				PackageInfo info = pckInfos.get(i);
//	        	if(!info.applicationInfo.sourceDir.matches("/system.*")){
	        	if(isSharable(info.applicationInfo)){
					App app = new App(pm, info);
					ret.put(app.getJSON());
					helper.addApp(db, app);
					count++;
					Intent intent = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
					intent.putExtra(Const.KEY_COUNT, count);
					intent.putExtra(Const.KEY_TOTAL, total);
					intent.putExtra(Const.KEY_APP, app.getJSON().toString());
					context.sendBroadcast(intent);
//					Log.d("", app.getName() + ": " + info.applicationInfo.flags);
				}
			}
			Utils.closeDB(db);
			cacheAppsStatus(context, ret);
			Log.v(Const.APP_NAME, Const.APP_NAME + " cached apps");
			Intent intent = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
			intent.putExtra(Const.KEY_FINISHED, true);
			context.sendBroadcast(intent);
			isCaching = false;
		}finally{
			isCaching = false;
		}
//		return ret;
	}
	
	static public int getShareableAppCount(Context context){
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> pckInfos = pm.getInstalledPackages(PackageManager.GET_META_DATA | PackageManager.GET_SIGNATURES);
		int total = 0;
		for(int i=0; i<pckInfos.size(); i++){
			PackageInfo info = pckInfos.get(i);
//	    	if(!info.applicationInfo.sourceDir.matches("/system.*")){
	    	if(isSharable(info.applicationInfo)){
	    		total ++;
			}
		}
		return total;
	}

	
	static private boolean isSharable(ApplicationInfo appInfo){
		boolean result = false;
    	if( ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
    		|| (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 )
    		&& (appInfo.flags & ApplicationInfo.FLAG_STOPPED) == 0 
    		) {
    		result = true;
    	}
		return result;
	}
	
    public static void cacheAppsStatus(final Context context, JSONArray apps){
    	Timer timer = new Timer();
    	timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				Intent intent = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
				intent.putExtra(Const.KEY_LOADING, true);
				context.sendBroadcast(intent);
//				Log.v(Const.APP_NAME, Const.APP_NAME + " Utils.cacheAppsStatus loading...");
			}
    	}, 0, 200);
		try {
			Log.v(Const.APP_NAME, Const.APP_NAME + " Utils.cacheAppsStatus start...");
			String strResult = null;
			//String url = Const.HTTP_PREFIX + "/apps/status_list?" + Utils.getLoginParameters(context);
			String url = Const.HTTP_PREFIX + "/apps/status_list?" + Utils.getClientParameters(context) + "&" + Utils.getClientParameters(context);
			HttpPost httpReq = new HttpPost(url);
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT);
			httpReq.setParams(httpParameters);
			List <NameValuePair> params = new ArrayList <NameValuePair>();
			params.add(new BasicNameValuePair("format", "json"));
			params.add(new BasicNameValuePair("apps", apps.toString()));
			httpReq.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
			strResult = EntityUtils.toString(httpResp.getEntity());
			if(httpResp.getStatusLine().getStatusCode() == 200){
//				JSONObject hash = new JSONObject(strResult);
//				apps = hash.optJSONArray("apps");
				apps = new JSONArray(strResult);
				AppHelper helper =  new AppHelper(context);
				SQLiteDatabase db = helper.getHelper().getWritableDatabase();
				for(int i=0; i<apps.length(); i++){
					JSONObject json = apps.getJSONObject(i);
					App cloudApp = new App(json);
					App localApp = helper.getApp(db, cloudApp.getPackage());
					if(cloudApp.getVersionCode() < localApp.getVersionCode()){
						cloudApp = new App();
//					}else{
//						cloudApp.getJSON().put(App.IS_SHARED_BY_ME, true); // since return value of the api didn't set is_shared_by_me to true;
					}
					cloudApp.mergeLocalApp(localApp);
					helper.updateOrAddApp(db, cloudApp);
				}
				Utils.closeDB(db);

				//JSONObject user = hash.optJSONObject("user");
				//user.put("key", Utils.getCurrentUserKey(context));
				//Utils.setCurrentUser(context, user);
				
				Log.v(Const.APP_NAME, Const.APP_NAME + " Utils.cacheAppsStatus done");
			}else{
				JSONObject json = new JSONObject(strResult); 
				Log.e(Const.APP_NAME, Const.APP_NAME + " Utils.cacheAppsStatus error: " + json.optString("error_message","error"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		timer.cancel();
		Intent intent = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
		intent.putExtra(Const.KEY_LOADED, true);
		context.sendBroadcast(intent);
		Log.v(Const.APP_NAME, Const.APP_NAME + " Utils.cacheAppsStatus loaded");
   }

    static public JSONObject getAppStatus(Context context, App app, boolean submit_name) throws Exception{
		String strResult = null;
		JSONObject ret = null;
		String url = Const.HTTP_PREFIX + "/apps/status?format=json&package=" + URLEncoder.encode(app.getPackage()) + "&signature=" + URLEncoder.encode(app.getSignature()) + "&" + Utils.getClientParameters(context);
		Log.d(Const.APP_NAME, Const.APP_NAME + " Utils getAppStatus: " + url);
		if(submit_name){
			url += "&name=" + URLEncoder.encode(app.getName());  
			url += "&version_code=" + app.getVersionCode();  
		}
		HttpGet httpReq = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT_SHORT);
		httpReq.setParams(httpParameters);
		HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
		strResult = EntityUtils.toString(httpResp.getEntity());
		JSONObject json = new JSONObject(strResult);
		if(httpResp.getStatusLine().getStatusCode() == 200){
			ret = json;
			Log.d(Const.APP_NAME, Const.APP_NAME + " Utils getAppStatus successed");
		}else{
			throw new Exception(json.optString("error_message","error"));
		}
		return ret;
    }

    static public JSONObject getUserInfo(Context context, String userId) throws Exception{
		Log.d(Const.APP_NAME, Const.APP_NAME + " updating user info...");
		String strResult = null;
		JSONObject ret = null;
		String url = Const.HTTP_PREFIX + "/users/info/"+userId+"?format=json&" + Utils.getLoginParameters(context) + "&" + Utils.getClientParameters(context);
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
		Log.d(Const.APP_NAME, Const.APP_NAME + " updated user info");
		return ret;
    }

    static public JSONObject getAppInfo(Context context, String appId) throws Exception{
		String strResult = null;
		JSONObject ret = null;
		String url = Const.HTTP_PREFIX + "/apps/info/"+appId+"?format=json&" + Utils.getClientParameters(context);
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

    static public JSONObject getReviewInfo(Context context, String id) throws Exception{
		String strResult = null;
		JSONObject ret = null;
		String url = Const.HTTP_PREFIX + "/comments/info/"+id+"?format=json&" + Utils.getClientParameters(context);
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

    static public JSONObject share(Context context, App app) throws Exception{
		String strResult = null;
		JSONObject ret = null;
		String url = Const.HTTP_PREFIX + "/apps/share?format=json&package=" + URLEncoder.encode(app.getPackage()) + "&signature=" + URLEncoder.encode(app.getSignature()) + "&name=" + URLEncoder.encode(app.getName()) + "&version_code=" + app.getVersionCode() + "&" + Utils.getLoginParameters(context) + "&" + Utils.getClientParameters(context);
		HttpPost httpReq = new HttpPost(url);
//		HttpParams httpParameters = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT);
//		httpReq.setParams(httpParameters);
		//Log.d(Const.APP_NAME, Const.APP_NAME + " Update getUpdate: " + url);
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

    static public String getClientParameters(Context context){
		return getClientParameters(context, null);	
	}

    static public String getClientParameters(Context context, String lang){
		String ret = "";
    	PackageInfo pi;
		try {
			pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Locale lc = Locale.getDefault();
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
			String imei = tm.getDeviceId(); // need READ_PHONE_STATE access permission  
			if(lang == null) lang = lc.getLanguage();
			ret = "client_version=" + pi.versionCode + "&lang=" + lc.getLanguage() + "&country=" + lc.getCountry() + "&imei=" + imei;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;	
	}

	static public boolean checkVersion(Context context){
    	boolean isObsolete = false;
		try {
	    	String actionURL = Const.HTTP_PREFIX + "/account/app_version?format=json&"+Utils.getClientParameters(context, Const.LANG);
Log.d("",Const.APP_NAME + " checkVersion: " + actionURL);	    	
			HttpGet httpReq = new HttpGet(actionURL);
			Log.d(Const.APP_NAME, Const.APP_NAME + " checking version ... ");
			final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
			if(httpResp.getStatusLine().getStatusCode() == 200){
				try {
					String result = EntityUtils.toString(httpResp.getEntity());
					Utils.setPrefString(context, "version_changes", result);
					Utils.setPrefString(context, "check_version_time", ""+System.currentTimeMillis());
					JSONArray changes = new JSONArray(result);
					if(changes.length() > 0) isObsolete = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isObsolete;
    }

	public static void reportRemove(Context context, App app) {
		try {
	    	String actionURL = Const.HTTP_PREFIX + "/apps/remove?format=json&package="+URLEncoder.encode(app.getPackage())+"&signature="+URLEncoder.encode(app.getSignature())+"&"+Utils.getClientParameters(context);
	    	Log.v(Const.APP_NAME,Const.APP_NAME + " reportRemove: " + actionURL);	    	
			HttpPost httpReq = new HttpPost(actionURL);
			final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void reportWarez(Context context, App app) {
		try {
	    	String actionURL = Const.HTTP_PREFIX + "/apps/flag/"+app.getCloudId()+"?format=json"+"&"+Utils.getLoginParameters(context) + "&" + Utils.getClientParameters(context);
	    	Log.v(Const.APP_NAME,Const.APP_NAME + " reportWarez: " + actionURL);	    	
			HttpPost httpReq = new HttpPost(actionURL);
			final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static App editApp(Context context, String appId, String attributeName, String attributeValue) {
		App ret = null;
		try {
	    	String actionURL = Const.HTTP_PREFIX + "/apps/update/"+appId+"?format=json"+"&"+Utils.getLoginParameters(context) + "&" + Utils.getClientParameters(context);
	    	Log.v(Const.APP_NAME,Const.APP_NAME + " editApp: " + actionURL);	    	
			HttpPost httpReq = new HttpPost(actionURL);
			List <NameValuePair> params = new ArrayList <NameValuePair>();
			if(attributeValue != null) attributeValue = attributeValue.trim();
			params.add(new BasicNameValuePair("app["+attributeName+"]", attributeValue ));
			httpReq.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
			String strResult = EntityUtils.toString(httpResp.getEntity());
			if(httpResp.getStatusLine().getStatusCode() == 200){
				ret = new App(new JSONObject(strResult));
				Utils.cacheData(context, ret.getJSON().toString(), AppProfile.cacheId(ret.getCloudId()));
				Intent i = new Intent(Const.BROADCAST_REFRESH_APP);
				i.putExtra(Const.KEY_ID, ret.getCloudId());
				context.sendBroadcast(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static void sendNotify(Context context, Notification noti, int notificationId){
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notificationId, noti);
	}

	public static void cancelNotify(Context context, App app){
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(app.getPackage().hashCode());
    }

//	public static String size4Human(long size){
//		long k = size/1024;
//		long m = 
//	}

	public static String getImageFileName(String relative_url){
		return Const.TMP_FOLDER + "/" + relative_url.replaceAll("[\\/\\\\:*?\"<>|\\[\\]]", "_");
	}

	static public Bitmap getImageFromFile(Context context, String relative_url){
		if(relative_url == null) return null;
		String pathName = getImageFileName(relative_url);
		return getImageFromFile(context, pathName, 0, 0);
	}
	
	static public Bitmap getImageFromFile(Context context, String pathName, int reqWidth, int reqHeight){
		Bitmap bm = null;
		try{
		    // First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(pathName, options);

		    // Calculate inSampleSize
		    options.inSampleSize = (reqWidth == 0 && reqHeight == 0) ? 1 : calculateInSampleSize(options, reqWidth, reqHeight);

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    bm = BitmapFactory.decodeFile(pathName, options);

		}catch(OutOfMemoryError e){
			e.printStackTrace();
		}
		return bm;
	}

	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if(reqWidth == 0){
	    	inSampleSize = Math.round((float)height / (float)reqHeight);
	    }else if(reqHeight == 0){
            inSampleSize = Math.round((float)width / (float)reqWidth);
	    }else if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } else {
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }
	    }
	    return inSampleSize;
	}

//	public static int calculateInSampleSize(
//            BitmapFactory.Options options, int reqWidth) {
//	    // Raw height and width of image
//	    final int width = options.outWidth;
//	    int inSampleSize = 1;
//	
//	    if (width > reqWidth) {
//            inSampleSize = Math.round((float)width / (float)reqWidth);
//	    }
//	    return inSampleSize;
//	}

	public static Boolean saveImageToFile(Context context, String relative_url, int timeout){
		Boolean ret = false;
		String fileName = getImageFileName(relative_url);
		File f = new File(fileName);
		if(f.length() == 0){ // not exists or size is 0
				Log.v(Const.APP_NAME, Const.APP_NAME + " Utils saving Image: " + relative_url);
				try {
					//DownloadImage.toFile(Const.HTTP_PREFIX + relative_url , f, timeout);
					String url = relative_url;
					if(relative_url.substring(0, 1).equals("/")) url = Utils.getStaticHttpPrefix(context) + relative_url;
					DownloadImage.toFile(url , f, timeout);
					ret = true;
				} catch (Exception e) {
					e.printStackTrace();
				} catch (OutOfMemoryError e){
//					Utils.showToast(context, "Out of memory");
					e.printStackTrace();
				}
				Log.v(Const.APP_NAME, Const.APP_NAME + " Utils saved Image: " + relative_url);
		}
		return ret;
	}
	
	
	public static Boolean saveImageToFile(Context context, String relative_url, int timeout, HashMap<String, Integer> loadingImages){
		Boolean ret = false;
		String fileName = getImageFileName(relative_url);
		File f = new File(fileName);
		if(f.length() == 0){ // not exists or size is 0
			if(loadingImages == null) loadingImages = new HashMap<String, Integer>();
			if(loadingImages.size()<=Const.MULITI_DOWNLOADING){
				Log.v(Const.APP_NAME, Const.APP_NAME + " Utils saving Image: " + relative_url);
				loadingImages.put(relative_url, 1);
				try {
					//DownloadImage.toFile(Const.HTTP_PREFIX + relative_url , f, timeout);
					String url = relative_url;
					if(relative_url.substring(0, 1).equals("/")) url = Utils.getStaticHttpPrefix(context) + relative_url;
					DownloadImage.toFile(url , f, timeout);
					ret = true;
				} catch (Exception e) {
					e.printStackTrace();
				} catch (OutOfMemoryError e){
//					Utils.showToast(context, "Out of memory");
					e.printStackTrace();
				}
				loadingImages.remove(relative_url);
				Log.v(Const.APP_NAME, Const.APP_NAME + " Utils saved Image: " + relative_url);
			}
		}
		return ret;
	}

	static public void asyncLoadImage(final Context context, int delay, final String imageUrl, HashMap<String, Integer> loadingImages){
		//Log.d(Const.APP_NAME, Const.APP_NAME + " FeedHelper asyncLoadImage: "+imageUrl);
		if(loadingImages == null) loadingImages = new HashMap<String, Integer>();
		if(!loadingImages.containsKey(imageUrl)){
			if(loadingImages.size()<=Const.MULITI_DOWNLOADING){
				loadingImages.put(imageUrl, 1);
				final HashMap<String, Integer> cache = loadingImages;
		    	AsyncTask<String, Void, Long> loadTask = new AsyncTask<String, Void, Long>() {
					@Override
					protected Long doInBackground(String... params) {
						long ret = 0;
				        try {
							Thread.sleep(Integer.parseInt(params[1]));
							if(saveImageToFile(context, params[0], Const.HTTP_TIMEOUT_LONG, cache))
								ret = 1;
						} catch (Exception e) {
							Log.e(Const.APP_NAME, Const.APP_NAME + " FeedHelper asyncLoadImage err: "+e.getMessage());
							//Log.i(Const.APP_NAME, Const.APP_NAME + " FeedHelper asyncLoadImage failed, clear cache: "+imageUrl);
						}
						cache.remove(imageUrl);
						return ret;
					}
		            protected void onPostExecute(Long result) {
		            	//if(result == 1 && adapter != null) adapter.notifyDataSetChanged();
		            	if(result == 1) context.sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
		            }
		        };
		        loadTask.execute(imageUrl, ""+delay);
			}
		}
	}
	
	public static boolean saveLocalApkIcon(PackageManager pm, String packageName){
		boolean ret = true;
		PackageInfo info;
		try {
			info = pm.getPackageInfo(packageName, 0);
			if(App.saveIcon(pm, info) != null) ret = true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	static int getUnreadFollowsCount(Context context){
		return Integer.parseInt(Utils.getUserPrefString(context, Const.KEY_UNREAD_FOLLOWS_COUNT, "0"));
	}
	public static void setUnreadFollowsCount(Context context, int value){
		Utils.setUserPrefString(context, Const.KEY_UNREAD_FOLLOWS_COUNT, Integer.toString(value));
	}
	static int getUnreadReviewsCount(Context context){
		return Integer.parseInt(Utils.getUserPrefString(context, Const.KEY_UNREAD_REVIEWS_COUNT, "0"));
	}
	public static void setUnreadReviewsCount(Context context, int value){
		Utils.setUserPrefString(context, Const.KEY_UNREAD_REVIEWS_COUNT, Integer.toString(value));
	}

    protected static void follow(final Context context, final String userId, final String name, final boolean isFollow, final ParamRunnable r, final boolean toast){
    	JSONObject ret = null;
    	String actionURL = Const.HTTP_PREFIX;
    	if(isFollow)
    		actionURL += "/relationships/follow/" + userId + "/json";
    	else
    		actionURL += "/relationships/unfollow/" + userId + "/json";
    	actionURL += "?" + getLoginParameters(context);

		if(toast) Utils.showToast(context, context.getString(R.string.sending_request));
		final HttpPost httpReq = new HttpPost(actionURL);
		try{
			final Handler handler = new Handler(); 
			new Thread() {
				public void run(){
					try {
						final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
						handler.post(new Runnable() {
							public void run(){
								if(httpResp.getStatusLine().getStatusCode() == 200){
									JSONObject user;
									try {
										user = new JSONObject(EntityUtils.toString(httpResp.getEntity()));

										// notify list to change
										Intent intent;
										if(isFollow)
											intent = new Intent(Const.BROADCAST_FOLLOW_ADDED);
										else
											intent = new Intent(Const.BROADCAST_FOLLOW_DELETED);
										intent.putExtra(Const.KEY_ID, userId);
										context.sendBroadcast(intent);
										
										int res;
//										if(user.optBoolean("protected") && isFollow)
//											res = R.string.request_prompt;
//										else
											res = isFollow ? R.string.follow_prompt : R.string.unfollow_prompt;
										if(toast) Utils.showToast(context, context.getString(res).replaceAll("%s", name));
										if(r != null){
											try {
												r.param = user;
												r.run();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}else{
									try {
										Utils.showToast(context, Utils.getError(EntityUtils.toString(httpResp.getEntity())));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						});
					} catch (final Exception e) {
						handler.post(new Runnable() {
							public void run(){
//								Utils.showToast(context, e.getMessage());
								Utils.showToast(context, context.getString(R.string.err_follow));
							}
						});
						e.printStackTrace();
					}
				}
			}.start();
		}catch (Exception e){
			Log.e(Const.APP_NAME, Const.APP_NAME + " follow error: " + e.getMessage());
		}
    }

    protected static void starApp(final Context context, final String appId, final boolean isLike, final ParamRunnable r){
    	JSONObject ret = null;
    	String actionURL = Const.HTTP_PREFIX;
    	if(isLike)
    		actionURL += "/likes/add/" + appId + "/json";
    	else
    		actionURL += "/likes/cancel/" + appId + "/json";
    	actionURL += "?" + getLoginParameters(context);

		final HttpPost httpReq = new HttpPost(actionURL);
		try{
			final Handler handler = new Handler(); 
			new Thread() {
				public void run(){
					try {
						final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
						handler.post(new Runnable() {
							public void run(){
								if(httpResp.getStatusLine().getStatusCode() == 200){
									JSONObject app;
									try {
										app = new JSONObject(EntityUtils.toString(httpResp.getEntity()));

										// notify list to change
										Intent intent;
										if(isLike)
											intent = new Intent(Const.BROADCAST_STAR_ADDED);
										else
											intent = new Intent(Const.BROADCAST_STAR_DELETED);
										intent.putExtra(Const.KEY_ID, appId);
										context.sendBroadcast(intent);

										if(r != null){
											try {
												r.param = app;
												r.run();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}else{
									try {
										Utils.showToast(context, Utils.getError(EntityUtils.toString(httpResp.getEntity())));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}catch (Exception e){
			Log.e(Const.APP_NAME, Const.APP_NAME + " like error: " + e.getMessage());
		}
    }

    protected static void dig(final Context context, final String reviewId, final boolean isLike, final ParamRunnable r){
    	JSONObject ret = null;
    	String actionURL = Const.HTTP_PREFIX;
    	if(isLike)
    		actionURL += "/digs/add/" + reviewId + "/json";
    	else
    		actionURL += "/digs/cancel/" + reviewId + "/json";
    	actionURL += "?" + getLoginParameters(context);

		final HttpPost httpReq = new HttpPost(actionURL);
		try{
			final Handler handler = new Handler(); 
			new Thread() {
				public void run(){
					try {
						final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
						handler.post(new Runnable() {
							public void run(){
								if(httpResp.getStatusLine().getStatusCode() == 200){
									JSONObject review;
									try {
										review = new JSONObject(EntityUtils.toString(httpResp.getEntity()));

										// notify list to change
										Intent intent;
										if(isLike)
											intent = new Intent(Const.BROADCAST_LIKE_ADDED);
										else
											intent = new Intent(Const.BROADCAST_LIKE_DELETED);
										intent.putExtra(Const.KEY_ID, reviewId);
										context.sendBroadcast(intent);

										if(r != null){
											try {
												r.param = review;
												r.run();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}else{
									try {
										Utils.showToast(context, Utils.getError(EntityUtils.toString(httpResp.getEntity())));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}catch (Exception e){
			Log.e(Const.APP_NAME, Const.APP_NAME + " dig error: " + e.getMessage());
		}
    }

    //    public static String getSnsName(String id){
//		HashMap<String, String> names = new HashMap<String, String>();
//		names.put("twitter", "Twitter");
//		names.put("sina", "新浪微博");
//		return names.get(id);
//    }
//    public static int getSnsIcon(String id){
//		HashMap<String, Integer> names = new HashMap<String, Integer>();
//		names.put("twitter", R.drawable.twitter);
//		names.put("sina", R.drawable.sina);
//		return names.get(id);
//    }

    public static Object getSnsResource(String id, String key){
		HashMap<String, HashMap<String, Object>> map = new HashMap<String, HashMap<String, Object>>();
		HashMap<String, Object> sns;

		sns = new HashMap<String, Object>();
		sns.put("name", "Facebook");
		sns.put("icon", R.drawable.facebook);
//		sns.put("icon_ontile", R.drawable.facebook_ontile);
		sns.put("sync_prompt", "Post to Facebook");
		sns.put("url", "http://www.facebook.com/profile.php?id=%s");
		map.put("facebook", sns);

		sns = new HashMap<String, Object>();
		sns.put("name", "Google+");
		sns.put("icon", R.drawable.plus);
//		sns.put("icon_ontile", R.drawable.plus_ontile);
//		sns.put("sync_prompt", "Post to Google+");
		sns.put("url", "https://plus.google.com/%s");
		map.put("plus", sns);

		sns = new HashMap<String, Object>();
		sns.put("name", "Twitter");
		sns.put("icon", R.drawable.twitter);
//		sns.put("icon_ontile", R.drawable.twitter_ontile);
		sns.put("sync_prompt", "Post it on Twitter");
		sns.put("check_sync", R.drawable.check_sync_twitter);
		sns.put("url", "http://twitter.com/%s");
		map.put("twitter", sns);
//
		sns = new HashMap<String, Object>();
		sns.put("name", "新浪微博");
		sns.put("icon", R.drawable.sina);
//		sns.put("icon_ontile", R.drawable.sina_ontile);
		sns.put("sync_prompt", "同步到新浪微博");
		sns.put("check_sync", R.drawable.check_sync_sina);
		sns.put("url", "http://weibo.com/u/%s");
		map.put("sina", sns);
//		
		sns = new HashMap<String, Object>();
		sns.put("name", "腾讯微博");
		sns.put("icon", R.drawable.qq);
//		sns.put("icon_ontile", R.drawable.sina_ontile);
		sns.put("sync_prompt", "同步到腾讯微博");
		sns.put("check_sync", R.drawable.check_sync_qq);
		sns.put("url", "http://t.qq.com/%s");
		map.put("qq", sns);

		return map.get(id).get(key);
    }

	public static String formatTimeDistance(Context context, Date time){
		String ret;
        Date now = new Date();
        long distance = (now.getTime() - time.getTime())/1000;
		String f;
        if(distance <= 0){
        	ret = DateFormat.getTimeInstance(DateFormat.SHORT).format(time);
        }else if(distance >= 3600*24*365){
        	ret = DateFormat.getDateInstance(DateFormat.MEDIUM).format(time);
	    }else if(distance >= 3600*24*30){
	    	SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.date_format), Locale.US);
	    	ret = sdf.format(time);
        }else{
    		long d;
        	if(distance < 60){
        		f = context.getString(R.string.dist_seconds);
        		d = distance;
        	}else if(distance < 3600){
        		f = context.getString(R.string.dist_minutes);
        		d = distance/60;
        	}else if(distance <3600*24){
        		f = context.getString(R.string.dist_hours);
        		d = distance/3600;
        	}else if(distance <3600*24*7){
        		f = context.getString(R.string.dist_days);
        		d = distance/3600/24;
        	}else {
        		f = context.getString(R.string.dist_weeks);
        		d = distance/3600/24/7;
        	}
        	ret = String.format(f, d);
        }
        return ret;
	}

	public static void startDownloading(Context context, App app){
		Intent i = new Intent(context, DownloadingApp.class);
		i.setData(Uri.parse(app.getCloudId()));
		i.putExtra(Const.KEY_APP, app.getJSON().toString());
		context.startActivity(i);
//    	i = new Intent(mActivity, Downloader.class);
i = new Intent(context, DownloaderEx.class);
		i.putExtra(Const.KEY_APP, app.getJSON().toString());
		context.startService(i);
	}
	
	public static void inviteSns(Context context, String sns_id, String eid,  String content) {
		try {
	    	String actionURL = Const.HTTP_PREFIX + "/invites/sns/"+sns_id+"?format=json&eid="+URLEncoder.encode(eid)+"&content="+URLEncoder.encode(content)+"&"+Utils.getLoginParameters(context) + "&" + Utils.getClientParameters(context);
			HttpPost httpReq = new HttpPost(actionURL);
			final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static int dp2px(Activity activity, int dp){
        Display display=activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return Math.round(dp*dm.density);
	}

	public static int getScreenWidthPx(Activity activity){
        Display display=activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dm.widthPixels;
	}

	public static int getScreenHeightPx(Activity activity){
        Display display=activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dm.heightPixels;
	}
	public static void goHome(Context context){
//		startActivity(new Intent(this, MyFollowingReviews.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//		startActivity(new Intent(this, PublicReviews.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		context.startActivity(new Intent(context, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}

	protected static String[] getSignedIns(Context context){
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		String usernames = pref.getString("usernames", "");
		if(usernames.equals(""))
			return new String[0]; 
		else
			return usernames.split(Const.USERNAME_SPLITOR);
	}


	protected static void saveSignedIn(Context context, String username, String password){
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		String usernames = pref.getString("usernames", "");
		// save and make current user the first order.
		String[] names = getSignedIns(context);
		String ret = username;
    	for(int i=0; i<names.length; i++){
    		if(!names[i].equalsIgnoreCase(username)){
    			ret +=  Const.USERNAME_SPLITOR + names[i];
    		}
    	}		
		//usernames = usernames.replaceAll(username, ""); //it has problem
		//usernames = username + Const.USERNAME_SPLITOR + usernames;
		//usernames = usernames.replaceAll(Const.USERNAME_SPLITOR+"+", Const.USERNAME_SPLITOR);
		//usernames = usernames.replaceAll("^"+Const.USERNAME_SPLITOR, "");
		//usernames = usernames.replaceAll(Const.USERNAME_SPLITOR+"$", "");
		editor.putString("usernames", ret);
		if(password != null)
			editor.putString(Const.USERNAME_PREFIX+username, password);
		editor.commit();
	}

	static public void confirm(Context context, String title, DialogInterface.OnClickListener listener)
	{ 
	      confirm(context, title, context.getString(R.string.confirm_prompt), listener);
    }

	static public void confirm(Context context, String title, String message, DialogInterface.OnClickListener listener)
	{ 
	      AlertDialog.Builder builders = new AlertDialog.Builder(context);
	      if(title != null) builders.setTitle(title);
	      builders.setMessage(message);
	      builders.setPositiveButton(context.getString(R.string.ok),  listener);
	      builders.setNegativeButton(context.getString(R.string.cancel), null);
	      builders.show();
    }

	static public void signout(Context context){
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS, Activity.MODE_APPEND);
		SharedPreferences.Editor editor = pref.edit();
		editor.remove("currentUser");
		editor.commit();
		
		FacebookApp.logout(context);
		
		context.sendBroadcast(new Intent(Const.BROADCAST_FINISH));

    }

    static public void logV(Object instanceForClassName, String message){
    	Log.v(Const.APP_NAME, Const.APP_NAME + " " + instanceForClassName.getClass().getSimpleName() + " " + message);
    }

    static public void clearCache(Context context, String cacheId){
    	if(cacheId != null){
//    		Utils.setUserPrefString(this, cacheId, null);
			CacheHelper helper = new CacheHelper(context);
			helper.clearCache(cacheId);
    	}
    }

    static public void cacheData(Context context, String data, String cacheId){
    	if(cacheId != null){
//    		Utils.setUserPrefString(this, cacheId, data);
			CacheHelper helper = new CacheHelper(context);
			helper.cacheData(data, cacheId);
    	}
    }

    static public String loadCache(Context context, String cacheId){
    	if(cacheId != null){
//    		return Utils.getUserPrefString(this, cacheId, null);
			CacheHelper helper = new CacheHelper(context);
    		return helper.loadCache(cacheId);
    	}else
    		return null;
    }

    static public long getCacheAt(Context context, String cacheId) {
    	if(cacheId != null){
//    		return Utils.getUserPrefString(this, cacheId, null);
			CacheHelper helper = new CacheHelper(context);
    		return helper.getCacheAt(cacheId);
    	}else
    		return 0;
	}

	static protected boolean createTempDirectoryBeforeFroyo(Context context) {
		Const.TMP_FOLDER = "/sdcard/"+Const.APP_NAME;
	    File tempdir = new File(Const.TMP_FOLDER);
	    if (!tempdir.exists()) {
	        if (!tempdir.mkdirs()) {
	        	Utils.showToast(context, context.getString(R.string.err_no_SD));
	            //Log.d(Const.APP_NAME, "Cannot create directory: " + Const.TMP_FOLDER);
	            return false;
	        }
	    }
	    return true;
	}

	@SuppressLint("NewApi")
	static public boolean createTempDirectory(Context context) {
		if(!Const.TMP_FOLDER.equals("")) return true;
		if(Build.VERSION.SDK_INT >= 8){ 
		    File tempdir = context.getExternalFilesDir(null); // this function needs API level 8
		    if (tempdir == null) {
	        	Utils.showToast(context, context.getString(R.string.err_no_SD));
	        	return false;
		    }else{
		    	Const.TMP_FOLDER = tempdir.getAbsolutePath();
			    return true;
		    }
		}else return createTempDirectoryBeforeFroyo(context);
	}
	
	static public boolean isEmpty(String str){
		return str == null || str.trim().equals("") || str.trim().equalsIgnoreCase("null");
	}

//	static public String getTimeName(long diffInSeconds){
//		String ret = null;
//		long diff[] = new long[] { 0, 0, 0, 0 };
//	    /* sec */diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
//	    /* min */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
//	    /* hours */diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
//	    /* days */diff[0] = (diffInSeconds = (diffInSeconds / 24));
//
//	    ret = (String.format(
//	        "%d day%s, %d hour%s, %d minute%s, %d second%s",
//	        diff[0],
//	        diff[0] > 1 ? "s" : "",
//	        diff[1],
//	        diff[1] > 1 ? "s" : "",
//	        diff[2],
//	        diff[2] > 1 ? "s" : "",
//	        diff[3],
//	        diff[3] > 1 ? "s" : ""));		
//	    return ret;
//	}

	public static String getFriendlyTime(Context context, long diffInSeconds) {
		StringBuffer sb = new StringBuffer();
		
	    long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
	    long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
	    long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
	    long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
	    long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
	    long years = (diffInSeconds = (diffInSeconds / 12));

//	    if (years > 0) {
//	        if (years == 1) {
//	            sb.append("a year");
//	        } else {
//	            sb.append(years + " years");
//	        }
////	        if (years <= 6 && months > 0) {
////	            if (months == 1) {
////	                sb.append(" and a month");
////	            } else {
////	                sb.append(" and " + months + " months");
////	            }
////	        }
//	    } else if (months > 0) {
//	        if (months == 1) {
//	            sb.append("a month");
//	        } else {
//	            sb.append(months + " months");
//	        }
////	        if (months <= 6 && days > 0) {
////	            if (days == 1) {
////	                sb.append(" and a day");
////	            } else {
////	                sb.append(" and " + days + " days");
////	            }
////	        }
//	    } else if (days > 0) {
//	        if (days == 1) {
//	            sb.append("a day");
//	        } else {
//	            sb.append(days + " days");
//	        }
////	        if (days <= 3 && hrs > 0) {
////	            if (hrs == 1) {
////	                sb.append(" and an hour");
////	            } else {
////	                sb.append(" and " + hrs + " hours");
////	            }
////	        }
//	    } else if (hrs > 0) {
	    
	    String name;
	    if (hrs > 0) {
	    	name = hrs == 1 ? context.getString(R.string.a_hour) : String.format(context.getString(R.string.span_hours), hrs); 
	    } else if (min > 0) {
	    	name = min == 1 ? context.getString(R.string.a_minute) : String.format(context.getString(R.string.span_minutes), min); 
	    } else {
	    	name = sec == 1 ? context.getString(R.string.a_second) : String.format(context.getString(R.string.span_seconds), sec); 
	    }
	    sb.append(name);
	    return sb.toString();
	}

	static public String getLocalIpAddress() {  
	    try {  
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {  
	            NetworkInterface intf = en.nextElement();  
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {  
	                InetAddress inetAddress = enumIpAddr.nextElement();  
	                if (!inetAddress.isLoopbackAddress()) {  
	                    return inetAddress.getHostAddress().toString();  
	                }  
	            }  
	        }  
	    } catch (SocketException ex) {  
	        ex.printStackTrace();  
	    }  
	    return null;  
	}  

	static public JSONObject getHost(Context context) {
		JSONObject result = null;
    	String url = Const.DNS_URL;
    	String body = null;
    	Log.d(Const.APP_NAME,"getRootIp...");	    	
		HttpResponse httpResp;
		try {
			httpResp = get(url);
			body = EntityUtils.toString(httpResp.getEntity());
			if(httpResp.getStatusLine().getStatusCode() == 200){
				result = new JSONObject(body);
				Log.d(Const.APP_NAME,"getRootIp: "+result);	    	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	static protected HttpResponse get(String url) throws ClientProtocolException, IOException{
		HttpGet httpReq = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT);
		httpReq.setParams(httpParameters);
		return new DefaultHttpClient().execute(httpReq);
	}

	static protected HttpResponse post(String url, List <NameValuePair> params) throws ClientProtocolException, IOException{
		HttpPost httpReq = new HttpPost(url);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT);
		httpReq.setParams(httpParameters);
		if (params != null) httpReq.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return new DefaultHttpClient().execute(httpReq);
	}

	static public void setTouchAnim(final Context context, View v){
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					Animation anim  = AnimationUtils.loadAnimation(context, R.anim.shrink);
					anim.setFillAfter(true);
					v.startAnimation(anim);
				}else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
					Animation anim  = AnimationUtils.loadAnimation(context, R.anim.grow);
					anim.setFillAfter(true);
					v.startAnimation(anim);
				}
				return false;
			}
		});
	}

	static public String genReviewUrl(JSONObject review){
    	return "http://" + Const.DEFAULT_MAIN_HOST+"/r/"+review.optString("id")+"?r=share";
	}
	
	static public String genAppUrl(App app){
		return "http://" + Const.DEFAULT_MAIN_HOST + "/a/" + app.getCloudId()+"?r=share";
	}

	static public String genReviewShareText(JSONObject review){
    	String ret = review.optString("content") + " " + genReviewUrl(review) + " -- @"+review.optJSONObject("user").optString("screen_name");
    	if(review.optJSONObject("app") != null){
    		App app = new App(review.optJSONObject("app"));
    		ret = "#" + app.getName() + " " + ret;
    	}
    	return ret;
    }

    static public void shareTo(Context context, String text, String subject, String toPackageName, String title){
		try {
	        Intent intent = new Intent(Intent.ACTION_SEND);
	        intent.setType("text/plain");
	        intent.putExtra(Intent.EXTRA_TEXT, text);
	        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
	        intent.setPackage(toPackageName);
//	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // cause can not share to QQ more than once
	        context.startActivity(intent);
		} catch (Exception e) {
			Utils.showToastLong(context, String.format(context.getString(R.string.share_no_app_x), title));
		}
    }

    static public void shareReview(Context context, JSONObject review) {
		Intent i = new Intent(context, ShareActivity.class);
		i.putExtra(Const.KEY_TEXT, Utils.genReviewShareText(review));
		if(review.optJSONObject("app") != null){
			App app = new App(review.optJSONObject("app"));
			i.putExtra(Const.KEY_SUBJECT, app.getName());
		}else{
			i.putExtra(Const.KEY_SUBJECT, context.getString(R.string.request_an_app));
		}
		i.putExtra(Const.KEY_REVIEW, review.toString());
		context.startActivity(i);
	}

    static public String genAppShareText(App app){
    	return "#" + app.getName() + " " + genAppUrl(app);
    }

    static public void shareApp(Context context, App app) {
		Intent i = new Intent(context, ShareActivity.class);
		i.putExtra(Const.KEY_TEXT, Utils.genAppShareText(app));
		i.putExtra(Const.KEY_SUBJECT, app.getName());
		i.putExtra(Const.KEY_APP, app.getJSON().toString());
		context.startActivity(i);
	}

	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	public static void pullDown(Context context, View v){
		(new ViewWrapper(v)).setHeight(LayoutParams.WRAP_CONTENT);
		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int targetHeight = v.getMeasuredHeight();
		ObjectAnimator anim = ObjectAnimator.ofInt(new ViewWrapper(v), "Height", 0, targetHeight);
		anim.setDuration(context.getResources().getInteger(R.integer.config_longAnimTime));
		anim.start();
	}

	public static void pushUp(Context context, View v){
		(new ViewWrapper(v)).setHeight(LayoutParams.WRAP_CONTENT);
		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int currentHeight = v.getMeasuredHeight();
		ObjectAnimator anim = ObjectAnimator.ofInt(new ViewWrapper(v), "Height", currentHeight, 0);
		anim.setDuration(context.getResources().getInteger(R.integer.config_longAnimTime));
		anim.start();
	}

    @SuppressLint("NewApi")
	public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
        	if(Build.VERSION.SDK_INT < 8) return true;
        	else return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
