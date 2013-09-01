package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.R;
import goofy2.swably.data.App;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UploadApp extends WithHeaderActivity {
	protected AppHeader header = new AppHeader(this);
	private View btnUpload;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_app);
		
        btnUpload = this.findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new OnClickListener_btnUpload());

        TextView txtTerms = (TextView) findViewById(R.id.txtTerms);
        txtTerms.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
    			startActivity(new Intent(UploadApp.this, Terms.class));
			}
		});

		Intent i = getIntent();
        String strApp = i.getStringExtra(Const.KEY_APP);
		String packageName = i.getDataString();
        try {
        	if(strApp == null)
            	header.setApp(new AppHelper(this).getApp(packageName));
        	else
        		header.setApp(new App(new JSONObject(strApp)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();
    }
    
    private void bind(){
		TextView tv;
//		tv = (TextView)findViewById(R.id.txtTitle);
//		if(tv != null){
//			tv.setText(header.getApp().getName());
//			tv.setTypeface(mHeaderFont);
//		}
    	header.bindAppHeader(viewBack);
    	findViewById(R.id.txtReviewsCount).setVisibility(View.GONE);
    	findViewById(R.id.txtSize).setVisibility(View.VISIBLE);
    	
    	tv = (TextView) findViewById(R.id.txtPrompt1);
    	tv.setText(String.format(getString(R.string.not_in_cloud), header.mApp.getName()));
    	tv.setTypeface(mLightFont);

    }

    protected class OnClickListener_btnUpload implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			Intent i2 = new Intent(UploadApp.this, UploadingApp.class);
			i2.setData(Uri.parse(header.getApp().getPackage()));
			i2.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
			startActivityForResult(i2,0);

			Intent i1 = new Intent(UploadApp.this, UploaderEx.class);
			//i.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
			i1.setData(Uri.parse(header.getApp().getPackage()));
			startService(i1);

		}
		
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setResult(RESULT_OK, data);
		finish();
    }
}
