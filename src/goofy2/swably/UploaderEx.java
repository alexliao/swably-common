package goofy2.swably;


import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.R;
import goofy2.swably.DownloaderEx.LocalBinder;
import goofy2.swably.data.App;
import goofy2.swably.fragment.AppCommentsFragment;
import goofy2.utils.JSONUtils;
import goofy2.utils.ParamRunnable;
import goofy2.utils.UploadImage;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

public class UploaderEx extends Service {
	public static final int MULITI_DOWNLOADING = 1;
	ExecutorService mPool;
//	NotificationManager mNotificationManager;
	private HashMap<String, Boolean> mPackageCanceled = new HashMap<String, Boolean>();
	private HashMap<String, Future<?>> mTasks = new HashMap<String, Future<?>>(); 
	private HashMap<String, Integer> mProgresses = new HashMap<String, Integer>(); 
	private final IBinder mBinder = new LocalBinder();
	private boolean mCheck = true;
	
	// begin for binding ----------------------------------
	public class LocalBinder extends Binder {
		UploaderEx getService() {
            return UploaderEx.this;
        }
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public void cancel(String packageName){
		Future<?> task = mTasks.get(packageName);
		if(task != null){
			boolean canceled = task.cancel(true);
			mTasks.remove(packageName);
			mPackageCanceled.put(packageName, true); // we need to finish the task in logic because the running task thread may not be canceled really.
		}
	}
	
//	public Set<String> getPackages(){
//		return mTasks.keySet();
//	}
	
	public boolean isUploading(String packageName){
		return mTasks.keySet().contains(packageName);
	}

	public int getProgress(String packageName){
		return mProgresses.get(packageName);
	}
	
// end for binding ----------------------------------

	@Override
	public void onDestroy(){
		Log.d(Const.APP_NAME, Const.APP_NAME + " UploaderEx - onDestroy");
		mCheck = false;
    	super.onDestroy();
	}

	@Override
    public void onCreate() {
		Log.d(Const.APP_NAME, Const.APP_NAME + " UploaderEx - onCreate");
		mPool = Executors.newFixedThreadPool(MULITI_DOWNLOADING);
//		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// loop check if all tasks are done then stop service
		new Thread() {
			public void run(){
				while(mCheck){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.d(Const.APP_NAME, Const.APP_NAME + " UploaderEx - check task: "+mTasks.size());
					boolean allDone = true;
					for(Future<?> task : mTasks.values()){
						if(!task.isDone()){ 
							allDone = false;
							break;
						}
					}
					if(allDone){
						stopSelf();
						break;
					}
				}
			}
		}.start();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	int ret = super.onStartCommand(intent, flags, startId); 
    	if(intent == null) return ret; // sometimes intent is null, cause FC;
//		String strApp = intent.getStringExtra(Const.KEY_APP);
		String packageName = intent.getDataString();
		final App app = new AppHelper(this).getApp(packageName);
		if(app != null){
			Log.d(Const.APP_NAME, Const.APP_NAME + " UploaderEx - onStartCommand: app: "+app.getName()+" startId: "+startId);
			if(!mTasks.containsKey(app.getPackage())){
				Future<?> task = mPool.submit(new Runnable(){
			    	public void run() {
						upload(app);
						mTasks.remove(app.getPackage());
			    	}
			    });
				mTasks.put(app.getPackage(), task);
				Intent i = new Intent(Const.BROADCAST_UPLOAD_PROGRESS);
				i.putExtra(Const.KEY_SIZE_TRANSFERRED, 0);
				i.putExtra(Const.KEY_PERCENT, 0);
				i.putExtra(Const.KEY_PACKAGE, app.getPackage());
				sendBroadcast(i);
				mProgresses.put(app.getPackage(), 0);
			}
		}			
//		return ret;
		return this.START_REDELIVER_INTENT;
    }

//    @Override
//	protected void onHandleIntent(Intent intent) {
////		String strApp = intent.getStringExtra(Const.KEY_APP);
////		JSONObject json;
////		try {
////			json = new JSONObject(strApp);
////			App app = new App(json);
////			upload(app);
////		} catch (JSONException e) {
////			e.printStackTrace();
////		}
//		String packageName = intent.getDataString();
//		if(canceled(packageName)) return;
//		App app = new AppHelper(this).getApp(packageName);
//		try {
//			App ret = new App(Utils.share(this, app));
//			if(ret.isInCloud()){
//				AppHelper helper = new AppHelper(UploaderEx.this);
//				ret.mergeLocalApp(app);
//				helper.updateOrAddApp(ret);
//
//				Intent i = new Intent(Const.BROADCAST_UPLOAD_PROGRESS);
//				i.putExtra(Const.KEY_FINISHED, true);
//				i.putExtra(Const.KEY_PACKAGE, app.getPackage());
//				//i.putExtra(Const.KEY_APP, ret.getJSON().);
//				sendBroadcast(i);
//			}else{
//				if(canceled(packageName)) return;
//				upload(app);
//			}
//			mCount ++;
//			Log.d("Clout", Const.APP_NAME + " Uploader count:" + mCount);
//		} catch (Exception e) {
//			e.printStackTrace();
//			Intent i = new Intent(Const.BROADCAST_UPLOAD_PROGRESS);
//			i.putExtra(Const.KEY_FAILED, getString(R.string.err_upload_failed));
//			i.putExtra(Const.KEY_PACKAGE, packageName);
//			sendBroadcast(i);
//		}
//		
//	}

	protected void upload(App app){
    	final String actionURL = Const.UPLOAD_HTTP_PREFIX+"/apps/upload?"+Utils.getLoginParameters(this)+"&"+Utils.getClientParameters(this);
//final String actionURL = Const.UPLOAD_HTTP_PREFIX+"/apps/uploadaaa?"+Utils.getLoginParameters(this)+"&"+Utils.getClientParameters(this);
		Map<String, String> map = JSONUtils.toMap(app.getJSON());
		map.put("format", "json");
		
		//FormFile formfile = new FormFile(fIcon.getName(), UploadImage.getBytesFromFile(fIcon), "icon_file", "image/png");
		//FormFile[] files = new FormFile[] { formfile };
		Map<String, File> files = new HashMap<String, File>();
		File fIcon = new File(app.getIconPath());
//File fIcon = new File("null");
		File fApk = new File(app.getApkPath());
		
		// sometime app is being refreshed by LocalAppRefreser, not valid to upload, need to refresh here
		if(!fIcon.exists() || !fApk.exists()){
			Utils.logV(this, "upload app not refreshed, refresh now: " + app.getJSON().toString());
	       	PackageManager pm = getPackageManager();
			try {
				App appRefresh = new App(pm, app.getPackage());
				fIcon = new File(appRefresh.getIconPath());
				fApk = new File(appRefresh.getApkPath());
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		try {
//			if(fIcon == null || fApk == null){  //refresh local app info
//				app = new App(getPackageManager(), app.getPackage());
//				fIcon = new File(app.getIconPath()+"sddf");
//				fApk = new File(app.getApkPath());
//			}
			files.put("icon_file", fIcon);
			files.put("apk_file", fApk);
			
			final long totalSize = fIcon.length()+fApk.length();
			final Map<String, String> mapParams = map;
			final Map<String, File> filesParams = files;
			final App appFinal = app;

			String ret = UploadImage.post_3(actionURL, mapParams, filesParams, true, 1024*10, new ParamRunnable() {
				public void run(){
					if(mPackageCanceled.containsKey(appFinal.getPackage())){ 
						param = true;
						mPackageCanceled.remove(appFinal.getPackage());
						return;
					}
					Intent i = new Intent(Const.BROADCAST_UPLOAD_PROGRESS);
					long sizeSent = Long.parseLong((String)param);
					int percent = (int)(sizeSent*100/totalSize-1);
            		Log.v(Const.APP_NAME, Const.APP_NAME + " progress: " + percent + " of "+appFinal.getPackage());
            		// send broadcast not more than 1 time per second to avoid UI scrolling feel not smooth 
        			long now = System.currentTimeMillis();
        			long dur = now - getLastTime();
        			long elapsed = now - getStartTime();
        			if(dur > 1000){
//            			long speed = (sizeSent-getLastSent())*1000/dur;
        				long speed = (sizeSent)*1000/elapsed;
            			long remainTime = speed > 0 ? (totalSize-sizeSent)/speed : -1;
						i.putExtra(Const.KEY_SIZE_TRANSFERRED, sizeSent);
						i.putExtra(Const.KEY_PERCENT, percent);
						i.putExtra(Const.KEY_SPEED, speed);
						i.putExtra(Const.KEY_REMAIN_TIME, remainTime);
						i.putExtra(Const.KEY_PACKAGE, appFinal.getPackage());
						sendBroadcast(i);
	        			setLastTime(now);
	        			setLastSent(sizeSent);
        			}
    				mProgresses.put(appFinal.getPackage(), percent);
					param = false;
				}
				long startTime = System.currentTimeMillis();
				long lastTime = startTime;
				private long getStartTime() {
					return startTime;
				}
				private long getLastTime() {
					return lastTime;
				}
				private void setLastTime(long time) {
					lastTime = time;
				}
				long lastSent = 0;
				private long getLastSent() {
					return lastSent;
				}
				private void setLastSent(long bytes) {
					lastSent = bytes;
				}
			});
			JSONObject json = new JSONObject(ret);
			App cloudApp = new App(json);
			AppHelper helper = new AppHelper(UploaderEx.this);
			cloudApp.mergeLocalApp(appFinal);
			helper.updateOrAddApp(cloudApp);
			
			// force AppCommentsFragment to refresh
//			Utils.clearCache(UploaderEx.this, AppProfile.cacheId(cloudApp.getCloudId()));
			Utils.cacheData(this, cloudApp.getJSON().toString(), AppProfile.cacheId(cloudApp.getCloudId()));
			Utils.clearCache(UploaderEx.this, AppCommentsFragment.cacheId(cloudApp.getCloudId()));
//			Utils.clearCache(UploaderEx.this, LocalAppsFragment.cacheId());
//			Utils.clearCache(UploaderEx.this, UserLikedAppsFragment.cacheId(Utils.getCurrentUserId(this)));
			
			//notifyFinished(appFinal, getString(R.string.uploaded));
			Intent i = new Intent(Const.BROADCAST_UPLOAD_PROGRESS);
			i.putExtra(Const.KEY_FINISHED, true);
			i.putExtra(Const.KEY_PACKAGE, appFinal.getPackage());
			//i.putExtra(Const.KEY_APP, ret);
			sendBroadcast(i);
			mProgresses.remove(appFinal.getPackage());
//						// refresh app list
//						i = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
//						i.putExtra(Const.KEY_FINISHED, true);
//						sendBroadcast(i);
			i = new Intent(Const.BROADCAST_REFRESH_APP);
			i.putExtra(Const.KEY_PACKAGE, appFinal.getPackage());
			sendBroadcast(i);
		} catch (final Exception e) {
			String errMsg = null;
			if(e.getClass() == CancellationException.class){
//				errMsg = getString(R.string.err_upload_canceled); // no need to nofify user as an error
			}else{
				errMsg = getString(R.string.err_upload_failed) + "\n" + e.toString();
				e.printStackTrace();
			}
			try {
				Thread.sleep(2000); // wait for UploadingApp initialization
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} 
			Intent i = new Intent(Const.BROADCAST_UPLOAD_PROGRESS);
			i.putExtra(Const.KEY_FAILED, errMsg);
			i.putExtra(Const.KEY_PACKAGE, app.getPackage());
			sendBroadcast(i);
			mProgresses.remove(app.getPackage());
		}
	}
	
//	protected Notification createNotifyUploading(App app){
//		int icon = R.drawable.icon;
//		String text = getString(R.string.uploading);
//		long when = System.currentTimeMillis();
//		
//		Notification noti = new Notification(icon, text, when);
//		//noti.flags |= Notification.FLAG_AUTO_CANCEL;
//		noti.flags |= Notification.FLAG_ONGOING_EVENT;
//		noti.contentView = new RemoteViews(getPackageName(),R.layout.upload_notification);
//		noti.contentView.setTextViewText(R.id.txtTitle, app.getName());
//		
//		Intent i = new Intent(this, OpenLocalApp.class);
//		//String str = app.getJSON().toString();
//		//i.putExtra(CloudActivity.APP, str);
//		i.setData(Uri.parse(app.getPackage()));
//		PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, 0);
//		noti.contentIntent = launchIntent;
//		return noti;
//	}
//	
//	protected void notifyFinished(App app, String result){
//		int icon = R.drawable.icon;
//		String text = result;
//		long when = System.currentTimeMillis();
//		String expandedText = result;
//		String expandedTitle = app.getName();
//		
//		Notification noti = new Notification(icon, text, when);
//		//noti.flags |= Notification.FLAG_AUTO_CANCEL;
//		
//		Intent i = new Intent(this, OpenLocalApp.class);
//		i.setData(Uri.parse(app.getPackage()));
//		PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, 0);
//		
//		noti.setLatestEventInfo(this, expandedTitle, expandedText, launchIntent);
//		
//		//Utils.sendNotify(Uploader.this, noti, app.getPackage().hashCode());
//		mNotificationManager.notify(app.getPackage().hashCode(), noti);
//	}

}
