package com.oldfeel.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.oldfeel.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by oldfeel on 15-8-22.
 */
public class BaseStatePagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> list = new ArrayList<Fragment>();

    public BaseStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void add(Fragment fragment) {
        list.add(fragment);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Fragment fragment = list.get(position);
        String title = fragment.getArguments().getString("title");
        if (title != null) {
            return title;
        }
        return super.getPageTitle(position);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void set(int position, Fragment fragment) {
        if (position > list.size()) {
            LogUtil.showLog("pager adapter 添加 fragment 越界了");
        } else if (position == list.size()) {
            list.add(fragment);
        } else {
            list.set(position, fragment);
        }
        notifyDataSetChanged();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
