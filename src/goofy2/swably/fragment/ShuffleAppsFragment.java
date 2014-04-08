package goofy2.swably.fragment;

import goofy2.swably.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class ShuffleAppsFragment extends CloudAppsFragment {

    @Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + "/apps/shuffle" + "?format=json&count=10&"+ca().getLoginParameters() + "&" + ca().getClientParameters();
	}

    @Override
	protected void loadMore(){
		// disable auto loading
	}	
	
    @Override
	public long getCacheExpiresIn(){
		return 60*1000; 
	}
//	@Override
//	protected String getIdName(){
//		return "like_id";
//	}

}
