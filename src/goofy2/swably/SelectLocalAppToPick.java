package goofy2.swably;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class SelectLocalAppToPick extends SelectLocalAppToReview {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
//        TextView txtTitle = (TextView) this.findViewById(R.id.txtTitle);
//        txtTitle.setText(getString(R.string.pick_app));
    	findViewById(R.id.viewBottomBar).setVisibility(View.GONE);
    }

    @Override
	protected void onCloudAction(JSONObject json){
		Intent ret = new Intent();
		ret.putExtra(Const.KEY_APP, json.toString());
		setResult(RESULT_OK, ret);
		finish();
    }

}
