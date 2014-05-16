package goofy2.swably.fragment;

public class SharingPostsFragment extends PeopleReviewsFragment{
    
    @Override
	protected String getPageTitle() {
		return null;
	}

	@Override
	protected String getAPI() {
		return "/comments/posts";
	}

    @Override
	public long getCacheExpiresIn(){
		return 60*1000;
	}

}
