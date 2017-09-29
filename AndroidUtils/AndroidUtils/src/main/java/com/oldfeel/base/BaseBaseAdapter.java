package com.oldfeel.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oldfeel.conf.BaseConstant;
import com.oldfeel.utils.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器基类
 *
 * @author oldfeel
 *         <p/>
 *         Created on: 2014-1-10
 */
public abstract class BaseBaseAdapter<T> extends BaseAdapter {
    protected DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected List<T> list = new ArrayList<>();
    private boolean isAddOver = false; // 是否加载完成
    private Class<?> itemClass;
    private int pageStart = 0;

    public BaseBaseAdapter() {
    }

    public BaseBaseAdapter(Class<?> itemClass) {
        this(itemClass, -1);
    }

    public BaseBaseAdapter(Class<?> itemClass, int id) {
        this.itemClass = itemClass;
        id = (id == -1) ? R.drawable.default_image : id;
        if (id > 0)
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(id)
                    .showImageForEmptyUri(id).showImageOnFail(id)
                    .cacheInMemory(true).cacheOnDisk(true).build();
    }

    public void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }

    /**
     * 使用此方法,itemClass不能为空
     *
     * @param page
     * @param data
     */
    public void addData(int page, String data) {
        if (page == pageStart) {
            clear();
        }
        try {
            Gson gson = new Gson();
            JSONArray array = new JSONArray(data);
            if (array.length() < BaseConstant.getInstance().getPageSize()) {
                setIsAddOver(true);
            }
            for (int i = 0; i < array.length(); i++) {
                if (itemClass == null) {
                    throw new Exception("itemClass is null");
                }
                T t = (T) gson.fromJson(array.get(i).toString(), itemClass);
                list.add(t);
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(T t) {
        list.add(t);
        notifyDataSetChanged();
    }

    public void add(int index, T t) {
        list.add(index, t);
        notifyDataSetChanged();
    }

    public void addAll(List<T> list) {
        if (list == null) {
            setIsAddOver(true);
            return;
        }
        if (list.size() < BaseConstant.getInstance().getPageSize()) { // 如果加载的数据量小于每页显示的数据,说明加载完成
            setIsAddOver(true);
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(T t) {
        list.remove(t);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        list.remove(index);
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return list;
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    public void setItem(int position, T t) {
        list.set(position, t);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return getView(inflater, parent, position);
    }

    public <T extends View> T getView(View view, int id) {
        return (T) view.findViewById(id);
    }

    public abstract View getView(LayoutInflater inflater, ViewGroup parent, int position);

    public void setIsAddOver(boolean isAddOver) {
        this.isAddOver = isAddOver;
        if (isAddOver && onAddOverListener != null) {
            onAddOverListener.addOver();
        }
    }

    public boolean isAddOver() {
        return isAddOver;
    }

    public void setList(List<T> tempList) {
        list = tempList;
        notifyDataSetChanged();
    }

    public interface OnAddOverListener {
        public void addOver();
    }

    private OnAddOverListener onAddOverListener;

    public void setOnAddOverListener(OnAddOverListener onAddOverListener) {
        this.onAddOverListener = onAddOverListener;
    }
}
