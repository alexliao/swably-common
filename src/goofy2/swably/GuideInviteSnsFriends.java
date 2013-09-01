package goofy2.swably;

import org.json.JSONException;

import goofy2.swably.R;
import goofy2.swably.CloudUsersActivity.OnClickListener_btnFollow;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GuideInviteSnsFriends extends InviteSnsFriends {

    @Override
    protected void setContent(){
	    setContentView(R.layout.guide_invite_sns_friends);
	    disableSliding();

//	    btnFollow = this.findViewById(R.id.btnFollow);
//	    btnFollow.setOnClickListener(new OnClickListener_btnFollow());
//	    btnFollow.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				new OnClickListener_btnFollow().onClick(btnFollow);
//				finish();
//				startActivity(new Intent(GuideInviteSnsFriends.this, GuideLocalApps.class));
//			}
//        });
	    
	    View btnNext = this.findViewById(R.id.btnNext);
	    btnNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				finish();
				startActivity(new Intent(GuideInviteSnsFriends.this, GuideLocalApps.class));
			}
        });
    }
    
    @Override
    public String getCacheId(){
    	return super.getCacheId();
    }
}
