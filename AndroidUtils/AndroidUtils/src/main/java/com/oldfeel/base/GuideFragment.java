package com.oldfeel.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.oldfeel.utils.R;

import butterknife.ButterKnife;
import viewpagerindicator.CirclePageIndicator;

/**
 * Created by oldfeel on 15-9-27.
 */
public class GuideFragment extends BaseFragment {
    ViewPager pager;
    CirclePageIndicator pageIndicator;

    private BasePagerAdapter adapter;
    private int[] guideImageIds;

    private int guideId, itemGuideId;

    public static GuideFragment newInstance(int guideId, int itemGuideId, int... guideImageIds) {
        GuideFragment fragment = new GuideFragment();
        fragment.guideId = guideId;
        fragment.itemGuideId = itemGuideId;
        fragment.guideImageIds = guideImageIds;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (guideId == 0) {
            guideId = R.layout.base_guide;
        }
        View view = inflater.inflate(guideId, container, false);
        pager = (ViewPager) view.findViewById(R.id.pager);
        pageIndicator = (CirclePageIndicator) view.findViewById(R.id.view_pager_indicator);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new BasePagerAdapter(getChildFragmentManager());
        if (guideImageIds != null && guideImageIds.length > 0) {
            putImagesToAdapter();
        }
        pager.setAdapter(adapter);
        pageIndicator.setViewPager(pager);
    }

    private void putImagesToAdapter() {
        adapter.clear();
        for (int i = 0; i < guideImageIds.length; i++) {
            ImageFragment fragment = ImageFragment.newInstance(onGuideClickListener, guideImageIds[i], i, itemGuideId);
            adapter.add(fragment);
        }
    }

    public static class ImageFragment extends BaseFragment {
        private OnGuideClickListener onPagerClickListener;
        private int imageId;
        private int position;
        private int itemGuideId;

        public static ImageFragment newInstance(OnGuideClickListener onPagerClickListener, int imageId, int position, int itemGuideId) {
            ImageFragment fragment = new ImageFragment();
            fragment.onPagerClickListener = onPagerClickListener;
            fragment.imageId = imageId;
            fragment.position = position;
            fragment.itemGuideId = itemGuideId;
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (itemGuideId == 0) {
                itemGuideId = R.layout.base_item_guide;
            }
            View view = inflater.inflate(itemGuideId, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            Button jump = (Button) view.findViewById(R.id.jump);
            imageView.setImageResource(imageId);
            if (onPagerClickListener != null) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPagerClickListener.onPagerClicked(position);
                    }
                });
                jump.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPagerClickListener.onJump();
                    }
                });
            }
            return view;
        }
    }

    public interface OnGuideClickListener {
        public void onPagerClicked(int position);

        public void onJump();
    }

    public OnGuideClickListener onGuideClickListener;

    public void setOnGuideClickListener(OnGuideClickListener onPagerClickListener) {
        this.onGuideClickListener = onPagerClickListener;
    }

    /**
     * 如果是第一次打开 app,启动向导页,否则直接进入 app
     *
     * @param activity
     * @return 是否需要显示向导
     */
    public static boolean isNeedGuide(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences("guide", Context.MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("is_first", true);
        if (isFirst) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("is_first", false);
            editor.apply();
        }
        return isFirst;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
