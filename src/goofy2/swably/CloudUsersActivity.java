package goofy2.swably;

import goofy2.swably.R;

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

public abstract class CloudUsersActivity extends CloudListActivity {

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
		return new UsersAdapter(this, mListData, mLoadingImages);
	}

//	protected void onClickItem(int position) throws JSONException {
//    	JSONObject user = mListData.getJSONObject(position-1);
//		openUser(user);
//	}
	 
	protected void onClickItem(final int position) throws JSONException {

    	JSONObject user = mListData.getJSONObject(position);
		openUser(user);
		
//    	final JSONObject user = mListData.getJSONObject(position-1);
//    	final String userId = user.getString("id");
//		final String fullName = user.getString("name");
//		//choose menu
//		int menu = user.optBoolean("is_followed") ? R.array.user_menu_unfollow : R.array.user_menu_follow;
//		if(user.getString("id").equals(Utils.getCurrentUserId(this))) menu = R.array.user_menu_me;
//    	final String[] menuItems = getResources().getStringArray(menu);
//    		
//		Builder ad = new AlertDialog.Builder(CloudUsersActivity.this);
//		ad.setTitle(fullName);
//		ad.setItems(menu, new DialogInterface.OnClickListener(){
//			public void onClick(DialogInterface dialog, int whichButton){
//				String name = menuItems[whichButton];
//				if(name.equals(getString(R.string.view_in_web))){
//					openUser(user);
//				}else if(name.equals(getString(R.string.follow))){
//					follow(userId, fullName, true, null, true);
//				}else if(name.equals(getString(R.string.unfollow))){
//					follow(userId, fullName, false, null, true);
//				}
//			}
//		});
//	    //ad.setNegativeButton(getString(R.string.cancel), null);
//		ad.show();
	
	}

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        mMenu = menu;
//        // Inflate the currently selected menu XML resource.
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.refresh, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	if(super.onOptionsItemSelected(item)) return true;
//        switch (item.getItemId()) {
//            case R.id.refresh:
//            	refresh();
//                return true;
//            default:
//                // Don't toast text when a submenu is clicked
//                if (!item.hasSubMenu()) {
//                    Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                break;
//        }
//        
//        return false;
//    }

	protected class OnClickListener_btnFollow implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
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
				showDialog(0);
				final Handler handler = new Handler();
				final String userIds = ids;
				new Thread() {
					public void run(){
						try {
							batch_follow(userIds);
							handler.post(new Runnable() {
								public void run(){
									removeDialog(0);
									//startActivity(new Intent(SnsFriends.this, LocalApps.class));
									mAdapter.setData(mListData);
									mAdapter.notifyDataSetChanged();
								}
							});
						} catch (final Exception e) {
							handler.post(new Runnable() {
								public void run(){
									removeDialog(0);
			            			Utils.alertTitle(CloudUsersActivity.this, getString(R.string.err_upload_failed), e.getMessage());
								}
							});
						}
					}
				}.start();
			}
		}
	}

}
