package goofy2.swably.fragment;

import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.UserHeader;
import goofy2.swably.R.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public abstract class UserUsersFragment extends CloudUsersFragment {
	protected UserHeader header;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	header = new UserHeader(ca());
        header.setUserFromBundle(getArguments());
		super.onCreate(savedInstanceState);
    }
	
    protected void setContent(){
	    setContentView(R.layout.list_fragment);
    }

    @Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + getAPI() + header.getUserId() + "?format=json&"+ ca().getLoginParameters() + "&" + ca().getClientParameters();
	}

	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		JSONArray ret = (new JSONObject(result)).getJSONArray("users"); 
		return ret;		
	}

    @Override
	protected void onClickHeader() {
	}

	@Override
	protected String getIdName(){
		return "follow_id";
	}
	

    abstract protected String getAPI();

    @Override
    public String getCacheId(){
    	return this.getClass().getName()+header.getUserId();
    }
}
