package goofy2.swably;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class TrackActivity extends Activity {
	public EasyTracker tracker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker = EasyTracker.getInstance(this);
	}

    @Override
    public void onStart(){
    	super.onStart();
    	tracker.activityStart(this);
    }

    @Override
    public void onStop(){
    	super.onStop();
    	tracker.activityStop(this);
    }
}
