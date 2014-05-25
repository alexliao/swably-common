package goofy2.swably.data;
import goofy2.swably.Const;
import goofy2.utils.Base64;
import goofy2.swably.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;


import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;


public class App {
	// identity
	public static final String PACKAGE = "package";
	public static final String SIGNATURE = "signature";
	// local property
	public static final String VERSION_CODE = "version_code";
	public static final String VERSION_NAME = "version_name";
	public static final String NAME = "name";
	public static final String PATH = "path";
	public static final String ICON = "icon";
	public static final String BANNER = "banner";
	// cloud property
	public static final String CLOUD_ID = "id";
	public static final String CLOUD_APK = "apk";
	public static final String CLOUD_NAME = "cloud_name";
	public static final String CLOUD_VERSION_CODE = "cloud_version_code";
	public static final String CLOUD_VERSION_NAME = "cloud_version_name";
	public static final String CLOUD_ICON = "cloud_icon";
	public static final String CLOUD_SIZE = "size";

	public static final String DEV = "dev";
	public static final String UPDATED_AT = "updated_at";
	public static final String ENABLED = "enabled";
	public static final String DESCRIPTION = "description";
	public static final String CONTACT = "contact";

	// permenent status
	public static final String IS_SHARED_BY_ME = "is_shared_by_me";
	public static final String IS_LIKED = "is_liked";
	public static final String POSTS_COUNT = "reviews_count";
	private static final String UPLOADS_COUNT = "uploaders_count";
	public static final String DOWNLOADS_COUNT = "downloads_count";
	public static final String STARRED_COUNT = "likes_count";
	//public static final String IS_CLOUDED = "is_clouded";
	public static final String IS_SYSTEM = "is_system";
	public static final String IS_SHAREABLE = "is_shareable";
	// temperary status
	public static final String STATUS = "status";
	public static final int STATUS_UPLOADING = 1;
	

	private JSONObject mJson = null;
	
	public boolean isLocalNew(Context context){
		return (getLocalVersionCode(context) > getVersionCode());
	}

	public int getLocalVersionCode(Context context){
		int ret = -1;
	   	PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(getPackage(), PackageManager.GET_META_DATA);
			ret = pi.versionCode;
		} catch (NameNotFoundException e) {
//			e.printStackTrace();
		}
		return ret; 
	}

	public String getLocalSignature(Context context){
		String ret = null;
	   	PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(getPackage(), PackageManager.GET_SIGNATURES);
			ret = getShortSignature(pi);
		} catch (NameNotFoundException e) {
//			e.printStackTrace();
		}
		return ret; 
	}
	
	public boolean isSameSignature(Context context){
		String localSignature = getLocalSignature(context);
		String cloudSignature = getSignature();
		if(cloudSignature == null) cloudSignature = "";
		if(localSignature == null) localSignature = "";
		return cloudSignature.equals(localSignature); 
	}
	
	public boolean isInCloud(){
		return !mJson.optString(CLOUD_ID, "null").equals("null");
	}
	
	public boolean isSharedByMe(){
		return mJson.optBoolean(IS_SHARED_BY_ME);
	}

	public boolean isSystem(){
		return mJson.optBoolean(IS_SYSTEM);
	}

	public boolean isShareable(){
		return mJson.optBoolean(IS_SHAREABLE);
	}

	public boolean isLiked(){
		return mJson.optBoolean(IS_LIKED);
	}

	public String getName(){
		return mJson.optString(NAME, null);
	}
	public JSONObject getDev(){
		return mJson.optJSONObject(DEV);
	}
	public String getPackage(){
		return mJson.optString(PACKAGE, null);
	}
	public int getVersionCode(){
		return mJson.optInt(VERSION_CODE, -1);
	}
	public String getVersionName(){
		return mJson.optString(VERSION_NAME, null);
	}
	public double getUpdatedAt(){
		return mJson.optDouble(UPDATED_AT);
	}
	public String getDescription(){
		return mJson.optString(DESCRIPTION, null);
	}
	public String getContact(){
		return mJson.optString(CONTACT, null);
	}
	public boolean getEnabled(){
		return mJson.optBoolean(ENABLED, true);
	}
	public String getApkPath(){
		return mJson.optString(PATH, null);
	}
	public String getIconPath(){
		return Const.TMP_FOLDER+"/"+getIcon();
	}
	public String getIcon(){
		return mJson.optString(ICON, null);
	}
	public String getBanner(){
		return mJson.optString(BANNER, null);
	}
	public String getSignature(){
		return mJson.optString(SIGNATURE, null);
	}
	public String getCloudId(){
		return mJson == null ? null : mJson.optString(CLOUD_ID, null);
	}
	public int getStatus(){
		return mJson.optInt(STATUS);
	}
	public String getCloudApk(){
		String url = mJson.optString(CLOUD_APK, null);
		if(url != null){
//			url = url.toLowerCase(); // can't do that because file name is capital sensitive
			if(url.startsWith("/")){
				url = Const.HTTP_PREFIX+url;
			}
		}
		return url;
	}
	public long getCloudSize(){
		return mJson.optLong(CLOUD_SIZE);
	}
	public String getInstallPath(){
		return Const.APK_FOLDER + "/" + getName() + "_" + getVersionName() + ".apk";
	}
	
	public long getSize(){
		long ret = 0;
		if(getApkPath() != null){
			File f = new File(getApkPath());
			ret = f.length(); 
		}
		return ret;
	}
	public int getPostsCount(){
		return mJson.optInt(POSTS_COUNT, 0);
	}
	public int getUploadsCount(){
		return mJson.optInt(UPLOADS_COUNT, 0);
	}
	public int getDownloadsCount(){
		return mJson.optInt(DOWNLOADS_COUNT, 0);
	}
	public int getStarredCount(){
		return mJson.optInt(STARRED_COUNT, 0);
	}

	public App(){
	}
	public App(JSONObject data){
		mJson = data;
	}
	public App(PackageManager pm, String packageName) throws NameNotFoundException{
		PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA | PackageManager.GET_SIGNATURES);
		setBy(pm, info);
	}
	public App(PackageManager pm, PackageInfo info) {
		setBy(pm, info);
	}

	public JSONObject getJSON(){
		return mJson;
	}
	
	public JSONObject getReview(){
		String str = mJson.optString("review", "");
		JSONObject ret = null;
		if(!Utils.isEmpty(str))
			try {
				ret = new JSONObject(str);
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		return ret;
	}

	
	public void setBy(PackageManager pm, PackageInfo info){
		try {
			if(mJson == null) mJson = new JSONObject();
			mJson.put(PACKAGE, info.packageName);
			mJson.put(VERSION_CODE, info.versionCode);
			mJson.put(VERSION_NAME, info.versionName);
			mJson.put(PATH, info.applicationInfo.sourceDir);
			mJson.put(NAME, info.applicationInfo.loadLabel(pm));
			mJson.put(SIGNATURE, getShortSignature(info));
//			Log.d(Const.APP_NAME, Const.APP_NAME + " "+getName()+" - sign: "+getSignature());
		    mJson.put(ICON, saveIcon(pm, info));
		    
//	    	if( ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
//	        		|| (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 )
//	        		&& (appInfo.flags & ApplicationInfo.FLAG_STOPPED) == 0 
//	        		) {
//	        		result = true;
//	        	}
		    if((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 && (info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0)
		    	mJson.put(IS_SYSTEM, true);
		    if((info.applicationInfo.flags & ApplicationInfo.FLAG_STOPPED) == 0)
		    	mJson.put(IS_SHAREABLE, true);
		    	
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static public String saveIcon(PackageManager pm, PackageInfo info){
		String ret = null;
		try {
			Drawable bd = info.applicationInfo.loadIcon(pm);
			if(bd != null){
				Bitmap bm = ((BitmapDrawable)bd).getBitmap();
				String pathName = Utils.getImageFileName(info.packageName);
//				File f = new File(Const.TMP_FOLDER+"/"+getIconFileName(info));
				File f = new File(pathName);
		        FileOutputStream out = new FileOutputStream(f);   
		        bm.compress(Bitmap.CompressFormat.PNG, 100, out);
		        ret = f.getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private static String getIconFileName(PackageInfo info){
		return info.packageName + ".png";
	}

	//generate short string by first signature or null if no signature or no MD5 algorithm in device.
	static public String getShortSignature(PackageInfo info){
		Signature[] signs = info.signatures;
//		Log.d(Const.APP_NAME, Const.APP_NAME + " "+getName()+" - sign: "+signs[0].toCharsString());
//		Log.d(Const.APP_NAME, Const.APP_NAME + " sign hash: "+signs[0].toCharsString().hashCode());
		String ret = null;
		if(signs != null && signs.length > 0){
			byte[] data = signs[0].toByteArray();
			MessageDigest md5;
			try {
				md5 = MessageDigest.getInstance("MD5");
				md5.update(data);
				ret = new String(Base64.encode(md5.digest()));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}  
		}
		return ret;
	}
	
//	public void mergeCloudApp(App cloudApp){
//		try {
//			mJson.put(App.CLOUD_ID, cloudApp.getCloudId());
//			mJson.put(App.CLOUD_APK, cloudApp.getCloudApk());
//			mJson.put(App.IS_SHARED_BY_ME, cloudApp.isSharedByMe());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}

	public void mergeLocalApp(App localApp){
		try {
			if(mJson == null) mJson = new JSONObject();
			mJson.put(App.PACKAGE, localApp.getPackage());
			mJson.put(App.SIGNATURE, localApp.getSignature());
			mJson.put(App.VERSION_CODE, localApp.getVersionCode());
			mJson.put(App.VERSION_NAME, localApp.getVersionName());
			mJson.put(App.NAME, localApp.getName());
			mJson.put(App.PATH, localApp.getApkPath());
			mJson.put(App.ICON, localApp.getIcon());
			mJson.put(App.IS_SHARED_BY_ME, localApp.isSharedByMe());
			mJson.put(App.IS_SYSTEM, localApp.isSystem());
			mJson.put(App.IS_SHAREABLE, localApp.isShareable());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
