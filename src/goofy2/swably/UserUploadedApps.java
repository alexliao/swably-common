package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.fragment.Back_User.RefreshUserBroadcastReceiver;
import goofy2.swably.fragment.UserLikedAppsFragment;
import goofy2.swably.fragment.UserReviewsFragment;
import goofy2.swably.fragment.UserUploadedAppsFragment;
import goofy2.utils.AsyncImageLoader;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserUploadedApps extends WithHeaderActivity {
	protected UserHeader header = new UserHeader(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
    	setContentView(R.layout.user_uploaded_apps);

        header.setUserFromIntent();
        header.setUserFromCache(header.getUserId());
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();

        Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_USER, header.getUser().toString());
		
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();	
		UserUploadedAppsFragment fragment = new UserUploadedAppsFragment();
		fragment.setArguments(bundle);
		ft.add(R.id.fragment, fragment);
		ft.commit();
    }

    public void bind() {
//		TextView tv = (TextView)findViewById(R.id.txtTitle);
//		tv.setText(header.getUser().optString("name"));
    	
    	ImageView iv = (ImageView) findViewById(R.id.btnBack);
    	iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    	
		String mask = header.getUser().optString("avatar_mask", "");
		String url = mask.replace("[size]", "sq");
		new AsyncImageLoader(this, iv, 0).loadUrl(url);

    }

}
