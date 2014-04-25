package goofy2.swably.fragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.CommentsAdapter;
import goofy2.swably.Const;
import goofy2.swably.FollowBtn;
import goofy2.swably.R;
import goofy2.swably.UserHeader;
import goofy2.swably.UserProfile;
import goofy2.swably.UserUploadedApps;
import goofy2.utils.AsyncImageLoader;
import goofy2.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class UserStarredPostsFragment extends CloudCommentsFragment {
	protected UserHeader header;

    @Override
	public void onCreate(Bundle savedInstanceState) {
    	header = new UserHeader(ca());
        header.setUserFromBundle(getArguments());
		super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        bind(v);
        return v;
    }
    
    protected void bind(View v){
    	if(v == null) return; 
		header.bindUserHeader(v, false);
	}
	
	@Override
	protected CloudBaseAdapter getAdapter() {
		return new CommentsAdapter(ca(), mListData, mLoadingImages);
	}

	@Override
	protected String getUrl() {
		return Const.HTTP_PREFIX + "/users/starred_posts/" + header.getUserId() + "?format=json&"+ca().getLoginParameters()+"&"+ca().getClientParameters();
	}

	@Override
	protected void onClickHeader() {
	}

	@Override
	protected String getIdName(){
		return "dig_id";
	}

    @Override
    public String getCacheId(){
    	return this.getClass().getName()+header.getUserId();
    }

//    @Override
//	protected void onDataChanged(int item){
//    	super.onDataChanged(item);
//		bind(getView());
//	}

}
