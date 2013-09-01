package goofy2.swably;



import java.io.File;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.R;
import goofy2.utils.DownloadImage;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

public class GuideTerms extends CloudActivity {

//	private Button btnNo;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_terms);
        disableSliding();

        Button btnOk = (Button) this.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				startActivity(new Intent(GuideTerms.this, Start.class));
		        Utils.setPrefString(GuideTerms.this, "terms_accepted", "true");
				startActivity(new Intent(GuideTerms.this, Start.class));
				finish();
			}
        });
        
        TextView tv = (TextView) this.findViewById(R.id.txtDesc);
        tv.setTypeface(mNormalFont);
        
//        btnNo = (Button) this.findViewById(R.id.btnNo);
//        ((Button)btnNo).setTypeface(mLightFont);
//        btnNo.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//				finish();
//			}
//        });
        
    }
    
}