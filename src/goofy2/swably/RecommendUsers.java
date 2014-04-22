package goofy2.swably;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import goofy2.swably.R;
import goofy2.utils.JSONUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RecommendUsers extends CloudUsersActivity {
	protected View btnFollow;
//	protected View btnHeaderInvite;
	protected View btnInvite;
	protected View viewFollow;
	protected View bottomLine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();
    }
    
    @Override
    protected void setContent(){
//    	enableSlidingMenu();
//	    setContentView(R.layout.sns_users);
    }

    protected void bind(){
	    viewFollow = this.findViewById(R.id.viewFollow);
	    btnFollow = this.findViewById(R.id.btnFollow);
	    btnFollow.setOnClickListener(new OnClickListener_btnFollow());
	    
//    	String name = getIntent().getStringExtra("name");
	    TextView tv = (TextView) findViewById(R.id.txtTitle);
//    	final String sns_id = getIntent().getDataString();

//    	int iconId = (Integer) Utils.getSnsResource(sns_id, "icon");
//    	Drawable d = this.getResources().getDrawable(iconId);
//    	d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
//    	tv.setCompoundDrawables(d, null, null, null);

    }

	protected String getUrl() {
		return Const.HTTP_PREFIX + "/users/recommend.json?count=10&"+getLoginParameters() + "&" + getClientParameters();
	}

	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		return new JSONObject(result).getJSONArray("local");		
	}

	@Override
	protected void onClickHeader() {
		// TODO Auto-generated method stub

	}

	@Override
	protected View getListHeader() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
	protected void loadMore(){
		// disable auto loading
	}	

	@Override
	protected CharSequence getListTitle() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public String getCacheId(){
//    	return RecommendFriendsFragment.class.getName();
//    }

}
