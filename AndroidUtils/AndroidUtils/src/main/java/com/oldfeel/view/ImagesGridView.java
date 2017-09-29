package com.oldfeel.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.oldfeel.base.BaseBaseAdapter;
import com.oldfeel.base.LookBigImage;
import com.oldfeel.utils.R;

import java.util.Arrays;

/**
 * Created by oldfeel on 4/16/15.
 */
public class ImagesGridView extends GridView {
    private String[] allImages;
    private int imageHeight;
    private int imageWidth;

    public ImagesGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImagesGridView(Context context) {
        super(context);
        init();
    }

    public ImagesGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), LookBigImage.class);
                if (allImages != null) {
                    intent.putExtra("images", allImages);
                } else {
                    intent.putExtra("images", adapter.getList().toArray(new String[adapter.getCount()]));
                }
                intent.putExtra("position", position);
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    MyAdapter adapter;

    public void setImages(String[] images) {
        if (adapter == null) {
            adapter = new MyAdapter(String.class);
            setAdapter(adapter);
        }
        adapter.clear();
        if (images != null && images.length > 0) {
            adapter.addAll(Arrays.asList(images));
        }
    }

    public void setImages(String[] images, int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        setImages(images);
    }

    public void setAllImages(String[] allImages) {
        this.allImages = allImages;
    }

    class MyAdapter extends BaseBaseAdapter<String> {

        public MyAdapter(Class<?> itemClass) {
            super(itemClass);
        }

        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent, final int position) {
            ImageView imageView = (ImageView) inflater.inflate(R.layout.sinlge_image_view, parent, false);
            ViewGroup.LayoutParams lp = imageView.getLayoutParams();
            lp.width = imageWidth;
            lp.height = imageHeight;
            imageLoader.displayImage(getItem(position), imageView, options);
            return imageView;
        }
    }
}