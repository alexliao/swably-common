package goofy2.swably;

import android.app.Application;
import android.content.Context;

public class SwablyApplication extends Application {

	@Override
	public void onCreate (){
		Utils.logV(this, "onCreate");
		setConst();
	}

	protected void setConst(){
		Const.APP_NAME = getString(R.string.app_name);
		Const.HTTP_PREFIX = getMainHttpPrefix(this);
		Const.UPLOAD_HTTP_PREFIX = getUploadHttpPrefix(this);
	}

	public String getMainHttpPrefix(Context context){
		return "http://"+getMainHost(context);
	}

	protected String getMainHost(final Context context){
		String result = Utils.getPrefString(context, "main_host", Const.DEFAULT_MAIN_HOST);
//		result = "172.24.1.100:3000";
//		result = "zh.swably.com:3000";
//		result = "117.121.10.138";
		return result;
	}
	
	public String getUploadHttpPrefix(Context context){
		return "http://"+getUploadHost(context);
	}

	protected String getUploadHost(final Context context){
		String result = Utils.getPrefString(context, "upload_host", Const.DEFAULT_UPLOAD_HOST);
//		result = "172.24.1.100:3000";
//		result = "117.121.10.138";
		return result;
	}

}
