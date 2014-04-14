package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class SearchApps2Add extends SearchApps {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View btnDone = findViewById(R.id.btnDone);
    	btnDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    }

    @Override
	protected void setContent(){
	    setContentView(R.layout.search_apps_2_add);
    }

	@Override
	protected void onClickItem(int position) throws JSONException {
    	JSONObject json = mListData.getJSONObject(position);
		Intent ret = new Intent();
		ret.putExtra(Const.KEY_APP, json.toString());
    	setResult(Activity.RESULT_OK, ret);
    	finish();
	}
}
