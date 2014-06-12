package goofy2.swably;

import goofy2.swably.UsersAdapter.ViewHolder;
import goofy2.swably.data.App;
import goofy2.swably.facebook.FacebookApp;
import goofy2.utils.AsyncImageLoader;
import goofy2.utils.ParamRunnable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class InviteSnsFriendsAdapter extends CloudBaseAdapter {
	 private String mSnsId;

	public InviteSnsFriendsAdapter(CloudActivity context, JSONArray stream,	HashMap<String, Integer> loadingImages, String sns_id) {
		super(context, stream, loadingImages);
		mSnsId = sns_id;
	}

	public void bindView(View viewInfo, final JSONObject info) {
		handleDivider(viewInfo);
		try {
			String str;
			ViewHolder holder = (ViewHolder) viewInfo.getTag();
			
			ImageView iv = holder.avatar;
			iv.setImageResource(R.drawable.noname);
			String url = null;
			if(!info.isNull("avatar")){
				url = info.optString("avatar", null);
			}
//			if(url != null){
//				Bitmap bm = Utils.getImageFromFile(mContext, url); 
//				//if(bm == null) FeedHelper.asyncLoadImage(context, 0, url, null);
//				if(bm != null) iv.setImageBitmap(bm);
//			}
			iv.setImageResource(R.drawable.noname);
//			new AsyncImageLoader(mContext, iv, mPosition, mLoadImageThreadPool).loadUrl(url);
			bindAvatar(viewInfo, iv, url);

			TextView tv;
			tv = holder.txtName;
			tv.setText(info.optString("name"));
			tv.setTypeface(mContext.mBoldFont);

			tv = holder.txtContent;
			tv.setText(info.optString("content"));
			tv.setTypeface(mContext.mNormalFont);

			final View btnInvite = holder.btnInvite;
			final View btnPending = holder.btnPending;
			boolean isPending = info.optBoolean("is_pending", false); 
			setStatus(btnInvite, btnPending, isPending);
			btnPending.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					invite(info);
				}
			});
			btnInvite.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					invite(info);
				}
			});
		} catch (Exception e) {
			Log.d(Const.APP_NAME, Const.APP_NAME + " SnsFriendsAdapter - bindView err: " + e.getMessage());
		}
	}
	
	void bindAvatar(View view, ImageView iv, String url){
		new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadUrl(url);
	}
	
	protected void invite(final JSONObject user){
		final String eid = user.optString("id");
		final String requestId = genRequestId();

		if(mSnsId.equals("facebook")){
	        Bundle params = new Bundle();
	        params.putString("message", mContext.getString(R.string.invite_request));
	        params.putString("to", eid);
	        FacebookApp.mFacebook.dialog(mContext, "apprequests", params, new InviteDialogListener(mContext, user));
		}else if(mSnsId.equals("twitter") || mSnsId.equals("sina")){
			inviteDialog("@"+user.optString("id"), getInviteContent(), new ParamRunnable(){
				@Override
				public void run() {
					final String content = (String) param;
//                	Utils.showToast(mContext, content);
        			onSent(user, requestId, eid);
        	        new Thread() {
        	            @Override public void run() {
        	    			Utils.inviteSns(mContext, mSnsId, eid, content);
        	            }
        	        }.start();
                	Utils.setUserPrefString(mContext, "invite_content", content);
				}
			});
		}
	}
	
	private void setStatus(View btnInvite, View btnPending, boolean isPending){
		if(isPending){
			btnInvite.setVisibility(View.GONE);
			btnPending.setVisibility(View.VISIBLE);
		}else{
			btnInvite.setVisibility(View.VISIBLE);
			btnPending.setVisibility(View.GONE);
		}
//		if(Utils.getCurrentUser(mContext).optInt("invites_left") <= 0){
//			btnInvite.setEnabled(false);
//			btnPending.setEnabled(false);
//		}
	}
	
	public View newView(ViewGroup parent) {
		View ret = null;
		int resId = R.layout.invite_friend_row;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}

	class InviteDialogListener implements DialogListener 
	{
		CloudActivity mContext;
		JSONObject mUser;
		
		public InviteDialogListener(CloudActivity context, JSONObject user){
			mContext = context;
			mUser = user;
		}
		
		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			Log.d("", Const.APP_NAME + " InviteSnsFriends onComplete: " + values.toString());
//			Utils.showToast(mContext, "Request sent: "+values.toString());
			String requestId = values.getString("request");
			String toId = values.getString("to[0]");
			if(requestId != null){
				onSent(mUser, requestId, toId);
			}
		}
	
		@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub
			Log.d("", Const.APP_NAME + " InviteSnsFriends onFacebookError: " + e.getMessage());
			Utils.showToast(mContext, e.getMessage());
			
		}
	
		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub
			Log.d("", Const.APP_NAME + " InviteSnsFriends onError: " + e.getMessage());
			Utils.showToast(mContext, e.getMessage());
			
		}
	
		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Log.d("", Const.APP_NAME + " InviteSnsFriends onCancel: ");
			Utils.showToast(mContext, "Canceled");
			
		}
	}

	protected void onSent(JSONObject user, String requestId, String toId){
//		Utils.showToast(mContext, "Request sent");
//		setStatus(btnInvite, btnPending, true);
		try {
			user.put("is_pending", true);
			JSONObject currentUser = Utils.getCurrentUser(mContext);
			currentUser.put("invites_left", currentUser.optInt("invites_left", 0)-1);
			mContext.setCurrentUser(currentUser);
//			InviteSnsFriendsAdapter.this.notifyDataSetInvalidated();
			mContext.sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
			//save pending status
			Utils.setUserPrefString(mContext, mSnsId+user.optString("id")+"_pending", "true");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		createInvite(requestId, toId);
	}
	
	protected void createInvite(String requestId, String to){
		final HttpPost httpReq = new HttpPost(Const.HTTP_PREFIX+"/invites/create?format=json&request_id="+URLEncoder.encode(requestId)+"&to="+URLEncoder.encode(to)+"&"+Utils.getLoginParameters(mContext) + "&" + Utils.getClientParameters(mContext));
//		final Handler handler = new Handler(); 
		new Thread() {
			public void run(){
				try {
					final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	protected String genRequestId(){
		return mSnsId+Utils.getCurrentUserId(mContext)+Math.round(Math.random()*10000);
	}


	protected String getInviteContent(){
//		return Utils.getUserPrefString(mContext, "invite_content", mContext.getString(R.string.invite_request));
		return  mContext.getString(R.string.recommend_text) + " " + RecommendSwably.genRecommendUrl(Utils.getCurrentUserId(mContext), "invite") ;
	}
	
	protected String genInviteContent(String requestId){
//		return getInviteContent() + " " + Const.HTTP_PREFIX+"/invites/show?request_ids="+requestId;
//		return getInviteContent() + "&request_ids="+requestId ;
		return getInviteContent() ;
	}


    void inviteDialog(String name, final String defaultValue, final ParamRunnable onSent){
		final View v = LayoutInflater.from(mContext).inflate(R.layout.edit_invite, null);

		TextView txtName = (TextView) v.findViewById(R.id.txtName) ;
		txtName.setText(name);
		
		final EditText et = (EditText) v.findViewById(R.id.editContent);
		if(!Utils.isEmpty(defaultValue)){
			et.setText(defaultValue);
		}
        new AlertDialog.Builder(mContext)
        	.setCancelable(false)
        	.setInverseBackgroundForced(true)
//            .setTitle(mContext.getString(R.string.invitation))
            .setView(v)
            .setNegativeButton(mContext.getString(R.string.cancel), null)
            .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	String content = et.getText().toString();
                	onSent.param = content;
                	onSent.run();
                }
            })
            .show();
    }
	
	
	@Override
	protected Object newViewHolder(View convertView){
		ViewHolder holder = new ViewHolder();
		holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
		holder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);
		holder.txtName = (TextView) convertView.findViewById(R.id.txtName);

		holder.btnInvite = convertView.findViewById(R.id.btnInvite);
		holder.btnPending = convertView.findViewById(R.id.btnPending);
		return holder;
	}
	
	static class ViewHolder	{
		ImageView avatar;
		TextView txtContent;
		TextView txtName;
		View btnInvite;
		View btnPending;
	}
}
