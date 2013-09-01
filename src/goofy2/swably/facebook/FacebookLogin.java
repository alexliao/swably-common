/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package goofy2.swably.facebook;

import goofy2.swably.Start.FbAPIsAuthListener;
import goofy2.swably.facebook.SessionEvents.AuthListener;
import goofy2.swably.facebook.SessionEvents.LogoutListener;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import goofy2.swably.Const;
import goofy2.swably.Start;
import goofy2.swably.Utils;

public class FacebookLogin {

    private Facebook mFb;
    private Handler mHandler;
//    private SessionListener mSessionListener = new SessionListener();
    private String[] mPermissions;
    private Activity mActivity;
    private int mActivityCode;
    private SessionEvents mSessionEvents;

//    public FacebookLoginButton(Context context) {
//        super(context);
//    }
//
//    public FacebookLoginButton(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public FacebookLoginButton(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//    }

//    public void init(final Activity activity, final int activityCode, final Facebook fb) {
//        init(activity, activityCode, fb, new String[] {});
//    }

    public void init(View button, final Activity activity, final int activityCode, final Facebook fb,
            final String[] permissions, final SessionEvents events) {
    	
        mActivity = activity;
        mActivityCode = activityCode;
        mFb = fb;
        mPermissions = permissions;
        mHandler = new Handler();
        mSessionEvents = events;

//        setBackgroundColor(Color.TRANSPARENT);
//        setImageResource(fb.isSessionValid() ? R.drawable.logout_button : R.drawable.login_button);
//        drawableStateChanged();

//        SessionEvents.addAuthListener(mSessionListener);
//        SessionEvents.addLogoutListener(mSessionListener);
        button.setOnClickListener(new ButtonOnClickListener());
    }

    private final class ButtonOnClickListener implements OnClickListener {
        /*
         * Source Tag: login_tag
         */
        @Override
        public void onClick(View arg0) {
//            if (mFb.isSessionValid()) {
//                SessionEvents.onLogoutBegin();
//                AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFb);
//                asyncRunner.logout(mActivity, new LogoutRequestListener());
//            } else {
//                mFb.authorize(mActivity, mPermissions, mActivityCode, new LoginDialogListener());
//            }
			Utils.confirm(mActivity, null, "This entry is not for new Swably user. Are you sure you have ever signed in Swably with Facebook?", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
		        	if(!Utils.HttpTest(mActivity)) return;
		            mFb.authorize(mActivity, mPermissions, mActivityCode, new LoginDialogListener());
				}
			});
        }
    }

    private final class LoginDialogListener implements DialogListener {
        @Override
        public void onComplete(Bundle values) {
        	mSessionEvents.onLoginSuccess();
        }

        @Override
        public void onFacebookError(FacebookError error) {
        	mSessionEvents.onLoginError(error.getMessage());
        }

        @Override
        public void onError(DialogError error) {
        	mSessionEvents.onLoginError(error.getMessage());
        }

        @Override
        public void onCancel() {
        	mSessionEvents.onLoginError("Action Canceled");
        }
    }

//    private class LogoutRequestListener extends BaseRequestListener {
//        @Override
//        public void onComplete(String response, final Object state) {
//            /*
//             * callback should be run in the original thread, not the background
//             * thread
//             */
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                	mSessionEvents.onLogoutFinish();
//                }
//            });
//        }
//    }

//    private class SessionListener implements AuthListener, LogoutListener {
//
//        @Override
//        public void onAuthSucceed() {
////            setImageResource(R.drawable.logout_button);
//            SessionStore.save(mFb, mActivity);
//        }
//
//        @Override
//        public void onAuthFail(String error) {
//        	Log.d("", Const.APP_NAME + " FacebookLogin onAuthFail: "+error);
//        }
//
//        @Override
//        public void onLogoutBegin() {
//        }
//
//        @Override
//        public void onLogoutFinish() {
//            SessionStore.clear(mActivity);
////            setImageResource(R.drawable.login_button);
//        }
//    }

}
