package goofy2.swably;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class Booter extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
Log.d("", "Clout Booter onReceive");        	
//        	final Timer timer = new Timer();
//        	timer.schedule(new TimerTask(){
//			Handler handler = new Handler();
//			handler.postDelayed(new Runnable(){
//        		public void run(){
//can't delay because anything that requires asynchronous operation is not available in this onReceive()
        			Checker.setNextInterval(context, Checker.class.getName(), 10);
        			context.startService(new Intent(context, Checker.class));
					//Utils.checkVersion(context);
//        		}
//        	}, 60*1000); // delay 1 minutes
        }
    }

}