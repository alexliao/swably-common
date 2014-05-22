package goofy2.swably.fragment;

import goofy2.swably.Checker;
import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.Const;
import goofy2.swably.FeedsAdapter;
import goofy2.swably.R;
import goofy2.swably.ReviewProfile;
import goofy2.swably.User;
import goofy2.swably.UserHeader;
import goofy2.swably.Utils;
import goofy2.swably.data.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class FeedsFragment extends CloudListFragment {
	protected UserHeader header;
	int lastReadAt;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	header = new UserHeader(ca());
        header.setUserFromBundle(getArguments());
    	super.onCreate(savedInstanceState);
    	lastReadAt = getArguments().getInt("lastReadAt");
   }
    @Override
    public void onDestroy(){
    	super.onDestroy();
    }
    
    @Override
	protected void loadedMore(boolean successed) {
    	super.loadedMore(successed);
    	if(successed){
    		FeedsFragment.setAllRead(a());
    		NotificationManager nm = (NotificationManager)a().getSystemService(Context.NOTIFICATION_SERVICE);
    		nm.cancel(Checker.NOTIFICATION_ID);
    	}
	}

	@Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + "/feeds/list/" + header.getUserId() + "?format=json&read_at="+lastReadAt+"&"+ca().getLoginParameters()+"&"+ca().getClientParameters();
	}

	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		JSONArray ret = (new JSONObject(result)).getJSONArray("feeds"); 
		return ret;		
	}

	@Override
	protected CloudBaseAdapter getAdapter() {
		return new FeedsAdapter(getCloudActivity(), mListData, mLoadingImages);
	}

	protected void onClickItem(final int position) throws JSONException {
    	final JSONObject json = mListData.getJSONObject(position);
		startActivity(getFeedIntent(a(), json));
	}

	static public Intent getFeedIntent(Context context, JSONObject feed){
		Intent ret = null;
    	String type = feed.optString("object_type");
    	String id = feed.optString("object_id");
    	
    	String http = "http://"+context.getString(R.string.host1);
    	if(type.equalsIgnoreCase("review")){
    		ret = new Intent(context, ReviewProfile.class);
    		ret.setData(Uri.parse(http+"/r/"+id));
    	}else if(type.equalsIgnoreCase("app")){
    		ret = new Intent(context, App.class);
    		ret.setData(Uri.parse(http+"/a/"+id));
    	}else if(type.equalsIgnoreCase("user")){
    		ret = new Intent(context, User.class);
    		ret.setData(Uri.parse(http+"/u/"+id));
    	}
    	return ret;
	}
	
    @Override
	public long getCacheExpiresIn(){
		return 5*1000;
	}

    static public int getLastReadTime(Context context){
		String strTime = Utils.getUserPrefString(context, Utils.getCurrentUserId(context) + "lastReadTime", ""+System.currentTimeMillis()/1000);
		return Integer.parseInt(strTime);
	}
    
	static public void setAllRead(Context context){
		int time = (int) (System.currentTimeMillis()/1000);
		FeedsFragment.setLastReadTime(context, time);
	}

	static protected void setLastReadTime(Context context, int value){
		Utils.setUserPrefString(context, Utils.getCurrentUserId(context) + "lastReadTime", ""+value);
	}

}
