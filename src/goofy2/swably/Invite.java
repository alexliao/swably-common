package goofy2.swably;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Invite extends WithHeaderActivity {
	protected View viewAdd;
	protected View btnContacts;
//	protected ListView mListNetworks;
//	ArrayList<HashMap<String, Object>> mNetworks;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent();
        //prepareUserBar();

        final String sns_id = Utils.getCurrentUser(Invite.this).optString("signup_sns");
        btnContacts = this.findViewById(R.id.btnContacts);
        btnContacts.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Invite.this, InviteContacts.class);
				startActivity(i);
			}
        });
//		mListNetworks=(ListView)findViewById(R.id.listNetworks);
//        bindList();
//        mListNetworks.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
//            @Override  
//            public void onItemClick(AdapterView<?> arg0, final View arg1, final int position, long arg3) {
//				onClickItem(position);
//             }  
//		});      
        viewAdd = this.findViewById(R.id.viewAdd);
        viewAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
	        	String title = (String) Utils.getSnsResource(sns_id, "name");
				Intent i = new Intent(Invite.this, InviteSnsFriends.class);
				i.setData(Uri.parse(sns_id));
				i.putExtra("name", title);
				startActivity(i);
			}
        });
    	int iconId = (Integer) Utils.getSnsResource(sns_id, "icon_ontile");
    	Drawable d = this.getResources().getDrawable(iconId);
//    	d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
//    	viewAdd.setCompoundDrawables(null, d, null, null);
    	ImageView iv = (ImageView) this.findViewById(R.id.imageAdd);
    	iv.setImageDrawable(d);
    	
        if(sns_id.equals("plus")){
//			Intent i = new Intent(Invite.this, InviteContacts.class);
//			startActivity(i);
//			finish();
        	viewAdd.setVisibility(View.GONE);
        }

    }

    protected void setContent(){
//    	enableSlidingMenu();
//	    setContentView(R.layout.invite);
    }
    
    @Override
    public void onStart(){
    	super.onStart();
//    	connected(getIntent());
//	    int invitesLeft = Utils.getCurrentUser(this).optInt("invites_left", 0);
//	    TextView tv = (TextView) findViewById(R.id.txtTitle);
//	    tv.setText(String.format(getString(R.string.invites_left), invitesLeft));
    }

}
