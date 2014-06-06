package goofy2.swably;

import java.net.URLEncoder;

import com.google.analytics.tracking.android.MapBuilder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ShareActivity extends TrackActivity {
	protected Button btnPrivate, btnPublic, btnLink;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);
		
		btnPrivate = (Button) findViewById(R.id.btnPrivate);
		btnPublic = (Button) findViewById(R.id.btnPublic);
		btnLink = (Button) findViewById(R.id.btnLink);
		if(Const.LOAD_FONTS){
			TextView tv = (TextView) findViewById(R.id.txtTitle);
			tv.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnPrivate.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnPublic.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnLink.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
		}
		
		btnPrivate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), Const.SHARE_PRIVATE_ACTIVITY);
				i.putExtra(Const.KEY_TEXT, getIntent().getStringExtra(Const.KEY_TEXT));
				i.putExtra(Const.KEY_SUBJECT, getIntent().getStringExtra(Const.KEY_SUBJECT));
				i.putExtra(Const.KEY_REVIEW, getIntent().getStringExtra(Const.KEY_REVIEW));
				i.putExtra(Const.KEY_APP, getIntent().getStringExtra(Const.KEY_APP));
				i.putExtra(Const.KEY_URL, getIntent().getStringExtra(Const.KEY_URL));
				startActivity(i);
				finish();
				tracker.send(MapBuilder.createEvent("ui_action", "button_press", getString(R.string.share_private), null).build());
			}
		});

		btnPublic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), Const.SHARE_PUBLIC_ACTIVITY);
				i.putExtra(Const.KEY_TEXT, getIntent().getStringExtra(Const.KEY_TEXT));
				i.putExtra(Const.KEY_SUBJECT, getIntent().getStringExtra(Const.KEY_SUBJECT));
				i.putExtra(Const.KEY_REVIEW, getIntent().getStringExtra(Const.KEY_REVIEW));
				i.putExtra(Const.KEY_APP, getIntent().getStringExtra(Const.KEY_APP));
				i.putExtra(Const.KEY_URL, getIntent().getStringExtra(Const.KEY_URL));
				startActivity(i);
				finish();
				tracker.send(MapBuilder.createEvent("ui_action", "button_press", getString(R.string.share_public), null).build());
			}
		});

		btnLink.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				cbm.setText(getIntent().getStringExtra(Const.KEY_TEXT).replace("?r=share", "?r="+getString(R.string.share_link_id)));		
				Utils.showToast(ShareActivity.this, getString(R.string.link_copied));
				finish();
				tracker.send(MapBuilder.createEvent("ui_action", "button_press", getString(R.string.share_link), null).build());
			}
		});
	
	}


}
