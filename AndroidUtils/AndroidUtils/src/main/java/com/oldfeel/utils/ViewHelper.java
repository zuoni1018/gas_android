package com.oldfeel.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oldfeel.base.BaseBaseAdapter;

public class ViewHelper {
    public View root;
    public View view;
    private Activity act;

    public ViewHelper(View view) {
        this.root = view;
        this.view = view;
    }

    public ViewHelper(Activity activity) {
        this.act = activity;
    }

    public ViewHelper id(int id) {
        this.view = findView(id);
        return this;
    }

    private View findView(int id) {
        View result = null;
        if (root != null) {
            result = root.findViewById(id);
        } else if (act != null) {
            result = act.findViewById(id);
        }
        return result;
    }

    public ViewHelper text(CharSequence text) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(text);
        }
        return this;
    }

    public ViewHelper clicked(View.OnClickListener listener) {
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return this;
    }

    public ViewHelper image(String uri) {
        return image(uri, -1);
    }

    public ViewHelper image(String uri, int defaultImageId) {
        if (defaultImageId == -1) {
            defaultImageId = R.drawable.default_image;
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(defaultImageId).showImageOnFail(defaultImageId)
                .cacheInMemory(true).cacheOnDisk(true).build();
        if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(uri, iv, options);
        }
        return this;
    }

    public ViewHelper rating(float rating) {
        if (view instanceof RatingBar) {
            RatingBar rb = (RatingBar) view;
            rb.setRating(rating);
        }
        return this;
    }

    public ViewHelper rgCheckedChange(RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
        if (view instanceof RadioGroup) {
            RadioGroup rg = (RadioGroup) view;
            rg.setOnCheckedChangeListener(onCheckedChangeListener);
        }
        return this;
    }

    public boolean isChecked() {
        if (view instanceof RadioButton) {
            RadioButton rb = (RadioButton) view;
            return rb.isChecked();
        }
        return false;
    }

    public void rbChecked(boolean isChecked) {
        if (view instanceof RadioButton) {
            RadioButton rb = (RadioButton) view;
            rb.setChecked(isChecked);
        }
    }

    public void cbChecked(boolean isChecked) {
        if (view instanceof CheckBox) {
            CheckBox cb = (CheckBox) view;
            cb.setChecked(isChecked);
        }
    }

    public void hide() {
        view.setVisibility(View.GONE);
    }

    public void show() {
        view.setVisibility(View.VISIBLE);
    }

    public void addView(int id) {
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            View temp = LayoutInflater.from(view.getContext()).inflate(id, parent, false);
            parent.addView(temp);
        }
    }

    public void imageResource(int id) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageResource(id);
        }
    }

    public void parentClick(View.OnClickListener onClickListener) {
        ((View) view.getParent()).setOnClickListener(onClickListener);
    }

    public void cbCheckedChange(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        if (view instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) view;
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }

    public void textColor(int textColor) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTextColor(textColor);
        }
    }

    public void adapter(BaseBaseAdapter<?> adapter, AdapterView.OnItemClickListener onItemClickListener) {
        if (view instanceof ListView) {
            ListView listView = (ListView) view;
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(onItemClickListener);
        }

    }

    public void selected(boolean isSelected) {
        view.setSelected(isSelected);
    }

    /**
     * VISIBLE/INVISIBLE
     *
     * @param isShow
     */
    public void isShow(boolean isShow) {
        if (isShow) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public ViewHelper setDrawableLeft(Drawable drawable) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv.setCompoundDrawables(drawable, null, null, null);
            tv.setCompoundDrawablePadding(20);
        }
        return this;
    }

    public ViewHelper enable(boolean enable) {
        view.setEnabled(enable);
        return this;
    }
}
