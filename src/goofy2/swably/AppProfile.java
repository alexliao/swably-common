package goofy2.swably;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.utils.ParamRunnable;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AppProfile extends WithHeaderActivity {
//	private View viewAdd;
//	protected AppHeader header = new AppHeader(this);
//	protected AppTribtn tribtn = new AppTribtn(); 
//	private Button btnReviews;
//	//private Button btnAdd;
//	private Button btnShare;
////	protected ListView mListNetworks;
////	ArrayList<HashMap<String, Object>> mNetworks;
//	private View viewBody;
//	
//	
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Intent i = getIntent();
//        String appId = getIdFromUrl(i);
////    	if(appId != null){
////    		JSONObject json = new JSONObject();
////    		try {
////				json.put("id", appId);
////	    		i.putExtra(Const.KEY_APP, json.toString());
////			} catch (JSONException e) {
////				e.printStackTrace();
////			}
////    	}
//        setContentView(R.layout.app_profile);
//        super.onCreate(savedInstanceState);
//    	header.setAppFromIntent();
//    	
//		tribtn.init(this, header.getApp());
////        btnAdd = (Button) this.findViewById(R.id.btnAdd);
////        btnAdd.setOnClickListener(new OnClickListener(){
////			@Override
////			public void onClick(View arg0) {
////				Intent i = new Intent(AppProfile.this, PostReview.class);
////				i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
////				startActivity(i);
////				finish();
////			}
////        });
//		btnReviews = (Button) this.findViewById(R.id.btnReviews);
//        btnShare = (Button) this.findViewById(R.id.btnShare);
//        viewBody = this.findViewById(R.id.viewBody);
//
////		mListNetworks=(ListView)findViewById(R.id.listNetworks);
////        bindList();
////        mListNetworks.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
////            @Override  
////            public void onItemClick(AdapterView<?> arg0, final View arg1, final int position, long arg3) {
////				onClickItem(position);
////             }  
////		});      
//        viewAdd = this.findViewById(R.id.viewAdd);
////        ImageView imgAdd = (ImageView)this.findViewById(R.id.imgAdd);
////        imgAdd.setImageDrawable(getResources().getDrawable((Integer) Utils.getSnsResource(sns_id, "btnHomeAdd")));
//	    
//		String str = loadCache();
//		if(str != null){
//			try {
//				header.setApp(new App(new JSONObject(str)));
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//    	if(appId != null)
//    		loadApp(appId);
//    }
//
//    @Override
//    public void onStart(){
//    	super.onStart();
//    	if(header.getApp() != null)
//    		bind();
////    	connected(getIntent());
//    }
////    
////    private void onClickItem(int position){
////    	@SuppressWarnings("unchecked")
////		HashMap<String, Object> network = (HashMap<String, Object>) mListNetworks.getAdapter().getItem(position);
////    	final String id = (String) network.get("id");
////    	Integer status = (Integer) network.get("status");
////    	if(status == null){
////			Intent i = new Intent(AppProfile.this, PostReview.class);
////			i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
////			i.putExtra("sync_sns", id);
////			startActivity(i);
////			finish();
////    	}else{
////			confirm_content(String.format(getString(R.string.link_confirm), id),  new DialogInterface.OnClickListener(){
////				public void onClick(DialogInterface dialog, int which)
////				{
////					String url = Const.HTTP_PREFIX + "/connections/connect/"+id+"?next=post&"+Utils.getLoginParameters(AppProfile.this);
////					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
////				}       
////			});
////    	}
////    }
////
////    private void bindList() {
////    	//String f = getString(R.string.friends_on); 
////    	mNetworks = new ArrayList<HashMap<String, Object>>();
////        HashMap<String, Object> network;
////
//////        String connections = Utils.getCurrentUser(this).optString("connections");
//////        String lang = getLang();
//////		if(lang.equalsIgnoreCase("zh")){
//////	        network = new HashMap<String, Object>(); network.put("logo", R.drawable.sina); network.put("name", "贴到微博"); network.put("id", "sina"); network.put("status", connections.contains("sina") ? null : R.drawable.unlinked);
//////	        mNetworks.add(network);
//////		}else{
//////	        network = new HashMap<String, Object>(); network.put("logo", R.drawable.twitter); network.put("name", "Tweet it"); network.put("id", "twitter"); network.put("status", connections.contains("twitter") ? null : R.drawable.unlinked); 
//////	        mNetworks.add(network);
//////		}
////		String id = Utils.getCurrentUser(this).optString("signup_sns");
////        network = new HashMap<String, Object>(); network.put("logo", Utils.getSnsResource(id, "icon")); network.put("name", Utils.getSnsResource(id, "tweet it")); network.put("id", id); network.put("status", null); 
////        mNetworks.add(network);
////		
////        
////        SimpleAdapter sa = new SimpleAdapter(this, mNetworks, R.layout.sns_friend_row, new String[] { "logo", "name", "status"}, new int[] { R.id.imgLogo, R.id.txtName, R.id.imgStatus});
////		mListNetworks.setAdapter(sa);		
////		
////	}
////
////    private boolean connected(Intent intent){
////    	boolean ret = false;
////    	Uri data = intent.getData();
////    	if(data != null){
////	    	String connections = data.getQueryParameter("connections");
////	    	JSONObject user = Utils.getCurrentUser(this);
////	    	try {
////				user.put("connections", connections);
////		    	setCurrentUser(user);
////		    	bindList();
////			} catch (JSONException e) {
////				e.printStackTrace();
////			}
////	    	intent.setData(null);
////    	}
////    	return ret;
////    }
//
//    private void bind(){
//		hideLoading();
//		viewBody.setVisibility(View.VISIBLE);
//    	header.bindView();
//		btnReviews.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				openAppReviews(header.getApp().getJSON());
//			}
//        });
//        btnShare.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				sendOutApp(header.getApp());
//			}
//        });
//        viewAdd.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				Intent i = new Intent(AppProfile.this, PostReview.class);
//				i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
////				i.putExtra("sync_sns", sns_id);
//				i.putExtra("simple", getIntent().getBooleanExtra("simple", false));
//				startActivity(i);
//			}
//        });
//		tribtn.setStatus(header.getApp());
//		if(getIntent().getBooleanExtra("simple", false)){
//			btnReviews.setVisibility(View.GONE);
//			findViewById(R.id.btnShare).setVisibility(View.GONE);
//			findViewById(R.id.tribtn).setVisibility(View.GONE);
//		}
//    }
//
////	  @Override
////	  public boolean onCreateOptionsMenu(Menu menu) {
////	      mMenu = menu;
////	      // Inflate the currently selected menu XML resource.
////	      MenuInflater inflater = getMenuInflater();
////	      inflater.inflate(R.menu.common, menu);
////	      //setNoticeMenu();        		
////	      return true;
////	  }
//    @Override
//	protected void onDataChanged(int item){
//		bind();
//	}
//
//    private String getIdFromUrl(Intent intent){
//    	String ret = null;
//    	Uri data = intent.getData();
//    	if(data != null){
//	    	List<String> params = data.getPathSegments();
//	    	//String action = params.get(0); // "a"
//	    	if("http".equalsIgnoreCase(data.getScheme())) ret = params.get(1);
//    	}
//    	return ret;
//    }
//
//    private void loadApp(final String appId){
//    	AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
//			private String mErr = null;
//			private JSONObject mRet = null;
//			protected void onPreExecute() {
//				showLoading();
//			}
//			protected Long doInBackground(Void... params) {
//				try {
//					mRet = Utils.getAppInfo(AppProfile.this, appId);
//				} catch (Exception e) {
//					mErr = e.getMessage();
//				}
//				return null;
//			}
//            protected void onPostExecute(Long result) {
//		    	if(mRet != null){
//		    		header.setApp(new App(mRet));
//		    		tribtn.init(AppProfile.this, header.getApp());
//		    		bind(); 
//		    		cacheData(mRet.toString());
//		    	}
//		    	hideLoading();
//            }
//        };
//        loadTask.execute();
//    }
//
//    @Override
//    protected String getCacheId(){
//    	return cacheId(header.getApp());
//    }
    
    static public String cacheId(String appId){
//    	if(app == null) return null; // in case opened from share link
    	return AppProfile.class.getName()+appId;
    }
}