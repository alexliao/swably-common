package goofy2.swably;

import goofy2.swably.AppTribtn.ViewHolder;
import goofy2.utils.ViewWrapper;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nineoldandroids.animation.ObjectAnimator;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public abstract class CloudInplaceActionsAdapter extends CloudBaseAdapter {
	public InplaceActionsHelper mHelper;
	
	public CloudInplaceActionsAdapter(CloudActivity context, JSONArray list,
			HashMap<String, Integer> loadingImages) {
		super(context, list, loadingImages);
		mHelper = new InplaceActionsHelper(context);
	}

	@Override
	protected void bindView(final View viewInfo, final JSONObject jsonObject) {
		ViewHolder holder = (ViewHolder) viewInfo.getTag();
		final View inplacePanel = holder.getInplacePanel();
		View ib3 = holder.getBtnTriangle();
		if(ib3 != null){
//			setTouchAnim(mContext, ib1);
			final int position = mPosition;
			ib3.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					boolean isOpened = (mHelper.mLastExpandedPosition == position);  
					mHelper.hideActionsAnim();
					if(!isOpened){
						onInplacePanelOpen(viewInfo, jsonObject);
						mHelper.showActionsAnim(inplacePanel, position);
					}
				}
			});
		}
//		ib3.setVisibility(View.GONE); // disable the feature

		if(mHelper.mLastExpandedPosition == mPosition){
			mHelper.showActions(inplacePanel);
		}else{
			mHelper.hideActions(inplacePanel);
		}
	}

	void onInplacePanelOpen(View viewInfo, JSONObject jsonObject){
	}
	
	static public interface ViewHolder{
		View getBtnTriangle();
		View getInplacePanel();
	}
}
