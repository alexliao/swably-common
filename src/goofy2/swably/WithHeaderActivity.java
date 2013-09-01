package goofy2.swably;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.R;
import goofy2.utils.Rotate3dAnimation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WithHeaderActivity extends CloudActivity {
	protected View viewHeaderBar;
	protected View viewBack;
	protected TextView txtHeader;
	protected ImageView imgLogo;
	protected View loading;
	private ArrayList<CharSequence> mUsernames = new ArrayList<CharSequence>();

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        TextView tv = (TextView) findViewById(R.id.txtTitle);
        if(tv != null){
			tv.setTypeface(mHeaderFont);
//        	tv.setOnClickListener(new OnClickListener(){
//				@Override
//				public void onClick(View arg0) {
//					goHome();
//				}
//		    });
        }
        
//      final View btnHome = this.findViewById(R.id.btnHome);
//      if(btnHome != null){
//    	  btnHome.setOnClickListener(new OnClickListener(){
//				@Override
//				public void onClick(View arg0) {
//					flipView(btnHome);
//			        new Timer().schedule(new TimerTask(){
//			    		@Override
//			    		public void run(){
//							goHome();
//			    		}
//			    	}, 500); // delay for animation
//				}
//		    });
//      }

        //        View btnBack = this.findViewById(R.id.btnBack);
//        if(btnBack != null){
//        	btnBack.setOnClickListener(new OnClickListener(){
//				@Override
//				public void onClick(View arg0) {
//					finish();
//				}
//		    });
//        }
        
//	    // workaround for Android bug: tileMode = "repeat" does not work before Android ICS
        viewHeaderBar = this.findViewById(R.id.viewHeaderBar);
//	    if(viewHeaderBar != null){
//	        BitmapDrawable bd = (BitmapDrawable)viewHeaderBar.getBackground(); 
//		    if(bd != null){
//		    	bd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
//		    }
//	    }
        viewBack = this.findViewById(R.id.viewBack);
//	    if(viewBack != null){
//	        BitmapDrawable bd = (BitmapDrawable) (((LayerDrawable)viewBack.getBackground()).getDrawable(0)); 
//		    if(bd != null){
//		    	bd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
//		    }
//	    }
//        View viewList = this.findViewById(R.id.list);
//	    if(viewList != null){
//	        BitmapDrawable bd = (BitmapDrawable) (((LayerDrawable)viewList.getBackground()).getDrawable(0));
//		    if(bd != null){
//		    	bd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
//		    }
//	    }

		View btnOptionsMenu = findViewById(R.id.btnOptionsMenu);
		if(btnOptionsMenu != null){
			btnOptionsMenu.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					openOptionsMenu(v);
				}
			});
		}
    
    }
    
    @Override
    public void onStart(){
    	super.onStart();
//    	prepareUserBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if(getMenu() != 0){
            getMenuInflater().inflate(getMenu(), menu);
    	}
        return true;
    }
    
    protected int getMenu(){
    	return 0;
    }

    public void openOptionsMenu(View v){
    	if(getMenu() == 0) return;
    	if(Build.VERSION.SDK_INT >= 11){
		    PopupMenu popup = new PopupMenu(getApplicationContext(), v);
//		    MenuInflater inflater = popup.getMenuInflater();
//		    inflater.inflate(getMenu(), popup.getMenu());
		    onCreateOptionsMenu(popup.getMenu());
		    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
				@Override
				public boolean onMenuItemClick(MenuItem item) {
	//				Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_SHORT).show();
					onOptionsItemSelected(item);
					return false;
				}
		    });
		    popup.show();
    	}else{
    		openOptionsMenu();
    	}
    }
	
}
