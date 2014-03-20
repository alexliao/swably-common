package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.data.App;
import goofy2.swably.fragment.CloudFragment;
import goofy2.utils.AsyncImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuFragment extends CloudFragment {
	private View viewHome;
	private View viewShares;
	private View viewRequests;
	private View viewNotifications;
	private View viewApps;
	private View viewMe;
	private View viewFindPeople;
	private ImageView imgMe;
	private View viewSettings;
	private View viewAdd;
	private View viewSearchApps;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Utils.logV(this, "CloudFragment onCreateView: " + savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
		View v = inflater.inflate(R.layout.main_menu, container, false);
        super.onCreate(savedInstanceState);
//        if(ca().redirectAnonymous()) return v;
//    	a().getWindow().setBackgroundDrawableResource(R.drawable.panel_background);
        
    	viewHome = v.findViewById(R.id.viewHome);
    	((TextView)viewHome).setTypeface(ca().mNormalFont);
    	viewHome.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				ca().showAbove();
				if(!(a() instanceof Home)) ca().goHome();
				else ca().showAbove();
			}
        });
    	viewShares = v.findViewById(R.id.viewShares);
    	((TextView)viewShares).setTypeface(ca().mNormalFont);
    	viewShares.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!(a() instanceof SharingPosts)) startActivity(new Intent(a(), SharingPosts.class));
				else ca().showAbove();
			}
        });
    	viewRequests = v.findViewById(R.id.viewRequests);
    	((TextView)viewRequests).setTypeface(ca().mNormalFont);
    	viewRequests.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!(a() instanceof HelpRequests)) startActivity(new Intent(a(), HelpRequests.class));
				else ca().showAbove();
			}
        });
    	viewNotifications = v.findViewById(R.id.viewNotifications);
    	((TextView)viewNotifications).setTypeface(ca().mNormalFont);
    	viewNotifications.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!(a() instanceof MyFollowingReviews)) startActivity(new Intent(a(), MyFollowingReviews.class));
				else ca().showAbove();
			}
        });
//        viewAdd = this.findViewById(R.id.viewAdd);
//        viewAdd.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				startActivity(new Intent(Home.this, SelectLocalApp.class));
//			}
//        });
	    viewApps = v.findViewById(R.id.viewApps);
    	((TextView)viewApps).setTypeface(ca().mNormalFont);
	    viewApps.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
//				ca().showAbove();
				if(!(a() instanceof Apps)) startActivity(new Intent(a(), Apps.class));
				else ca().showAbove();
			}
		});
	    viewSearchApps = v.findViewById(R.id.viewSearchApps);
    	((TextView)viewSearchApps).setTypeface(ca().mNormalFont);
	    viewSearchApps.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
//				ca().showAbove();
				if(!(a() instanceof SearchApps)) startActivity(new Intent(a(), SearchApps.class));
				else ca().showAbove();
			}
		});

//        ImageView imgAdd = (ImageView)this.findViewById(R.id.imgAdd);
//        imgAdd.setImageDrawable(getResources().getDrawable((Integer) Utils.getSnsResource(Utils.getCurrentUser(Home.this).optString("signup_sns"), "btnHomeAdd")));
//        ImageView imgFriends = (ImageView)this.findViewById(R.id.imgFriends);
//        imgFriends.setImageDrawable(getResources().getDrawable((Integer) Utils.getSnsResource(Utils.getCurrentUser(Home.this).optString("signup_sns"), "icon_ontile")));
        viewMe = v.findViewById(R.id.viewMe);
        viewMe.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				ca().showAbove();
				if(Utils.getCurrentUser(a()) == null){
					startActivity(new Intent(a(), Const.START_ACTIVITY));
				}else{
					if(!(a() instanceof Me)) ca().openMe();
					else ca().showAbove();
				}
			}
        });
//        imgMe = (ImageView)v.findViewById(R.id.imgMe);
        viewFindPeople = v.findViewById(R.id.viewFindPeople);
    	((TextView)viewFindPeople).setTypeface(ca().mNormalFont);
        viewFindPeople.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				startActivity(new Intent(Home.this, FindPeople.class));
//				ca().showAbove();
//				if(!(a() instanceof SnsFriends)) {
//			        final String sns_id = Utils.getCurrentUser(a()).optString("signup_sns");
//		        	String title = (String) Utils.getSnsResource(sns_id, "name");
//					Intent i = new Intent(a(), SnsFriends.class);
//					i.setData(Uri.parse(sns_id));
//					i.putExtra("name", title);
//					startActivity(i);
//				}
				if(!(a() instanceof People)) startActivity(new Intent(a(), People.class));
				else ca().showAbove();
			}
        });
//        View btnAbout = this.findViewById(R.id.btnAbout);
//        btnAbout.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//    			startActivity(new Intent(Home.this, About.class));
//			}
//        });
//        View btnSignout = v.findViewById(R.id.btnSignout);
//        btnSignout.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				ca().confirm(getString(R.string.sign_out),  new DialogInterface.OnClickListener(){
//					public void onClick(DialogInterface dialog, int which)
//					{
//						ca().showAbove();
//						ca().signout();
//						a().finish();
//					}       
//				});
//			}
//        });

        viewSettings = v.findViewById(R.id.viewSettings);
    	((TextView)viewSettings).setTypeface(ca().mNormalFont);
        viewSettings.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				ca().showAbove();
				if(!(a() instanceof Settings)) startActivity(new Intent(a(), Settings.class));
				else ca().showAbove();
			}
        });

        View viewTestClaiming = v.findViewById(R.id.viewTestClaiming);
        viewTestClaiming.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				ca().showAbove();
//				if(!(a() instanceof Claiming)) startActivity(new Intent(a(), Claiming.class));
//				else ca().showAbove();
				Intent i = new Intent(a(), Claiming.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra(App.SIGNATURE, "j2jRG2U7+oMzG/s27J/+Xw==");
				a().startActivity(i);
			}
        });

        viewAdd = v.findViewById(R.id.viewAdd);
		viewAdd.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
//				ca().showAbove();
				startActivity(new Intent(a(), SelectLocalAppToReview.class));
//				a().overridePendingTransition(R.anim.slide_in_bottom, R.anim.nothing);
			}
		});
        
        bind(v);
        
        return v;
    }

    @Override
    public void onDestroy(){
    	Log.d("", Const.APP_NAME + " mainmenu onDestroy");
    	super.onDestroy();
    }
    
    @Override
    public void onDetach(){
    	Log.d("", Const.APP_NAME + " mainmenu onDetach");
    	super.onDetach();
    }

    private void bind(View v){
    	if(v == null) return; 
    	JSONObject user = Utils.getCurrentUser(a());
    	if(user != null){
        	TextView tv = (TextView) v.findViewById(R.id.txtMenuUserName);
	    	tv.setText(user.optString("name"));
	    	tv.setTypeface(ca().mNormalFont);
			if(!user.isNull("avatar_mask")){
				String mask = user.optString("avatar_mask", "");
				String url = mask.replace("[size]", "bi");
				ImageView iv = (ImageView) v.findViewById(R.id.avatarMenu);
	//			Bitmap bm = Utils.getImageFromFile(a(), url);
	//			if(bm == null) Utils.asyncLoadImage(a(), 0, url, null);
	//			if(bm != null) iv.setImageBitmap(bm);
				new AsyncImageLoader(a(), iv, 0).loadUrl(url);
			}
		}
    }

    @Override
    public void onStart(){
    	super.onStart();
//    	Button tv = (Button) findViewById(R.id.viewReviews);
//    	tv.setText(getString(R.string.reviews));
//    	int count = Utils.getUnreadReviewsCount(this);
//    	if(count > 0)
//    		tv.setText(tv.getText()+" ("+count+")");
    		
    }

//    @Override
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
		bind(getView());
	}
}
