package goofy2.swably;

import android.content.Intent;

public class Const {
	
	public static String APP_NAME;
//	public static String HTTP_PREFIX = "http://192.168.2.107:2001";
	public static String DNS_URL = "http://swably.com/account/dns?format=json";
//	public static String UPLOAD_HTTP_PREFIX = HTTP_PREFIX;
	public static String DEFAULT_MAIN_HOST = "swably.com";
	public static String DEFAULT_UPLOAD_HOST = "upload.swably.com";
	public static boolean LOAD_FONTS = true;
	public static String LANG = "en";
//	public static String UPGRADE_URL = "/downloads/swably_en.apk";
	public static String UPGRADE_URL = "/account/upgrade";
	
	public static final int IMAGE_MAX_SIZE = 1000;
	//public static final String API_PREFIX = HTTP_PREFIX + "/";
	public static final String PREFS = "Swably";
	public static final long LOCATE_IN_MILLS = 5*60*1000; // seek the location when uploading if photo is shoot in this number of millseconds
	public static final String USERNAME_PREFIX = "@";
	public static final String USERNAME_SPLITOR = ",";
	public static final String METADATA_DATE_TIME_FORMAT = "yyyy:MM:dd HH:mm:ss";
	public static final String UPLOAD_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String FILENAME_FORMAT = "yyyyMMddHHmmss";
	public static final int TASK_SCAN_INTERVAL = 1*1000;  
	public static final int HTTP_TIMEOUT = 30000;
	public static final int HTTP_TIMEOUT_SHORT = 10000;
	public static final int HTTP_TIMEOUT_LONG = 120000;
	public static final int IMAGE_CACHE_MAX_SIZE = 500;
	public static final int IMAGE_CACHE_MIN_SIZE = 50;
	public static final int DATA_CACHE_LIVE_DAYS = 7;
	public static final int MULITI_DOWNLOADING = 2;
	public static  String BROADCAST_CACHE_APPS_PROGRESS = "goofy2.swably.CACHE_APPS_PROGRESS";
	public static  String BROADCAST_DOWNLOAD_PROGRESS = "goofy2.swably.DOWNLOAD_PROGRESS";
	public static  String BROADCAST_UPLOAD_PROGRESS = "goofy2.swably.UPLOAD_PROGRESS";
	public static  String BROADCAST_REVIEW_DELETED = "goofy2.swably.REVIEW_DELETED";
	public static  String BROADCAST_REVIEW_ADDED = "goofy2.swably.REVIEW_ADDED";
	public static  String BROADCAST_STAR_ADDED = "goofy2.swably.STAR_ADDED";
	public static  String BROADCAST_STAR_DELETED = "goofy2.swably.STAR_DELETED";
	public static  String BROADCAST_LIKE_ADDED = "goofy2.swably.LIKE_ADDED";
	public static  String BROADCAST_LIKE_DELETED = "goofy2.swably.LIKE_DELETED";
	public static  String BROADCAST_FOLLOW_ADDED = "goofy2.swably.FOLLOW_ADDED";
	public static  String BROADCAST_FOLLOW_DELETED = "goofy2.swably.FOLLOW_DELETED";
	public static  String BROADCAST_REFRESH_APP = "goofy2.swably.REVIEW_APP";
	public static  String BROADCAST_REFRESH_USER = "goofy2.swably.REVIEW_USER";
	public static  String BROADCAST_FINISH = "goofy2.swably.FINISH";
	public static final String KEY_PERCENT = "percent";
	public static final String KEY_PACKAGE = "package";
	public static final String KEY_APP = "app";
	public static final String KEY_COUNT = "count";
	public static final String KEY_TOTAL = "total";
	public static final String KEY_REFRESH = "refresh";
	public static final String KEY_FINISHED = "finished";
	public static final String KEY_FAILED = "failed";
	public static final String KEY_SIZE_TRANSFERRED = "size_sent";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_CHECKED = "checked";
	public static final String KEY_ID = "id";
	public static final String KEY_PATH = "path";
	protected static final String KEY_LOADING = "loading";
	protected static final String KEY_LOADED = "loaded";
	public static final String KEY_USER = "user";
	public static final String KEY_REVIEW = "review";
	public static final String KEY_UNREAD_REVIEWS_COUNT = "unread_reviews_count";
	public static final String KEY_UNREAD_FOLLOWS_COUNT = "unread_follows_count";
	public static final String KEY_TEXT = "text";
	public static final String KEY_SUBJECT = "subject";

	
    final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
    public final static int LIST_SIZE = 20;
	public static final long DEFAULT_CACHE_EXPIRES_IN = 5*60*1000; // mill-second
	
	public static final String VERSION_NAME_CLAIM = "goofy2.swably.claim";
	public static final String KEY_SPEED = "speed";
	public static final String KEY_REMAIN_TIME = "remain_time";
	
	static public String TMP_FOLDER = "";
	
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	
	public static String HTTP_PREFIX;
	public static String UPLOAD_HTTP_PREFIX;
}
