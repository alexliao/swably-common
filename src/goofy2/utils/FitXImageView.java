package goofy2.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
public class FitXImageView extends ImageView {
	/**
	 * 
	 */
	private static final String TAG = FitXImageView.class.getSimpleName();
	/**
	 * This height come with normal layout, don`t know where take this constant  
	 */
	//private static final int WIDE_HEIGHT = -2147483218;
	private static final int MAX_HEIGHT = 10000;

	public FitXImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FitXImageView(Context context) {
		super(context);
	}

	public FitXImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		if (Log.isLoggable(TAG, Log.DEBUG)) {
//			Log.d(TAG, "onMeasure, widthMeasureSpec=" + widthMeasureSpec
//					+ ", heightMeasureSpec=" + heightMeasureSpec);
//			Log.d(TAG, "onMeasure, widthMeasureSpec size:" + MeasureSpec.getSize(widthMeasureSpec)
//					+ ", heightMeasureSpec size:" + MeasureSpec.getSize(heightMeasureSpec));
//		}
		
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight;
		Drawable d = this.getDrawable();
		if(d != null){
			int imageWidth = d.getIntrinsicWidth();
			int imageHeight = d.getIntrinsicHeight();
			sizeHeight = imageHeight > 0 ? imageHeight*sizeWidth/imageWidth : 0;
		}else sizeHeight = 0;
		
		
		int heightSpec = MeasureSpec.makeMeasureSpec(sizeHeight, View.MeasureSpec.EXACTLY);
		//Log.d(TAG, "onMeasure, set heightMeasureSpec=" + heightSpec);
		//super.onMeasure(widthMeasureSpec, heightSpec);
		super.onMeasure(widthMeasureSpec, heightSpec);
	}
}
