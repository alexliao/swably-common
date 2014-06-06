package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.R;
import goofy2.swably.data.App;
import goofy2.utils.AsyncImageLoader;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommendSwably extends WithHeaderActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(redirectAnonymous()) return;
        setContentView(R.layout.recommend_swably);
		
        View btnShare = this.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text =  getString(R.string.recommend_text) + " " + genRecommendUrl(Utils.getCurrentUserId(getApplicationContext())) ;
				Intent i = new Intent(getApplicationContext(), ShareActivity.class);
				i.putExtra(Const.KEY_TEXT, text);
				i.putExtra(Const.KEY_SUBJECT,  getString(R.string.app_name));
				i.putExtra(Const.KEY_URL, genRecommendUrl(Utils.getCurrentUserId(getApplicationContext())));
				startActivity(i);
			}
		});

    }

	static public String genRecommendUrl(String userId){
    	return "http://" + Const.DEFAULT_MAIN_HOST+"/users/recommend_swably/" + userId + "?r=recommend";
	}
    
    
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();
    }
    
    private void bind(){
    	JSONObject user = Utils.getCurrentUser(getApplicationContext());
		ImageView iv;
		TextView tv;
		Bitmap bm = null;
		String url = null;
		View v = findViewById(R.id.viewReview);
    	
		tv = (TextView)v.findViewById(R.id.txtUserName);
		tv.setText(user.optString("name"));
		tv.setTypeface(mBoldFont);

		if(!user.isNull("avatar_mask")){
			String mask = user.optString("avatar_mask", "");
			url = mask.replace("[size]", "bi");
		}
		iv = (ImageView)v.findViewById(R.id.avatar);
		iv.setImageResource(R.drawable.noname);
		new AsyncImageLoader(this, iv, 0).loadUrl(url);
		
		tv = (TextView) v.findViewById(R.id.txtContent);
		tv.setTypeface(mLightFont);
		tv = (TextView) v.findViewById(R.id.txtAppName);
		tv.setTypeface(mBoldFont);
		
    }

}
