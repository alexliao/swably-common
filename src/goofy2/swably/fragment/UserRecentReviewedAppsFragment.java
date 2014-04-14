package goofy2.swably.fragment;

import goofy2.swably.AddWatcher;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.swably.SearchApps;
import goofy2.swably.SearchApps2Add;
import goofy2.swably.SearchWatcher;
import goofy2.swably.fragment.LocalAppsFragment.OnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.TextView;


public class UserRecentReviewedAppsFragment extends UserAppsFragment {
	OnClickListener mClickListener;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =super.onCreateView(inflater, container, savedInstanceState); 
        
		final EditText editQuery = (EditText) v.findViewById(R.id.editQuery);
		editQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					onSearch(editQuery);
					return true;
				}
				return false;
			}
		});
        
        return v;
    }

	protected void setContent() {
		setContentView(R.layout.user_recent_reviewed_apps_fragment);
	}

	@Override
	protected String getAPI() {
		return "/users/recent_reviewed_apps/";
	}
	
    @Override
	protected String getUrl() {
		return super.getUrl() + "&count=50";
	}
	
    @Override
	protected void loadMore(){
		// disable auto loading
	}	

//    @Override
//	protected void loadedMore(boolean successed) {
//    	super.loadedMore(successed);
//    	// hide footer
////    	listFooter.setVisibility(View.GONE);
//    	listFooter.setLayoutParams(new AbsListView.LayoutParams(0,1));
//	}

//    @Override
//    public String getCacheId(){
//    	return null;
//    }

    @Override
	public long getCacheExpiresIn(){
		return 30*1000;
	}

	@Override
	protected void onClickItem(int position) throws JSONException {
    	JSONObject json = mListData.getJSONObject(position);
    	this.mClickListener.onClick(json);
	}
    

//	@Override
//	protected View getRowTop() {
//		LayoutInflater inflater = LayoutInflater.from(a());
//		View v = inflater.inflate(R.layout.add_app_searchbar, null);
////		TextView tv = (TextView) v.findViewById(R.id.txtTitle);
////		tv.setText(getListTitle());
////		v.setFocusable(true);
//		return v;
//	}
	
//	@Override
//	protected View getRowBottom() {
//		LayoutInflater inflater = LayoutInflater.from(a());
//		final View v = inflater.inflate(R.layout.add_app_searchbar, null);
//		final EditText editQuery = (EditText) v.findViewById(R.id.editQuery);
//		editQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				if(actionId == EditorInfo.IME_ACTION_GO){
//					onSearch(editQuery);
//					return true;
//				}
//				return false;
//			}
//		});
//		
//		View btnSearch = v.findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				onSearch(editQuery);
//			}
//		});
//		return v;
//	}

	void onSearch(EditText editQuery){
		String query = editQuery.getText().toString().trim();
		if(query.length() > 0){
			Intent i = new Intent(a(), SearchApps2Add.class);
			i.putExtra(SearchManager.QUERY, query);
			startActivityForResult(i, 0);
		}else{
			editQuery.requestFocus();
		}
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mClickListener = (OnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnClickListener");
        }
    }
    
}
