package goofy2.swably;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import goofy2.swably.fragment.AppTagsFragment;

public class AppTags extends WithHeaderActivity
{
	protected AppHeader header = new AppHeader(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        super.onCreate(savedInstanceState);
    	enableSlidingMenu();
        setContentView(R.layout.app_tags);
        header.setAppFromIntent();
        header.setAppFromCache(header.getAppId());
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
		bind();
		addHistoryFragment();		
    }

    protected void addHistoryFragment(){
		Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_APP, header.getApp().getJSON().toString());

		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();	
		AppTagsFragment fragment = new AppTagsFragment();
		fragment.setArguments(bundle);
		ft.add(R.id.fragment, fragment);
		ft.commit();

    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    }

    public void bind() {
//		TextView tv = (TextView)findViewById(R.id.txtTitle);
//		tv.setText(header.getApp().getName());
		final View btnAdd = findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AddTag.class);
				intent.putExtra(Const.KEY_APP, header.getApp().getJSON().toString());
				startActivity(intent);
			}
		});
		Utils.setTouchAnim(this, btnAdd);
	}

}
