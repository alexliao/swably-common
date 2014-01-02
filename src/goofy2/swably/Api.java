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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
