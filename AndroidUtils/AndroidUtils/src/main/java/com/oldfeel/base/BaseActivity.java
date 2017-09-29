package com.oldfeel.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oldfeel.conf.BaseApplication;
import com.oldfeel.utils.DialogUtil;
import com.oldfeel.utils.ETUtil;
import com.oldfeel.utils.LogUtil;
import com.oldfeel.utils.R;
import com.oldfeel.utils.StringUtil;
import com.oldfeel.utils.ViewHelper;

import easypermissions.EasyPermissions;

/**
 * activity基类
 *
 * @author oldfeel
 *         <p>
 *         Created on: 2014-1-10
 */
public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private BaseApplication baseApplication;
    protected DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected ViewHelper viewHelper;
    protected Toolbar toolbar;
    protected TextView toolbarTitle;
    protected int defaultImage = R.drawable.default_image;
    private boolean isShowing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseApplication = (BaseApplication) getApplication();
        baseApplication.addActivity(this);
        viewHelper = new ViewHelper(this);
        if (defaultImage > 0) {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(defaultImage)
                    .showImageForEmptyUri(defaultImage).showImageOnFail(defaultImage)
                    .cacheInMemory(true).cacheOnDisk(true).build();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null && getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int maxWidth = toolbar.getWidth();
                        int titleWidth = toolbarTitle.getWidth();
                        int iconWidth = maxWidth - titleWidth;

                        if (iconWidth > 0) {
                            int width = maxWidth - iconWidth * 2;
                            toolbarTitle.setMinimumWidth(width);
                            toolbarTitle.getLayoutParams().width = width;
                        }
                    }
                }, 0);
            }
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public TextView getToolbarTitle() {
        return toolbarTitle;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        baseApplication.removeActivity(this);
        DialogUtil.getInstance().cancelPd();
        DialogUtil.getInstance().cancelToast();
        super.onDestroy();
    }

    /**
     * 显示toast提示
     *
     * @param text
     */
    public void showToast(String text) {
        DialogUtil.getInstance().showToast(this, text);
    }

    /**
     * 显示一个简易的dialog
     *
     * @param text
     */
    public void showSimpleDialog(String text) {
        DialogUtil.getInstance().showSimpleDialog(this, text);
    }

    /**
     * 显示一个简易的dialog
     *
     * @param text
     */
    public void showSimpleDialog(String text, DialogInterface.OnClickListener clickListener) {
        DialogUtil.getInstance().showSimpleDialog(this, text, clickListener);
    }

    /**
     * 打开指定activity
     *
     * @param targetClass
     */
    public void openActivity(Class<?> targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
    }

    public void openActivity(Class<?> targetClass, int requestCode) {
        Intent intent = new Intent(this, targetClass);
        startActivityForResult(intent, requestCode);
    }

    public void openActivity(Class<?> targetClass, BaseItem item) {
        openActivity(targetClass, item, 0);
    }

    public void openActivity(Class<?> targetClass, BaseItem item, int requestCode) {
        Intent intent = new Intent(this, targetClass);
        intent.putExtra("item", item);
        if (requestCode == 0)
            startActivity(intent);
        else
            startActivityForResult(intent, requestCode);
    }

    public <T extends BaseItem> T getItem() {
        return (T) getIntent().getSerializableExtra("item");
    }

    public <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    public <T extends View> T getView(View view, int id) {
        return (T) view.findViewById(id);
    }

    public String getString(EditText et) {
        return ETUtil.getString(et);
    }

    public int getInt(EditText et) {
        return ETUtil.getInt(et);
    }

    public String getString(TextView tv) {
        return tv.getText().toString().trim();
    }

    public String getString(Spinner sp) {
        return sp.getSelectedItem().toString();
    }

    public int getInt(Spinner sp) {
        return Integer.valueOf(sp.getSelectedItem().toString());
    }

    public int getSelection(int resId, String string) {
        if (StringUtil.isEmpty(string))
            return 0;
        String[] strings = getResources().getStringArray(resId);
        for (int i = 0; i < strings.length; i++) {
            if (string.equals(strings[i])) {
                return i;
            }
        }
        return 0;
    }

    public boolean isEmpty(EditText... ets) {
        return ETUtil.isEmpty(ets);
    }

    public void cancelPd() {
        DialogUtil.getInstance().cancelPd();
    }

    @Override
    protected void onResume() {
        isShowing = true;
        super.onResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        } else if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @Override
    protected void onPause() {
        isShowing = false;
        super.onPause();
    }

    boolean isAnim = true;

    public void setAnim(boolean anim) {
        isAnim = anim;
    }

    public boolean isAnim() {
        return isAnim;
    }

    /**
     * doubleClickExit 可以实现,再点一次退出应用
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isAnim())
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (isAnim())
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (isAnim())
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showTitle(String title) {
        setTitle(title);
        showBack(true);
    }

    public void showBack(boolean isShowBack) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(isShowBack);
        }
    }

    public void setSingleFrame(Fragment fragment) {
        if (findViewById(R.id.content_frame) != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void finishDelayed(long time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, time);
    }

    public void showLog(String log) {
        LogUtil.showLog(log);
    }

    public Activity getActivity(Class<? extends Activity> activityClass) {
        return baseApplication.getActivity(activityClass);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    long lastClickTime;

    public void doubleClickExit() {
        long now = System.currentTimeMillis();
        if (now - lastClickTime < 2000) {
            super.onBackPressed();
        } else {
            lastClickTime = now;
            showToast("再点一次退出应用");
        }
    }
}
