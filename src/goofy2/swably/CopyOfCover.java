package goofy2.swably;



import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.animation.AnimatorProxy;

import goofy2.swably.R;
import goofy2.utils.DownloadImage;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

public class CopyOfCover extends WithHeaderActivity {
	private Timer mTimerNext;
	private View btnStart;
	//private ImageView imgLogoBig;
//	private View headNavBar;
	//private View slogon;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover);
        disableSliding();
    }
    
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        btnStart = this.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new OnClickListener_btnStart());

        tryCacheApps();
		
        mTimerNext = new Timer();
        mTimerNext.schedule(new TimerTask(){
    		@Override
    		public void run(){
    			nextStep();
    		}
    	}, 12*1000); // delay execution
        
        if(AnimatorProxy.NEEDS_PROXY){
        	AnimatorProxy.wrap(findViewById(R.id.imgLogo)).setAlpha(0f);
        	AnimatorProxy.wrap(findViewById(R.id.imgTagline)).setAlpha(0f);
        	AnimatorProxy.wrap(findViewById(R.id.imgAppName)).setAlpha(0f);
        	AnimatorProxy.wrap(findViewById(R.id.imgBeta)).setAlpha(0f);
        }
        
    	Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
    		public void run(){
    			Animator at;
    			View view;
      	        int default_duration = 1200;
      	        int default_static_time = 300;
      	        int offset = 0;
      	        int duration = 0;
      	        
      	        view = findViewById(R.id.imgLogo);
      	        duration = default_duration;
//  	        anim = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//      	    anim = new AlphaAnimation(0f, 1f);
//      		anim.setDuration(duration);
//      	    view.startAnimation(anim);
      	        at = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
      	        at.setDuration(duration);
      	        at.setStartDelay(offset);
      	        at.start();
      	        offset += duration + default_static_time;

      	        view = findViewById(R.id.imgTagline);
      	        duration = default_duration;
      	        at = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
      	        at.setDuration(duration);
      	        at.setStartDelay(offset);
      	        at.start();
      	        offset += duration + default_static_time*6;
      	        duration = default_duration;
      	        at = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
      	        at.setDuration(duration);
      	        at.setStartDelay(offset);
      	        at.start();
      	        offset += duration + default_static_time;
      	        
       	        view = findViewById(R.id.imgAppName);
      	        duration = default_duration;
      	        at = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
      	        at.setDuration(duration);
      	        at.setStartDelay(offset);
      	        at.start();
      	        offset += duration + default_static_time;

      	        view = findViewById(R.id.imgBeta);
      	        duration = default_duration;
      	        at = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
      	        at.setDuration(duration);
      	        at.setStartDelay(offset);
      	        at.start();
      	        offset += duration + default_static_time;
    		}
    	}, 300);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    }
//    private void detectStaticHost(){
//    	long tb, te, t1 = 0, t2 = 0, r=0;
//    	File f = new File(Const.TMP_FOLDER + "/logo.png");
//    	
//    	for(int i=1; i<=3; i++){
//	    	tb = System.currentTimeMillis();
//	    	try {
//				DownloadImage.toFile(Const.HTTP_PREFIX + "/logo.png" , f, Const.HTTP_TIMEOUT_LONG);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			te = System.currentTimeMillis();
//			t1 = te - tb;
//			Log.i(Const.APP_NAME, Const.APP_NAME + " Main detectStaticHost " + Const.HTTP_PREFIX + " " + t1);
//			
//	    	tb = System.currentTimeMillis();
//	    	try {
//				DownloadImage.toFile(Const.STATIC_HTTP_PREFIX_CN + "/logo.png" , f, Const.HTTP_TIMEOUT_LONG);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			te = System.currentTimeMillis();
//			t2 = te - tb;
//			Log.i(Const.APP_NAME, Const.APP_NAME + " Main detectStaticHost " + Const.STATIC_HTTP_PREFIX_CN + " " + t2);
//			
//			if(t2 < t1) r += 1;
//			else if(t2 > t1) r -= 1;
//    	}
//    	
//		if(r > 0)
//			Utils.setPrefString(this, "static_http_prefix", Const.STATIC_HTTP_PREFIX_CN);
//		else
//			Utils.setPrefString(this, "static_http_prefix", Const.HTTP_PREFIX);
//		
//		Log.i(Const.APP_NAME, Const.APP_NAME + " Main detectStaticHost r=" + r + " select: " + Utils.getStaticHttpPrefix(this));
//		
//    }

//    @Override
//    protected void prepareUserBar(){
//    	super.prepareUserBar();
//    	if(currentUser == null && getSignedIns().length == 0){
//        	header.setVisibility(View.GONE);
//        }else{
//        	header.setVisibility(View.VISIBLE);
//            imgLogo.setVisibility(View.GONE);
//        }
//        if(currentUser == null){
//        	navigationBar.setVisibility(View.GONE);
//		    navBar.setVisibility(View.GONE);
//        	btnSnap.setText(getString(R.string.start));
//        }
//        else{
//        	navigationBar.setVisibility(View.VISIBLE);
//		    navBar.setVisibility(View.VISIBLE);
//        	btnSnap.setText(getString(R.string.snap));
//        }
//    }
    
//    @Override
//    protected void onNewIntent(Intent intent){
//    	super.onNewIntent(intent);
//    	autoSignin(intent);
//    	//setHeader();
//    }

    
//    private void setHeader(){
//    	if(currentUser == null && getSignedIns().length == 0){
//        	//slogon.setVisibility(View.VISIBLE);
//        	header.setVisibility(View.GONE);
//        }else{
//        	//slogon.setVisibility(View.GONE);
//        	header.setVisibility(View.VISIBLE);
//            imgLogo.setVisibility(View.GONE);
//        }
//        if(currentUser == null){
//		    navBar.setVisibility(View.GONE);
//        	btnSnap.setVisibility(View.GONE);
//        	btnStart.setVisibility(View.VISIBLE);
//        }
//        else{
//		    navBar.setVisibility(View.VISIBLE);
//        	btnSnap.setVisibility(View.VISIBLE);
//        	btnStart.setVisibility(View.GONE);
//        }
//    }
    
//    @Override
//    public void onResume(){
//    	super.onResume();
//    }
    
    
//    private class OnClickListener_btnHome implements Button.OnClickListener {
//		@Override
//		public void onClick(View v) {
//			//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Const.API_PREFIX + "public?" + getLoginParameters() + " &" + System.currentTimeMillis())));
//			startActivity(new Intent(Main.this, PublicStream.class));
//		}
//		
//	}

    protected void nextStep(){
		startActivity(new Intent(CopyOfCover.this, GuideTerms.class));
		//startActivity(new Intent(Main.this, MyApps.class));
		mTimerNext.cancel();
		finish();
    }
    
    protected class OnClickListener_btnStart implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			nextStep();
		}
		
	}
   

}