package goofy2.swably.fragment;

public class HelpRequestsFragment extends PeopleReviewsFragment{
    
    @Override
	protected String getPageTitle() {
		return null;
	}

	@Override
	protected String getAPI() {
		return "/comments/requests";
	}

    @Override
	public long getCacheExpiresIn(){
		return 60*1000;
	}

}
