package goofy2.swably.fragment;

import goofy2.swably.AppHeader;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.R.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public abstract class AppUsersFragment extends CloudUsersFragment {
	protected AppHeader header;

    @Override
	public void onCreate(Bundle savedInstanceState) {
    	header = new AppHeader(ca());
        header.setAppFromBundle(getArguments());
		super.onCreate(savedInstanceState);
    }
    
    protected void setContent(){
	    setContentView(R.layout.list_fragment);
    }
   
	@Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + getAPI() + header.getAppId() + "?format=json&"+ca().getLoginParameters()+"&"+ca().getClientParameters();
	}

	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		return new JSONArray(result);		
	}

	@Override
	protected void onClickHeader() {
	}

    abstract protected String getAPI();

    @Override
    public String getCacheId(){
    	return this.getClass().getName()+header.getAppId();
    }
}
