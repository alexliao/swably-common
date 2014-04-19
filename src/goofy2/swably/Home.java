package goofy2.swably;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Home extends PublicTabs {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		this.showBehind();

        Timer timer = new Timer();
    	timer.schedule(new TimerTask(){
    		@Override
    		public void run(){
    	    	long lastCheckTime = Long.parseLong(Utils.getPrefString(getApplicationContext(), "check_version_time", "0"));
    	    	if(System.currentTimeMillis() - lastCheckTime > 3600*8*1000){
    	    		if(Utils.checkVersion(getApplicationContext()))	notifyNewVersion();	
    	    	}
    		}
    	}, 10*1000); // delay execution
		
    }

	protected void notifyNewVersion(){
    	try{
			String s = Utils.getPrefString(getApplicationContext(), "version_changes", null);
			JSONArray changes = new JSONArray(s);
			Log.d(Const.APP_NAME, Const.APP_NAME + " CloudActitivy get " + changes.length() + " new version");
			if(changes.length() > 0){
				int newVersion = changes.getJSONObject(0).getInt("code");
				String versionName = ""+(newVersion/1000.0);
				String text = String.format(getString(R.string.not_up2date), versionName);
				NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = Utils.getDefaultNotification(text);
				Intent i = new Intent(this, About.class);
				PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, 0);
				notification.setLatestEventInfo(this, getString(R.string.cloud_update), text, launchIntent);
				
				nm.notify(100, notification);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @Override
    public void onResume(){
    	super.onResume();
//    	postShowAbove();
    }

}
