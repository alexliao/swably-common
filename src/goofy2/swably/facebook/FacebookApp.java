package goofy2.swably.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import goofy2.swably.Const;
import goofy2.swably.Start.FbAPIsAuthListener;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class FacebookApp extends Application {
    public static final String APP_ID = "229666087090658";
    public static Facebook mFacebook;
    public static AsyncFacebookRunner mAsyncRunner;
    public static String[] permissions = { "offline_access", "publish_stream", "user_about_me"};
    public final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
    
    static public void init(Context context){
    	mFacebook = new Facebook(FacebookApp.APP_ID);
      // Instantiate the asynrunner object for asynchronous api calls.
      mAsyncRunner = new AsyncFacebookRunner(mFacebook);
        // restore session if one exists
        SessionStore.restore(FacebookApp.mFacebook, context);
//        SessionEvents.addLogoutListener(new FbAPIsLogoutListener());
    }
    
    static public void logout(final Context context){
      AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
      asyncRunner.logout(context, new RequestListener() {

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			Log.d("", Const.APP_NAME + " FacebookApp logout onComplete: " + response);
            SessionStore.clear(context);
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			Log.d("", Const.APP_NAME + " FacebookApp logout onIOException: " + e.getMessage());
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			Log.d("", Const.APP_NAME + " FacebookApp logout onFileNotFoundException: " + e.getMessage());
			
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			Log.d("", Const.APP_NAME + " FacebookApp logout onMalformedURLException: " + e.getMessage());
			
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			Log.d("", Const.APP_NAME + " FacebookApp logout onFacebookError: " + e.getMessage());
			
		}});
    }
}
