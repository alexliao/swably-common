package goofy2.swably;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ShareActivity extends Activity {
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
				startActivity(i);
				finish();
			}
		});

		btnPublic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), Const.SHARE_PUBLIC_ACTIVITY);
				i.putExtra(Const.KEY_TEXT, getIntent().getStringExtra(Const.KEY_TEXT));
				i.putExtra(Const.KEY_SUBJECT, getIntent().getStringExtra(Const.KEY_SUBJECT));
				startActivity(i);
				finish();
			}
		});

		btnLink.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				cbm.setText(getIntent().getStringExtra(Const.KEY_TEXT));		
				Utils.showToast(ShareActivity.this, getString(R.string.link_copied));
				finish();
			}
		});
	
	}


}
