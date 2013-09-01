package goofy2.swably.fragment;


import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import goofy2.swably.AppsAdapter;
import goofy2.swably.CloudBaseAdapter;
import goofy2.swably.R;
import goofy2.swably.data.App;
import goofy2.utils.JSONUtils;

public abstract class CloudAppsFragment extends CloudListFragment {
	
//	@Override
//	protected String getImageUrl(JSONObject item, int index) throws JSONException {
//		return item.getString(App.ICON);
//	}

	@Override
	protected CloudBaseAdapter getAdapter() {
		return new AppsAdapter(ca(), mListData, mLoadingImages, true);
	}

	protected void onClickItem(int position) throws JSONException {
    	JSONObject json = mListData.getJSONObject(position);
    	ca().openApp(json);
	}
}
