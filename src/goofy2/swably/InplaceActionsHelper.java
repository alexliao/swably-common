package goofy2.swably;

import goofy2.swably.R;
import goofy2.utils.ViewWrapper;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.nineoldandroids.animation.ObjectAnimator;

public class InplaceActionsHelper {
	Context mContext;
	View mLastExpandedPanel = null; // record which row is expanded
	int mLastExpandedPosition = -1;

	public InplaceActionsHelper(Context context){
		mContext = context;
	}
	
//	@Override
//	protected void bindView(final View viewInfo, JSONObject jsonObject) {
//		View ib3 = viewInfo.findViewById(R.id.btnTriangle);
//		if(ib3 != null){
////			setTouchAnim(mContext, ib1);
//			final int position = mPosition;
//			ib3.setOnClickListener(new View.OnClickListener(){
//				@Override
//				public void onClick(View v) {
//					boolean isOpened = (mLastExpandedPosition == position);  
//					hideActionsAnim(mLastExpandedRow);
//					if(!isOpened){
//						showActionsAnim(viewInfo, position);
//					}
//				}
//			});
//		}
//
//		View inplacePanel = viewInfo.findViewById(R.id.inplacePanel);
//		if(mLastExpandedPosition == mPosition){
//			showActions(inplacePanel);
//		}else{
//			hideActions(inplacePanel);
//		}
//	}

	public void showActions(View panel){
		(new ViewWrapper(panel)).setHeight(LayoutParams.WRAP_CONTENT);
	}
	public void hideActions(View panel){
		(new ViewWrapper(panel)).setHeight(0);
	}
	
	public void showActionsAnim(View panel, int position){
//		Log.d("", Const.APP_NAME + " showActions mPosition:" + mPosition + " mLast: " + mLastExpandedPosition);
		mLastExpandedPanel = panel;
		mLastExpandedPosition = position;
		
//		View inplacePanel = (View) panel.findViewById(R.id.inplacePanel);
//		(new ViewWrapper(inplacePanel)).setHeight(LayoutParams.WRAP_CONTENT);
		showActions(panel);
		panel.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int targetHeight = panel.getMeasuredHeight();
		
		ObjectAnimator anim = ObjectAnimator.ofInt(new ViewWrapper(panel), "Height", 0, targetHeight);
		anim.setDuration(mContext.getResources().getInteger(R.integer.config_mediumAnimTime));
		anim.start();
	}
	public void hideActionsAnim(View panel){
		mLastExpandedPanel = null;
		mLastExpandedPosition = -1;
		if(panel == null) return;
		
//		View inplacePanel = (View) panel.findViewById(R.id.inplacePanel);
		ObjectAnimator anim = ObjectAnimator.ofInt(new ViewWrapper(panel), "Height", panel.getHeight(), 0);
		anim.setDuration(mContext.getResources().getInteger(R.integer.config_mediumAnimTime));
		anim.start();
	}

	public void hideActionsAnim(){
		hideActionsAnim(mLastExpandedPanel);
	}
}
