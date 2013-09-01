package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.data.App;
import goofy2.utils.ParamRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class DownloaderEx extends Service {
	public static final int MULITI_DOWNLOADING = 2;
//	MyThreadPoolExecutor mPool;
	ExecutorService mPool;
	NotificationManager mNotificationManager;
	static public int mCount = 0;
//	static public HashMap<String, Boolean> idCanceled = new HashMap<String, Boolean>();
	HashMap<String, Boolean> mPackageCanceled = new HashMap<String, Boolean>();
	private HashMap<String, Future<?>> mTasks = new HashMap<String, Future<?>>(); 
	private final IBinder mBinder = new LocalBinder();
	
// begin for binding ----------------------------------
	public class LocalBinder extends Binder {
    	DownloaderEx getService() {
            return DownloaderEx.this;
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
//			Utils.showToast(this, canceled ? "canceled" : "not canceled");
			mTasks.remove(packageName);
			mPackageCanceled.put(packageName, true); // we need to finish the task in logic because the running task thread may not be canceled really.
		}
	}
// end for binding ----------------------------------

	@Override
    public void onCreate() {
		Log.d(Const.APP_NAME, Const.APP_NAME + " DownloaderEx - onCreate");
//		mPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(MULITI_DOWNLOADING);
//		mPool = new MyThreadPoolExecutor(MULITI_DOWNLOADING, MULITI_DOWNLOADING, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>() );
		mPool = Executors.newFixedThreadPool(MULITI_DOWNLOADING);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// loop check if all tasks are done then stop service
		new Thread() {
			public void run(){
				boolean checking = true;
				while(checking){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
//					Log.d(Const.APP_NAME, Const.APP_NAME + " DownloaderEx - check task: "+tasks.size());
					boolean allDone = true;
					for(Future<?> task : mTasks.values()){
						if(!task.isDone()){ 
							allDone = false;
							break;
						}
					}
					if(allDone){
						checking = false;
						stopSelf();
					}
				}
			}
		}.start();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	int ret = super.onStartCommand(intent, flags, startId); 
		String strApp = intent.getStringExtra(Const.KEY_APP);
		try {
			final App app = new App(new JSONObject(strApp));
			Log.d(Const.APP_NAME, Const.APP_NAME + " DownloaderEx - onStartCommand: app: "+app.getName()+" startId: "+startId);
//			if(canceled(app.getPackage())) return ret;
			
//	        mPool.execute(new Runnable(){
			if(!mTasks.containsKey(app.getPackage())){
				final Notification noti = createNotifyDownloading(app);
				mNotificationManager.notify(app.getPackage().hashCode(), noti);
				Future<?> task = mPool.submit(new Runnable(){
		        	public void run() {
		    			download(app, noti);
		        	}
		        });
				mTasks.put(app.getPackage(), task);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
//		return ret;
		return this.START_REDELIVER_INTENT;
    }
//    @Override
//    public IBinder onBind(Intent intent) {
//        // A client is binding to the service with bindService()
//        return mBinder;
//    }
//    @Override
//    public boolean onUnbind(Intent intent) {
//        // All clients have unbound with unbindService()
//        return mAllowRebind;
//    }
//    @Override
//    public void onRebind(Intent intent) {
//        // A client is binding to the service with bindService(),
//        // after onUnbind() has already been called
//    }
    @Override
    public void onDestroy() {
		Log.d(Const.APP_NAME, Const.APP_NAME + " DownloaderEx - onDestroy");
    }

	protected void download(final App app, Notification noti){
		File downloaded = new File(app.getInstallPath());
//		if(downloaded.length() == 0){ // disable downloaded apk cache to avoid refresh issue
			String tmpFileName = Const.TMP_FOLDER + "/" + System.currentTimeMillis() + ".tmp";
			File tmpFile = new File(tmpFileName);
			Intent i = new Intent(Const.BROADCAST_DOWNLOAD_PROGRESS);
			try {
				if(app.getCloudApk() == null) throw new Exception("apk path is null");
				URL url = new URL(app.getCloudApk());
				HttpURLConnection con = (HttpURLConnection)url.openConnection();
				InputStream in = con.getInputStream();
				int totalSize = con.getContentLength();
				FileOutputStream out = new FileOutputStream(tmpFile);
				byte[] bytes = new byte[10240];
				long sizeReceived = 0;
				int c;
				long startTime = System.currentTimeMillis();
				long lastTime = startTime;
				int lastPercent = 0;
				long lastReceived = 0;
				while((c = in.read(bytes)) != -1){
					if(mPackageCanceled.containsKey(app.getPackage())){ 
						in.close();
						out.close();
						tmpFile.delete();
						mNotificationManager.cancel(app.getPackage().hashCode());
						mPackageCanceled.remove(app.getPackage());
						throw new CancellationException();
					}
					out.write(bytes, 0 ,c);
					sizeReceived += c;
					 
					int percent = (int)(sizeReceived*100/totalSize);
            		Log.v(Const.APP_NAME, Const.APP_NAME + " download progress: " + percent + " of "+app.getPackage());
            		// send broadcast not more frequently to avoid UI scrolling feel not smooth 
        			long now = System.currentTimeMillis();
        			long dur = now - lastTime;
        			long elapsed = now - startTime;
        			if(dur > 1000){
//            			long speed = (sizeReceived-lastReceived)*1000/dur;
        				long speed = (sizeReceived)*1000/elapsed;
            			long remainTime = speed > 0 ? (totalSize-sizeReceived)/speed : -1;
	            		if(percent > 0 && percent < 100){
	            			if(percent - lastPercent >= MULITI_DOWNLOADING){ // avoid produce lots of notifications, make sure not more than 100 notifications of them
//		            			noti.contentView.setTextViewText(R.id.txtPercent, ""+percent+"%");
			            		noti.contentView.setProgressBar(R.id.progressBar, 100, percent, false);
			        			mNotificationManager.notify(app.getPackage().hashCode(), noti);
			        			lastPercent = percent;
	            			}
	            		}
						i.putExtra(Const.KEY_SIZE_TRANSFERRED, sizeReceived);
						i.putExtra(Const.KEY_PERCENT, percent);
						i.putExtra(Const.KEY_SPEED, speed);
						i.putExtra(Const.KEY_REMAIN_TIME, remainTime);
						i.putExtra(Const.KEY_ID, app.getCloudId());
						sendBroadcast(i);
	        			lastTime = now;
	        			lastReceived = sizeReceived;
        			}
				}
				in.close();
				out.close();
				tmpFile.renameTo(new File(app.getInstallPath()));
			} catch (final Exception e) {
				String errMsg = "";
				if(e.getClass() == CancellationException.class){
//					errMsg = getString(R.string.err_download_canceled);
				}else{
					errMsg = getString(R.string.err_download_failed)+": "+e.getMessage();
					notifyFailed(app, errMsg);
				}
				i = new Intent(Const.BROADCAST_DOWNLOAD_PROGRESS);
				i.putExtra(Const.KEY_FAILED, errMsg);
				i.putExtra(Const.KEY_ID, app.getCloudId());
				sendBroadcast(i);
			}
//		}
		if(downloaded.length() > 0){
			Intent j = new Intent(Const.BROADCAST_DOWNLOAD_PROGRESS);
			j.putExtra(Const.KEY_FINISHED, true);
			j.putExtra(Const.KEY_ID, app.getCloudId());
			//i.putExtra(Const.KEY_PATH, tmpFileName);
			sendBroadcast(j);
			notifyFinished(app);
		}
	}
	
	protected Notification createNotifyDownloading(App app){
		String text = String.format(getString(R.string.downloading_app), app.getName());
		
		Notification noti = Utils.getDefaultNotification(text);
		//noti.flags |= Notification.FLAG_AUTO_CANCEL;
		noti.flags = Notification.FLAG_ONGOING_EVENT;
		noti.contentView = new RemoteViews(getPackageName(),R.layout.download_notification);
		noti.contentView.setTextViewText(R.id.txtTitle, app.getName());
		Bitmap bm = null;
		String url = app.getIcon();
		bm = Utils.getImageFromFile(this, url); // file store 
//		if(bm == null)  Utils.asyncLoadImage(this, 0, url, null);
//		noti.contentView.setImageViewBitmap(R.id.icon, bm);
		if(bm != null) noti.contentView.setImageViewBitmap(R.id.icon, bm);
		
		Intent i = new Intent(this, DownloadingApp.class);
		String str = app.getJSON().toString();
		i.putExtra(Const.KEY_APP, str);
//		i.setData(Uri.parse(app.getPackage()));
		PendingIntent launchIntent = PendingIntent.getActivity(this, app.getPackage().hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT);
		noti.contentIntent = launchIntent;
		return noti;
	}
	
	protected void notifyFinished(App app){
		String text = String.format(getString(R.string.app_downloaded), app.getName());
		String expandedText = getString(R.string.downloaded);
		String expandedTitle = app.getName();
		
		Notification noti = Utils.getDefaultNotification(text);

		Intent i = new Intent();
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction(Intent.ACTION_VIEW);
		String type = "application/vnd.android.package-archive";
		i.setDataAndType(Uri.fromFile(new File(app.getInstallPath())), type);
		PendingIntent launchIntent = PendingIntent.getActivity(this, app.getPackage().hashCode(), i, PendingIntent.FLAG_ONE_SHOT);
		
		noti.setLatestEventInfo(this, expandedTitle, expandedText, launchIntent);
		
		mNotificationManager.notify(app.getPackage().hashCode(), noti);
	}

	protected void notifyFailed(App app, String result){
		String text = result;
		String expandedText = result;
		String expandedTitle = app.getName();
		
		Notification noti = Utils.getDefaultNotification(text);

		Intent i = new Intent(this, goofy2.swably.fragment.App.class);
		String str = app.getJSON().toString();
		i.putExtra(Const.KEY_APP, str);
		PendingIntent launchIntent = PendingIntent.getActivity(this, app.getPackage().hashCode(), i, PendingIntent.FLAG_ONE_SHOT);
		
		noti.setLatestEventInfo(this, expandedTitle, expandedText, launchIntent);
		
		mNotificationManager.notify(app.getPackage().hashCode(), noti);
	}

//	private boolean canceled(String packageName){
//		if(mPackageCanceled.containsKey(packageName)){
//			return true;
//		}else return false;
//	}

//	private class MyThreadPoolExecutor extends ThreadPoolExecutor
//	{
//
//		public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
//				long keepAliveTime, TimeUnit unit,
//				BlockingQueue<Runnable> workQueue) {
//			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
//		}
//		
//		@Override
//		protected void terminated (){
//			Log.d(Const.APP_NAME, Const.APP_NAME + " DownloaderEx.MyThreadPoolExecutor terminated");
//			super.terminated();
//		}
//		
//	}
}
