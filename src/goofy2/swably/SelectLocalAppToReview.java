package goofy2.swably;

import goofy2.swably.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

public class SelectLocalAppToReview extends LocalApps {
	String mImagePath = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
    	super.onCreate(savedInstanceState);
        disableSliding();
//    	this.getWindow().setBackgroundDrawableResource(R.drawable.panel_background);
//        TextView txtTitle = (TextView) this.findViewById(R.id.txtTitle);
//        txtTitle.setText(getString(R.string.pick_app));
//    	Drawable d = this.getResources().getDrawable(R.drawable.add);
//    	d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
//        txtTitle.setCompoundDrawables(d, null, null, null);
        View btnRequest = findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(SelectLocalAppToReview.this, PostReview.class);
				i.putExtra("image", mImagePath);
				startActivity(i);
				finish();
			}
        });
        
        // get picture from ACTION_SEND intent
        Uri imageUri = null;
        Intent it = getIntent();
    	Bundle bd = it.getExtras();
    	if(bd != null){
    		Object o = bd.get(Intent.EXTRA_STREAM);
    		imageUri = (Uri)o;
    	}
        if(imageUri != null){
        	mImagePath = getRealPathFromURI(imageUri);
        }
   	
        
        
    }

    protected void setContent(){
	    setContentView(R.layout.select_local_apps);
    }

    @Override
	protected void onCloudAction(JSONObject json){
		Intent i = new Intent(SelectLocalAppToReview.this, PostReview.class);
		i.putExtra(Const.KEY_APP, json.toString());
		i.putExtra("image", mImagePath);
		startActivity(i);
		finish();
    }

    @Override
	protected View getRowTop() {
		return null;
	}
    @Override
	protected View getRowBottom() {
		return null;
	}
    
//	@Override
//	protected CloudBaseAdapter getAdapter() {
//		return new SelectLocalAppAdapter(this, mListData, mLoadingImages);
//	}


    private String getRealPathFromURI(Uri uri){
    	if("content".equalsIgnoreCase(uri.getScheme())){
    		return getRealPathFromMediaURI(uri);
    	}else{
    		return uri.getPath();
    	}
    }
    
    public String getRealPathFromMediaURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
