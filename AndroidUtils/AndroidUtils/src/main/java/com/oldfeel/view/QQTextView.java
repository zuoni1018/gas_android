package com.oldfeel.view;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.oldfeel.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oldfeel on 15/7/16.
 */
public class QQTextView extends TextView {
    public QQTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                initQQ();
            }
        });
        initQQ();
    }

    private void initQQ() {
        String text = getText().toString();
        if (text.length() < 5) { // QQ 号最少5位
            return;
        }
        final List<String> qqList = new ArrayList<>();
        String tempQQ = "";
        for (int i = 0; i < text.length() - 1; i++) {
            char c1 = text.charAt(i);
            char c2 = text.charAt(i + 1);
            if (Character.isDigit(c1) && Character.isDigit(c2)) {
                if (tempQQ.length() == 0) {
                    tempQQ += c1;
                }
                tempQQ += c2;
            } else {
                if (tempQQ.length() >= 5) {
                    qqList.add(tempQQ);
                }
                tempQQ = "";
            }
        }
        if (tempQQ.length() >= 5) {
            qqList.add(tempQQ);
        }
        for (int i = 0; i < qqList.size(); i++) {
            final String qq = qqList.get(i);
            ViewUtil.clickify(this, qq, new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("复制QQ号码到剪贴板？")
                            .setMessage(qq)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData cd = ClipData.newPlainText("qq", qq);
                                    cm.setPrimaryClip(cd);
                                }
                            }).setNegativeButton("取消", null).show();
                }
            });
        }
    }

    public QQTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QQTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

}
