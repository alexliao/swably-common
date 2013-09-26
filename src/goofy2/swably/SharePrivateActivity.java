package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SharePrivateActivity extends Activity {
	Button btnFacebookMessenger, btnText, btnGmail;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_private);
		
		final String shareText = getIntent().getStringExtra(Const.KEY_TEXT);
		final String shareSubject = getIntent().getStringExtra(Const.KEY_SUBJECT);

		btnFacebookMessenger = (Button) findViewById(R.id.btnFacebookMessenger);
		btnText = (Button) findViewById(R.id.btnText);
		btnGmail = (Button) findViewById(R.id.btnGmail);
		if(Const.LOAD_FONTS){
			TextView tv = (TextView) findViewById(R.id.txtTitle);
			tv.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnFacebookMessenger.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnText.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
			btnGmail.setTypeface(CloudActivity.FONT_ROBOTO_LIGHT);
		}

		btnFacebookMessenger.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.shareTo(SharePrivateActivity.this, shareText, shareSubject, "com.facebook.orca");
				finish();
			}
		});


		btnText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.shareTo(SharePrivateActivity.this, shareText, shareSubject, "com.whatsapp");
				finish();
			}
		});
	
		btnGmail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.shareTo(SharePrivateActivity.this, shareText, shareSubject, "com.google.android.gm");
				finish();
			}
		});

	}


}
