package goofy2.swably;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
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
import goofy2.swably.data.App;
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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

public class UploadingApp extends WithHeaderActivity {
	protected AppHeader header = new AppHeader(this);
	private View btnCancel;
	private ProgressBar progressBar;
	private TextView txtSizeSent;
	private int finalPercent = 0; // the final uploading percent when error occurs
	
	UploaderExServiceConnection mConnection;
	protected ProgressBroadcastReceiver mProgressReceiver = new ProgressBroadcastReceiver();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	header.setAppFromIntent();
    	//refetch app info from local
		PackageManager pm = getPackageManager();
		PackageInfo pi;
		try {
			header.setApp(new App(pm, header.getApp().getPackage()));
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
    	
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.uploading_app);
		
        btnCancel = this.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener_btnCancel());
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        int progress = getIntent().getIntExtra(Const.KEY_PERCENT, 0); 
        if( progress > 0){
    		progressBar.setIndeterminate(false);
    		progressBar.setProgress(progress);
        }

        txtSizeSent = (TextView) this.findViewById(R.id.txtSizeSent);
        txtSizeSent.setTypeface(mLightFont);

        try {
			registerReceiver(mProgressReceiver, new IntentFilter(Const.BROADCAST_UPLOAD_PROGRESS));
//	    	Intent i = new Intent(UploadApp.this, Uploader.class);
//			i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
//			startService(i);

        } catch (Exception e) {
			Log.d(Const.APP_NAME, Const.APP_NAME + " UploadApp onCreate err: "+e.getMessage());
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
    	Log.d(Const.APP_NAME, Const.APP_NAME + " UploadApp onDestroy");
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
        Intent intent = new Intent(this, UploaderEx.class);
        mConnection = new UploaderExServiceConnection();
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        UploaderEx service = mConnection.getService();  
        if(service == null) return;
//		  The following doesn't work, service always be null;
////    	if(service.isUploading(header.getApp().getPackage())) finish();
//        if(service.isUploading(header.getApp().getPackage())){
//    		int progress = service.getProgress(header.getApp().getPackage());
//    		if(progress > 0){
//	    		progressBar.setIndeterminate(false);
//	    		progressBar.setProgress(progress);
//    		}
//    	}
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
//    	findViewById(R.id.txtReviewsCount).setVisibility(View.GONE);
//    	findViewById(R.id.txtSize).setVisibility(View.VISIBLE);
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
//		Intent i = new Intent(this, UploadApp.class);
//		i.setData(Uri.parse(app.getPackage()));
//		PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, 0);
//		
//		noti.setLatestEventInfo(this, expandedTitle, expandedText, launchIntent);
//		Utils.sendNotify(this, noti, app.getPackage().hashCode());
//	}
    

    protected class ProgressBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
    		Log.d(Const.APP_NAME, Const.APP_NAME + " ProgressBroadcastReceiver onReceive: " + intent.toString());
            if(intent.getAction().equals(Const.BROADCAST_UPLOAD_PROGRESS)){
            	String packageName = intent.getStringExtra(Const.KEY_PACKAGE);
            	if(packageName.equals(header.getApp().getPackage())){
            		int percent = intent.getIntExtra(Const.KEY_PERCENT, 0);
            		long sizeSent = intent.getLongExtra(Const.KEY_SIZE_TRANSFERRED, 0);
            		if(percent > 0 ){
            			finalPercent = percent;
                		progressBar.setIndeterminate(false);
                		progressBar.setProgress(percent);
                		long speed = intent.getLongExtra(Const.KEY_SPEED, 0);
                		long remainTime = intent.getLongExtra(Const.KEY_REMAIN_TIME, 0);
//	            		if(txtSizeSent != null) txtSizeSent.setText(String.format(getString(R.string.size_sent), percent, sizeSent/1024));
	            		if(txtSizeSent != null) txtSizeSent.setText(String.format(getString(R.string.transfer_progress), speed/1024, Utils.getFriendlyTime(context, remainTime)));
            		}
            		if(progressBar.getProgress()>=100) progressBar.setIndeterminate(true);
            		final String errMsg = intent.getStringExtra(Const.KEY_FAILED);
            		if(errMsg != null){
//        				Utils.showToastLong(UploadingApp.this, getString(R.string.err_upload_failed));
            			// prompt user to report the error
            			final int reportPercent = finalPercent;
        				AlertDialog.Builder ad = new AlertDialog.Builder(context);
        				ad.setCancelable(false);
        				ad.setTitle(getString(R.string.err_upload_failed));
        				ad.setMessage(getString(R.string.send_report_prompt));
        				ad.setPositiveButton(context.getString(R.string.send_report), new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog,	int which) {
//		        				Utils.showToastLong(UploadingApp.this, errMsg);
		        				try {
		        					Date now = new Date(); 
		        					// generate report content
		        					String content = errMsg 
		        						+ "\n\nUploading percent: "	+ reportPercent
		        						+ "\n\nApp: " + header.getApp().getJSON().toString() + " size: " + header.getApp().getSize()
		        						+ "\n\nSwably: " + Utils.getClientParameters(context, Const.LANG)
		        						+ "\n\nModel: " + Build.MODEL
		        						+ "\n\nSDK: " + Build.VERSION.SDK_INT
		        						+ "\n\nTime: " + now.toString()
		        						+ "\n\nIP: " + Utils.getLocalIpAddress();
		        					JSONObject user = Utils.getCurrentUser(context);
		        					if(user != null){
		        						content += "\n\nUser: " + user.optString("id") + ", " + user.optString("name");
		        					}

		        					Uri uri = Uri.parse("mailto:alex197445@gmail.com");  
		        					Intent intent = new Intent(Intent.ACTION_SENDTO, uri);  
		        					intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.send_report_subject));
		        					intent.putExtra(android.content.Intent.EXTRA_TEXT, content);
//		        					intent.putExtra(Intent.EXTRA_STREAM, "file://"+header.getApp().getApkPath());
		        					startActivity(Intent.createChooser(intent, getString(R.string.send_report)));
//		        					startActivity(intent);
		        				} catch (Exception e) {
		        					e.printStackTrace();
		        					Utils.alert(context, getString(R.string.send_report_no_email_client));
		        				}
		        				finish();
							}
        				});
        				ad.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog,	int which) {
		        				finish();
							}
        				});
        				ad.show();
        				setExitTransition();
        			}else{
	            		boolean finished = intent.getBooleanExtra(Const.KEY_FINISHED, false);
	            		if(finished){
	            			header.setApp(new AppHelper(UploadingApp.this).getApp(header.getApp().getPackage())); // refresh from database for cloud id;
//							Intent i = new Intent(UploadApp.this, AppProfile.class);
//							i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
//							startActivity(i);
//	            			openApp(header.getApp().getJSON());
	            			Intent ret = new Intent();
	            			ret.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
	            			UploadingApp.this.setResult(RESULT_OK, ret);
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
//			Uploader.idCanceled.put(header.getApp().getCloudId(), true);
//			UploaderEx.idCanceled.put(header.getApp().getCloudId(), true);
	        if(mConnection.getService() != null){
	        	mConnection.getService().cancel(header.getApp().getPackage());
	        	Utils.cancelNotify(UploadingApp.this, header.getApp());
				Intent i = new Intent(Const.BROADCAST_UPLOAD_PROGRESS);
				i.putExtra(Const.KEY_FINISHED, true);
				i.putExtra(Const.KEY_PACKAGE, header.getApp().getPackage());
				sendBroadcast(i);
	        }
			
			finish();
			setExitTransition();
		}
		
	}

    static public class UploaderExServiceConnection implements ServiceConnection{
    	private UploaderEx mService = null;
    	
    	public UploaderEx getService(){
    		return mService;
    	}
    	
        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            UploaderEx.LocalBinder binder = (UploaderEx.LocalBinder) service;
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