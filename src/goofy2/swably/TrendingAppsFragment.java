package goofy2.swably;

import goofy2.swably.fragment.CloudAppsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class TrendingAppsFragment extends CloudAppsFragment {

    @Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + "/apps/trending" + "?format=json&"+ca().getLoginParameters() + "&" + ca().getClientParameters();
	}

	
	@Override
	protected String getIdName(){
		return "like_id";
	}

}
