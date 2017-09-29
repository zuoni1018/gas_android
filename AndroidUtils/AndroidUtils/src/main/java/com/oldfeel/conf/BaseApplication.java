package com.oldfeel.conf;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.oldfeel.base.BaseActivity;
import com.oldfeel.utils.R;

import java.util.ArrayList;

/**
 * 监听异常信息的application
 *
 * @author oldfeel
 *         <p/>
 *         Created on: 2014-2-8
 */
public abstract class BaseApplication extends Application {
    private ArrayList<BaseActivity> list = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader(getApplicationContext());
        initBaseConstant();
    }

    public static void initImageLoader(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(500 * 1024 * 1024); // 500 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        config.defaultDisplayImageOptions(defaultOptions);

        ImageLoader.getInstance().init(config.build());
        L.writeLogs(false);
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(BaseActivity a) {
        list.remove(a);
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public void addActivity(BaseActivity a) {
        list.add(a);
    }

    /**
     * @return 当前显示的activity
     */
    public BaseActivity getLastActivity() {
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void exit() {
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
        // 杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void finishOther(final BaseActivity thisActivity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    Activity activity = list.get(i);
                    if (activity != thisActivity && activity != null) {
                        activity.finish();
                    }
                }
                list.clear();
                list.add(thisActivity);
            }
        }, getResources().getInteger(R.integer.anim_duration) + 100);
    }

    public abstract void initBaseConstant();

    public void finish(final Class<?> activityClass) {
        finish(activityClass, getResources().getInteger(R.integer.anim_duration) + 100);
    }

    public void finish(final Class<?> activityClass, int delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    Activity activity = list.get(i);
                    if (activity.getClass() == activityClass) {
                        activity.finish();
                    }
                    list.remove(activity);
                    return;
                }
            }
        }, delayMillis);
    }

    public Activity getActivity(Class<? extends Activity> activityClass) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getClass() == activityClass) {
                return list.get(i);
            }
        }
        return null;
    }

    public boolean isExist(Class<?> className) {
        for (Activity activity : list) {
            if (activity.getClass().getName().equals(className.getName())) {
                return true;
            }
        }
        return false;
    }
}