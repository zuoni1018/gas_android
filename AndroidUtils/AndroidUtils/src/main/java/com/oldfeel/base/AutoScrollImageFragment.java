package com.oldfeel.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oldfeel.utils.R;

import java.util.List;

import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import viewpagerindicator.CirclePageIndicator;

/**
 * Created by oldfeel on 4/14/15.
 */
public class AutoScrollImageFragment extends BaseFragment {
    private AutoScrollViewPager pager;
    private BasePagerAdapter adapter;
    private CirclePageIndicator pageIndicator;
    private List<String> images;
    private int height; // 默认为屏幕宽度的 1/2(需要在代码里设置 framelayout 的宽高度)
    private View disableView; // 滑动轮播图时禁止使用的 view;

    public static AutoScrollImageFragment newInstance(List<String> images, int height) {
        AutoScrollImageFragment fragment = new AutoScrollImageFragment();
        fragment.images = images;
        fragment.height = height;
        return fragment;
    }

    public static AutoScrollImageFragment newInstance(List<String> images) {
        AutoScrollImageFragment fragment = new AutoScrollImageFragment();
        fragment.images = images;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auto_scroll_image_fragment, container, false);
        pager = (AutoScrollViewPager) view.findViewById(R.id.auto_scroll_image_fragment);
        pageIndicator = (CirclePageIndicator) view.findViewById(R.id.view_pager_indicator);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new BasePagerAdapter(getFragmentManager());
        if (images != null && images.size() > 0) {
            putImagesToAdapter();
        }
        pager.setAdapter(adapter);
        pageIndicator.setViewPager(pager);
        if (disableView != null) {
            pager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    disableView.setEnabled(false);
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            disableView.setEnabled(true);
                            break;
                    }
                    return false;
                }
            });
        }
    }

    private void putImagesToAdapter() {
        adapter.clear();
        for (int i = 0; i < images.size(); i++) {
            ImageFragment fragment = ImageFragment.newInstance(onPagerClickListener, images.get(i), i, height);
            adapter.add(fragment);
        }
        pager.startAutoScroll();
    }

    public void setImages(List<String> images) {
        this.images = images;
        putImagesToAdapter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setDisableView(View view) {
        this.disableView = view;
    }

    public static class ImageFragment extends BaseFragment {
        private OnPagerClickListener onPagerClickListener;
        private String image;
        private int position;
        private int height; // 默认为屏幕宽度的 1/2

        public static ImageFragment newInstance(OnPagerClickListener onPagerClickListener, String image, int position, int height) {
            ImageFragment fragment = new ImageFragment();
            fragment.onPagerClickListener = onPagerClickListener;
            fragment.position = position;
            fragment.image = image;
            fragment.height = height;
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageLoader.displayImage(image, imageView, options);
            if (onPagerClickListener != null) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPagerClickListener.onPagerClicked(position);
                    }
                });
            }
            return imageView;
        }
    }

    public interface OnPagerClickListener {
        public void onPagerClicked(int position);
    }

    public OnPagerClickListener onPagerClickListener;

    public void setOnPagerClickListener(OnPagerClickListener onPagerClickListener) {
        this.onPagerClickListener = onPagerClickListener;
    }

    public AutoScrollViewPager getPager() {
        return pager;
    }
}
