package goofy2.swably;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Me extends User {
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	JSONObject user = Utils.getCurrentUser(this);
		Intent i = getIntent();
		i.putExtra(Const.KEY_USER, user.toString());
		super.onCreate(savedInstanceState);
//		this.showBehind();
    }

    @Override
    public void onResume(){
    	super.onResume();
    	postShowAbove();
    }
}
