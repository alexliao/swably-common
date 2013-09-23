package goofy2.swably;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ShareActivity extends Activity {
	Button btnPrivate, btnPublic, btnLink;
	
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
				Intent i = new Intent(getApplicationContext(), SharePrivateActivity.class);
				startActivity(i);
			}
		});

		btnPublic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), SharePublicActivity.class);
				i.putExtra(Const.KEY_REVIEW, getIntent().getStringExtra(Const.KEY_REVIEW));
				startActivity(i);
			}
		});
	}


}
