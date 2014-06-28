package goofy2.swably;

import org.json.JSONObject;

import goofy2.swably.fragment.TagAppsFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

public class TagApps extends WithHeaderActivity {
	JSONObject mTag;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
    	setContentView(R.layout.tag_apps);
        try {
        	mTag = new JSONObject(getIntent().getStringExtra(Const.KEY_TAG));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();

        Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_TAG, mTag.toString());
		
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();	
		TagAppsFragment fragment = new TagAppsFragment();
		fragment.setArguments(bundle);
		ft.add(R.id.fragment, fragment);
		ft.commit();
    }

    public void bind() {
		TextView tv = (TextView)findViewById(R.id.txtTitle);
		tv.setText("#"+mTag.optString("name"));
    }

}
