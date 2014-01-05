package goofy2.swably;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.MapBuilder;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SharePublicActivity extends TrackActivity {
	protected Button btnPublic1, btnPublic2, btnPublic3;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_public);
		
		final String shareText = getIntent().getStringExtra(Const.KEY_TEXT);
		final String shareSubject = getIntent().getStringExtra(Const.KEY_SUBJECT);
		
		btnPublic1 = (Button) findViewById(R.id.btnPublic1);
		btnPublic2 = (Button) findViewById(R.id.btnPublic2);
		btnPublic3 = (Button) findViewById(R.id.btnPublic3);
		if(Const.LOAD_FONTS){
			TextView tv = (TextView) findViewById(R.id.txtTitle);
			tv.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnPublic1.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnPublic2.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnPublic3.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
		}
		
		btnPublic1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.shareTo(SharePublicActivity.this, shareText.replace("?r=share", "?r="+getString(R.string.share_public1_id)), shareSubject, getString(R.string.share_package_public1), getString(R.string.share_public1));
				finish();
				tracker.send(MapBuilder.createEvent("ui_action", "button_press", getString(R.string.share_public1), null).build());
			}
		});

		btnPublic2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Utils.shareTo(SharePublicActivity.this, shareText.replace("?r=share", "?r="+getString(R.string.share_public2_id)), shareSubject, getString(R.string.share_package_public2), getString(R.string.share_public2));
				finish();
				tracker.send(MapBuilder.createEvent("ui_action", "button_press", getString(R.string.share_public2), null).build());

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
		
		btnPublic3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.shareTo(SharePublicActivity.this, shareText.replace("?r=share", "?r="+getString(R.string.share_public3_id)), shareSubject, getString(R.string.share_package_public3), getString(R.string.share_public3));
				finish();
				tracker.send(MapBuilder.createEvent("ui_action", "button_press", getString(R.string.share_public3), null).build());
			}
		});


	}

	
}
