package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.R;
import goofy2.swably.CloudUsersActivity.OnClickListener_btnFollow;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GuideLocalApps extends AddApp {

	@Override
    protected void setContent(){
	    setContentView(R.layout.guide_add_app);
	    
	    View btnNext = this.findViewById(R.id.btnNext);
	    btnNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				sendBroadcast(new Intent(Const.BROADCAST_FINISH));
				goHome();
			}
        });

	
		JSONObject me = Utils.getCurrentUser(this);
		try {
			me.put("guided", true);
			setCurrentUser(me);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//    @Override
//	protected void onCloudAction(JSONObject json){
//		Intent i = new Intent(GuideLocalApps.this, PostReview.class);
//		i.putExtra(Const.KEY_APP, json.toString());
////		i.putExtra("simple", true);
//		startActivity(i);
//    }

    @Override
    public String getCacheId(){
    	return super.getCacheId();
    }

}
