package com.zuoni.zuoni_common.widget;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

import com.zuoni.zuoni_common.callback.SimpleTextWatcher;

/**
 * Created by zangyi_shuai_ge on 2017/9/28
 * 4位加空格
 */

public class CardEditText extends android.support.v7.widget.AppCompatEditText {

    public CardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setInputType(InputType.TYPE_CLASS_NUMBER);
        this.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != 4 && i != 9 && i != 14 && s.charAt(i) == ' ') {
                        continue;
                    } else {
                        sb.append(s.charAt(i));
                        if ((sb.length() == 5 || sb.length() == 10 || sb.length() == 15) && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }

                if (!sb.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            CardEditText.this.setText(sb.subSequence(0, sb.length() - 1));
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    CardEditText.this.setText(sb.toString());
                    CardEditText.this.setSelection(index);
                }
            }
        });

    }
}
