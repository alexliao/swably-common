package goofy2.swably;

import goofy2.swably.InviteSnsFriendsAdapter.InviteDialogListener;
import goofy2.swably.facebook.FacebookApp;
import goofy2.utils.AsyncImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;


public class InviteContactsAdapter extends InviteSnsFriendsAdapter {

	public InviteContactsAdapter(CloudActivity context, JSONArray stream,
			HashMap<String, Integer> loadingImages) {
		super(context, stream, loadingImages, "contacts");
	}

	@Override
	protected void invite(final JSONObject user){
		ArrayList<String> menuItems = new ArrayList<String>();

		String contactId = user.optString("id","");
		ContentResolver cr = mContext.getContentResolver();
		
		// phone numbers
		Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "  
				+ contactId, null, null);
		while(phone.moveToNext()){
			String str = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			menuItems.add(str);
		}
		phone.close();

		// Emails
		Cursor email = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "  
				+ contactId, null, null);
		while(email.moveToNext()){
			String str = email.getString(email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
			menuItems.add(str);
		}
		email.close();

		String[] tmp = new String[menuItems.size()];
		menuItems.toArray(tmp);
		final String[] items = tmp;
		
		Builder ad = new AlertDialog.Builder(mContext);
		ad.setTitle(mContext.getString(R.string.send_invite_to));
		ad.setItems(items, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
//				onPopupClick(position, whichButton, menuItems, update);	
				sendVia(user, items[whichButton]);
			}
		});
	    //ad.setNegativeButton(getString(R.string.cancel), null);
		ad.show();
	}

	protected void sendVia(JSONObject user, String item) {
		String requestId = genRequestId();
		String content = genInviteContent(requestId); 
		Intent intent = null;
		if(item.contains("@")){ // Email
//			intent = new Intent(android.content.Intent.ACTION_SEND);
//			intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {item});
//			intent.putExtra(android.content.Intent.EXTRA_SUBJECT, mContext.getString(R.string.app_name));
////	    	Intent i = Intent.createChooser(intent, mContext.getString(R.string.tell_friends_via));
//			intent.putExtra(android.content.Intent.EXTRA_TEXT, content);
//			intent.setType("text/plain");
//			mContext.startActivity(intent);

			Uri uri = Uri.parse("mailto:"+item);  
			intent = new Intent(Intent.ACTION_SENDTO, uri);  
			intent.putExtra(android.content.Intent.EXTRA_SUBJECT, mContext.getString(R.string.app_name));
			intent.putExtra(android.content.Intent.EXTRA_TEXT, content);
		}else{
			Uri uri = Uri.parse("smsto:"+item); 
			intent = new Intent(Intent.ACTION_SENDTO, uri); 
			intent.putExtra("sms_body", content); 
		}
		
//		try {
//			user.put("is_pending", true);
//			JSONObject currentUser = Utils.getCurrentUser(mContext);
//			currentUser.put("invites_left", currentUser.optInt("invites_left", 0)-1);
//			mContext.setCurrentUser(currentUser);
//			mContext.sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
//			//save pending status
//			Utils.setUserPrefString(mContext, "C"+user.optString("id")+"_pending", "true");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		createInvite(requestId, item);
		onSent(user, requestId, item);
		
		mContext.startActivity(intent);		
//		mContext.startActivityForResult(intent, 28);
	}

	@Override
	void bindAvatar(View view, ImageView iv, String url){
		new AsyncImageLoader(mContext, iv, mPosition).setThreadPool(mLoadImageThreadPool).loadContactAvatar(url);
	}

}
