package goofy2.swably.fragment;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import goofy2.swably.Const;
import goofy2.swably.Utils;
import goofy2.swably.data.App;
import goofy2.swably.fragment.CloudCommentsFragment.ReviewAddedBroadcastReceiver;
import goofy2.swably.fragment.CloudCommentsFragment.ReviewDeletedBroadcastReceiver;
import goofy2.utils.JSONUtils;

public class AppUploadersFragment extends AppUsersFragment {

	@Override
	protected String getAPI() {
		return "/apps/uploaders/";
	}

	@Override
	protected String getIdName(){
		return "share_id";
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
   }
    @Override
    public void onDestroy(){
    	super.onDestroy();
    }

}
