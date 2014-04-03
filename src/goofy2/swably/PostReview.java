package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.data.App;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PostReview extends WithHeaderActivity {
	public final int RESULT_LOAD_IMAGE = 2;
	protected AppHeader header = new AppHeader(this);
	private JSONObject mInReplyTo = null;
	private String mSnsId;
	private String mImagePath;
	private View btnMore;
	private View btnPost;
	private EditText editContent;
	private ImageButton btnImage;
	private ImageView imgImage;
	private View viewBody;
//	private View viewSync;
	private TextView txtSync;
	private CheckBox chkSync;
	NotificationManager mNotificationManager;
//	protected ListView mListNetworks;
//	ArrayList<HashMap<String, Object>> mNetworks;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_review); 
        disableSliding();
        if(redirectAnonymous()) return;
        
        Intent i = getIntent();
        try {
        	//mApp = new AppHelper(this).getApp(packageName);
            String strInReplyTo = i.getStringExtra(Const.KEY_REVIEW);
            if(strInReplyTo != null){
                mInReplyTo = new JSONObject(strInReplyTo);
        		header.setApp(new App(mInReplyTo.optJSONObject("app")));
            }

            String str = i.getStringExtra(Const.KEY_APP);
        	if(str != null)	header.setApp(new App(new JSONObject(str)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mSnsId = Utils.getCurrentUser(PostReview.this).optString("signup_sns");
		
		viewBody = this.findViewById(R.id.viewBody);
		header.bindAppHeader(this.findViewById(R.id.viewBody));
		btnMore = this.findViewById(R.id.btnMore);
		btnMore.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
//				if(noApp()){
//					selectAppToReply(mInReplyTo, editContent.getText().toString());
//					finish();
//				}else{
//					openApp(header.getApp().getJSON());
//				}
				if(mInReplyTo == null){
					openApp(header.getApp().getJSON());
				}else{
					selectAppToReply(mInReplyTo, editContent.getText().toString());
					finish();
				}
			}
		});
		
//    	viewSync = this.findViewById(R.id.viewSync);
		chkSync = (CheckBox) this.findViewById(R.id.chkSync);
    	String prompt = (String) Utils.getSnsResource(mSnsId, "sync_prompt");
    	if(prompt == null) chkSync.setVisibility(View.GONE);
    	else{
//			txtSync = (TextView) this.findViewById(R.id.txtSync);
//			txtSync.setText(prompt);
    		chkSync.setText(prompt);
    		chkSync.setTypeface(mLightFont);
    		chkSync.setButtonDrawable((Integer)Utils.getSnsResource(mSnsId, "check_sync"));
    		
    		chkSync.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
//					chkSync.toggle();
					Utils.setUserPrefString(PostReview.this, "sync_"+mSnsId, chkSync.isChecked() ? "true" : "false");
				}
			});
			String syncSns = Utils.getUserPrefString(this, "sync_"+mSnsId, "true");
			chkSync.setChecked(syncSns.equals("true")); 
    	}
    	
    	btnPost = this.findViewById(R.id.btnPost);
    	btnPost.setOnClickListener(new OnClickListener_btnPost());
		editContent = (EditText) this.findViewById(R.id.editContent);
		String content = i.getStringExtra("content");
		editContent.setText(content);
//		mListNetworks=(ListView)findViewById(R.id.listNetworks);
//        mListNetworks.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
//            @Override  
//            public void onItemClick(AdapterView<?> arg0, final View arg1, final int position, long arg3) {
//				onClickItem(position);
//             }  
//		});      
		
		mImagePath = i.getStringExtra("image");
		imgImage = (ImageView) this.findViewById(R.id.imgImage);
		imgImage.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Utils.confirm(PostReview.this, getString(R.string.delete_screenshot),  new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which)
					{
						mImagePath = null;
						bind();
					}       
				});
			}
		});
		btnImage = (ImageButton) this.findViewById(R.id.btnImage);
		btnImage.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);			
			}
		});
		

//        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
//        View btnTitle = findViewById(R.id.btnTitle);
//    	btnTitle.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				selectApp();
//			}
//	    });
//    	// disable changing app if it is not a reply
//        if(mInReplyTo == null){
//        	btnTitle.setClickable(false);
//        	txtTitle.setCompoundDrawables(null, null, null, null);
//        }
    
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//    	if(header.getApp() == null){
//    		Timer timer = new Timer();
//        	timer.schedule(new TimerTask(){
//        		@Override
//        		public void run(){
//        			selectApp();
//        		}
//        	}, 300); // delay execution
//    	}
    	bind();

    }
    
    protected void selectApp(){
		Intent i = new Intent(PostReview.this, SelectLocalAppToPick.class);
		startActivityForResult(i, 0);
    }
    
//    @Override
//    protected void onNewIntent(Intent intent){
//    	super.onNewIntent(intent);
//    	connected(intent);
//    	bindList();
//    }

    @Override
    public void onStart(){
    	super.onStart();
    }
    
	private void bind() {
		if(noApp()){
			editContent.setHint(R.string.request_an_app_hint);
			btnMore.setEnabled((getIntent().getStringExtra(Const.KEY_REVIEW) != null)); // disable when init a request
		}else{
			editContent.setHint(R.string.post_review_hint);
			btnMore.setEnabled(true);
		}
		editContent.setTypeface(mLightFont);
		if(mImagePath == null){
			btnImage.setVisibility(View.VISIBLE);
			imgImage.setVisibility(View.INVISIBLE);
			imgImage.setImageBitmap(null);
		}else{
			btnImage.setVisibility(View.INVISIBLE);
			imgImage.setVisibility(View.VISIBLE);
			imgImage.setImageBitmap(Utils.getImageFromFile(this, mImagePath, 192, 192));
		}
	}
	
	boolean noApp(){
		return header.getApp() == null || header.getApp().getJSON() == null;
	}

//    private boolean connected(Intent intent){
//    	boolean ret = false;
//    	Uri data = intent.getData();
//    	if(data != null){
//	    	String connections = data.getQueryParameter("connections");
//	    	JSONObject user = Utils.getCurrentUser(this);
//	    	try {
//				user.put("connections", connections);
//		    	setCurrentUser(user);
//		    	bindList();
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//	    	intent.setData(null);
//    	}
//    	return ret;
//    }

//    private void onClickItem(int position){
//    	@SuppressWarnings("unchecked")
//		HashMap<String, Object> network = (HashMap<String, Object>) mListNetworks.getAdapter().getItem(position);
//    	final String id = (String) network.get("id");
//    	if(Utils.getCurrentUser(this).optString("connections","").contains(id)){
//    		Boolean selected = ! (Boolean) network.get("select");
//    		network.put("select", selected);
//    		Utils.setUserPrefString(this, "sync_"+id, selected.toString());
//    		bindList();
//    	}else{
//			confirm_content(String.format(getString(R.string.link_confirm), id),  new DialogInterface.OnClickListener(){
//				public void onClick(DialogInterface dialog, int which)
//				{
//					String url = Const.HTTP_PREFIX + "/connections/connect/"+id+"?next=sync&"+Utils.getLoginParameters(PostReview.this);
//					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//				}       
//			});
//     	}
//    }

//    private void prepareList(){
//    	String f = getString(R.string.sync_to); 
//    	mNetworks = new ArrayList<HashMap<String, Object>>();
//        HashMap<String, Object> network;
//
//        String connections = Utils.getCurrentUser(this).optString("connections");
//        String lang = getLang();
//		if(lang.equalsIgnoreCase("zh")){
//	        network = new HashMap<String, Object>(); network.put("logo", R.drawable.sina32); network.put("name", String.format(f,"新浪微博")); network.put("id", "sina"); 
//	        network.put("select", Boolean.parseBoolean(Utils.getUserPrefString(this, "sync_sina", connections.contains("sina") ? "true" : "false")));
//	        mNetworks.add(network);
//		}else{
////	        network = new HashMap<String, Object>(); network.put("logo", R.drawable.facebook); network.put("name", String.format(f,"Facebook")); network.put("id", "facebook"); 
////	        network.put("select", Boolean.parseBoolean(Utils.getUserPrefString(this, "sync_facebook", connections.contains("facebook") ? "true" : "false")));
////	        mNetworks.add(network);
//	        network = new HashMap<String, Object>(); network.put("logo", R.drawable.twitter); network.put("name", String.format(f,"Twitter")); network.put("id", "twitter"); 
//	        network.put("select", Boolean.parseBoolean(Utils.getUserPrefString(this, "sync_twitter", connections.contains("twitter") ? "true" : "false"))); 
//	        mNetworks.add(network);
//		}
//    }
//    private void bindList() {
//    	prepareList();
//        SimpleAdapter sa = new SimpleAdapter(this, mNetworks, R.layout.sns_sync_row, new String[] { "logo", "name", "select"}, new int[] { R.id.imgLogo, R.id.txtName, R.id.checkBox});
//		mListNetworks.setAdapter(sa);		
//	}

    private class OnClickListener_btnPost implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			if(!Utils.HttpTest(PostReview.this)) return;
			final String content = editContent.getText().toString().trim();
			// valid fields
			String errMsg = null ;
			if(content.equals("")){
//			if(header.getApp() == null){
				errMsg = getString(R.string.err_no_content);
				editContent.requestFocus();
//				errMsg = getString(R.string.err_no_app);
			}
			if(errMsg != null){
				Utils.showToast(PostReview.this,errMsg);
			}else{
				//post form
//				final Handler handler = new Handler(); 
//				showDialog(0);
				final int notiCode = notifySending();				
				new Thread() {
					public void run(){
						String inReplyToId = null;
						if(mInReplyTo != null) inReplyToId = mInReplyTo.optString("id");
//						String sync_sns = "";
//						for(int i=0; i<mNetworks.size(); i++){
//							HashMap<String, Object> network = (HashMap<String, Object>) mNetworks.get(i);
//					    	String id = (String) network.get("id");
//				    		Boolean selected = (Boolean) network.get("select");
//							if(selected) sync_sns += id+" ";
//						}
						String sync_sns = chkSync.isChecked() ? mSnsId : "";
//						final JSONObject json = doReview(header.getApp().getCloudId(), content, inReplyToId, sync_sns);
						final JSONObject json = doReview(header.getAppId(), content, inReplyToId, sync_sns, mImagePath);
//						removeDialog(0);
//						handler.post(new Runnable() {
//							public void run(){
//									if(json != null){
//										if(getIntent().getBooleanExtra("simple", false)){
//											goHome();
//										}else{
//											Intent i = new Intent(PostReview.this, ReviewProfile.class);
//											i.putExtra(Const.KEY_REVIEW, json.toString());
//											startActivity(i);
//											Intent intent = new Intent(Const.BROADCAST_REVIEW_ADDED);
//											intent.putExtra(Const.KEY_REVIEW, json.toString());
//											sendBroadcast(intent);
//										}
//										finish();
//									}else{
//										Utils.alert(PostReview.this, getString(R.string.err_post_failed));
//									}
//							}
//						});
						mNotificationManager.cancel(notiCode);
						if(json != null){
							Intent intent = new Intent(Const.BROADCAST_REVIEW_ADDED);
							intent.putExtra(Const.KEY_REVIEW, json.toString());
							sendBroadcast(intent);
						}else{
							notifyFailed(notiCode, content);
						}
					}
				}.start();
				finish();
			}
		}
    }

	protected int notifySending(){
		String text = getString(R.string.sending);
		String expandedText = text;
		String expandedTitle = getString(R.string.app_name);
		
		Notification noti = Utils.getDefaultNotification(text);
//		noti.flags = Notification.FLAG_ONGOING_EVENT; // don't set the flag in case something wrong and the noti can't be canceled
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);		
		noti.setLatestEventInfo(this, expandedTitle, expandedText, contentIntent);
		
		int ret = (int) Math.round(Math.random()*1000000); 
		mNotificationManager.notify(ret, noti);
		return ret;
	}
    
	protected void notifyFailed(int notiCode, String content){
		String text = getString(R.string.err_post_failed);
		String expandedText = getString(R.string.err_post_failed_desc);
		String expandedTitle = getString(R.string.app_name);
		
		Notification noti = Utils.getDefaultNotification(text);

		Intent i = new Intent(this, PostReview.class);
		if(!noApp())
			i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
		if(mInReplyTo != null) i.putExtra(Const.KEY_REVIEW, mInReplyTo.toString());
		if(mImagePath != null) i.putExtra("image", mImagePath);
		i.putExtra("content", content);
		PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
		
		noti.setLatestEventInfo(this, expandedTitle, expandedText, launchIntent);
		
		mNotificationManager.notify(notiCode, noti);
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(null == data) return;
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            
            // String picturePath contains the path of selected Image
//            Utils.showToastLong(this, picturePath);
            mImagePath = picturePath;
            bind();
        }else{
    		String str = data.getStringExtra(Const.KEY_APP);
    		try {
				header.setApp(new App(new JSONObject(str)));
				header.bindAppHeader(this.findViewById(R.id.viewBody));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			bind();
    	}
    }

}
