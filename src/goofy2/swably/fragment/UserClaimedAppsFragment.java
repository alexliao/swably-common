package goofy2.swably.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class UserClaimedAppsFragment extends UserAppsFragment {

	@Override
	protected String getAPI() {
		return "/users/claimed_apps/";
	}
	
//	@Override
//	protected String getIdName(){
//		return "user_sign_id";
//	}

    @Override
    public String getCacheId(){
    	return cacheId(header.getUserId());
    }

    static public String cacheId(String userId){
//    	if(app == null) return null; // in case opened from share link
    	return UserClaimedAppsFragment.class.getName()+userId;
    }

}
