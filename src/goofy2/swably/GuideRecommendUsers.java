package goofy2.swably;

import org.json.JSONException;

import goofy2.swably.R;
import goofy2.swably.CloudUsersActivity.OnClickListener_btnFollow;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GuideRecommendUsers extends RecommendUsers {
    @Override
    protected void setContent(){
	    setContentView(R.layout.guide_recommend_users);
	    disableSliding();
    }
    
	@Override
    protected void bind(){
		super.bind();
		final String id = Utils.getCurrentUser(GuideRecommendUsers.this).optString("signup_sns");
    	final String name = (String) Utils.getSnsResource(id, "name");
	    btnFollow = this.findViewById(R.id.btnFollow);
	    btnFollow.setOnClickListener(new OnClickListener_btnFollow());
	    btnFollow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				new OnClickListener_btnFollow().onClick(btnFollow);
				Intent i = new Intent(GuideRecommendUsers.this, GuideLocalApps.class);
				i.setData(Uri.parse(id));
				i.putExtra("name", name);
				startActivity(i);
			}
        });

	    View btnNext = this.findViewById(R.id.btnNext);
	    btnNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
//				finish();
				Intent i = new Intent(GuideRecommendUsers.this, GuideLocalApps.class);
				i.setData(Uri.parse(id));
				i.putExtra("name", name);
				startActivity(i);
			}
        });
	}
	
	@Override
	protected void onClickItem(final int position) throws JSONException {
		// disable open user
	}

    @Override
    public String getCacheId(){
    	return super.getCacheId();
    }

    @Override
    public void showLoading(){
    	showDialog(0);
    }
    @Override
    public void hideLoading(){
    	super.hideLoading();
    	removeDialog(0);
    }
}
