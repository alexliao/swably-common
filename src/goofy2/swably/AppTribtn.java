package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.data.App;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AppTribtn {
	protected CloudActivity mActivity;
	protected App mApp;
	protected View btnDownload;
	protected View btnUpload;
	protected View btnPlay;
	protected View btnInstall;
	
//	public void init(final Activity activity, App app, final Runnable callback){
//		mActivity = activity;
//		mApp = app;
//		btnDownload = mActivity.findViewById(R.id.btnDownload);
//		btnDownload.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
////				Intent i = new Intent(mActivity, DownloadingApp.class);
////				i.setData(Uri.parse(app.getCloudId()));
////				i.putExtra(Const.KEY_APP, app.getJSON().toString());
////				mActivity.startActivity(i);
//////		    	i = new Intent(mActivity, Downloader.class);
////				i = new Intent(mActivity, DownloaderEx.class);
////				i.putExtra(Const.KEY_APP, app.getJSON().toString());
////				mActivity.startService(i);
//				Utils.startDownloading(mActivity, mApp);
//				if(callback != null) callback.run();
//			}
//			
//		});
//		btnUpload = mActivity.findViewById(R.id.btnUpload);
//		btnUpload.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
////				Intent i = new Intent(mActivity, OpenLocalApp.class);
////				i.putExtra(Const.KEY_APP, app.getJSON().toString());
////				mActivity.startActivity(i);
//				Intent i1 = new Intent(mActivity, UploaderEx.class);
//				//i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
//				i1.setData(Uri.parse(mApp.getPackage()));
//				mActivity.startService(i1);
//
//				Intent i2 = new Intent(mActivity, UploadingApp.class);
//				i2.setData(Uri.parse(mApp.getPackage()));
//				i2.putExtra(Const.KEY_APP, mApp.getJSON().toString());
//				mActivity.startActivity(i2);
//
//				if(callback != null) callback.run();
//			}
//			
//		});
//		btnPlay = mActivity.findViewById(R.id.btnPlay);
//		btnPlay.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//			   PackageManager packageManager = mActivity.getPackageManager(); 
//			   Intent intent=new Intent(); 
//		       intent =packageManager.getLaunchIntentForPackage(mApp.getPackage());
//		       mActivity.startActivity(intent);
//
//				if(callback != null) callback.run();
//			}
//			
//		});
//		btnInstall = mActivity.findViewById(R.id.btnInstall);
//		btnInstall.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//    			Intent i = new Intent();
//    			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    			i.setAction(Intent.ACTION_VIEW);
//    			String type = "application/vnd.android.package-archive";
//    			i.setDataAndType(Uri.fromFile(new File(mApp.getInstallPath())), type);
// 		       	mActivity.startActivity(i);
//
//				if(callback != null) callback.run();
//			}
//			
//		});
//	}

	public void init(final CloudActivity activity, View container, App app){
		init(activity, container, app, null);
	}

	public void init(final CloudActivity activity, View container, App app, final Runnable callback){
		mActivity = activity;
		mApp = app;
		ViewHolder holder = (ViewHolder) container.getTag();
		
		if(holder == null) btnDownload = container.findViewById(R.id.btnDownload);
		else btnDownload = holder.getBtnDownload();
		btnDownload.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
//				Intent i = new Intent(mActivity, DownloadingApp.class);
//				i.setData(Uri.parse(app.getCloudId()));
//				i.putExtra(Const.KEY_APP, app.getJSON().toString());
//				mActivity.startActivity(i);
////		    	i = new Intent(mActivity, Downloader.class);
//				i = new Intent(mActivity, DownloaderEx.class);
//				i.putExtra(Const.KEY_APP, app.getJSON().toString());
//				mActivity.startService(i);
				if(mApp.getEnabled()){
					Utils.startDownloading(mActivity, mApp);
				}else{
					Utils.showToast(mActivity, mActivity.getString(R.string.downloading_disabled_prompt));
				}

				if(callback != null) callback.run();
			}
			
		});
		if(holder == null) btnUpload = container.findViewById(R.id.btnUpload);
		else btnUpload = holder.getBtnUpload();
		btnUpload.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
		        if(mActivity.redirectAnonymous(false)) return;
//				Intent i = new Intent(mActivity, OpenLocalApp.class);
//				i.putExtra(Const.KEY_APP, app.getJSON().toString());
//				mActivity.startActivity(i);
				Intent i1 = new Intent(mActivity, UploaderEx.class);
				//i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
				i1.setData(Uri.parse(mApp.getPackage()));
				mActivity.startService(i1);

				Intent i2 = new Intent(mActivity, UploadingApp.class);
				i2.setData(Uri.parse(mApp.getPackage()));
				i2.putExtra(Const.KEY_APP, mApp.getJSON().toString());
				mActivity.startActivity(i2);

				if(callback != null) callback.run();
			}
			
		});
		if(holder == null) btnPlay = container.findViewById(R.id.btnPlay);
		else btnPlay = holder.getBtnPlay();
		btnPlay.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
			   PackageManager packageManager = mActivity.getPackageManager(); 
			   Intent intent=new Intent(); 
		       intent =packageManager.getLaunchIntentForPackage(mApp.getPackage());
		       try{
		    	   mActivity.startActivity(intent);
		       }catch(Exception e){
		    	   Utils.showToast(mActivity, mActivity.getString(R.string.cant_play));
		       }

				if(callback != null) callback.run();
			}
			
		});
		if(holder == null) btnInstall = container.findViewById(R.id.btnInstall);
		else btnInstall = holder.getBtnInstall();
		btnInstall.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
    			Intent i = new Intent();
    			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			i.setAction(Intent.ACTION_VIEW);
    			String type = "application/vnd.android.package-archive";
    			i.setDataAndType(Uri.fromFile(new File(mApp.getInstallPath())), type);
 		       	mActivity.startActivity(i);

				if(callback != null) callback.run();
			}
			
		});
	}

	public void setStatus(App app){
		mApp = app;
		int localVersion = app.getLocalVersionCode(mActivity);
		int cloudVersion = app.getVersionCode();
		
//		String localSignature = app.getLocalSignature(mActivity);
//		String cloudSignature = app.getSignature();
//		if(cloudSignature == null) cloudSignature = "";
//		if(localSignature == null) localSignature = "";
//		if(!cloudSignature.equals(localSignature)) localVersion = -1; // not exactly the same app
		if(!app.isSameSignature(mActivity)) localVersion = -1; // not exactly the same app
		 
		if(localVersion > cloudVersion){
			btnDownload.setVisibility(View.GONE);
			btnUpload.setVisibility(View.VISIBLE);
			btnPlay.setVisibility(View.GONE);
			btnInstall.setVisibility(View.GONE);
		}else if(localVersion < cloudVersion){
			File downloaded = new File(app.getInstallPath());
			if(downloaded.length() == 0 || !app.getEnabled()){
				btnDownload.setVisibility(View.VISIBLE);
				ImageView imgDownload = (ImageView) btnDownload.findViewById(R.id.imgDownload);
				if(app.getEnabled())
					imgDownload.setImageResource(R.drawable.hover_download);
				else
					imgDownload.setImageResource(R.drawable.hover_stop);
				btnUpload.setVisibility(View.GONE);
				btnPlay.setVisibility(View.GONE);
				btnInstall.setVisibility(View.GONE);
			}else{
//				btnDownload.setVisibility(View.GONE);
				btnDownload.setVisibility(View.VISIBLE);
				btnUpload.setVisibility(View.GONE);
				btnPlay.setVisibility(View.GONE);
//				btnInstall.setVisibility(View.VISIBLE);
			}
		}else{
			btnDownload.setVisibility(View.GONE);
			btnUpload.setVisibility(View.GONE);
			btnPlay.setVisibility(View.VISIBLE);
			btnInstall.setVisibility(View.GONE);
		}
	}
	
	static public interface ViewHolder{
		View getBtnDownload();
		View getBtnUpload();
		View getBtnPlay();
		View getBtnInstall();
	}
}
