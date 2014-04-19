package goofy2.swably;

import goofy2.swably.R;
import goofy2.swably.TabStripActivity;
import goofy2.swably.fragment.HelpRequestsFragment;
import goofy2.swably.fragment.PublicReviewsFragment;
import goofy2.swably.fragment.SharingPostsFragment;

import android.os.Bundle;

public class PublicTabs extends PeopleReviews
{
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        
		mPagerAdapter.addTab("all", getString(R.string.all), PublicReviewsFragment.class, null);
		mPagerAdapter.addTab("shares", getString(R.string.menu_shares), SharingPostsFragment.class, null);
		mPagerAdapter.addTab("requests", getString(R.string.menu_requests), HelpRequestsFragment.class, null);

		mViewPager.setCurrentItem(1);
    }


//	@Override
//    public void onDestroy(){
//    	super.onDestroy();
//    }

	void setContent(){
        setContentView(R.layout.public_tabs);
    }

}
