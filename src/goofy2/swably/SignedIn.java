package goofy2.swably;

import goofy2.swably.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SignedIn extends WithHeaderActivity {
	protected UserHeader header = new UserHeader(this);
	private View btnNext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signed_in);
        disableSliding();
    	header.setUserFromIntent();
        
		if(header.getUser().optBoolean("activated")){
    		setCurrentUser(header.getUser());
    		saveSignedIn(header.getUser().optString("name"), null);
			sendBroadcast(new Intent(Const.BROADCAST_FINISH));
			goHome();
			finish();
		}

		btnNext = this.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				boolean needInviteCode = header.getUser().optBoolean("need_invite_code", true);
//				if(needInviteCode){
//	//				String id = header.getUser().optString("signup_sns");
//	//	        	String name = (String) Utils.getSnsResource(id, "name");
//					Intent i = new Intent(SignedIn.this, InviteCode.class);
//					i.putExtra(Const.KEY_USER, header.getUser().toString());
//					startActivity(i);
//				}else{
					try {
						JSONObject user = header.getUser();
						user.put("activated", true);
			    		setCurrentUser(user);
			    		saveSignedIn(user.optString("name"), null);
						String id = user.optString("signup_sns");
			        	String name = (String) Utils.getSnsResource(id, "name");
//						Intent i = new Intent(SignedIn.this, GuideSnsFriends.class);
//						i.setData(Uri.parse(id));
//						i.putExtra("name", name);
			        	// ignore guide process, go to home directly.
						Intent i = new Intent(SignedIn.this, Home.class);
						startActivity(i);
					} catch (JSONException e) {
						e.printStackTrace();
					}
//				}
	    		finish();
			}
        });
        bind();
        
        btnNext.callOnClick(); // skip the page
    }

    private void bind(){
    	header.bindUserHeader(findViewById(R.id.viewBody));
//    	TextView txtBio = (TextView) findViewById(R.id.txtBio);
//    	txtBio.setText(header.getUser().optString("bio"));
//    	if(txtBio.getText().equals("null")) txtBio.setText("");
    }

//	  @Override
//	  public boolean onCreateOptionsMenu(Menu menu) {
//	      mMenu = menu;
//	      // Inflate the currently selected menu XML resource.
//	      MenuInflater inflater = getMenuInflater();
//	      inflater.inflate(R.menu.common, menu);
//	      //setNoticeMenu();        		
//	      return true;
//	  }
    @Override
	protected void onDataChanged(int item){
		bind();
	}
}
