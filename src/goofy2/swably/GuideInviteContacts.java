package goofy2.swably;

import goofy2.swably.R;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class GuideInviteContacts extends InviteContacts {

    @Override
    protected void setContent(){
	    setContentView(R.layout.guide_invite_sns_friends);
	    disableSliding();

	    View btnNext = this.findViewById(R.id.btnNext);
	    btnNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				finish();
				startActivity(new Intent(GuideInviteContacts.this, GuideLocalApps.class));
			}
        });
    }
    
    @Override
    public String getCacheId(){
    	return super.getCacheId();
    }
}
