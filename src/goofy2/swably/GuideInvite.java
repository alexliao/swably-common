package goofy2.swably;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class GuideInvite extends Invite {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    View btnNext = this.findViewById(R.id.btnNext);
	    btnNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				finish();
				startActivity(new Intent(GuideInvite.this, GuideLocalApps.class));
			}
        });

        btnContacts.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(GuideInvite.this, GuideInviteContacts.class);
				startActivity(i);
			}
        });
        final String sns_id = Utils.getCurrentUser(GuideInvite.this).optString("signup_sns");
	    viewAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
	        	String title = (String) Utils.getSnsResource(sns_id, "name");
				Intent i = new Intent(GuideInvite.this, GuideInviteSnsFriends.class);
				i.setData(Uri.parse(sns_id));
				i.putExtra("name", title);
				startActivity(i);
			}
        });

//        if(sns_id.equals("plus")){
//			Intent i = new Intent(GuideInvite.this, GuideInviteContacts.class);
//			startActivity(i);
//			finish();
//        }
    }

    @Override
    protected void setContent(){
	    setContentView(R.layout.guide_invite);
	    disableSliding();
    }
}
