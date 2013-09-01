package goofy2.utils;

import goofy2.swably.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.View;

public class LinearGradientView extends View {
	protected int mStartColor;
	protected int mEndColor;
	protected String mOrientation = "horizontal";
//	protected  int[] mArrayOfColor = new int[2];
//	protected float mAngle;
	protected Paint mPaint = new Paint();
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";	
	
	public LinearGradientView(Context context) {
		super(context);
	}
	public LinearGradientView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mStartColor = attrs.getAttributeIntValue(NAMESPACE, "startColor", Color.TRANSPARENT);
		mEndColor = attrs.getAttributeIntValue(NAMESPACE, "endColor", Color.TRANSPARENT);
		String s = attrs.getAttributeValue(NAMESPACE, "orientation");
		if(s != null) mOrientation = s;
//		mArrayOfColor[0] = mStartColor;
//		mArrayOfColor[1] = mEndColor;
	}
	
	public void setStartColor(int color){
		mStartColor = color;
	}
	public void setEndColor(int color){
		mEndColor = color;
	}
	public void setOrientation(String orientation){
		mOrientation = orientation;
	}
	public void onDraw(Canvas canvas) {
		LinearGradient shader;
		if(mOrientation.equals("horizontal"))
			shader = new LinearGradient(0, 0, getWidth(), 0, mStartColor, mEndColor, TileMode.CLAMP);
		else
			shader = new LinearGradient(0, 0, 0, getHeight(), mStartColor, mEndColor, TileMode.CLAMP);
		mPaint.setShader(shader);
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
//		GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mArrayOfColor);
//		GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.BLACK, Color.GREEN, Color.TRANSPARENT});
//		d.setGradientType(GradientDrawable.LINEAR_GRADIENT);
//		d.draw(canvas);
//		super.onDraw(canvas);
	}
}
