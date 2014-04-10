package goofy2.swably.fragment;

import goofy2.swably.AppHelper;
import goofy2.swably.Const;
import android.util.Log;

public class SystemAppsFragment extends LocalAppsFragment {

    @Override
	protected String loadStream(String url, String lastId) {
		Log.d(Const.APP_NAME, Const.APP_NAME + " SystemApps loadStream lastId: " + lastId);
		String err = null;
		try{
			AppHelper helper = new AppHelper(a());
			cursor = helper.getApps(db, true);
		}catch (Exception e){
			err = e.getMessage();
			Log.e(Const.APP_NAME, Const.APP_NAME + " SystemApps loadStream err: " + err);
		}
		return err;
	}
}
