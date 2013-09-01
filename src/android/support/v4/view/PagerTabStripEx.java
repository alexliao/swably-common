package android.support.v4.view;

import goofy2.utils.LinearGradientView;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerTitleStrip;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.TextView;

public class PagerTabStripEx extends PagerTitleStrip
{
  private static final String TAG = "PagerTabStrip";
  private static final int INDICATOR_HEIGHT = 6;
  private static final int MIN_PADDING_BOTTOM = 2;
  private static final int TAB_PADDING = 16;
  private static final int TAB_SPACING = 32;
  private static final int MIN_TEXT_SPACING = 64;
  private static final int FULL_UNDERLINE_HEIGHT = 2;
  private static final int MIN_STRIP_HEIGHT = 32;
  private static final int FADE_WIDTH = 32;
  
  private int mIndicatorColor;
  private int mIndicatorHeight;
  private int mMinPaddingBottom;
  private int mMinTextSpacing;
  private int mMinStripHeight;
  private int mTabPadding;
  private int mFadeWidth;
  private int mBackgroundColor = Color.BLACK;
  private final Paint mTabPaint = new Paint();
  private final Rect mTempRect = new Rect();

  private int mTabAlpha = 255;

  private boolean mDrawFullUnderline = false;
  private boolean mDrawFullUnderlineSet = false;
  private int mFullUnderlineHeight;
  private boolean mIgnoreTap;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private int mTouchSlop;
  
//  private LinearGradientView mGradientLeft;
//  private LinearGradientView mGradientRight;

  public PagerTabStripEx(Context context)
  {
    this(context, null);
  }

  public PagerTabStripEx(Context context, AttributeSet attrs) {
    super(context, attrs);

    this.mIndicatorColor = this.mTextColor;
    this.mTabPaint.setColor(this.mIndicatorColor);

    float density = context.getResources().getDisplayMetrics().density;
    this.mIndicatorHeight = (int)(INDICATOR_HEIGHT * density + 0.5F);
    this.mMinPaddingBottom = (int)(MIN_PADDING_BOTTOM * density + 0.5F);
    this.mMinTextSpacing = (int)(MIN_TEXT_SPACING * density);
    this.mTabPadding = (int)(TAB_PADDING * density + 0.5F);
    this.mFullUnderlineHeight = (int)(FULL_UNDERLINE_HEIGHT * density + 0.5F);
    this.mMinStripHeight = (int)(MIN_STRIP_HEIGHT * density + 0.5F);
    this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    this.mFadeWidth = (int)(FADE_WIDTH * density + 0.5F);

    setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    setTextSpacing(getTextSpacing());

    setWillNotDraw(false);

    this.mPrevText.setFocusable(true);
    this.mPrevText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View v) {
        PagerTabStripEx.this.mPager.setCurrentItem(PagerTabStripEx.this.mPager.getCurrentItem() - 1);
      }
    });
    this.mNextText.setFocusable(true);
    this.mNextText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View v) {
        PagerTabStripEx.this.mPager.setCurrentItem(PagerTabStripEx.this.mPager.getCurrentItem() + 1);
      }
    });
    if (getBackground() == null)
      this.mDrawFullUnderline = true;
    
//    mGradientLeft = new LinearGradientView(context);
////    mGradientLeft.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//    mGradientLeft.setLayoutParams(new LayoutParams(40, 40));
//    addView(mGradientLeft);
//    View v = mGradientLeft = (LinearGradientView) new View(context);
//    v.setLayoutParams(new LayoutParams(100,100));
//    v.setBackgroundColor(Color.GREEN);
//    addView(v);
//    TextView t = new TextView(context);
//    t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//    t.setText("test");
//    addView(t);
  }

  public void setTabIndicatorColor(int color)
  {
    this.mIndicatorColor = color;
    this.mTabPaint.setColor(this.mIndicatorColor);
    invalidate();
  }

  public void setTabIndicatorColorResource(int resId)
  {
    setTabIndicatorColor(getContext().getResources().getColor(resId));
  }

  public int getTabIndicatorColor()
  {
    return this.mIndicatorColor;
  }

  public void setPadding(int left, int top, int right, int bottom)
  {
    if (bottom < this.mMinPaddingBottom) {
      bottom = this.mMinPaddingBottom;
    }
    super.setPadding(left, top, right, bottom);
  }

  public void setTextSpacing(int textSpacing)
  {
    if (textSpacing < this.mMinTextSpacing) {
      textSpacing = this.mMinTextSpacing;
    }
    super.setTextSpacing(textSpacing);
  }

  public void setBackgroundDrawable(Drawable d)
  {
    super.setBackgroundDrawable(d);
    if (!this.mDrawFullUnderlineSet)
      this.mDrawFullUnderline = (d == null);
  }

  public void setBackgroundColor(int color)
  {
	mBackgroundColor = color;  
    super.setBackgroundColor(color);
    if (!this.mDrawFullUnderlineSet)
      this.mDrawFullUnderline = ((color & 0xFF000000) == 0);
  }

  public void setBackgroundResource(int resId)
  {
    super.setBackgroundResource(resId);
    if (!this.mDrawFullUnderlineSet)
      this.mDrawFullUnderline = (resId == 0);
  }

  public void setDrawFullUnderline(boolean drawFull)
  {
    this.mDrawFullUnderline = drawFull;
    this.mDrawFullUnderlineSet = true;
    invalidate();
  }

  public boolean getDrawFullUnderline()
  {
    return this.mDrawFullUnderline;
  }

  int getMinHeight()
  {
    return Math.max(super.getMinHeight(), this.mMinStripHeight);
  }

  public boolean onTouchEvent(MotionEvent ev)
  {
    int action = ev.getAction();
    if ((action != 0) && (this.mIgnoreTap)) {
      return false;
    }

    float x = ev.getX();
    float y = ev.getY();
    switch (action) {
    case 0:
      this.mInitialMotionX = x;
      this.mInitialMotionY = y;
      this.mIgnoreTap = false;
      break;
    case 2:
      if ((Math.abs(x - this.mInitialMotionX) <= this.mTouchSlop) && (Math.abs(y - this.mInitialMotionY) <= this.mTouchSlop))
        break;
      this.mIgnoreTap = true; break;
    case 1:
      if (x < this.mCurrText.getLeft() - this.mTabPadding) {
        this.mPager.setCurrentItem(this.mPager.getCurrentItem() - 1); } else {
        if (x <= this.mCurrText.getRight() + this.mTabPadding) break;
        this.mPager.setCurrentItem(this.mPager.getCurrentItem() + 1);
      }

    }

    return true;
  }

  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);

    int height = getHeight();
    int bottom = height;
    int left = this.mCurrText.getLeft() - this.mTabPadding;
    int right = this.mCurrText.getRight() + this.mTabPadding;
    int top = bottom - this.mIndicatorHeight;

    this.mTabPaint.setColor(this.mTabAlpha << 24 | this.mIndicatorColor & 0xFFFFFF);
    canvas.drawRect(left, top, right, bottom, this.mTabPaint);

    if (this.mDrawFullUnderline) {
      this.mTabPaint.setColor(0xFF000000 | this.mIndicatorColor & 0xFFFFFF);
      canvas.drawRect(0.0F, height - this.mFullUnderlineHeight, getWidth(), height, this.mTabPaint);
    }
    
  }

  void updateTextPositions(int position, float positionOffset, boolean force)
  {
    Rect r = this.mTempRect;
    int bottom = getHeight();
    int left = this.mCurrText.getLeft() - this.mTabPadding;
    int right = this.mCurrText.getRight() + this.mTabPadding;
    int top = bottom - this.mIndicatorHeight;

    r.set(left, top, right, bottom);

    super.updateTextPositions(position, positionOffset, force);
    this.mTabAlpha = (int)(Math.abs(positionOffset - 0.5F) * 2.0F * 255.0F);

    left = this.mCurrText.getLeft() - this.mTabPadding;
    right = this.mCurrText.getRight() + this.mTabPadding;
    r.union(left, top, right, bottom);

    invalidate(r);
  }
  
//  protected void onLayout(boolean changed, int l, int t, int r, int b){
//	  super.onLayout(changed, l, t, r, b);
//	  this.mGradientLeft.layout(l, t, r, b);
//  }
  
}