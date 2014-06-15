package goofy2.swably.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import goofy2.swably.About;
import goofy2.swably.Checker;
import goofy2.swably.Const;
import goofy2.swably.InviteContacts;
import goofy2.swably.People;
import goofy2.swably.R;
import goofy2.swably.SnsFriendsFragment;
import goofy2.swably.Utils;
import goofy2.utils.ParamRunnable;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class SharingPostsFragment extends PeopleReviewsFragment{
    
    @Override
	protected String getPageTitle() {
		return null;
	}

	@Override
	protected String getAPI() {
		return "/comments/posts";
	}

    @Override
	public long getCacheExpiresIn(){
		return 60*1000;
	}

	protected View getListHeader() {
		if(!shouldShowInvite()) return null;
		LayoutInflater inflater = LayoutInflater.from(a());
		View v = inflater.inflate(R.layout.invite_box, null);
		return v;
	}

    public double getLastCloseInviteTime(Context context){
		String strTime = Utils.getUserPrefString(context, Utils.getCurrentUserId(context) + "LastCloseInviteTime", null);
		double time;
		if(strTime == null){
			time = 0;
			setLastCloseInviteTime(context, time);
		}
		else time = Double.parseDouble(strTime);
		return time;
	}
	static public void setLastCloseInviteTime(Context context, double value){
		Utils.setUserPrefString(context, Utils.getCurrentUserId(context) + "LastCloseInviteTime", Double.toString(value));
	}
	
	boolean shouldShowInvite(){
		JSONObject me = Utils.getCurrentUser(a());
    	if(me == null) return false;
		if(Utils.isEmpty(me.optString("email"))) return false; // don't show invite box if there is an Email input box;
		if((System.currentTimeMillis()/1000 - getLastCloseInviteTime(a())) < 7*24*3600) return false;
		return true;
	}
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	if(!shouldShowInvite()) return;
    	
        final Handler handler = new Handler();
		final TextView txtFriendCount = (TextView) getView().findViewById(R.id.txtFriendCount);
		final View viewNext = getView().findViewById(R.id.viewNext);
		final View loadingFriends = getView().findViewById(R.id.loadingFriends);
		new Thread() {
			public void run(){
				int friendsCount = 0;
				try {
					
			    	// get friends count
					Checker.checkSnsJoin(a());
					String strSnsFriends = Utils.loadCache(a(), SnsFriendsFragment.cacheId());
					if(strSnsFriends != null){
						JSONArray json = new JSONArray(strSnsFriends);
						friendsCount = json.length();
					}
					handler.post(new Runnable(){
						public void run(){
							loadingFriends.setVisibility(View.GONE);
							txtFriendCount.setVisibility(View.VISIBLE);
						}
					});
	
			    	// show invite
					for(int i=1; i<=friendsCount; i++){
							Thread.sleep(100);
							handler.post(new ParamRunnable(i){
								public void run(){
									txtFriendCount.setText(""+(Integer)param);
								}
							});
					}
					Thread.sleep(300);
					handler.post(new ParamRunnable(friendsCount){
						public void run(){
							txtFriendCount.setVisibility(View.GONE);
							viewNext.setVisibility(View.VISIBLE);
							TextView txtInvitePrompt = (TextView) getView().findViewById(R.id.txtInvitePrompt);
							txtInvitePrompt.setText(String.format(a().getString(R.string.invite_prompt1), Utils.getCurrentUser(a()).optString("name"), (Integer)param));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}.start();
    	
    	
//		new Thread() {
//			public void run(){
//				try {
//					for(int i=1; i<=max; i++){
//							Thread.sleep(100);
//							handler.post(new ParamRunnable(i){
//								public void run(){
//									txtFriendCount.setText(""+(Integer)param);
//								}
//							});
//					}
//					Thread.sleep(300);
//					handler.post(new Runnable(){
//						public void run(){
//							txtFriendCount.setVisibility(View.GONE);
//							viewNext.setVisibility(View.VISIBLE);
//							TextView txtInvitePrompt = (TextView) getView().findViewById(R.id.txtInvitePrompt);
//							txtInvitePrompt.setText(String.format(a().getString(R.string.invite_prompt1), Utils.getCurrentUser(a()).optString("name"), max));
//						}
//					});
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}.start();
		
		View btnInviteMore = getView().findViewById(R.id.btnInviteMore);
		btnInviteMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				a().startActivity(new Intent(a(), InviteContacts.class));
			}
		});

		View btnCloseInvite = getView().findViewById(R.id.btnCloseInvite);
		btnCloseInvite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View viewInviteBox = getView().findViewById(R.id.viewInviteBox);
				((ListView) mList).removeHeaderView(viewInviteBox);
				setLastCloseInviteTime(a(), System.currentTimeMillis()/1000);
			}
		});
    }

//	public void getFriendsCount(){
//		String cacheId = SnsFriendsFragment.cacheId();
//		String strCache = Utils.loadCache(a(), cacheId);
//
//		if(strCache == null || (System.currentTimeMillis() - Utils.getCacheAt(a(),cacheId)) > SnsFriendsFragment.cacheExpiresIn()){
//			String snsId = Utils.getCurrentUser(a()).optString("signup_sns");
//		    String url = Const.HTTP_PREFIX + "/connections/find_friends/"+snsId+"?format=json&check=true&"+Utils.getLoginParameters(a())+"&"+Utils.getClientParameters(a());
//			String strResult = null;
//			String err = null;
//			
//			try{
//				Log.d(Const.APP_NAME, Const.APP_NAME + " getFriendsCount run");
//				HttpPost httpReq = new HttpPost(url);
//				List <NameValuePair> params = new ArrayList <NameValuePair>();
//				params.add(new BasicNameValuePair("contacts", SnsFriendsFragment.getContactsEmails(a())));
//				httpReq.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//				HttpParams httpParameters = new BasicHttpParams();
//				HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT);
//				httpReq.setParams(httpParameters);
//				HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
//				strResult = EntityUtils.toString(httpResp.getEntity());
//				int code = httpResp.getStatusLine().getStatusCode(); 
//				if( code != 200){
//					JSONObject json = new JSONObject(strResult);
//					err = json.getString("error_message");
//				}
//			}catch (Exception e){
//				err = e.getMessage();
//				Log.e(Const.APP_NAME, Const.APP_NAME + " getFriendsCount err: " + err);
//			}
//			
//			if(err == null){
//				try {
//					JSONArray latest = new JSONArray(strResult);
//					if(strCache != null){
//						JSONArray cached = new JSONArray(strCache);
//						int lastTime = cached.getJSONObject(0).optInt("created_at");
//						JSONArray newJoins = new JSONArray();
//						for(int i=0; i<latest.length(); i++){
//							if(latest.getJSONObject(i).optInt("created_at") > lastTime){
//								newJoins.put(latest.getJSONObject(i));
//							}
//						}
//						if(newJoins.length() > 0) notifyJoins(newJoins);
//					}
//					Utils.cacheData(a(), latest.toString(), cacheId);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//    
//    private void notifyJoins(JSONArray newJoins) {
//    	final int NOTIFICATION_ID = 3828;
//		if(!Checker.isNoticeOn(a())) return;
//    	int thisCount = newJoins.length();
//    	if(thisCount == 0) return;
//
//    	String text;
//		String expandedText;
//		String expandedTitle;
//		Intent i;
//    	if(thisCount == 1){
//    		JSONObject user = newJoins.optJSONObject(0); 
//        	text = String.format(getString(R.string.joining_noti_title), user.optString("name"));
//    		expandedTitle = text;
//    		expandedText = "";
//    	}else{
//        	text = String.format(getString(R.string.new_joins_count), thisCount);
//        	expandedTitle = text;
//        	expandedText = getString(R.string.tap_to_check);
//    	}
//		i = new Intent(a(), People.class);
//		i.setData(Uri.parse("0")); // initial at sns friends tab
//
//    	NotificationManager nm = (NotificationManager)a().getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notification = Utils.getDefaultNotification(text);
//		PendingIntent launchIntent = PendingIntent.getActivity(a(), newJoins.hashCode(), i, PendingIntent.FLAG_ONE_SHOT);
//		notification.setLatestEventInfo(a(), expandedTitle, expandedText, launchIntent);
//		nm.notify(NOTIFICATION_ID, notification);
//	}

}
