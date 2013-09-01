package goofy2.utils;

import goofy2.swably.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class DashLine extends View {
	protected float mDashWidth = 15f;
	protected float mGapWidth = 2.5f; 
	protected int mDashColor;
	protected int mGapColor;
	protected float mDensity;

	public DashLine(Context context) {
		super(context);
	}
	public DashLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DashLine);
		mDashColor = a.getColor(R.styleable.DashLine_dashColor, 0xff000000);
		mGapColor = a.getColor(R.styleable.DashLine_gapColor, 0x00000000);
		mDashWidth = a.getFloat(R.styleable.DashLine_dashWidth, 15f);
		mGapWidth = a.getFloat(R.styleable.DashLine_gapWidth, 2.5f);

}
	public DashLine(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onDraw(Canvas canvas) {
		int density = canvas.getDensity();
		
		Rect r = canvas.getClipBounds();
		boolean isVertical = false;
		if(r.width() < r.height()) isVertical = true;
		
		Paint paint = new Paint();
		paint.setStrokeWidth(1);
		paint.setStyle(Style.STROKE);

		paint.setColor(mGapColor);
		if(isVertical) canvas.drawLine(r.left, r.top, r.left, r.bottom, paint);
		else canvas.drawLine(r.left, r.top, r.right, r.top, paint);

		paint.setPathEffect(new DashPathEffect(new float[] {(mDashWidth*density/160f),(mGapWidth*density/160f)}, 0));
		paint.setColor(mDashColor);
		if(isVertical) canvas.drawLine(r.left, r.top, r.left, r.bottom, paint);
		else canvas.drawLine(r.left, r.top, r.right, r.top, paint);

		super.onDraw(canvas);
	}
}
