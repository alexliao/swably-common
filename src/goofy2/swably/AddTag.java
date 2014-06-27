package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import goofy2.swably.fragment.AppTagsFragment;
import goofy2.swably.fragment.MyAddedTagsFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;

public class AddTag extends WithHeaderActivity {
	protected AppHeader header = new AppHeader(this);
	static final int REQUEST_CODE = 5542;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	JSONObject user = Utils.getCurrentUser(this);
        super.onCreate(savedInstanceState);
    	disableSliding();
    	setContentView(R.layout.add_tag);
        header.setAppFromIntent();
        header.setAppFromCache(header.getAppId());
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();

        Bundle bundle = new Bundle();
		bundle.putString(Const.KEY_APP, header.getApp().getJSON().toString());
		
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();	
		MyAddedTagsFragment fragment = new MyAddedTagsFragment();
		fragment.setArguments(bundle);
		ft.add(R.id.fragment, fragment);
		ft.commit();
    }

    public void bind() {
//		TextView tv = (TextView)findViewById(R.id.txtTitle);
//		tv.setText(header.getUser().optString("name"));
        
//		final EditText editQuery = (EditText) findViewById(R.id.editQuery);
//		editQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				if(actionId == EditorInfo.IME_ACTION_SEARCH){
//					final String query = editQuery.getText().toString().trim();
//					if(query.length() > 0){
////						Intent i = new Intent(AddTag.this, SearchWatcher.class);
////						i.putExtra(SearchManager.QUERY, query);
////						startActivity(i);
//					}
//					return true;
//				}
//				return false;
//			}
//		});
		
		View btnSend = findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText editTag = (EditText) findViewById(R.id.editTag);
				String tag = editTag.getText().toString().trim();
				if(isValid(tag)){
					Api.appTag(AddTag.this, header.getAppId(), tag, true, null);
					Utils.clearCache(AddTag.this, AppTagsFragment.cacheId(header.getAppId()));
					Utils.clearCache(AddTag.this, MyAddedTagsFragment.cacheId(header.getAppId()));
					sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
					
					// Add fake tag row
					JSONObject json = new JSONObject();
					try {
						json.put("name", tag);
						json.put("is_mine", true);
						Intent intent = new Intent(Const.BROADCAST_TAG_ADDED);
						intent.putExtra(Const.KEY_TAG, json.toString());
						sendBroadcast(intent);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					editTag.setText("");
				}else{
					Utils.showToast(AddTag.this, getString(R.string.err_invalid_tag));
					editTag.requestFocus();
				}
			}
		});

        View btnDone = findViewById(R.id.btnDone);
    	btnDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    }

    boolean isValid(String tag){
    	if(0 == tag.length()) return false;
    	String invalidChars = "`~!@#$%^&*()-=_+{}|\\";
    	return !tag.matches(".*[\\p{P}\\p{S}\\p{Z}].*");
    }
}
