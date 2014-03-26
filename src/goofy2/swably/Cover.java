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
import android.widget.TextView;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

public class Cover extends CloudActivity {
//	private Timer mTimerNext;
	private View btnStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover);
        disableSliding();
    }
    
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        TextView tv = (TextView) findViewById(R.id.txtSlogon);
        tv.setTypeface(mLightFont);
        
        btnStart = this.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nextStep();
			}
		});

//        tryCacheApps();
		
//        mTimerNext = new Timer();
//        mTimerNext.schedule(new TimerTask(){
//    		@Override
//    		public void run(){
//    			nextStep();
//    		}
//    	}, 12*1000); // delay execution
//        
//        if(AnimatorProxy.NEEDS_PROXY){
//        	AnimatorProxy.wrap(findViewById(R.id.imgLogo)).setAlpha(0f);
//        	AnimatorProxy.wrap(findViewById(R.id.imgTagline)).setAlpha(0f);
//        	AnimatorProxy.wrap(findViewById(R.id.imgAppName)).setAlpha(0f);
//        	AnimatorProxy.wrap(findViewById(R.id.imgBeta)).setAlpha(0f);
//        }
//        
//    	Handler handler = new Handler();
//		handler.postDelayed(new Runnable(){
//    		public void run(){
//    			Animator at;
//    			View view;
//      	        int default_duration = 1200;
//      	        int default_static_time = 300;
//      	        int offset = 0;
//      	        int duration = 0;
//      	        
//      	        view = findViewById(R.id.imgLogo);
//      	        duration = default_duration;
////  	        anim = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
////      	    anim = new AlphaAnimation(0f, 1f);
////      		anim.setDuration(duration);
////      	    view.startAnimation(anim);
//      	        at = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
//      	        at.setDuration(duration);
//      	        at.setStartDelay(offset);
//      	        at.start();
//      	        offset += duration + default_static_time;
//
//      	        view = findViewById(R.id.imgTagline);
//      	        duration = default_duration;
//      	        at = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
//      	        at.setDuration(duration);
//      	        at.setStartDelay(offset);
//      	        at.start();
//      	        offset += duration + default_static_time*6;
//      	        duration = default_duration;
//      	        at = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
//      	        at.setDuration(duration);
//      	        at.setStartDelay(offset);
//      	        at.start();
//      	        offset += duration + default_static_time;
//      	        
//       	        view = findViewById(R.id.imgAppName);
//      	        duration = default_duration;
//      	        at = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
//      	        at.setDuration(duration);
//      	        at.setStartDelay(offset);
//      	        at.start();
//      	        offset += duration + default_static_time;
//
//      	        view = findViewById(R.id.imgBeta);
//      	        duration = default_duration;
//      	        at = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
//      	        at.setDuration(duration);
//      	        at.setStartDelay(offset);
//      	        at.start();
//      	        offset += duration + default_static_time;
//    		}
//    	}, 300);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    }
   
    protected void nextStep(){
		startActivity(new Intent(Cover.this, GuideTerms.class));
		//startActivity(new Intent(Main.this, MyApps.class));
//		mTimerNext.cancel();
		finish();
    }
    
    protected class OnClickListener_btnStart implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			nextStep();
		}
		
	}
   

}