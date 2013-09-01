package goofy2.swably.fragment;

import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import goofy2.utils.Rotate3dAnimation;

public class PublicReviewsFragment extends PeopleReviewsFragment{
	

    @Override
	protected String getPageTitle() {
		return null;
	}

	@Override
	protected String getAPI() {
		return "/comments/public";
	}

    @Override
	public long getCacheExpiresIn(){
		return 60*1000;
	}
    
}
