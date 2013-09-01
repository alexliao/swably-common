package goofy2.swably.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class UserFollowingFragment extends UserUsersFragment {

	@Override
	protected String getAPI() {
		return "/users/following/";
	}

}
