package goofy2.swably.fragment;

import goofy2.swably.Const;

import org.json.JSONObject;

import android.os.Bundle;


public class TagAppsFragment extends CloudAppsFragment {
	JSONObject mTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
        	mTag = new JSONObject(getArguments().getString(Const.KEY_TAG));
		} catch (Exception e) {
			e.printStackTrace();
		}
        super.onCreate(savedInstanceState);
    }

    @Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + "/tags/apps/" + mTag.optString("id") + "?format=json&"+ca().getLoginParameters() + "&" + ca().getClientParameters();
	}

//	@Override
//	protected JSONArray getListArray(String result) throws JSONException {
//		return new JSONArray(result);		
//	}

	@Override
	protected void onClickHeader() {
	}


    @Override
    public String getCacheId(){
    	return this.getClass().getName()+mTag.optString("id");
    }
	
	@Override
	protected String getIdName(){
		return "app_tag_id";
	}

}
