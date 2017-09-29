package com.oldfeel.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oldfeel.conf.BaseConstant;
import com.oldfeel.utils.DialogUtil;
import com.oldfeel.utils.NetUtil;
import com.oldfeel.utils.R;

import java.util.List;

/**
 * 必须要初始化 itemClass
 * 网络加载需要初始化 netUtil,本地加载需要初始化 isLocal
 * Created by oldfeel on 4/17/15.
 */
public abstract class BaseList<T extends BaseItem> extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected ListView mListView;
    protected TextView tvEmpty;
    protected FrameLayout flParent;
    protected BaseBaseAdapter<T> adapter;
    private int lastVisibleIndex;
    protected int page;
    protected ProgressBar progressBar;
    protected View emptyView;
    protected NetUtil netUtil;
    protected Class<?> itemClass;
    protected boolean isLocal; // 本地list,获取数据不用发送网络请求
    protected int pageStart = 0;
    protected String pageKey = "page";
    /**
     * 注意此list只在onActivityCreate和notifyDataSetChanged方法中使用
     */
    protected List<T> list;
    protected boolean isShowEmpty = true;
    protected int viewId = R.layout.base_list_fragment;
    protected boolean isAddFoot = true;
    protected boolean isSwipeRefresh = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (!isSwipeRefresh) {
            viewId = R.layout.single_list;
        }
        View view = inflater.inflate(viewId, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.swiperefresh);
        mListView = (ListView) view.findViewById(R.id.listview);
        tvEmpty = (TextView) view.findViewById(R.id.empty);
        flParent = (FrameLayout) view.findViewById(R.id.list_parent);
        return view;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHeaderView();
        progressBar = new ProgressBar(getActivity());
        if (isAddFoot) {
            getListView().addFooterView(progressBar);
        }
        if (adapter == null) {
            adapter = new MyAdapter(itemClass);
        }
        adapter.setPageStart(pageStart);
        adapter.setOnAddOverListener(new BaseBaseAdapter.OnAddOverListener() {
            @Override
            public void addOver() {
                getListView().removeFooterView(progressBar);
            }
        });
        getListView().setAdapter(adapter);
        getListView().setOnScrollListener(this);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                position = position - getListView().getHeaderViewsCount();
                if (position >= 0) {
                    BaseList.this.onItemClick(position);
                }
            }
        });

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }

        if (emptyView != null) {
            setEmptyView(emptyView);
        } else if (tvEmpty != null) {
            emptyView = tvEmpty;
        }

        if (list != null && list.size() != 0) { //如果list直接有数据的话,就不请求网络了
            adapter.addAll(list);
            refreshComplete();
        } else {
            if (netUtil != null) {
                getData(pageStart);
            }
            if (isLocal) {
                getData(pageStart);
            }
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }
    }

    private void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        flParent.removeView(tvEmpty);
        flParent.addView(emptyView);
    }

    public void setItemClass(Class<?> itemClass) {
        this.itemClass = itemClass;
    }

    public ListView getListView() {
        return mListView;
    }


    public BaseBaseAdapter<T> getAdapter() {
        return adapter;
    }

    public void setNetUtil(NetUtil netUtil) {
        this.netUtil = netUtil;
        getData(pageStart);
    }

    public void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }

    public void setPageKey(String pageKey) {
        this.pageKey = pageKey;
    }

    public void getData(final int page) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (netUtil == null) {
            refreshComplete();
            return;
        }
        this.page = page;
        if (page != pageStart && adapter.isAddOver()) {
            return;
        }
        if (page == pageStart && adapter.isAddOver()) {
            adapter.setIsAddOver(false);
            getListView().removeFooterView(progressBar);
            progressBar = new ProgressBar(getActivity());
            getListView().addFooterView(progressBar);
        }
        netUtil.setParams(pageKey, page);
        netUtil.setCancelListener(new NetUtil.OnCancelListener() {

            @Override
            public void cancel() {
                refreshComplete();
            }
        });
        netUtil.sendPost("", new NetUtil.OnJsonResultListener() {
            @Override
            public void onSuccess(String data) {
                adapter.addData(page, parseData(data));
                refreshComplete();
            }

            @Override
            public void onFail(int code, String data) {
                refreshComplete();
            }
        });
    }

    public String parseData(String data) {
        return data;
    }

    protected void refreshComplete() {
        if (adapter.getCount() < BaseConstant.getInstance().getPageSize()) {
            if (isVisible() && getListView() != null && progressBar != null) {
                getListView().removeFooterView(progressBar);
            }
        }
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (isShowEmpty && getAdapter().getCount() == 0) {
            if (emptyView != null) {
                mListView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            mListView.setVisibility(View.VISIBLE);
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRefresh() {
        getData(pageStart);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && lastVisibleIndex == adapter.getCount()) {
            if (!adapter.isAddOver()) {
                if (isAddFoot)
                    getData(++page);
            } else { // 加载完成后移除底部进度条
                getListView().removeFooterView(progressBar);
            }
        }
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            onScrollStop();
        }
    }

    protected void onScrollStop() {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
    }

    public void setShowEmpty(boolean showEmpty) {
        isShowEmpty = showEmpty;
    }

    public boolean isShowEmpty() {
        return isShowEmpty;
    }

    public void showToast(String text) {
        DialogUtil.getInstance().showToast(getActivity(), text);
    }

    public abstract void onItemClick(int position);

    public abstract void initHeaderView();

    public abstract View getItemView(LayoutInflater inflater, ViewGroup parent, int position);

    public void setItem(int position, T t) {
        adapter.setItem(position, t);
    }

    public void addItem(int i, T t) {
        adapter.add(i, t);
        if (adapter.getCount() == 1) {
            refreshComplete();
        }
    }

    public void setList(List list) {
        this.list = list;
        if (isAdded()) {
            getAdapter().setList(list);
        }
    }

    public void setParams(String key, Object value, boolean isGetData) {
        netUtil.setParams(key, value);
        if (isGetData) {
            getData(pageStart);
        }
    }


    public void setParams(String key, String value) {
        setParams(key, value, true);
    }

    /**
     * @param isRefreshList true 为更新 adapter 中的 list 为 BaseList.list
     */
    public void notifyDataSetChanged(boolean isRefreshList) {
        if (isAdded() && getAdapter() != null) {
            if (isRefreshList) {
                getAdapter().setList(list);
            } else {
                getAdapter().notifyDataSetChanged();
            }
        }
    }


    class MyAdapter extends BaseBaseAdapter<T> {

        public MyAdapter(Class<?> itemClass) {
            super(itemClass);
        }

        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent, int position) {
            return getItemView(inflater, parent, position);
        }
    }

    public T getItem(int position) {
        return adapter.getItem(position);
    }

    public void addAll(List<T> list) {
        adapter.addAll(list);
        refreshComplete();
    }


    public List getList() {
        if (adapter == null) {
            return null;
        }
        return adapter.getList();
    }

    public void clear() {
        adapter.clear();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (getActivity() instanceof BaseActivity && ((BaseActivity) getActivity()).isAnim())
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
    }

    public interface OnListItemClickListener {
        void onItemClick(Object object);
    }

    public OnListItemClickListener onListItemClickListener;

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
    }
}
