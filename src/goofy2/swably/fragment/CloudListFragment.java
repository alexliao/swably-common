package goofy2.swably.fragment;

import org.json.JSONException;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import goofy2.swably.Const;
import goofy2.swably.R;
import goofy2.utils.PullToRefreshListView;


public abstract class CloudListFragment extends CloudListFragmentBase {
	protected PullToRefreshListView mListContainer = null;
	private View viewFooter;
	// protected View footerDivider;
	protected View loadingMore;
	private TextView txtMore;
	private TextView txtNoMore;
	private boolean mIsScrolling = false;
	private boolean mIsDirty = false;
	private View mListHeader;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result =super.onCreateView(inflater, container, savedInstanceState); 
        if(mListContainer != null) mListContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	refreshWithoutLoading();
            }
        });
        return result;
    }

    protected AbsListView prepareList(View v){
    	ListView result;
		View lv = v.findViewById(R.id.list);
		if (lv.getClass() == PullToRefreshListView.class) {
			mListContainer = (PullToRefreshListView) lv;
			result = mListContainer.getRefreshableView();
		} else
			result = (ListView) lv;
        
		mListHeader = getListHeader();
		if (mListHeader != null) result.addHeaderView(mListHeader);

		View vt = getRowTop();
		if (vt != null)	result.addHeaderView(vt);
		
		viewFooter = LayoutInflater.from(a()).inflate(R.layout.list_footer, null);
		result.addFooterView(viewFooter);
		View vb = getRowBottom();
		if (vb != null)	result.addFooterView(vb);
		
		loadingMore = (View) viewFooter.findViewById(R.id.loadingMore);
		txtMore = (TextView) viewFooter.findViewById(R.id.txtMore);
		txtNoMore = (TextView) viewFooter.findViewById(R.id.txtNoMore);
		txtMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMore();
			}
		});

		result.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				onListScrollStateChanged(view, scrollState);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				onListScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});

		return result;
    }


	protected void loadMore() {
		String lastId = null;
		if (txtNoMore.getVisibility() != View.VISIBLE
				&& loadingMore.getVisibility() != View.VISIBLE) {
			try {
				if (mListData.length() > 0)
					lastId = mListData.getJSONObject(mListData.length() - 1)
							.getString(getIdName());
				loadingMore.setVisibility(View.VISIBLE);
				txtMore.setVisibility(View.GONE);
				txtNoMore.setVisibility(View.GONE);
				loadList(getUrl(), lastId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	protected void loadedMore(boolean successed) {
    	if(mListContainer != null) mListContainer.onRefreshComplete();
		loadingMore.setVisibility(View.GONE);
		txtMore.setVisibility(View.GONE);
		txtNoMore.setVisibility(View.GONE);
		if (successed) {
			if (mLastLoaded == 0) txtNoMore.setVisibility(View.VISIBLE);
			else txtNoMore.setVisibility(View.INVISIBLE);
		} else {
			txtMore.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void refresh() {
		ca().showLoading();
		loadingMore.setVisibility(View.VISIBLE);
		refreshWithoutLoading();
	}

	public void onListScrollStateChanged(AbsListView view, int scrollState) {
		Log.v(Const.APP_NAME, Const.APP_NAME + " CloudListFragment onScrollStateChanged: "
				+ scrollState);
		if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {
			mIsScrolling = true;
		} else {
			mIsScrolling = false;
			if (mIsDirty)
				refreshListView();
			if (view.getCount() >= Const.LIST_SIZE
					&& view.getLastVisiblePosition() > (view.getCount() - 2)) {
				Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment try load");
				loadMore();
			}
		}
	}

	@Override
	protected void onDataChanged(int item) {
		Log.d(Const.APP_NAME, Const.APP_NAME + " CloudListFragment onDataChanged item:" + item
				+ " mIsScrolling:" + mIsScrolling + " isItemVisible:"
				+ isItemVisible(item));
		mIsDirty = true;
		if (!mIsScrolling) {
			if (item < 0 || isItemVisible(item))
				refreshListView();
		}

	}

	@Override
	protected void refreshListView() {
		super.refreshListView();
		mIsDirty = false;
	}
	
	@Override
    protected int getDataPosition(int listPosition){
    	return listPosition - ((ListView)mList).getHeaderViewsCount();
    }

	protected void setAdpater(AbsListView list, BaseAdapter adapter){
		((ListView)list).setAdapter(adapter);
	}
	
}