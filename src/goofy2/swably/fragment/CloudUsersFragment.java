package goofy2.swably.fragment;

import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.R;
import goofy2.swably.UsersAdapter;
import goofy2.swably.Utils;
import goofy2.swably.R.string;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public abstract class CloudUsersFragment extends CloudListFragment {

	@Override
	protected void loadedMore(boolean succeeded){
//		setFooterDivider();
		super.loadedMore(succeeded);
	}	
//	@Override
//	protected String getImageUrl(JSONObject item, int index) throws JSONException {
//		String mask = item.optString("avatar_mask", "");
//		String url;
//		url = mask.replace("[size]", "bi");
//		return url;
//	}


	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		return new JSONArray(result);		
	}


	@Override
	protected CloudBaseAdapter getAdapter() {
		return new UsersAdapter(ca(), mListData, mLoadingImages);
	}

	protected void onClickItem(final int position) throws JSONException {

    	JSONObject user = mListData.getJSONObject(position);
		ca().openUser(user);
		
	}

	public void followAll() {
		int count = 0;
		String ids = "";
		for(int i=0; i<mListData.length(); i++){
			JSONObject user = mListData.optJSONObject(i);
			count ++;
			ids += user.optString("id") + ",";
			try {
				user.put("is_followed", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(count == 0){
			//Utils.showToast(SnsFriends.this, getString(R.string.not_select_user));
		}else{
			a().showDialog(0);
			final Handler handler = new Handler();
			final String userIds = ids;
			new Thread() {
				public void run(){
					try {
						ca().batch_follow(userIds);
						handler.post(new Runnable() {
							public void run(){
								a().removeDialog(0);
								//startActivity(new Intent(SnsFriends.this, LocalApps.class));
								mAdapter.setData(mListData);
								mAdapter.notifyDataSetChanged();
							}
						});
					} catch (final Exception e) {
						handler.post(new Runnable() {
							public void run(){
								a().removeDialog(0);
		            			Utils.alertTitle(a(), getString(R.string.err_upload_failed), e.getMessage());
							}
						});
					}
				}
			}.start();
		}
	}
	
	public class OnClickListener_btnFollow implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			followAll();
		}
	}

}
