package com.oldfeel.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.oldfeel.utils.R;

/**
 * Created by weidingqiang on 15/9/23.
 */
public class DialogManager {

    private Dialog dialog;

    private ImageView mIcon;
    private ImageView mVoice;
    private TextView mLable;

    private Context mContext;

    public DialogManager(Context context) {
        mContext = context;
    }

    /**
     * 显示录音的对话框
     */
    public void showRecordingDialog() {
        dialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recorder, null);
        dialog.setContentView(view);
        mIcon = (ImageView) dialog.findViewById(R.id.recorder_dialog_icon);
        mVoice = (ImageView) dialog.findViewById(R.id.recorder_dialog_voice);
        mLable = (TextView) dialog.findViewById(R.id.tv_recorder_dialog_label);
        dialog.show();
    }

    public void recording() {
        if (dialog != null && dialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mLable.setText("手指上滑,取消发送");
        }
    }

    /**
     * 显示想取消的对话框
     */
    public void wantToCancel() {
        if (dialog != null && dialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mLable.setText("松开手指,取消发送");
        }
    }

    /**
     * 显示时间过短的对话框
     */
    public void tooShort() {
        if (dialog != null && dialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.voice_to_short);
            mLable.setText("录音时间过短");
        }
    }

    /**
     * 显示取消的对话框
     */
    public void dimissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 显示更新音量级别的对话框
     *
     * @param level 1-7
     */
    public void updateVoiceLevel(int level) {
        if (dialog != null && dialog.isShowing()) {
            if (level > 7) {
                level = 7;
            }
            int resID = mContext.getResources().getIdentifier("v" + level, "drawable", mContext.getPackageName());
            mVoice.setImageResource(resID);
        }
    }

}
