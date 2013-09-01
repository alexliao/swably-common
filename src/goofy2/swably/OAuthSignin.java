package goofy2.swably;

import goofy2.swably.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class OAuthSignin extends Activity {
	protected WebView mWeb;
	protected Handler mHandler = new Handler();
	public static final int RESULT_ERROR = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
//    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    	super.onCreate(savedInstanceState);
	    setContentView(R.layout.oauth_signin);
//	    disableSliding();
//	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.oauth_signin_title);
	    
    	final String sns_id = getIntent().getDataString();
		String url = Const.HTTP_PREFIX + "/connections/signin/"+sns_id+"?app_scheme=nappstr" + "&" + Utils.getClientParameters(this);

//	    TextView tv = (TextView) findViewById(R.id.txtTitle);
//    	int iconId = (Integer) Utils.getSnsResource(sns_id, "icon");
//	    String name = (String) Utils.getSnsResource(sns_id, "name");
//	    tv.setText(name);
//    	Drawable d = this.getResources().getDrawable(iconId);
//    	d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
//    	tv.setCompoundDrawables(d, null, null, null);
		
		mWeb = (WebView) this.findViewById(R.id.webView);
		WebSettings set = mWeb.getSettings();  
		set.setJavaScriptEnabled(true);
		set.setLightTouchEnabled(true);
		String ua = set.getUserAgentString();
		set.setUserAgentString(ua+" "+Const.APP_NAME);
		mWeb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); 
		//mWeb.setBackgroundColor(getResources().getColor(R.color.main_bg));
		mWeb.setWebViewClient(new WebViewClient() {     
			@Override     
			public boolean shouldOverrideUrlLoading(WebView view, String url)     
			{     
				return false;     
			}     
			@Override     
			public void onPageStarted (WebView view, String url, Bitmap favicon)
			{
				showDialog(0);
			}
			@Override     
			public void onPageFinished (WebView view, String url)
			{
				removeDialog(0);
			}
			@Override
			public void onLoadResource (WebView view, String url)
			{
				String origin_url = mWeb.getUrl();
				if(origin_url != null && !origin_url.equalsIgnoreCase(url)) // means the original url is loaded
					removeDialog(0);
			}
		});                    		
		
		mWeb.setWebChromeClient(new WebChromeClient() {  
//		@Override  
//			public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)  
//			{  
//				new AlertDialog.Builder(BannkaWebActivity.this)  
//				    .setTitle("javaScript dialog")  
//				    .setMessage(message)  
//				    .setPositiveButton(android.R.string.ok,  
//		                new AlertDialog.OnClickListener()  
//		                {  
//		                    public void onClick(DialogInterface dialog, int which)  
//		                    {  
//		                        result.confirm();  
//		                    }  
//		                })  
//		            .setCancelable(false)  
//		            .create()  
//		            .show();  
//				return true;  
//		    };  
		});  		
		mWeb.addJavascriptInterface(new Object() {  
			@SuppressWarnings("unused")
			public void onGetAccessToken(String access_token) {
//				Utils.alert(OAuthSignin.this, access_token);
				Intent ret = new Intent();
				ret.putExtra("access_token", access_token);
				ret.putExtra("sns_id", sns_id);
				OAuthSignin.this.setResult(RESULT_OK, ret);
				finish();
            }  
			@SuppressWarnings("unused")
			public void onError(String err) {
				Intent ret = new Intent();
				ret.putExtra("error", err);
				ret.putExtra("sns_id", sns_id);
				OAuthSignin.this.setResult(OAuthSignin.RESULT_ERROR, ret);
				finish();
            }  
         }, "android_callback");  		
    
		mWeb.loadUrl(url);
    }
    
//    protected void bind(){
//	    
//    	final String sns_id = getIntent().getDataString();
//	    TextView tv = (TextView) findViewById(R.id.txtTitle);
//
//    	int iconId = (Integer) Utils.getSnsResource(sns_id, "icon");
//	    String name = getIntent().getStringExtra("name");
//	    tv.setText(name);
//    	Drawable d = this.getResources().getDrawable(iconId);
//    	d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
//    	tv.setCompoundDrawables(d, null, null, null);
//    }

    @Override 
    public void onStart(){
    	super.onStart();
    	//if(getCurrentUser() != null) finish();
        //reload(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent){
    	super.onNewIntent(intent);
        //reload(intent);
    }
    
//    public void reload(Intent intent){
//		Uri data  = intent.getData();
//		if(data != null){ // force refresh
//			mWeb.reload();
//			intent.setData(null);
//		}
//    }

//    private class OnClickListener_btnRefresh implements Button.OnClickListener {
//		@Override
//		public void onClick(View v) {
//			//mWeb.reload(); // it's expensive for bandwidth because all images are forced to reload.
//			mWeb.loadUrl(mWeb.getUrl());
//			showDialog(0);
//		}
//		
//	}
}
