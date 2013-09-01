package goofy2.swably;

import goofy2.swably.data.App;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class LocalAppRefresher extends BroadcastReceiver {
	static public int claimCode = 1958355937;

    @Override
    public void onReceive(final Context context, Intent intent) {
    	if(Utils.isCaching) return;
       	//Log.d("", Const.APP_NAME + " AppRefresher working...");  
       	//Utils.cacheMyApps(context);
       	//Log.d("", Const.APP_NAME + " AppRefresher done.");
       	final String action = intent.getAction();
       	final String packageName = intent.getData().getSchemeSpecificPart();
       	Log.d("", Const.APP_NAME + " AppRefresher receive: " + action + " " + packageName);
       	final Handler handle = new Handler(); 
       	new Thread(new Runnable(){
			@Override
			public void run() {
		       	PackageManager pm = context.getPackageManager();
				try {
					AppHelper helper = new AppHelper(context);
					App app = null;
			        if(action.equals(Intent.ACTION_PACKAGE_ADDED)){
						if(!doClaim(handle, context, pm, packageName)){
							app = new App(pm, packageName);
	            			Utils.cancelNotify(context, app); 
				        	helper.addApp(setStatus(context, app));
//				        	Utils.clearCache(context, LocalAppsFragment.cacheId());
				        	Log.v("", Const.APP_NAME + " AppRefresher add: " + packageName);
						}
			        }else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
			        	app = helper.deleteApp(packageName);
			        	//Utils.reportRemove(context, app);
//			        	Utils.clearCache(context, LocalAppsFragment.cacheId());
				       	Log.v("", Const.APP_NAME + " AppRefresher delete: " + packageName);  
			        }else if(action.equals(Intent.ACTION_PACKAGE_REPLACED)){
						app = new App(pm, packageName);
            			Utils.cancelNotify(context, app); 
			        	helper.updateOrAddApp(setStatus(context, app));
//			        	Utils.clearCache(context, LocalAppsFragment.cacheId());
				       	Log.v("", Const.APP_NAME + " AppRefresher update: " + packageName);  
			        }
			        if(app != null){
						Intent i = new Intent(Const.BROADCAST_REFRESH_APP);
						i.putExtra(Const.KEY_PACKAGE, app.getPackage());
						context.sendBroadcast(i);
			        }
		//				// refresh app list
		//				Intent i = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
		//				i.putExtra(Const.KEY_FINISHED, true);
		//				context.sendBroadcast(i);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
       	}).start();
        
    }

    private boolean doClaim(Handler handler, final Context context, PackageManager pm, String packageName) {
    	boolean ret = false;
    	try{
    		PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA | PackageManager.GET_SIGNATURES);
			int code = info.versionCode;
			String name = info.versionName;
			final String signature = App.getShortSignature(info);
			if(name.equalsIgnoreCase(Const.VERSION_NAME_CLAIM)){
//				if(code == LocalAppRefresher.claimCode){
//					String userId = Utils.getCurrentUserId(context);
////					Utils.showToast(context, "userId: "+userId+" signature: "+signature);
//				}else{
////					Utils.showToast(context, "claim code is invalid");
//				}
				handler.post(new Runnable(){
					@Override
					public void run() {
						Intent i = new Intent(context, Claiming.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra(App.SIGNATURE, signature);
						context.startActivity(i);
					}
				});
				ret = true;
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		return ret;
	}

	protected App setStatus(Context context, App app){
    	App ret = app;
		JSONObject json = null;
		try {
			json = Utils.getAppStatus(context, app, true);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(json != null){
			App cloudApp = new App(json);
			if(cloudApp.getVersionCode() >= app.getVersionCode()){
				//app.getJSON().put(App.CLOUD_ID, cloud_app.getCloudId());
				ret = cloudApp;
				ret.mergeLocalApp(app);
			}
		}
		return ret;
    }
}