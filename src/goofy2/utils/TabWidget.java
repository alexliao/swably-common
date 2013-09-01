package goofy2.utils;

import goofy2.swably.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class TabWidget extends android.widget.TabWidget {

	public TabWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public TabWidget(Context context, AttributeSet attrs) {
//		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabWidget);
//		mDashColor = a.get(R.styleable.DashLine_dashColor, 0xff000000);
		super(context, attrs);
	}
	public TabWidget(Context context) {
		super(context);
	}

}
