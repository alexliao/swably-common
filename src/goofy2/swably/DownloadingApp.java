package goofy2.swably;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.R;
import goofy2.swably.CloudActivity.ImageMessageBroadcastReceiver;
import goofy2.swably.CloudActivity.OnClickListener_btnSnap;
import goofy2.utils.FormFile;
import goofy2.utils.JSONUtils;
import goofy2.utils.ParamRunnable;
import goofy2.utils.UploadImage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadingApp extends WithHeaderActivity {
	protected AppHeader header = new AppHeader(this);
	private View btnCancel;
	private ProgressBar progressBar;
	private TextView txtSizeSent;
	
	DownloaderExServiceConnection mConnection;
	protected ProgressBroadcastReceiver mProgressReceiver = new ProgressBroadcastReceiver();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	header.setAppFromIntent();
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.downloading_app);
		
        btnCancel = this.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener_btnCancel());
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);

        txtSizeSent = (TextView) this.findViewById(R.id.txtSizeSent);
        txtSizeSent.setTypeface(mLightFont);

        View btnShare = this.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Utils.shareApp(DownloadingApp.this, header.getApp());
			}
        });

        try {
			registerReceiver(mProgressReceiver, new IntentFilter(Const.BROADCAST_DOWNLOAD_PROGRESS));
//	    	Intent i = new Intent(DownloadApp.this, Downloader.class);
//			i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
//			startService(i);

        } catch (Exception e) {
			Log.d(Const.APP_NAME, Const.APP_NAME + " DownloadApp onCreate err: "+e.getMessage());
			e.printStackTrace();
		}
		

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();
    }

    @Override
    public void onDestroy(){
    	Log.d(Const.APP_NAME, Const.APP_NAME + " DownloadApp onDestroy");
    	try{
    		unregisterReceiver(mProgressReceiver);
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	super.onDestroy();
    }
    
    @Override 
    public void onStart(){
//    	Utils.cancelNotify(this, mApp);
    	super.onStart();
        Intent intent = new Intent(this, DownloaderEx.class);
        mConnection = new DownloaderExServiceConnection();
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override 
    public void onStop(){
    	super.onStop();
    	if(mConnection.getService() != null) unbindService(mConnection);
    }

//    protected void updateStatus(String cloudId){
//		try {
//			mApp.getJSON().put(App.CLOUD_ID, cloudId);
//			Intent intent = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
//			intent.putExtra(Const.KEY_FINISHED, true);
//			sendBroadcast(intent);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//    }

    private void bind() {
		TextView tv;
//		tv = (TextView)findViewById(R.id.txtTitle);
//		if(tv != null){
//			tv.setText(header.getApp().getName());
//			tv.setTypeface(mHeaderFont);
//		}
    	header.bindAppHeader(viewBack);
    	findViewById(R.id.txtReviewsCount).setVisibility(View.GONE);
    	findViewById(R.id.txtSize).setVisibility(View.VISIBLE);
    }

//	protected void notifyQueued(App app){
//		int icon = R.drawable.icon;
//		String text = getString(R.string.uploading);
//		long when = System.currentTimeMillis();
//		String expandedText = getString(R.string.uploading_queued);
//		String expandedTitle = app.getName();
//		
//		Notification noti = new Notification(icon, text, when);
//		noti.flags |= Notification.FLAG_ONGOING_EVENT;
//		
//		Intent i = new Intent(this, DownloadApp.class);
//		i.setData(Uri.parse(app.getPackage()));
//		PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, 0);
//		
//		noti.setLatestEventInfo(this, expandedTitle, expandedText, launchIntent);
//		Utils.sendNotify(this, noti, app.getPackage().hashCode());
//	}
    

    protected class ProgressBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
    		Log.d(Const.APP_NAME, Const.APP_NAME + " ProgressBroadcastReceiver onReceive: " + intent.toString());
            if(intent.getAction().equals(Const.BROADCAST_DOWNLOAD_PROGRESS)){
            	String id = intent.getStringExtra(Const.KEY_ID);
            	if(id.equals(header.getApp().getCloudId())){
            		int percent = intent.getIntExtra(Const.KEY_PERCENT, 0);
            		long sizeReceived = intent.getLongExtra(Const.KEY_SIZE_TRANSFERRED, 0);
            		if(percent > 0 ){
                		progressBar.setIndeterminate(false);
                		progressBar.setProgress(percent);
                		long speed = intent.getLongExtra(Const.KEY_SPEED, 0);
                		long remainTime = intent.getLongExtra(Const.KEY_REMAIN_TIME, 0);
//	            		txtSizeSent.setText(String.format(getString(R.string.size_received), percent, sizeReceived/1024));
	            		txtSizeSent.setText(String.format(getString(R.string.transfer_progress), speed/1024, Utils.getFriendlyTime(context, remainTime)));
            		}
            		if(progressBar.getProgress()>=100) progressBar.setIndeterminate(true);
            		String errMsg = intent.getStringExtra(Const.KEY_FAILED);
            		if(errMsg != null){
            			if(!errMsg.equals("")) Utils.showToastLong(DownloadingApp.this, errMsg);
        				finish();
        				setExitTransition();
            		}else{
	            		boolean finished = intent.getBooleanExtra(Const.KEY_FINISHED, false);
	            		if(finished){
	                		//String path = intent.getStringExtra(Const.KEY_PATH);
	            			Intent i = new Intent();
	            			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            			i.setAction(Intent.ACTION_VIEW);
	            			String type = "application/vnd.android.package-archive";
	            			i.setDataAndType(Uri.fromFile(new File(header.getApp().getInstallPath())), type);
	            			startActivity(i);
	            			Utils.cancelNotify(DownloadingApp.this, header.getApp()); 
	            			finish();
	        				setExitTransition();
	            		}
            		}
            	}
            	
            }
        }
    }
		
    protected class OnClickListener_btnCancel implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
//			Downloader.idCanceled.put(header.getApp().getCloudId(), true);
//			DownloaderEx.idCanceled.put(header.getApp().getCloudId(), true);
	        if(mConnection.getService() != null){
	        	mConnection.getService().cancel(header.getApp().getPackage());
	        	Utils.cancelNotify(DownloadingApp.this, header.getApp());
	        }
			
			finish();
			setExitTransition();
		}
		
	}

    private class DownloaderExServiceConnection implements ServiceConnection{
    	private DownloaderEx mService = null;
    	
    	public DownloaderEx getService(){
    		return mService;
    	}
    	
        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            DownloaderEx.LocalBinder binder = (DownloaderEx.LocalBinder) service;
            mService = binder.getService();
//            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
//            mBound = false;
        }
    }
    
    @Override
	protected void onDataChanged(int item){
    	try {
			bind();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    protected void setExitTransition(){
    	overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
