package goofy2.swably;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
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
import org.json.JSONObject;

import goofy2.swably.R;
import goofy2.utils.ParamRunnable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

public class Api {

	static public JSONObject changeEmail(Context context, String email) throws Exception {
		JSONObject ret = null;
		try {
			String strResult = null;
			
			JSONObject user = Utils.getCurrentUser(context);
			String url = Const.HTTP_PREFIX + "/users/" + user.getString("id") + ".json?" + Utils.getClientParameters(context);
			
			List <NameValuePair> params = new ArrayList <NameValuePair>();
			params.add(new BasicNameValuePair("_method", "PUT"));
			params.add(new BasicNameValuePair("user[email]", email));
			HttpResponse httpResp = post(url, params);
			strResult = EntityUtils.toString(httpResp.getEntity());
			if(httpResp.getStatusLine().getStatusCode() == 200){
				JSONObject json = new JSONObject(strResult);
				ret = json;
			}else{
//				throw new Exception(json.optString("error_message","error"));
//				strResult = EntityUtils.toString(httpResp.getEntity());
				throw new Exception(strResult);
			}
			return ret;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

    protected static void watch(final Context context, final String reviewId, final String userId, final boolean isWatch, final ParamRunnable r){
    	JSONObject ret = null;
    	String actionURL = Const.HTTP_PREFIX;
    	if(isWatch)
    		actionURL += "/watches/add/" + userId + ".json";
    	else
    		actionURL += "/watches/cancel/" + userId + ".json";
    	actionURL += "?review_id=" + reviewId + "&" + Utils.getLoginParameters(context);

//		if(toast) Utils.showToast(context, context.getString(R.string.sending_request));
		final HttpPost httpReq = new HttpPost(actionURL);
		try{
			final Handler handler = new Handler(); 
			new Thread() {
				public void run(){
					try {
						final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
						handler.post(new Runnable() {
							public void run(){
								if(httpResp.getStatusLine().getStatusCode() == 200){
									JSONObject user;
									try {
										user = new JSONObject(EntityUtils.toString(httpResp.getEntity()));

										// notify list to change
										Intent intent;
										if(isWatch)
											intent = new Intent(Const.BROADCAST_FOLLOW_ADDED);
										else
											intent = new Intent(Const.BROADCAST_FOLLOW_DELETED);
										intent.putExtra(Const.KEY_ID, reviewId);
										context.sendBroadcast(intent);
										
//										int res;
//										res = isWatch ? R.string.follow_prompt : R.string.unfollow_prompt;
//										if(toast) Utils.showToast(context, context.getString(res).replaceAll("%s", name));
										if(r != null){
											try {
												r.param = user;
												r.run();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}else{
									try {
										Utils.showToast(context, Utils.getError(EntityUtils.toString(httpResp.getEntity())));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						});
					} catch (final Exception e) {
						handler.post(new Runnable() {
							public void run(){
								Utils.showToast(context, e.getMessage());
//								Utils.showToast(context, context.getString(R.string.err_follow));
							}
						});
						e.printStackTrace();
					}
				}
			}.start();
		}catch (Exception e){
			Log.e(Const.APP_NAME, Const.APP_NAME + " watch error: " + e.getMessage());
		}
    }

    protected static void appTag(final Context context, final String appId, final String tagName, final boolean isAdd, final ParamRunnable r){
    	String actionURL = Const.HTTP_PREFIX;
    	if(isAdd)
    		actionURL += "/app_tags.json" + "?app_id=" + appId;
    	else
    		actionURL += "/app_tags/destroy.json" + "?app_id=" + appId;
    	actionURL += "&" + Utils.getLoginParameters(context);
		final List <NameValuePair> params = new ArrayList <NameValuePair>();
		params.add(new BasicNameValuePair("tag_name", tagName));

		try{
			final String url = actionURL;
			final Handler handler = new Handler(); 
			new Thread() {
				public void run(){
					try {
						final HttpResponse httpResp = post(url, params);
						handler.post(new Runnable() {
							public void run(){
								if(httpResp.getStatusLine().getStatusCode() == 200){
									JSONObject json;
									try {
										json = new JSONObject(EntityUtils.toString(httpResp.getEntity()));

										// notify list to change
										Intent intent;
//										if(isAdd)
//											intent = new Intent(Const.BROADCAST_TAG_ADDED);
//										else
//											intent = new Intent(Const.BROADCAST_TAG_DELETED);
//										intent.putExtra(Const.KEY_ID, appId);
//										context.sendBroadcast(intent);
										
										if(r != null){
											try {
												r.param = json;
												r.run();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}else{
									try {
										Utils.showToast(context, Utils.getError(EntityUtils.toString(httpResp.getEntity())));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						});
					} catch (final Exception e) {
						handler.post(new Runnable() {
							public void run(){
								Utils.showToast(context, e.getMessage());
							}
						});
						e.printStackTrace();
					}
				}
			}.start();
		}catch (Exception e){
			Log.e(Const.APP_NAME, Const.APP_NAME + " appTag error: " + e.getMessage());
		}
    }
	
	static protected HttpResponse get(String url) throws ClientProtocolException, IOException{
		HttpGet httpReq = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT);
		httpReq.setParams(httpParameters);
		return new DefaultHttpClient().execute(httpReq);
	}

	static protected HttpResponse post(String url, List <NameValuePair> params) throws ClientProtocolException, IOException{
		HttpPost httpReq = new HttpPost(url);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT);
		httpReq.setParams(httpParameters);
		if (params != null) httpReq.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return new DefaultHttpClient().execute(httpReq);
	}

}
