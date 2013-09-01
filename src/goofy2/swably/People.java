package goofy2.swably;

import goofy2.swably.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;

public class People extends TabStripActivity
	implements SnsFriendsFragment.OnInviteListener
{
	protected View btnSearch;
//	protected View btnFollowAll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(redirectAnonymous()) return;
    	enableSlidingMenu();
        setContentView(R.layout.people);
		this.showBehind();
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
		
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSearchRequested();
			}
		});

//        btnFollowAll = findViewById(R.id.btnFollowAll);
//	    btnFollowAll.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//based on article: http://stackoverflow.com/questions/7379165/update-data-in-listfragment-as-part-of-viewpager
//				SnsFriendsFragment f = (SnsFriendsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":0");
//				if(f != null){
//					f.followAll();
//				}
//			}
//		});

        // !! when change the tab order, don't forget change i.setData(Uri.parse("0")) in Checker.java
        mPagerAdapter.addTab("friends", getString(R.string.friends), SnsFriendsFragment.class, null);

        String sns_id = Utils.getCurrentUser(this).optString("signup_sns");
        String name = (String) Utils.getSnsResource(sns_id, "name");
        if(!sns_id.equals("plus"))
    		mPagerAdapter.addTab("sns", name, InviteSnsFriendsFragment.class, null);

		mPagerAdapter.addTab("contacts", getString(R.string.contacts), InviteContactsFragment.class, null);

		mViewPager.setCurrentItem(0);
    }

    protected PagerTabAdapter getAdapter(FragmentActivity activity, ViewPager pager){
		return new PeoplePagerTabAdapter(activity, pager);
	}

    @Override
    public void onResume(){
    	super.onResume();
    	postShowAbove();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    	LocalAppsFragment frag = (LocalAppsFragment) getSupportFragmentManager().findFragmentByTag("apps");
    	LocalAppsFragment frag = (LocalAppsFragment) mPagerAdapter.getItem(0);
    	if(frag != null) frag.onActivityResult(this, requestCode, resultCode, data);
    }
    
	@Override
	public void onInvite() {
		mViewPager.setCurrentItem(1);
	}

    protected class PeoplePagerTabAdapter extends PagerTabAdapter{

		public PeoplePagerTabAdapter(FragmentActivity activity, ViewPager pager) {
			super(activity, pager);
		}
    	
	    @Override
	    public void onPageSelected(int position) {
//	    	Utils.showToast(App.this, ""+position);
//	    	btnFollowAll.setVisibility(View.GONE);
//	    	if(position == 0){
//	    		btnFollowAll.setVisibility(View.VISIBLE);
//	    	}	    		
	    }
		
    }
}
