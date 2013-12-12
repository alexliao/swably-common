package goofy2.swably;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.App.RefreshAppBroadcastReceiver;
import goofy2.swably.data.App;
import goofy2.swably.fragment.Back_User.RefreshUserBroadcastReceiver;
import goofy2.swably.fragment.AppAboutFragment;
import goofy2.swably.fragment.AppCommentsFragment;
import goofy2.swably.fragment.UserLikedAppsFragment;
import goofy2.swably.fragment.UserReviewsFragment;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AppAbout extends WithHeaderActivity {
	String mId = null;
	protected AppHeader header = new AppHeader(this);
//	protected AppActionHelper actionHelper = new AppActionHelper(this, header);  
	protected RefreshAppBroadcastReceiver mRefreshAppReceiver = new RefreshAppBroadcastReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        mId = getIdFromUrl(i);
    	if(mId != null){
    		JSONObject json = new JSONObject();
    		try {
				json.put("id", mId);
	    		String str = loadCache(AppProfile.cacheId(mId));
	    		if(str != null){
	    			i.putExtra(Const.KEY_APP, str);
	    		}else{
		    		i.putExtra(Const.KEY_APP, json.toString());
	    		}
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.app_about);
        header.setAppFromIntent();
        header.setAppFromCache(header.getAppId());
//        if(header.getApp().getPackage() != null) header.setAppFromDb(header.getApp());
		registerReceiver(mRefreshAppReceiver, new IntentFilter(Const.BROADCAST_REFRESH_APP));
    }

    private String getIdFromUrl(Intent intent){
    	String ret = null;
    	Uri data = intent.getData();
    	if(data != null){
	    	List<String> params = data.getPathSegments();
	    	//String action = params.get(0); // "a"
	    	if("http".equalsIgnoreCase(data.getScheme())) ret = params.get(1);
    	}
    	return ret;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//		actionHelper.init(findViewById(R.id.body));
		bind();
		addAboutFragment();		
    }

    protected void addAboutFragment(){
		Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_APP, header.getApp().getJSON().toString());

		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();	
		AppAboutFragment fragment = new AppAboutFragment();
		fragment.setArguments(bundle);
		ft.add(R.id.fragment, fragment);
		ft.commit();
    }

    @Override
    public void onDestroy(){
		unregisterReceiver(mRefreshAppReceiver);
    	super.onDestroy();
    }

    public void bind() {
//		TextView tv = (TextView)findViewById(R.id.txtTitle);
//		tv.setText(header.getApp().getName());
//    	actionHelper.bind();
	}

    protected int getMenu(){
    	return R.menu.app_about;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getMenu(), menu);

    	goofy2.swably.data.App app = header.getApp();
        int localVersion = app.getLocalVersionCode(this);
		if(!app.isSameSignature(this)) localVersion = -1; // not exactly the same app
		if(localVersion < 0)
			menu.removeItem(R.id.uninstall);

		final JSONObject dev = app.getDev(); 
		if(dev != null && dev.optString("id").equals(Utils.getCurrentUserId(this))){ // current user is the developer
			menu.removeItem(R.id.claim);
		}else{
			menu.removeItem(R.id.manage);
		}
        

		return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.google_play) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+header.getApp().getPackage())));
	    	return true;
	    }else if (item.getItemId() == R.id.manage) {
	    	final goofy2.swably.data.App app = header.getApp();
			int menu = app.getEnabled() ? R.array.manage_menu_enabled : R.array.manager_menu_disabled;
	    	final String[] menuItems = getResources().getStringArray(menu);
			Builder ad = new AlertDialog.Builder(this);
//			ad.setTitle(fullName);
			ad.setItems(menu, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					String name = menuItems[whichButton];
					if(name.equals(getString(R.string.edit_description))){
						edit(app.getCloudId(), R.layout.edit_description, R.string.edit_description, "description", app.getDescription());
					}else if(name.equals(getString(R.string.edit_contact))){
						edit(app.getCloudId(), R.layout.edit_contact, R.string.edit_contact, "contact", app.getContact());
					}else if(name.equals(getString(R.string.enable_downloading))){
						enableDownloading(app, true);
					}else if(name.equals(getString(R.string.disable_downloading))){
						enableDownloading(app, false);
					}else if(name.equals(getString(R.string.renounce_your_claim))){
						renounce(app);
					}
				}
			});
			ad.show();
	    	return true;
	    }else if (item.getItemId() == R.id.claim) {
			new AlertDialog.Builder(this)
				.setTitle(getString(R.string.claim_title))
				.setMessage(getString(R.string.claim_instruction))
				.setNegativeButton(R.string.ok, null)
				.show();
	    	return true;
	    }else if (item.getItemId() == R.id.flag) {
	    	flag(header.getApp());
	    	return true;
	    }else if (item.getItemId() == R.id.uninstall) {
//	    	goofy2.swably.data.App app = header.getApp();
//			int localVersion = app.getLocalVersionCode(this);
//			if(!app.isSameSignature(this)) localVersion = -1; // not exactly the same app
//	
//			if(localVersion >= 0){
				Uri packageUri = Uri.parse("package:"+header.getApp().getPackage());
	            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
	            startActivity(uninstallIntent);
//			}
	    	return true;
		}else {
			return super.onOptionsItemSelected(item);
		}
	}
    
    void flag(final App app){
		Utils.confirm(AppAbout.this, getString(R.string.report_title), getString(R.string.report_desc), new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final Handler handler = new Handler(); 
				new Thread(new Runnable(){
					@Override
					public void run() {
						Utils.reportWarez(AppAbout.this, app);
						handler.post(new Runnable(){
							@Override
							public void run() {
								Utils.showToast(AppAbout.this, getString(R.string.report_sent));
							}
						});
					}
				}).start();
			}
		});
    }
    
    void renounce(final App app){
		Utils.confirm(AppAbout.this, getString(R.string.renounce_title), getString(R.string.renounce_desc), new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
            	showDialog(0);
            	final Handler handler = new Handler();
            	new Thread(new Runnable(){
					@Override
					public void run() {
						final App ret = Utils.editApp(AppAbout.this, app.getCloudId(), "dev_id", null);
						removeDialog(0);
						handler.post(new Runnable(){
							@Override
							public void run() {
								if(ret == null){
									Utils.showToast(AppAbout.this, getString(R.string.err_saving_failed));
								}
							}
							
						});
					}
            	}).start();
			}
		});
    }

    void enableDownloading(final App app, boolean enable){
    	final int message;
    	final String value;
    	if(enable){
    		value = "1";
    		message = R.string.downloading_enabled;
    	}else{
    		value = "0";
    		message = R.string.downloading_disabled;
    	}
    	showDialog(0);
    	final Handler handler = new Handler();
    	new Thread(new Runnable(){
			@Override
			public void run() {
				final App ret = Utils.editApp(AppAbout.this, app.getCloudId(), "enabled", value );
				removeDialog(0);
				handler.post(new Runnable(){
					@Override
					public void run() {
						if(ret == null){
							Utils.showToast(AppAbout.this, getString(R.string.err_saving_failed));
						}else{
							Utils.showToast(AppAbout.this, getString(message));
						}
					}
					
				});
			}
    	}).start();
    }
    
    void edit(final String appId, int layout, int title, final String attributeName, final String defaultValue){
		final View v = LayoutInflater.from(this).inflate(layout, null);
		final EditText et = (EditText) v.findViewById(R.id.editContent);
		if(!Utils.isEmpty(defaultValue)){
			et.setText(defaultValue);
		}
        new AlertDialog.Builder(this)
        	.setCancelable(false)
        	.setInverseBackgroundForced(true)
            .setTitle(title)
            .setView(v)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	showDialog(0);
                	final Handler handler = new Handler();
                	new Thread(new Runnable(){
						@Override
						public void run() {
							final App ret = Utils.editApp(AppAbout.this, appId, attributeName, et.getText().toString());
							removeDialog(0);
							handler.post(new Runnable(){
								@Override
								public void run() {
									if(ret == null){
										Utils.showToast(AppAbout.this, getString(R.string.err_saving_failed));
									}
								}
								
							});
						}
                	}).start();
                }
            })
            .show();
    }
	
	
	
	public class RefreshAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_REFRESH_APP)){
            	String pkg = intent.getStringExtra(Const.KEY_PACKAGE);
            	if(pkg != null && pkg.equalsIgnoreCase(header.getApp().getPackage())){
        			AppHelper helper = new AppHelper(AppAbout.this);
        			goofy2.swably.data.App app = helper.getApp(pkg);
            		if(app != null) header.setApp(app);
            		bind();
            	}else{
                	String id = intent.getStringExtra(Const.KEY_ID);
            		if(id != null && id.equals(header.getApp().getCloudId())){
            			String str = loadCache(AppProfile.cacheId(id));
            			if(str != null){
            				try {
								header.setApp(new goofy2.swably.data.App(new JSONObject(str)));
								bind();
							} catch (JSONException e) {
								e.printStackTrace();
							}
            			}
            		}
            	}
            }
        }
    }
}
