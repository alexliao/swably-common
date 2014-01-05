package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.MapBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SharePrivateActivity extends TrackActivity {
	protected Button btnPrivate1, btnPrivate2, btnPrivate3;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_private);
		
		final String shareText = getIntent().getStringExtra(Const.KEY_TEXT);
		final String shareSubject = getIntent().getStringExtra(Const.KEY_SUBJECT);

		btnPrivate1 = (Button) findViewById(R.id.btnPrivate1);
		btnPrivate2 = (Button) findViewById(R.id.btnPrivate2);
		btnPrivate3 = (Button) findViewById(R.id.btnPrivate3);
		if(Const.LOAD_FONTS){
			TextView tv = (TextView) findViewById(R.id.txtTitle);
			tv.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnPrivate1.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnPrivate2.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnPrivate3.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
		}

		btnPrivate1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.shareTo(SharePrivateActivity.this, shareText.replace("?r=share", "?r="+getString(R.string.share_private1_id)), shareSubject, getString(R.string.share_package_private1), getString(R.string.share_private1));
				finish();
				tracker.send(MapBuilder.createEvent("ui_action", "button_press", getString(R.string.share_private1), null).build());
			}
		});


		btnPrivate2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.shareTo(SharePrivateActivity.this, shareText.replace("?r=share", "?r="+getString(R.string.share_private2_id)), shareSubject, getString(R.string.share_package_private2), getString(R.string.share_private2));
				finish();
				tracker.send(MapBuilder.createEvent("ui_action", "button_press", getString(R.string.share_private2), null).build());
			}
		});
	
		btnPrivate3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.shareTo(SharePrivateActivity.this, shareText.replace("?r=share", "?r="+getString(R.string.share_private3_id)), shareSubject, getString(R.string.share_package_private3), getString(R.string.share_private3));
				finish();
				tracker.send(MapBuilder.createEvent("ui_action", "button_press", getString(R.string.share_private3), null).build());
			}
		});

	}


}
