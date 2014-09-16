package goofy2.swably;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import goofy2.utils.JSONUtils;
import goofy2.utils.PredicateLayout;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class AllTags extends WithHeaderActivity{

	private PredicateLayout pdl_main;
	private String mData;
	private JSONArray mListData;
	private Context mContext;
	private View loading;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.alltags);
		initViews();
		(new TagsAsyncTask()).execute();
		
		
	}

	private void initViews() {
		pdl_main = (PredicateLayout) findViewById(R.id.pdl_main);
		loading = findViewById(R.id.loading);
		
	}
	
	static public String genRecommendUrl(){
    	return "http://" + Const.DEFAULT_MAIN_HOST+"/tags/public.json";
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
	}
	
	
	@Override
	public void onStart() {
		
		super.onStart();
		
		
	}


	private final class TagsAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			return loadStream(genRecommendUrl(),null);
			
		}

		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
			loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if(result == null) {
					fillData();
				}
				else{
					
				}
				loading.setVisibility(View.GONE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	protected String loadStream(String url, String lastId) {
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment loadStream: " + lastId);
		if (lastId != null)
			url += "&max_id=" + lastId;
		String err = null;
		String strResult = null;
		try {
			HttpGet httpReq = new HttpGet(url);
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					Const.HTTP_TIMEOUT);
			httpReq.setParams(httpParameters);
			HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
			strResult = EntityUtils.toString(httpResp.getEntity());
			int code = httpResp.getStatusLine().getStatusCode();
			if (code == 200) {
				Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListActivity loadStream ok: "
						+ lastId);
				mData = strResult;
				// mListData = JSONUtils.appendArray(mListData,
				// getListArray(strResult));
				JSONArray loaded = new JSONArray(mData);
//				cacheItems(loaded);
//				mLastLoaded = loaded.length();
				if (lastId == null) {
					mListData = loaded;
				} 
			} else {
				onHttpError(strResult, code);
			}
		} catch (Exception e) {
			// Utils.alertTitle(this, getString(R.string.err_no_network_title),
			// e.getMessage());
			err = e.getMessage();
			Log.e(Const.APP_NAME, Const.APP_NAME + " CloudListFragment loadStream err: " + err);
		}
		return err;
	}
	
	public void fillData() throws JSONException {
		if(mListData != null){
			for(int i = 0, size = mListData.length();i<size;i++){
				
					final JSONObject o = mListData.getJSONObject(i);
					TextView tv_tag = new TextView(mContext);
					tv_tag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
					tv_tag.setPadding(5, 3, 5, 3);
					tv_tag.setTextSize(15 * Const.SCREEN_DESITY);
					tv_tag.setBackgroundDrawable(getResources().getDrawable(R.drawable.tag_background));
					tv_tag.setText("#"+o.optString("name"));
					tv_tag.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							Toast.makeText(mContext, o.optInt("id")+"", Toast.LENGTH_SHORT).show();
							
						}
					});
					pdl_main.addView(tv_tag);
					
			}
			
		}
		
	}

	protected String onHttpError(String strResult, int code)
			throws JSONException {
		JSONObject json = new JSONObject(strResult);
		String err = json.getString("error_message");
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment loadStream err: " + err);
		return err;
	}


}
