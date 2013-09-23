package goofy2.swably;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SharePublicActivity extends Activity {
	Button btnFacebook, btnTwitter, btnGooglePlus;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_public);
		
		final String shareText = getIntent().getStringExtra(Const.KEY_TEXT);
		final String shareSubject = getIntent().getStringExtra(Const.KEY_SUBJECT);
		
		btnFacebook = (Button) findViewById(R.id.btnFacebook);
		btnTwitter = (Button) findViewById(R.id.btnTwitter);
		btnGooglePlus = (Button) findViewById(R.id.btnGooglePlus);
		if(Const.LOAD_FONTS){
			TextView tv = (TextView) findViewById(R.id.txtTitle);
			tv.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnFacebook.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnTwitter.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnGooglePlus.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
		}
		
		btnFacebook.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Utils.shareTo(SharePublicActivity.this, shareText, shareSubject, "com.facebook.katana");
				finish();

//				List<Intent> targetedShareIntents = new ArrayList<Intent>();
//		        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
//		        shareIntent.setType("text/plain");
//		        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
//		        if (!resInfo.isEmpty()){
//		            for (ResolveInfo resolveInfo : resInfo) {
//		                String packageName = resolveInfo.activityInfo.packageName;
//		                Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
//		                targetedShareIntent.setType("text/plain");
//		                targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "subject to be shared");
//		                if (packageName.equalsIgnoreCase("com.facebook.katana")){
//		                    targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://link-to-be-shared.com");
//		                }else{
//		                    targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "text message to shared");
//		                }
//
//		                targetedShareIntent.setPackage(packageName);
//		                targetedShareIntents.add(targetedShareIntent);
//
//
//		            }
//		            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
//
//		            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
//
//		            startActivity(chooserIntent);
//		        }
			}
		});
		
		btnTwitter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.shareTo(SharePublicActivity.this, shareText, shareSubject, "com.twitter.android");
				finish();
			}
		});


		btnGooglePlus.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.shareTo(SharePublicActivity.this, shareText, shareSubject, "com.google.android.apps.plus");
				finish();
			}
		});

	}

	
}
