package goofy2.swably.fragment;

import goofy2.swably.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public abstract class PeopleReviewsFragment extends CloudCommentsFragment{

	abstract protected String getPageTitle();

//	  @Override
//	  public boolean onCreateOptionsMenu(Menu menu) {
//	      mMenu = menu;
//	      // Inflate the currently selected menu XML resource.
//	      MenuInflater inflater = getMenuInflater();
//	      inflater.inflate(R.menu.refresh, menu);
//	      //setNoticeMenu();        		
//	      return true;
//	  }

	@Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + getAPI() + "?format=json&"+getCloudActivity().getLoginParameters()+"&"+getCloudActivity().getClientParameters();
	}
	
    abstract protected String getAPI();

	@Override
	protected void onClickHeader() {
	}

}
