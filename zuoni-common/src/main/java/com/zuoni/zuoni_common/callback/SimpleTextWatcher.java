package com.zuoni.zuoni_common.callback;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by zangyi_shuai_ge on 2017/9/28
 * 监听 EditText 输入变化
 * 比原来少实现2个方法
 */

public abstract class SimpleTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
