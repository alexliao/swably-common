package goofy2.swably;

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
	}


}
