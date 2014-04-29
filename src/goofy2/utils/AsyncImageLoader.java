package goofy2.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import goofy2.swably.R;
import goofy2.swably.CloudActivity;
import goofy2.swably.Const;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import goofy2.swably.Utils;

public class AsyncImageLoader {
	Context mContext;
	ImageView mImageView;
	int mInitPosition;
	ExecutorService mThreadPool;
	int mReqWidth = 0;
	int mReqHeight = 0;
	Runnable mCallback = null;
//	Object mOldTag;
	
	public AsyncImageLoader(Context context, ImageView iv, int initPosition){
		mContext = context;
		mInitPosition = initPosition;
		mImageView = iv;
//		mOldTag = iv.getTag();
		iv.setTag(initPosition);
	}

	public AsyncImageLoader setThreadPool(ExecutorService threadPool){
		mThreadPool = threadPool;
		return this;
	}
	
	private ExecutorService getThreadPool(){
		if(mThreadPool == null) mThreadPool = Executors.newFixedThreadPool(Const.MULITI_DOWNLOADING); 
		return mThreadPool;
	}
	

	public AsyncImageLoader setRequestSize(int reqWidth, int reqHeight){
		mReqWidth = reqWidth;
		mReqHeight = reqHeight;
		return this;
	}

	public AsyncImageLoader setCallback(Runnable callback){
		mCallback = callback;
		return this;
	}

	//	public void setUrl(String url){
//		if(url == null) return;
//		Bitmap bm = Utils.getImageFromFile(mContext, url);
//		if(bm != null){
//			mImageView.setImageBitmap(bm);
//		}else{
//	    	AsyncTask<String, Void, Bitmap> loadTask = new AsyncTask<String, Void, Bitmap>() {
//				@Override
//				protected Bitmap doInBackground(String... params) {
//					Bitmap ret = null;
//			        try {
//	//		        	ret = Utils.getImageFromFile(mContext, params[0]);
//	//					if(ret == null){
//							Utils.saveImageToFile(mContext, params[0], Const.HTTP_TIMEOUT_LONG);
//							ret = Utils.getImageFromFile(mContext, params[0]);
//	//					}
//					} catch (Exception e) {
//						Log.e(Const.APP_NAME, Const.APP_NAME + " AsyncImageLoader err: "+e.getMessage());
//					}
//	//				cache.remove(imageUrl);
//					return ret;
//				}
//	            protected void onPostExecute(Bitmap result) {
//	            	//if(result == 1 && adapter != null) adapter.notifyDataSetChanged();
//	            	if(result != null){
//	            		int position = (Integer) mImageView.getTag();
//            			Utils.logV(AsyncImageLoader.this, "position:" + position + " initPosition:" + mInitPosition);
//	            		if(position == mInitPosition){
//	            			mImageView.setImageBitmap(result);
//	            			Animation anim = new AlphaAnimation(0f, 1f);
//	            			anim.setDuration(mContext.getResources().getInteger(R.integer.config_shortAnimTime));
////	            			anim.setDuration(10000);
//	            			mImageView.startAnimation(anim);
//	            		}
//	            		else
//	            			Utils.logV(AsyncImageLoader.this, "position changed");
//	            	}        		
//	            }
//	        };
//	        loadTask.execute(url);
//		}
		
	public void loadApkPath(final String packageName, final PackageManager packageManager){
		load(packageName, new Loader(){
			@Override
			public void run(String uri) {
//				File f = new File(uri);
//				if(!f.exists())
					Utils.saveLocalApkIcon(packageManager, uri);
			}
		});
	}
	
	public void loadUrl(final String url){
		load(url, new Loader(){
			@Override
			public void run(String uri) {
				Utils.saveImageToFile(mContext, uri, Const.HTTP_TIMEOUT);
			}
		});
	}
	
	public void load(final String uri, final Loader loader){
		if(uri == null) return;
		
		final String pathName = Utils.getImageFileName(uri);
		Bitmap bm = Utils.getImageFromFile(mContext, pathName, mReqWidth, mReqHeight);
		if(bm != null){
			int position = (Integer) mImageView.getTag();
			if(position != mInitPosition) return;
			if(mImageView.getVisibility() != View.VISIBLE) return;
			mImageView.setImageBitmap(bm);
//			mImageView.setTag(mOldTag);
			if(mCallback != null) mCallback.run();
		}else{
			final Handler handler = new Handler();
			ParamRunnable pr = new ParamRunnable(){
		    	public void run() {
		    		try{
			    		loader.run(uri);
	            		int position = (Integer) mImageView.getTag();
	//		        			Utils.logV(AsyncImageLoader.this, "position:" + position + " initPosition:" + mInitPosition);
	            		if(position == mInitPosition){
//	            			mImageView.setTag(mOldTag);
				    		if(mImageView.getVisibility() != View.VISIBLE) return; // The object mImageView pointed to may be set invisible at this moment.
	    					final Bitmap bm = Utils.getImageFromFile(mContext, pathName, mReqWidth, mReqHeight);
	    					if(bm != null){
	    						handler.post(new Runnable(){
	    							@Override
	    							public void run() {
	    		            			mImageView.setImageBitmap(bm);
	    		            			Animation anim = new AlphaAnimation(0.3f, 1f);
	    		            			anim.setDuration(mContext.getResources().getInteger(R.integer.config_mediumAnimTime));
	    		            			mImageView.startAnimation(anim);
	    		            			if(mCallback != null) mCallback.run();
	    							}
	    						});
	    					}
	            		}
	//            		else
	//            			Utils.logV(AsyncImageLoader.this, "position changed");
		    		}catch(Exception e){
		    			Log.e(Const.APP_NAME, "AsyncImageLoader.load error: " + e.getMessage());
		    		}
		    	}
			};
			getThreadPool().execute(pr);
		}
	}

	static interface Loader{
		public void run(String uri);
	}
//	private static class LoadTask extends AsyncTask {
//		Context mContext;
//		ImageView mImageView;
//		int mPosition;
//	
//	    public LoadTask(Context context, int position, ImageView iv) {
//			mContext = context;
//			mImageView = iv;
//			mPosition = position;
//	    }
//
//	
////	    @Override
////	    protected long doInBackground(Void... arg0) {
////	        // Download bitmap here
////	    }
////	
////	    @Override
////	    protected void onPostExecute(Bitmap bitmap) {
////	        if (mHolder.position == mPosition) {
////	            mHolder.thumbnail.setImageBitmap(bitmap);
////	        }
////	    }
//	}

	public void loadContactAvatar(String url){
		load(url, new Loader(){
			@Override
			public void run(String uri) {
				try {
					ContentResolver cr = mContext.getContentResolver();
					long id = Long.parseLong(uri.substring(uri.lastIndexOf("/")+1));
					Uri uriContact =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
				    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uriContact);
				    Bitmap contactPhoto = BitmapFactory.decodeStream(input);
					String fileName = Utils.getImageFileName(uri);
					File f = new File(fileName);
			        FileOutputStream out = new FileOutputStream(f);   
			        contactPhoto.compress(Bitmap.CompressFormat.PNG, 100, out);
				} catch (Exception e) {
					e.printStackTrace();
				} catch (OutOfMemoryError e){
					e.printStackTrace();
				}
			}
		});
	}

}
