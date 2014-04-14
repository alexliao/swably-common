package goofy2.swably;

import java.net.URLEncoder;

import org.json.JSONObject;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SearchApps extends CloudAppsActivity {
	View viewNoResult;
	private String mQuery;

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);

        View btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSearchRequested();
			}
		});
	    
	    viewNoResult = LayoutInflater.from(this).inflate(R.layout.search_no_result, null, false);
	    handleIntent(getIntent());
	    if(mQuery == null || mQuery.equals("")) onSearchRequested();
	}

	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
//	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	mQuery = intent.getStringExtra(SearchManager.QUERY);
			if(mQuery == null) return;
	    	refresh();
	    	TextView tv = (TextView) findViewById(R.id.txtTitle);
	    	tv.setText(String.format(getString(R.string.search_query), mQuery));
//	    }
	}
	
	@Override
	protected void loadedMore(boolean succeeded){
    	TextView tv = (TextView) viewNoResult.findViewById(R.id.txtNoResult);
    	tv.setText(String.format(getString(R.string.search_no_result), mQuery));
	    mList.setEmptyView(viewNoResult);
		super.loadedMore(succeeded);
	}	

	protected void setContent(){
	    setContentView(R.layout.search_apps);
    }
	
	@Override
	public boolean onSearchRequested() {
	     startSearch(mQuery, false, null, false);
	     return true;
	}
    
	@Override
	protected String getIdName(){
		return "row";
	}

    @Override
    public String getCacheId(){
    	return null;
    }

    @Override
	protected String getUrl() {
    	if(mQuery == null) return null;
    	else return Const.HTTP_PREFIX + "/apps/find" + "?count="+getListSize()+"&format=json&name="+URLEncoder.encode(mQuery)+"&"+getLoginParameters() + "&" + getClientParameters();
	}

//	protected int getListSize(){
//		return 10;
//	}

}
