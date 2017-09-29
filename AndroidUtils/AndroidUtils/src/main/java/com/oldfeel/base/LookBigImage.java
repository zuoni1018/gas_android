package com.oldfeel.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.oldfeel.conf.BaseConstant;
import com.oldfeel.utils.DialogUtil;
import com.oldfeel.utils.ImageUtil;
import com.oldfeel.utils.LogUtil;
import com.oldfeel.utils.R;
import com.oldfeel.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by oldfeel on 4/2/15.
 */
public class LookBigImage extends BaseActivity {
    TextView tvIndicator;
    ViewPager pager;
    BasePagerAdapter adapter;
    String[] images;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.look_big_image);
        showBack(true);
        setTitle("查看大图");
        pager = (ViewPager) findViewById(R.id.look_big_image_pager);
        tvIndicator = (TextView) findViewById(R.id.look_big_image_indicator);
        adapter = new BasePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        images = getIntent().getStringArrayExtra("images");
        position = getIntent().getIntExtra("position", 0);

        for (int i = 0; i < images.length; i++) {
            adapter.add(BigImageFragment.newInstance(images[i]));
        }
        pager.setCurrentItem(position);
        tvIndicator.setText((position + 1) + "/" + images.length);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvIndicator.setText((position + 1) + "/" + images.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onPermissionsGranted(List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(List<String> perms) {

    }

    @Override
    public void onRequestDenied() {

    }

    public static class BigImageFragment extends BaseFragment {
        private String imagePath;
        private ImageView imageView;
        private Button btnSave;

        public static BigImageFragment newInstance(String imagePath) {
            BigImageFragment fragment = new BigImageFragment();
            fragment.imagePath = imagePath;
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.item_look_big_image, container, false);
            imageView = (ImageView) view.findViewById(R.id.item_look_big_image);
            btnSave = getView(view, R.id.item_look_big_image_save);
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            imageLoader.displayImage(imagePath, imageView, options);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageLoader.loadImage(imagePath, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            DialogUtil.getInstance().showPd(getActivity(), "正在保存图片");
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            LogUtil.showLog("loading complete ");
                            DialogUtil.getInstance().cancelPd();
                            File dir = new File(BaseConstant.SDCARD_PATH + "Download");
                            if (!dir.exists()) {
                                dir.mkdir();
                            }
                            String fileName = BaseConstant.SDCARD_PATH + "Download/" + StringUtil.getFileName(imageUri);
                            try {
                                ImageUtil.saveImageToSD(getActivity(), fileName, loadedImage);
                                DialogUtil.getInstance().showToast(getActivity(), "保存成功," + fileName);
                            } catch (IOException e) {
                                e.printStackTrace();
                                DialogUtil.getInstance().showToast(getActivity(), "保存失败");
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                }
            });
        }
    }
}
