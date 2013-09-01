package goofy2.utils;

import goofy2.swably.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.PorterDuff.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class Avatar extends ImageView {
	private Bitmap mFramedPhoto;	
	private float mRadiusDp = 0f;
	private float mRadius = 0f;
	private float mInset = 1f;
//	static private float MAX_RADIUS=10f;
	private float mDensity;
	
	public Avatar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	    mDensity = context.getResources().getDisplayMetrics().density;
	}
	public Avatar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Avatar);
//		mDashColor = a.getColor(R.styleable.DashLine_dashColor, 0xff000000);
//		mGapColor = a.getColor(R.styleable.DashLine_gapColor, 0x00000000);
//		mDashWidth = a.getFloat(R.styleable.DashLine_dashWidth, 15f);
		mRadiusDp = a.getFloat(R.styleable.Avatar_radius, 0f);
	    mDensity = context.getResources().getDisplayMetrics().density;
	}
	public Avatar(Context context) {
		super(context);
	    mDensity = context.getResources().getDisplayMetrics().density;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
//		int density = canvas.getDensity(); // return 0 on some devices
		
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint.setColor(0xffeaeff0);
//		paint.setShadowLayer(3, 3, 3, 0x88000000);
//		Rect r = canvas.getClipBounds();
//		r.offset(-3, -3);
////		canvas.save();
////		canvas.rotate((float) 7, r.left+r.width()/2, r.top+r.height()/2);
//		canvas.drawRect(r, paint);
////		canvas.drawRoundRect(new RectF(r), 5, 5, paint);
//		
////		canvas.restore();
////		canvas.drawCircle(px - 10, py - 10, 10, paint);
		
//		super.onDraw(canvas);

//		Drawable image = getDrawable();
//		
//		float radius = 10f;
//		RectF rect = new RectF(canvas.getClipBounds());
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint.setColor(Color.RED);
//		canvas.drawRoundRect(rect, radius, radius, paint);
//
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		canvas.saveLayer(rect, paint, Canvas.ALL_SAVE_FLAG);
//		image.draw(canvas);
//		canvas.restore();

		int padding = Math.max( getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom() );
		int size = Math.min(getWidth(), getHeight()) - padding;
//		mRadius = Math.min(size/5, MAX_RADIUS*density/160f);
		mRadius = mRadiusDp*mDensity;
		
//		Rect rect = canvas.getClipBounds();
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint.setColor(Color.argb(128, 192, 192, 192)); // half transparent gray shadow
//		float[] direction = new float[]{ -1f, -1f, 0.7f };    
//		float light = 0.2f;    // low ambient light
//		float specular = 0f;   // smooth mirror
//		float blur = mInset;    
//		EmbossMaskFilter emboss=new EmbossMaskFilter(direction,light,specular,blur);    
//		paint.setMaskFilter(emboss);   
//		canvas.drawRoundRect(new RectF(rect), mRadius, mRadius, paint);

//		if (mFramedPhoto == null) { // error cached the image when scrolling in list
//			mFramedPhoto = createRoundedPhoto(size);
//		}

		Bitmap bm = createRoundedPhoto(size);
		if(bm != null) canvas.drawBitmap(bm, getPaddingLeft(), getPaddingTop(), null);
//		canvas.drawBitmap(createShadow(size), 0, 0, null);

	}
	

//	private Bitmap createBlurPhoto(int size) {
//		Bitmap output = Bitmap.createBitmap(size, size,	Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(output);
//		RectF outerRect = new RectF(0, 0, size, size);
//		
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint.setColor(Color.BLACK);
//		canvas.drawRoundRect(outerRect, mRadius, mRadius, paint);
//		
//		BlurMaskFilter filter = new BlurMaskFilter(2, BlurMaskFilter.Blur.INNER);
//		paint.setMaskFilter(filter);
//		Shader bitmapShader = new BitmapShader(getImageBitmap(size), TileMode.CLAMP, TileMode.CLAMP);
//		paint.setShader(bitmapShader);
//		canvas.drawRoundRect(outerRect, mRadius, mRadius, paint);
//		
//		return output;
//	}

	
//	private Bitmap createShadowPhoto(int size) {
//		Bitmap output = Bitmap.createBitmap(size, size,	Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(output);
//		RectF outerRect = new RectF(0, 0, size, size);
////		RectF outerRect = new RectF(mInset, mInset, size-mInset, size-mInset);
////		float outerRadius = mRadius-mInset;
//		
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		float[] direction = new float[]{ -1f, -1f, 0.7f };    
//		float light = 0.8f;    // low ambient light
//		float spectacular = 3f;   // smooth
//		float blur = 0.1f;    
//		EmbossMaskFilter emboss=new EmbossMaskFilter(direction,light,spectacular,blur);    
//		paint.setMaskFilter(emboss);   
//
////		BitmapDrawable imageDrawable = (BitmapDrawable) getDrawable();
////		imageDrawable.setBounds(0, 0, size, size);
//		Shader bitmapShader = new BitmapShader(getImageBitmap(size), TileMode.CLAMP, TileMode.CLAMP);
//		paint.setShader(bitmapShader);
//		canvas.drawRoundRect(outerRect, mRadius, mRadius, paint);
//		
//		return output;
//	}

//	private Bitmap getImageBitmap(int size) {
//		Drawable imageDrawable = getDrawable();
//		Bitmap output = Bitmap.createBitmap(size, size,	Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(output);
//		imageDrawable.setBounds(0, 0, size, size);
//		imageDrawable.draw(canvas);
//		return output;
//	}

//	private Bitmap createShadow(int size) {
//		Bitmap output = Bitmap.createBitmap(size, size,	Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(output);
//		RectF outerRect = new RectF(0, 0, size, size);
////		RectF outerRect = new RectF(mInset, mInset, size-mInset, size-mInset);
////		float outerRadius = mRadius-mInset;
//		
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint.setColor(Color.argb(192, 64, 64, 64)); // half transparent gray shadow
//		float[] direction = new float[]{ 0f, -1f, 0.7f };    
//		float light = 0.8f;    // low ambient light
//		float spectacular = 2f;   // smooth
//		float blur = mInset/2;    
//		EmbossMaskFilter emboss=new EmbossMaskFilter(direction,light,spectacular,blur);    
//		paint.setMaskFilter(emboss);   
//		canvas.drawRoundRect(outerRect, mRadius, mRadius, paint);
//		
//		// make inner hole.
//		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint.setColor(Color.RED);
//		paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
//		// Save the layer to apply the paint.
////		canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
//		float inset = mInset;
//		outerRect.inset(inset, inset);
//		canvas.drawRoundRect(outerRect, mRadius-inset, mRadius-inset, paint);
////		canvas.restore();		
//
//		return output;
//	}

	private Bitmap createRoundedPhoto(int size) {
		Drawable imageDrawable = getDrawable();
		if(imageDrawable == null) return null;
		Bitmap output = Bitmap.createBitmap(size, size,	Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
//		RectF outerRect = new RectF(mInset, mInset, size-mInset, size-mInset);
//		float outerRadius = mRadius-mInset;
		RectF outerRect = new RectF(0, 0, size, size-1);
		float outerRadius = mRadius;
		
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.RED);
		canvas.drawRoundRect(outerRect, outerRadius, outerRadius, paint);
		
		// Compose the image with the red rectangle.
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		imageDrawable.setBounds(0, 0, size, size);
		// Save the layer to apply the paint.
		canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
		imageDrawable.draw(canvas);
		canvas.restore();		

		return output;
	}

	private Bitmap createShadow(int size) {
		Bitmap output = Bitmap.createBitmap(size, size,	Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		RectF rect = new RectF(0, 0, size, size);
	//	RectF outerRect = new RectF(mInset, mInset, size-mInset, size-mInset);
	//	float outerRadius = mRadius-mInset;
		
		int dark = Color.parseColor("#80202020");
		int middle = Color.parseColor("#FFb0b0b0");
		int light = Color.parseColor("#FFffffff");
		
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint.setStyle(Paint.Style.STROKE);
//		paint.setStrokeWidth(2f);
//		paint.setColor(dark); 
//		canvas.drawLine(rect.left+mRadius, rect.top, rect.right-mRadius, rect.top, paint);
////		canvas.drawRect(rect.left+mRadius, rect.top, rect.right-mRadius, rect.top+1, paint);
//		paint.setColor(middle); 
//		canvas.drawLine(rect.left, rect.top+mRadius, rect.left, rect.bottom-mRadius, paint);
//		canvas.drawLine(rect.right, rect.top+mRadius, rect.right, rect.bottom-mRadius, paint);
//		paint.setColor(light); 
//		canvas.drawLine(rect.left+mRadius, rect.bottom, rect.right-mRadius, rect.bottom, paint);
//
//		paint.setStyle(Paint.Style.STROKE);
//		paint.setStrokeWidth(1f);
//		LinearGradient shader1 = new LinearGradient(0, 0, 0, mRadius, dark, middle, TileMode.CLAMP);
//		SweepGradient shader2 = new SweepGradient(mRadius, mRadius, Color.WHITE, Color.BLACK);
//		paint.setShader(shader1);
//		RectF oval = new RectF(rect.left, rect.top, mRadius*2, mRadius*2);
//		canvas.drawArc(oval, 180, 90, false, paint);
		
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		LinearGradient shader1 = new LinearGradient(0, 0, 0, size, dark, light, TileMode.CLAMP);	
		paint.setShader(shader1);
		canvas.drawRoundRect(rect, mRadius, mRadius, paint);
		
		return output;
	}

}
