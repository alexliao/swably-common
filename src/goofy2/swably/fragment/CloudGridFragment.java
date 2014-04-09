package goofy2.swably.fragment;

import goofy2.swably.R;

import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;

public abstract class CloudGridFragment extends CloudListFragmentBase {

    protected AbsListView prepareList(View v){
    	GridView result;
    	result = (GridView) v.findViewById(R.id.list);
    	return result;
    }

    protected void setAdpater(AbsListView list, BaseAdapter adapter){
		((GridView)list).setAdapter(adapter);
	}

}
