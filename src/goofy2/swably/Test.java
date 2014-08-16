package goofy2.swably;

import goofy2.swably.data.App;
import goofy2.swably.fragment.AppCommentsFragment;
import goofy2.utils.JSONUtils;
import goofy2.utils.ParamRunnable;
import goofy2.utils.UploadImage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

// Some test cases
public class Test {
	
	static public void changeCurrentUserBanner(final Context context){
		new Thread(new Runnable(){
			@Override
			public void run() {
				JSONObject user = Utils.getCurrentUser(context);
				File fBanner = new File("/sdcard/temp/pic2.jpg");
				JSONObject ret = uploadUserPicture(context, user.optString("id"), "banner_file", fBanner);
				if(ret != null) Utils.setCurrentUser(context, ret); // update current user
			}
		}).start();
	}
	
	static public void changeCurrentUserAvatar(final Context context){
		new Thread(new Runnable(){
			@Override
			public void run() {
				JSONObject user = Utils.getCurrentUser(context);
				File fAvatar = new File("/sdcard/temp/pic1.jpg");
				JSONObject ret = uploadUserPicture(context, user.optString("id"), "avatar_file", fAvatar);
				if(ret != null) Utils.setCurrentUser(context, ret); // update current user
					}
		}).start();
	}

	static protected JSONObject uploadUserPicture(Context context, String userId, final String paramName, File picture){
		JSONObject result = null;
		String url = Const.HTTP_PREFIX + "/users/" + userId + ".json";
		Map<String, String> map = new HashMap<String, String>();
		map.put("_method", "PUT");
		
		Map<String, File> files = new HashMap<String, File>();
		try {
			files.put(paramName, picture);
			
			final long totalSize = picture.length();
			final Map<String, String> mapParams = map;
			final Map<String, File> filesParams = files;

			String ret = UploadImage.post_3(url, mapParams, filesParams, true, 1024*10, new ParamRunnable() {
				public void run(){
					long sizeSent = Long.parseLong((String)param);
					int percent = (int)(sizeSent*100/totalSize-1);
            		Log.v(Const.APP_NAME, paramName + " upload progress: " + percent);
            		// show upload progress here
            		// ...
					param = false; // You can cancel the upload by set param = true
				}
			});
			result = new JSONObject(ret);
		} catch (final Exception e) {
			String errMsg = null;
			if(e.getClass() == CancellationException.class){
			}else{
				errMsg = e.toString();
	    		Log.v(Const.APP_NAME, paramName + " upload failed: " + errMsg);
			}
		}
		return result;
	}

}
