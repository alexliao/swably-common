package goofy2.swably;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.R;
import goofy2.swably.CloudActivity.OnClickListener_btnSnap;
import goofy2.swably.data.App;
import goofy2.utils.JSONUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//public class Update extends WithHeaderActivity {
public class About extends Activity {
	protected ListView listChanges;
	private Button btnUpgrade;
	private Button btnDone;
	private TextView txtVersionName;
	private View checking;
	private TextView txtUp2Date;
	private TextView txtNotUp2Date;
	private int mCurrentVersion = 0;
	String mNewVersionName;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
//        disableSliding();
        
        btnUpgrade = (Button) this.findViewById(R.id.btnUpgrade);
        btnUpgrade.setOnClickListener(new OnClickListener_btnUpgrade());
        btnDone = (Button) this.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new OnClickListener_btnDone());
        txtVersionName = (TextView) this.findViewById(R.id.txtVersionName);
        txtUp2Date = (TextView) this.findViewById(R.id.txtUp2Date);
        txtNotUp2Date = (TextView) this.findViewById(R.id.txtNotUp2Date);
        checking = this.findViewById(R.id.checking);
        listChanges=(ListView)findViewById(R.id.listChanges);
        
    	PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
	        txtVersionName.setText(Const.APP_NAME.toUpperCase()+" "+String.format(getString(R.string.version_name), pi.versionName));
	        mCurrentVersion = pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        //bind();
        final Handler handler = new Handler();
		new Thread() {
			public void run(){
				final boolean ret = Utils.checkVersion(About.this);
				handler.post(new Runnable(){
					public void run(){
						checking.setVisibility(View.GONE);
						if(ret){
							bind();
						}else{
							txtUp2Date.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		}.start();
    }
    
    private void bind(){
    	boolean up2date = true;
    	ArrayList<HashMap<String, String>> changes = new ArrayList<HashMap<String, String>>();
        String strChanges = Utils.getPrefString(this, "version_changes", null);
        if(strChanges != null){
        	JSONArray jsonChanges;
			try {
				jsonChanges = new JSONArray(strChanges);
				if(jsonChanges.length() > 0){
					int newVersion = jsonChanges.getJSONObject(0).getInt("code");
					if(newVersion > mCurrentVersion){
						up2date = false;
						String versionName = ""+(newVersion/1000.0);
						mNewVersionName = versionName;
						txtNotUp2Date.setText(String.format(txtNotUp2Date.getText().toString(), versionName));
			        	for(int i=0;i<jsonChanges.length();i++){
			        		JSONObject jsonChange = jsonChanges.getJSONObject(i);
			                HashMap<String, String> hashChange = new HashMap<String, String>();
			                hashChange.put("change", jsonChange.getString("note").trim());
			        		changes.add(hashChange);
			        	}
						SimpleAdapter sa = new SimpleAdapter(this, changes, R.layout.change_row, new String[] { "change"}, new int[] { R.id.txtChange});
						listChanges.setAdapter(sa);	
					}
				}
				if(up2date){
					btnUpgrade.setVisibility(View.GONE);
					btnDone.setVisibility(View.VISIBLE);
					txtNotUp2Date.setVisibility(View.GONE);
					txtUp2Date.setVisibility(View.VISIBLE);
				}else{
					btnUpgrade.setVisibility(View.VISIBLE);
					btnDone.setVisibility(View.GONE);
					txtNotUp2Date.setVisibility(View.VISIBLE);
					txtUp2Date.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
    }
    
    private class OnClickListener_btnUpgrade implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
//			Intent i = new Intent(About.this, DownloadingApp.class);
//			i.putExtra(Const.KEY_APP, getFakeApp().getJSON().toString());
//			startActivity(i);
////			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Const.HTTP_PREFIX + "/downloads/nappstr.apk")));

// disable OTA for compliance with Google Play policy			
//			Utils.startDownloading(About.this, getFakeApp());			
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+getPackageName())));
			
			finish();
		}
    }

    private App getFakeApp(){
    	App ret = null;
    	JSONObject json = new JSONObject();
    	PackageInfo pi;
    	PackageManager pm;
		try {
			pm = getPackageManager();
			ret = new App(pm, getPackageName());
			ret.getJSON().put("id", 1);
			ret.getJSON().put("apk", Const.UPGRADE_URL + "?" + Utils.getClientParameters(this, Const.LANG));
			ret.getJSON().put("version_name", mNewVersionName);
			Log.d(Const.APP_NAME, Const.APP_NAME + " upgrade_url: "+Const.UPGRADE_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
    }
    
    private class OnClickListener_btnDone implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
    }
}
