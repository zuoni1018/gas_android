package com.oldfeel.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.oldfeel.conf.BaseConstant;
import com.oldfeel.utils.DialogUtil;
import com.oldfeel.utils.NetUtil;
import com.oldfeel.utils.R;

import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * 必须要初始化 itemClass
 * 网络加载需要初始化 netUtil,本地加载需要初始化 isLocal
 * Created by oldfeel on 15-8-13.
 */
public abstract class BaseGrid<T extends BaseItem> extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected GridViewWithHeaderAndFooter mGridView;
    protected BaseBaseAdapter<T> adapter;
    private int lastVisibleIndex;
    private int page;
    private ProgressBar progressBar;
    protected NetUtil netUtil;
    protected Class<?> itemClass;
    protected boolean isLocal; // 本地list,获取数据不用发送网络请求
    protected int pageStart = 0;
    protected String pageKey = "page";
    private List<T> list;
    protected boolean isAddFoot = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_grid_fragment, container,
                false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.swiperefresh);
        mGridView = (GridViewWithHeaderAndFooter) view.findViewById(R.id.gridview);
        mGridView.setBackgroundResource(R.color.white);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHeaderView();
        progressBar = new ProgressBar(getActivity());
        if (isAddFoot) {
            getGridView().addFooterView(progressBar);
        }
        adapter = new MyAdapter(itemClass);
        adapter.setPageStart(pageStart);
        adapter.setOnAddOverListener(new BaseBaseAdapter.OnAddOverListener() {
            @Override
            public void addOver() {
                getGridView().removeFooterView(progressBar);
            }
        });
        getGridView().setAdapter(adapter);
        getGridView().setOnScrollListener(this);
        getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                BaseGrid.this.onItemClick(position);
            }
        });
        if (list == null || list.size() == 0) {//如果list直接有数据的话,就不请求网络了
            if (netUtil != null) {
                getData(pageStart);
            }
            if (isLocal) {
                getData(pageStart);
            }
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            adapter.addAll(list);
            refreshComplete();
        }
    }

    public GridViewWithHeaderAndFooter getGridView() {
        return mGridView;
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
        this.page = page;
        if (page != pageStart && adapter.isAddOver()) {
            return;
        }
        if (page == pageStart && adapter.isAddOver()) {
            adapter.setIsAddOver(false);
            getGridView().removeFooterView(progressBar);
            progressBar = new ProgressBar(getActivity());
            getGridView().addFooterView(progressBar);
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
        if (adapter.getCount() - mGridView.getHeaderViewCount() < BaseConstant
                .getInstance().getPageSize()) {
            if (isVisible() && getGridView() != null && progressBar != null) {
                getGridView().removeFooterView(progressBar);
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        getData(pageStart);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && lastVisibleIndex == adapter.getCount()
                + mGridView.getHeaderViewCount() + mGridView.getFooterViewCount()) {
            if (!adapter.isAddOver()) {
                getData(++page);
            } else { // 加载完成后移除底部进度条
                getGridView().removeFooterView(progressBar);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
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
    }

    public void setList(List list) {
        this.list = list;
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
    }


    public List getList() {
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
}
