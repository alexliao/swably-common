package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.data.App;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AppTribtnText {
	protected CloudActivity mActivity;
	protected App mApp;
	JSONObject insideReview;
	protected TextView btnDownload;
	protected TextView btnUpload;
	protected TextView btnPlay;
	protected TextView btnInstall;
	
	public void init(final CloudActivity activity, View container, App app, JSONObject review){
		init(activity, container, app, review, null);
	}

	public void init(final CloudActivity activity, View container, App app, JSONObject review, final Runnable callback){
		mActivity = activity;
		mApp = app;
		insideReview = review;
		ViewHolder holder = (ViewHolder) container.getTag();
		
		if(holder == null) btnDownload = (TextView) container.findViewById(R.id.btnDownload);
		else btnDownload = (TextView) holder.getBtnDownload();
		btnDownload.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mApp.getEnabled()){
					if(null != insideReview)
						try {
							mApp.getJSON().put("review_id", insideReview.optInt("id"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					Utils.startDownloading(mActivity, mApp);
				}else{
					Utils.showToast(mActivity, mActivity.getString(R.string.downloading_disabled_prompt));
				}

				if(callback != null) callback.run();
			}
			
		});
		if(holder == null) btnUpload = (TextView) container.findViewById(R.id.btnUpload);
		else btnUpload = (TextView) holder.getBtnUpload();
		btnUpload.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
		        if(mActivity.redirectAnonymous(false)) return;
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
		if(holder == null) btnPlay = (TextView) container.findViewById(R.id.btnPlay);
		else btnPlay = (TextView) holder.getBtnPlay();
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
		if(holder == null) btnInstall = (TextView) container.findViewById(R.id.btnInstall);
		else btnInstall = (TextView) holder.getBtnInstall();
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
				btnDownload.setEnabled(app.getEnabled());
				if(app.getEnabled())
					btnDownload.setText(mActivity.getString(R.string.btn_download));
				else
					btnDownload.setText(mActivity.getString(R.string.btn_stop));
				btnUpload.setVisibility(View.GONE);
				btnPlay.setVisibility(View.GONE);
				btnInstall.setVisibility(View.GONE);
			}else{
				btnDownload.setVisibility(View.GONE);
//				btnDownload.setVisibility(View.VISIBLE);
				btnUpload.setVisibility(View.GONE);
				btnPlay.setVisibility(View.GONE);
				btnInstall.setVisibility(View.VISIBLE);
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
