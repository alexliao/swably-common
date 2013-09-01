package goofy2.swably;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import goofy2.swably.facebook.FacebookApp;
import goofy2.utils.JSONUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InviteSnsFriends extends CloudUsersActivity {
	private Button btnInviteMany;

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        btnInviteMany = (Button) this.findViewById(R.id.btnInviteMany);
//        btnInviteMany.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//		        Bundle params = new Bundle();
//		        params.putString("message", getString(R.string.invite_request));
//		        FacebookApp.mFacebook.dialog(InviteSnsFriends.this, "apprequests", params, new InviteDialogListener(InviteSnsFriends.this)); 
//			}
//        });
        bind();
    }
    
	@Override
	protected void onDataChanged(int item){
		super.onDataChanged(item);
		bind();
	}
    
//    // disable cache
//    @Override
//    protected String getCacheId(){
//    	return null;
//    }

    @Override
	protected CloudBaseAdapter getAdapter() {
		return new InviteSnsFriendsAdapter(this, mListData, mLoadingImages, getIntent().getDataString());
	}
	
//	@Override
//	protected String getImageUrl(JSONObject item, int index) throws JSONException {
//		String url;
//		url = item.optString("avatar", "");
//		return url;
//	}

	@Override
    protected void setContent(){
//    	enableSlidingMenu();
//	    setContentView(R.layout.invite_sns_friends);
    }

    protected void bind(){
	    
	    int invitesLeft = Utils.getCurrentUser(this).optInt("invites_left", 0);
	    TextView tv = (TextView) findViewById(R.id.txtInvitesLeft);
	    tv.setText(String.format(getString(R.string.invites_left), invitesLeft));

//    	String id = getIntent().getDataString();
//    	int iconId = (Integer) Utils.getSnsResource(id, "icon");
//    	Drawable d = this.getResources().getDrawable(iconId);
//    	d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
//    	tv.setCompoundDrawables(d, null, null, null);

    }
    @Override
	protected String getUrl() {
    	String id = getIntent().getDataString();
		return Const.HTTP_PREFIX + "/connections/invite_friends/"+id+"?format=json&"+getLoginParameters() + "&" + getClientParameters();
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

	protected void onClickItem(final int position) throws JSONException {
//		final JSONObject user = mListData.getJSONObject(position); 
//    	final String eid = user.optString("id");
//        Bundle params = new Bundle();
//        params.putString("message", getString(R.string.invite_request));
//        params.putString("to", eid);
//        FacebookApp.mFacebook.dialog(InviteSnsFriends.this, "apprequests", params, new InviteDialogListener(this)); 

//		confirm("Invite "+user.optString("name"),  new DialogInterface.OnClickListener(){
//			public void onClick(DialogInterface dialog, int which)
//			{
//                Bundle params = new Bundle();
//                params.putString("message", "a test request");
//				Log.d("", Const.APP_NAME + " InviteSnsFriends sending to " + user.optString("name"));
//                FacebookApp.mAsyncRunner.request(eid+"/apprequests", params, "POST", 
//                        new RequestListener(){
//
//							@Override
//							public void onComplete(String response, Object state) {
//								Log.d("", Const.APP_NAME + " InviteSnsFriends onComplete: " + response);
//							}
//
//							@Override
//							public void onIOException(IOException e,
//									Object state) {
//								e.printStackTrace();
//							}
//
//							@Override
//							public void onFileNotFoundException(
//									FileNotFoundException e, Object state) {
//								e.printStackTrace();
//							}
//
//							@Override
//							public void onMalformedURLException(
//									MalformedURLException e, Object state) {
//								e.printStackTrace();
//							}
//
//							@Override
//							public void onFacebookError(FacebookError e,
//									Object state) {
//								e.printStackTrace();
//							}
//                	
//                }, null);
//			}       
//		});
//	
	}

	@Override
	public String getCacheId(){
    	return InviteSnsFriendsFragment.class.getName();
    }
	
}
