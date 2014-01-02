package goofy2.swably;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.TextView;
import goofy2.swably.fragment.AppAboutFragment;
import goofy2.swably.fragment.AppCommentsFragment;
import goofy2.swably.fragment.AppHistoryFragment;
import goofy2.swably.fragment.AppUploadersFragment;
import goofy2.swably.fragment.UserReviewsFragment;
import goofy2.swably.fragment.Back_App.RefreshAppBroadcastReceiver;

public class AppHistory extends WithHeaderActivity
{
	protected AppHeader header = new AppHeader(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.app_history);
        header.setAppFromIntent();
        header.setAppFromCache(header.getAppId());
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
		bind();
		addHistoryFragment();		
    }

    protected void addHistoryFragment(){
		Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_APP, header.getApp().getJSON().toString());

		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();	
		AppHistoryFragment fragment = new AppHistoryFragment();
		fragment.setArguments(bundle);
		ft.add(R.id.fragment, fragment);
		ft.commit();

    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    }

    public void bind() {
//		TextView tv = (TextView)findViewById(R.id.txtTitle);
//		tv.setText(header.getApp().getName());
	}

}
