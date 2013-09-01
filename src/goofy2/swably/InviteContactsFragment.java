package goofy2.swably;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import goofy2.swably.facebook.FacebookApp;
import goofy2.swably.fragment.CloudUsersFragment;
import goofy2.utils.DownloadImage;
import goofy2.utils.JSONUtils;
import goofy2.utils.ParamRunnable;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class InviteContactsFragment extends CloudUsersFragment {

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        bind(v);
        return v;
    }
    
	@Override
	protected void onDataChanged(int item){
		super.onDataChanged(item);
		bind(getView());
	}
    
//    // disable cache
//    @Override
//    public String getCacheId(){
//    	return null;
//    }

    @Override
	protected CloudBaseAdapter getAdapter() {
		return new InviteContactsAdapter(ca(), mListData, mLoadingImages);
	}
	
//	@Override
//	protected String getImageUrl(JSONObject item, int index) throws JSONException {
//		String url;
//		url = item.optString("avatar", null);
//		return url;
//	}
//	@Override
//	protected String getImageUrl(JSONObject item, int index) throws JSONException {
//		long photo_id = item.optLong("photo_id", 0);
//		String ret = null;
//		if(photo_id > 0){
//			long id = item.optLong("id", 0);
//			ret = "/contacts/"+id;
//		}
//		return ret;
//	}

	@Override
    protected void setContent(){
	    setContentView(R.layout.invite_sns_friends_fragment);
    }

    protected void bind(View v){
    	if(v == null) return; 
	    int invitesLeft = Utils.getCurrentUser(a()).optInt("invites_left", 0);
	    TextView tv = (TextView) v.findViewById(R.id.txtInvitesLeft);
	    tv.setText(String.format(getString(R.string.invites_left), invitesLeft).toUpperCase());

    }

    @Override
	protected String getUrl() {
		return "";
	}

	@Override
	protected void onClickHeader() {
		// TODO Auto-generated method stub

	}

	@Override
	protected View getListHeader() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
	protected void loadMore(){
		// disable auto loading
	}	

	@Override
	protected CharSequence getListTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onClickItem(final int position) throws JSONException {
	}
	
	@Override
	protected String loadStream(String url, String lastId) {
		Log.d(Const.APP_NAME, Const.APP_NAME + " InviteContacts loadStream ");
		String err = null;
		try{
			mListData = getContacts();
		}catch (Exception e){
//	    	Utils.alertTitle(this, getString(R.string.err_no_network_title), e.getMessage());
			err = e.getMessage();
			Log.e(Const.APP_NAME, Const.APP_NAME + " InviteContacts loadStream err: " + err);
		}
		return err;
	}

	private JSONArray getContacts(){
		JSONArray ret = new JSONArray();
		
		ContentResolver cr = a().getContentResolver();
		String[] mProjection =
		{
			ContactsContract.Contacts._ID,  
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.PHOTO_ID
		};
//		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, mProjection, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
//		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1", null, ContactsContract.Contacts.LAST_TIME_CONTACTED + " DESC, " + ContactsContract.Contacts.DISPLAY_NAME);
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, mProjection, ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1", null, ContactsContract.Contacts.LAST_TIME_CONTACTED + " DESC, " + ContactsContract.Contacts.DISPLAY_NAME);
//		Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, mProjection, null, null, null);
		int limit = 200;
		while (cursor.moveToNext())   
		{
			JSONObject contact = new JSONObject();
			String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//			String avatar = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		    String content = null;
		    long photo_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
		    
		    try {
				contact.put("name", name);
				contact.put("content", content);
				contact.put("id", id);
				if(photo_id > 0) contact.put("avatar", "/contacts/"+id);
				
				String pending = Utils.getUserPrefString(a(), "contacts"+id+"_pending", "false");
				if(pending.equals("true")) contact.put("is_pending", true);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		    ret.put(contact);
//		    if(ret.length() >= limit) break; 
		}
		cursor.close();
		return ret;
	}
	
//	private String getAvatar(long id){
//		return "/contacts/photo/"+id;
//	}

//	@Override
//	protected boolean saveImage(String imageUrl){
//		Boolean ret = false;
//		String fileName = Utils.getImageFileName(imageUrl);
//		File f = new File(fileName);
//		if(f.length() == 0){ // not exists or size is 0
//			try {
//				ContentResolver cr = a().getContentResolver();
//				long id = Long.parseLong(imageUrl.substring(imageUrl.lastIndexOf("/")+1));
//				Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
//			    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
//			    Bitmap contactPhoto = BitmapFactory.decodeStream(input);
//		        FileOutputStream out = new FileOutputStream(f);   
//		        contactPhoto.compress(Bitmap.CompressFormat.PNG, 100, out);
//			    ret = true;
//			} catch (Exception e) {
//				e.printStackTrace();
//			} catch (OutOfMemoryError e){
//				Utils.showToast(a(), "Out of memory");
//				e.printStackTrace();
//			}
//		}
//		return ret;
//	}

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
////        if (resultCode == RESULT_CANCELED) {
////        	Utils.showToast(this, "RESULT_CANCELED " + requestCode);
////        } 
////        if (resultCode == RESULT_OK) { 
////        	Utils.showToast(this, "RESULT_OK " + requestCode);
////        } 
//// ACTION_SENDTO doesn't return result, you always get RESULT_CANCELED whatever message is sent or canceled.
//    	
//    	
//    } 

//    @Override
//	public long getCacheExpiresIn(){
//		return Const.DEFAULT_CACHE_EXPIRES_IN*10;
//	}

    @Override
	public long getCacheExpiresIn(){
		return 26*3600*1000; 
	}

}
