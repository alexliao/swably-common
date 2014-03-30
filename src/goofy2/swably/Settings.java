package goofy2.swably;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import goofy2.swably.R;


public class Settings extends WithHeaderActivity {
	final static String KEY_NOTIFICATION = "notification";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.settings);
//		this.showBehind();
    }
    
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
	        TextView txtVersion = (TextView) this.findViewById(R.id.txtVersion);
	        txtVersion.setText(pi.versionName);
	        View btnVersion = this.findViewById(R.id.btnVersion);
	        btnVersion.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
	    			startActivity(new Intent(Settings.this, About.class));
				}
			});
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		View btnSignout = this.findViewById(R.id.btnSignout);
		if(Utils.getCurrentUser(this) == null){
			TextView txtSignout = (TextView) this.findViewById(R.id.txtSignout);
			txtSignout.setText(getString(R.string.signin));
			btnSignout.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					startActivity(new Intent(Settings.this, Const.START_ACTIVITY));
				}
			});
		}else{
			btnSignout.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					Utils.confirm(Settings.this, getString(R.string.sign_out),  new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which)
						{
							Utils.signout(Settings.this);
							finish();
						}       
					});
				}
			});
		}
		View btnTerms = this.findViewById(R.id.btnTerms);
		btnTerms.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
    			startActivity(new Intent(Settings.this, Terms.class));
			}
		});
		
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		final ToggleButton tglNotification = (ToggleButton) this.findViewById(R.id.tglNotification);
		tglNotification.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				boolean newValue = tglNotification.isChecked();
				sp.edit().putBoolean(KEY_NOTIFICATION, newValue).commit();
				if((Boolean)newValue == true){
					startService(new Intent(Settings.this, Checker.class));
				}
			}
		});
		tglNotification.setChecked(Settings.getNotification(this));
		
    }

    @Override
    public void onResume(){
    	super.onResume();
    	postShowAbove();
    }

	static public boolean getNotification(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_NOTIFICATION, true);
	}

}
