package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.data.App;
import goofy2.swably.fragment.UserClaimedAppsFragment;
import goofy2.utils.JSONUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Claiming extends CloudAppsActivity {
	protected UserHeader header = new UserHeader(this);
	private View viewBottomBar;
	String mSignature;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	header.setUser(Utils.getCurrentUser(this));
        super.onCreate(savedInstanceState);
        mSignature = getIntent().getStringExtra(goofy2.swably.data.App.SIGNATURE);
    }
    
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        
		View btnClaim = this.findViewById(R.id.btnClaim);
		btnClaim.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//post form
		    	String actionURL = Const.HTTP_PREFIX + "/apps/claim_apps_by_signature/" + "?format=json&signature="+URLEncoder.encode(mSignature)+"&"+getLoginParameters()+"&"+getClientParameters();;
				final HttpPost httpReq = new HttpPost(actionURL);
				final Handler handler = new Handler(); 
				showDialog(0);
				new Thread() {
					public void run(){
						try {
							final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
							removeDialog(0);
							handler.post(new Runnable() {
								public void run(){
									String strResult = null;
									JSONObject json = null;
									try {
										if(httpResp.getStatusLine().getStatusCode() == 200){
											Utils.clearCache(Claiming.this, UserClaimedAppsFragment.cacheId(Utils.getCurrentUserId(Claiming.this)));
								    		Intent i = new Intent(Claiming.this, Me.class);
								    		i.setData(Uri.parse("claimed")); // initial at claimed tab
								    		startActivity(i);
											finish();
										}else{
											strResult = EntityUtils.toString(httpResp.getEntity());
											json = new JSONObject(strResult);
											JSONObject errs = json.getJSONObject("error_message");
											JSONArray errNames = errs.names();
											JSONArray errMsgs = errs.toJSONArray(errNames);
											Utils.showToast(Claiming.this, JSONUtils.joinArray(errMsgs, "\n"));
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						} catch (final Exception e) {
							removeDialog(0);
							handler.post(new Runnable() {
								public void run(){
									Utils.showToast(Claiming.this, e.getMessage());
								}
							});
						}
					}
				}.start();

			}
        });
		
		View btnCancel = this.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
	    		finish();
			}
        });

		View viewBottomBar = this.findViewById(R.id.viewBottomBar);
		bind();
    }

    private void bind(){
    	header.bindUserHeader(findViewById(R.id.viewBody), false);
    	TextView txtClaimingPrompt = (TextView) findViewById(R.id.txtClaimingPrompt);
    	txtClaimingPrompt.setText(String.format(getString(R.string.claiming_prompt), header.getUser().optString("name").split(" ")[0]));
    }

	@Override
	protected void setContent() {
        setContentView(R.layout.claiming);
        disableSliding();
	}

	@Override
	protected CloudBaseAdapter getAdapter() {
		return new AppsAdapter(this, mListData, mLoadingImages, true);
	}
    // disable caching
	public String getCacheId(){
    	return null;
    }

	@Override
	protected String getUrl() {
//mSignature = "j2jRG2U7+oMzG/s27J/+Xw==";
		return Const.HTTP_PREFIX + "/apps/find4claiming/" + "?format=json&signature="+URLEncoder.encode(mSignature)+"&"+getLoginParameters()+"&"+getClientParameters();
//		return Const.HTTP_PREFIX + "/users/liked_apps/" + header.getUserId() + "?format=json&"+getLoginParameters();
	}

    @Override
	protected void loadMore(){
		// disable auto loading
	}	

//	@Override
//	protected CloudBaseAdapter getAdapter() {
//		return new LocalAppsAdapter(this, mListData, mLoadingImages);
//	}


}
