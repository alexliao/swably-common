package goofy2.swably.fragment;

import goofy2.swably.AppHeader;
import goofy2.swably.AppHelper;
import goofy2.swably.AppProfile;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.Utils;
import goofy2.swably.CloudCommentsActivity.ReviewAddedBroadcastReceiver;
import goofy2.swably.R.array;
import goofy2.swably.R.drawable;
import goofy2.swably.R.id;
import goofy2.swably.R.layout;
import goofy2.swably.R.string;
import goofy2.swably.data.App;
import goofy2.swably.fragment.App.RefreshAppBroadcastReceiver;
import goofy2.utils.AsyncImageLoader;
import goofy2.utils.JSONUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAboutFragment extends CloudFragment {
	protected AppHeader header;
//	protected LikeBar likeBar = new LikeBar(this, header);
	protected String mAppCacheId;
//	protected HoverBar hoverBar = new HoverBar();
//	protected AppTribtn tribtn = new AppTribtn(); 
	protected RefreshAppBroadcastReceiver mRefreshAppReceiver = new RefreshAppBroadcastReceiver();
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	header = new AppHeader(ca());
        header.setAppFromBundle(getArguments());
		super.onCreate(savedInstanceState);
//		mAppCacheId = AppProfile.cacheId(header.getAppId());
//		String str = ca().loadCache(mAppCacheId);
//		if(str != null){
//			try {
//				header.setApp(new App(new JSONObject(str)));
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
		
		a().registerReceiver(mRefreshAppReceiver, new IntentFilter(Const.BROADCAST_REFRESH_APP));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v = super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.app_about_fragment, container, false);
//		tribtn.init(a(), v, header.getApp());
        bind(v);
        return v;
    }

    @Override
    public void onDestroy(){
		a().unregisterReceiver(mRefreshAppReceiver);
    	super.onDestroy();
    }

    protected void bind(View v){
    	if(v == null) return; 
//		header.bindAppHeader(v);
		
		final App app = header.getApp();
    	TextView tv;
		Bitmap bm;
		String url = null;
		String str = null;
		
//		tv = (TextView)v.findViewById(R.id.txtTitle);
//		if(tv != null){
//			tv.setText(app.getName());
//			tv.setTypeface(ca().mHeaderFont);
//		}

//		tv = (TextView)v.findViewById(R.id.txtAppName);
//		tv.setText(app.getName());
		header.bindAppHeader(v);
    	v.findViewById(R.id.txtReviewsCount).setVisibility(View.GONE);
    	v.findViewById(R.id.txtSize).setVisibility(View.VISIBLE);

//		View btnReport = v.findViewById(R.id.btnReport);
//		btnReport.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Utils.confirm(a(), getString(R.string.report_title), getString(R.string.report_confirm), new OnClickListener(){
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						final Handler handler = new Handler(); 
//						new Thread(new Runnable(){
//							@Override
//							public void run() {
//								Utils.reportWarez(a(), app);
//								handler.post(new Runnable(){
//									@Override
//									public void run() {
//										Utils.showToast(a(), getString(R.string.report_sent));
//									}
//								});
//							}
//						}).start();
//					}
//				});
//			}
//		});

		View viewClaim = v.findViewById(R.id.viewClaim);
		View btnDeveloper = v.findViewById(R.id.btnDeveloper);
//		View btnManage = v.findViewById(R.id.btnManage);
		View btnEmail = v.findViewById(R.id.btnEmail);
		final JSONObject dev = app.getDev(); 
		if(dev == null){
//			viewClaim.setVisibility(View.VISIBLE);
			btnDeveloper.setVisibility(View.GONE);
//			btnManage.setVisibility(View.GONE);
			btnEmail.setVisibility(View.GONE);
//			tv = (TextView)v.findViewById(R.id.txtDevName);
//			tv.setText("");
//			View btnClaim = v.findViewById(R.id.btnClaim);
//			btnClaim.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//				  new AlertDialog.Builder(a())
//				  .setTitle(getString(R.string.claim_title))
//				  .setMessage(getString(R.string.claim_instruction))
//				  .setNegativeButton(R.string.ok, null)
//				  .show();
//				}
//			});
		}else{
//			viewClaim.setVisibility(View.GONE);
			btnDeveloper.setVisibility(View.VISIBLE);
			btnDeveloper.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ca().openUser(dev);
				}
			});
			
//			tv = (TextView)v.findViewById(R.id.txtDevName);
//			tv.setText(dev.optString("name"));
			tv = (TextView)v.findViewById(R.id.txtDevName2);
			tv.setText(dev.optString("name"));

			if(!dev.isNull("avatar_mask")){
				String mask = dev.optString("avatar_mask", "");
				url = mask.replace("[size]", "bi");
			}
			ImageView iv = (ImageView)v.findViewById(R.id.avatarDev);
			new AsyncImageLoader(a(), iv, 1).loadUrl(url);

			tv = (TextView)v.findViewById(R.id.txtContact);
			final String email = app.getContact();
			if(Utils.isEmpty(email)){
				btnEmail.setVisibility(View.GONE);
			}else{
				btnEmail.setVisibility(View.VISIBLE);
				tv.setText(email);
				btnEmail.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Uri uri = Uri.parse("mailto:"+email);  
						Intent intent = new Intent(Intent.ACTION_SENDTO, uri);  
						intent.putExtra(android.content.Intent.EXTRA_SUBJECT, app.getName());
						startActivity(intent);
					}
				});
				
			}
			
			
//			if(dev.optString("id").equals(Utils.getCurrentUserId(a()))){ // current user is the developer
//				bindManage(v, app);
//			}
		}

		tv = (TextView)v.findViewById(R.id.txtVersion2);
//		tv.setText(String.format(a().getString(R.string.app_version), app.getVersionName()));
		tv.setText(app.getVersionName());

//		tv = (TextView)v.findViewById(R.id.txtSize);
//		tv.setVisibility(View.VISIBLE);
//		long size = app.getCloudSize();
//		if(size == 0) size = app.getSize();
//		tv.setText(String.format(a().getString(R.string.app_size), size/1048576.0));

		tv = (TextView)v.findViewById(R.id.txtUpdatedOn);
//		double dTime = app.getUpdatedAt();
//		String time = Utils.formatTimeDistance(a(), new Date((long) (app.getUpdatedAt()*1000)));
    	DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
    	String time = df.format(new Date((long) (app.getUpdatedAt()*1000)));
		tv.setText(String.format(a().getString(R.string.app_updated_on), time));

//		View btnGoogle = v.findViewById(R.id.btnGoogle);
////		if(Const.LANG.equals("zh")){
////			btnGoogle.setVisibility(View.GONE);
////		}else{
//			btnGoogle.setVisibility(View.VISIBLE);
//			btnGoogle.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+app.getPackage())));
//				}
//			});
////		}

		
		tv = (TextView)v.findViewById(R.id.txtDescription);
		str = app.getDescription();
		if(Utils.isEmpty(str)){
			tv.setVisibility(View.GONE);
		}else{
			tv.setVisibility(View.VISIBLE);
			tv.setText(str);
		}

		url = app.getIcon();
		ImageView iv = (ImageView)v.findViewById(R.id.icon);
		iv.setImageResource(R.drawable.noimage);
		new AsyncImageLoader(a(), iv, 0).loadUrl(url);

		
//		tribtn.setStatus(app);

//		View btnUninstall = v.findViewById(R.id.btnUninstall);
//		
//		int localVersion = app.getLocalVersionCode(a());
//		if(!app.isSameSignature(a())) localVersion = -1; // not exactly the same app
//
//		if(localVersion >= 0){
//			btnUninstall.setVisibility(View.VISIBLE);
//			btnUninstall.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Uri packageUri = Uri.parse("package:"+header.getApp().getPackage());
//		            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
//		            startActivity(uninstallIntent);
//				}
//			});
//		}else{
//			btnUninstall.setVisibility(View.GONE);
//		}
    }
    
//    void bindManage(View v, final App app){
//		View btnManage = v.findViewById(R.id.btnManage);
//		btnManage.setVisibility(View.VISIBLE);
//		btnManage.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				int menu = app.getEnabled() ? R.array.manage_menu_enabled : R.array.manager_menu_disabled;
//		    	final String[] menuItems = getResources().getStringArray(menu);
//				Builder ad = new AlertDialog.Builder(a());
////				ad.setTitle(fullName);
//				ad.setItems(menu, new DialogInterface.OnClickListener(){
//					public void onClick(DialogInterface dialog, int whichButton){
//						String name = menuItems[whichButton];
//						if(name.equals(getString(R.string.edit_description))){
//							edit(app.getCloudId(), R.layout.edit_description, R.string.edit_description, "description", app.getDescription());
//						}else if(name.equals(getString(R.string.edit_contact))){
//							edit(app.getCloudId(), R.layout.edit_contact, R.string.edit_contact, "contact", app.getContact());
//						}else if(name.equals(getString(R.string.enable_downloading))){
//							enableDownloading(app, true);
//						}else if(name.equals(getString(R.string.disable_downloading))){
//							enableDownloading(app, false);
//						}else if(name.equals(getString(R.string.renounce_your_claim))){
//							Utils.confirm(a(), getString(R.string.renounce_title), getString(R.string.renounce_desc), new OnClickListener(){
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//				                	a().showDialog(0);
//				                	final Handler handler = new Handler();
//				                	new Thread(new Runnable(){
//										@Override
//										public void run() {
//											final App ret = Utils.editApp(a(), app.getCloudId(), "dev_id", null);
//											a().removeDialog(0);
//											handler.post(new Runnable(){
//												@Override
//												public void run() {
//													if(ret == null){
//														Utils.showToast(a(), getString(R.string.err_saving_failed));
//													}
//												}
//												
//											});
//										}
//				                	}).start();
//								}
//							});
//						}
//					}
//				});
//			    //ad.setNegativeButton(getString(R.string.cancel), null);
//				ad.show();
//			}
//		});
//    	
//    }

//    void enableDownloading(final App app, boolean enable){
//    	final int message;
//    	final String value;
//    	if(enable){
//    		value = "1";
//    		message = R.string.downloading_enabled;
//    	}else{
//    		value = "0";
//    		message = R.string.downloading_disabled;
//    	}
//    	a().showDialog(0);
//    	final Handler handler = new Handler();
//    	new Thread(new Runnable(){
//			@Override
//			public void run() {
//				final App ret = Utils.editApp(a(), app.getCloudId(), "enabled", value );
//				a().removeDialog(0);
//				handler.post(new Runnable(){
//					@Override
//					public void run() {
//						if(ret == null){
//							Utils.showToast(a(), getString(R.string.err_saving_failed));
//						}else{
//							Utils.showToast(a(), getString(message));
//						}
//					}
//					
//				});
//			}
//    	}).start();
//    }
//    
//    void edit(final String appId, int layout, int title, final String attributeName, final String defaultValue){
//		final View v = LayoutInflater.from(a()).inflate(layout, null);
//		final EditText et = (EditText) v.findViewById(R.id.editContent);
//		if(!Utils.isEmpty(defaultValue)){
//			et.setText(defaultValue);
//		}
//        new AlertDialog.Builder(a())
//        	.setCancelable(false)
//        	.setInverseBackgroundForced(true)
//            .setTitle(title)
//            .setView(v)
//            .setNegativeButton(getString(R.string.cancel), null)
//            .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                	a().showDialog(0);
//                	final Handler handler = new Handler();
//                	new Thread(new Runnable(){
//						@Override
//						public void run() {
//							final App ret = Utils.editApp(a(), appId, attributeName, et.getText().toString());
//							a().removeDialog(0);
//							handler.post(new Runnable(){
//								@Override
//								public void run() {
//									if(ret == null){
//										Utils.showToast(a(), getString(R.string.err_saving_failed));
//									}
//								}
//								
//							});
//						}
//                	}).start();
//                }
//            })
//            .show();
//    }
    
    @Override
    public String getCacheId(){
    	return this.getClass().getName()+header.getAppId();
    }

    @Override
	protected void onDataChanged(int item){
    	super.onDataChanged(item);
		bind(getView());
	}

    protected class RefreshAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_REFRESH_APP)){
            	String pkg = intent.getStringExtra(Const.KEY_PACKAGE);
            	if(pkg != null && pkg.equalsIgnoreCase(header.getApp().getPackage())){
        			AppHelper helper = new AppHelper(a());
        			goofy2.swably.data.App app = helper.getApp(pkg);
            		if(app != null) header.setApp(app);
            		bind(getView());
            	}else{
                	String id = intent.getStringExtra(Const.KEY_ID);
            		if(id != null && id.equals(header.getApp().getCloudId())){
            			String str = ca().loadCache(AppProfile.cacheId(id));
            			if(str != null){
            				try {
								header.setApp(new goofy2.swably.data.App(new JSONObject(str)));
								bind(getView());
							} catch (JSONException e) {
								e.printStackTrace();
							}
            			}
            		}
            	}
            }
        }
    }
    
    public void setApp(App newApp){
    	header.setApp(newApp);
    	bind(getView());
    }
}
