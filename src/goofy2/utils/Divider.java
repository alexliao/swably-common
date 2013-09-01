package goofy2.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class Divider extends View {

	public Divider(Context context) {
		super(context);
	}
	public Divider(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public Divider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onDraw(Canvas canvas) {
		Rect r = canvas.getClipBounds();
		boolean isVertical = false;
		if(r.width() < r.height()) isVertical = true;
		
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Paint paint = new Paint();
		paint.setStrokeWidth(1);
		paint.setStyle(Style.STROKE);
		paint.setPathEffect(new DashPathEffect(new float[] {15,2.5f}, 0));
		
		paint.setColor(0xff9fc5d2);
		if(isVertical) canvas.drawLine(r.left, r.top, r.left, r.bottom, paint);
		else canvas.drawLine(r.left, r.top, r.right, r.top, paint);
		
		paint.setColor(0xffffffff);
		if(isVertical) canvas.drawLine(r.left+1, r.top, r.left+1, r.bottom, paint);
		else canvas.drawLine(r.left, r.top+1, r.right, r.top+1, paint);
		
//		r.offset(-3, -3);
////		canvas.save();
////		canvas.rotate((float) 7, r.left+r.width()/2, r.top+r.height()/2);
//		canvas.drawRect(r, paint);
////		canvas.drawRoundRect(new RectF(r), 5, 5, paint);
//		
////		canvas.restore();
////		canvas.drawCircle(px - 10, py - 10, 10, paint);
		super.onDraw(canvas);
	}
}
