package com.oldfeel.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.Button;

import com.oldfeel.utils.DialogUtil;

/**
 * Created by oldfeel on 11/22/15.
 */
public class VCodeGetButton extends Button {
    public VCodeGetButton(Context context) {
        super(context);
    }

    public VCodeGetButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VCodeGetButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startGet() {
        time = 60;
        handler.post(task);
    }

    int time = 60;
    Handler handler = new Handler();
    Runnable task = new Runnable() {
        @Override
        public void run() {
            if (time <= 0) {
                setText("获取验证码");
                setEnabled(true);
                handler.removeCallbacks(task);
            } else {
                time--;
                setEnabled(false);
                setText(time + "秒后重发");
                handler.postDelayed(task, 1000);
            }
        }
    };

    public void onFail() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setText("获取验证码");
                setEnabled(true);
                handler.removeCallbacks(task);
                DialogUtil.getInstance().showToast(getContext(), "验证短信失败");
            }
        });
        DialogUtil.getInstance().cancelPd();
    }

    public void stopTime() {
        handler.removeCallbacks(task);
    }

    public void onSuccess() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                DialogUtil.getInstance().showToast(getContext(), "获取验证码成功");
            }
        });
        DialogUtil.getInstance().cancelPd();
    }
}
