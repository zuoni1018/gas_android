package com.oldfeel.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oldfeel.utils.ETUtil;
import com.oldfeel.utils.LogUtil;
import com.oldfeel.utils.R;

/**
 * fragment基类
 *
 * @author oldfeel
 *         <p/>
 *         Created on: 2014-1-20
 */
public class BaseFragment extends Fragment {

    protected DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected int defaultImageId = R.drawable.default_image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (defaultImageId > 0) {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(defaultImageId)
                    .showImageForEmptyUri(defaultImageId).showImageOnFail(defaultImageId)
                    .cacheInMemory(true).cacheOnDisk(true).build();
        }
    }

    protected void showToast(String text) {
        ((BaseActivity) getActivity()).showToast(text);
    }

    protected void showSimpleDialog(String text) {
        ((BaseActivity) getActivity()).showSimpleDialog(text);
    }

    protected void openActivity(Class<?> className) {
        Intent intent = new Intent(getActivity(), className);
        startActivity(intent);
    }

    protected void openActivity(Class<?> className, BaseItem item) {
        Intent intent = new Intent(getActivity(), className);
        intent.putExtra("item", item);
        startActivity(intent);
    }

    public String getString(EditText et) {
        return ETUtil.getString(et);
    }

    public String getString(Spinner spinner) {
        return ETUtil.getString(spinner);
    }

    public int getInt(EditText et) {
        return ETUtil.getInt(et);
    }

    public <T extends View> T getView(View view, int id) {
        return (T) view.findViewById(id);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (getActivity() instanceof BaseActivity && ((BaseActivity) getActivity()).isAnim())
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (getActivity() instanceof BaseActivity && ((BaseActivity) getActivity()).isAnim())
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
    }

    public void onSelected(boolean isSelect) {

    }

    public void showLog(String log) {
        LogUtil.showLog(log);
    }

    /**
     * 只能在初始化fragment时调用
     *
     * @param title
     */
    public void setTitle(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        setArguments(bundle);
    }
}
