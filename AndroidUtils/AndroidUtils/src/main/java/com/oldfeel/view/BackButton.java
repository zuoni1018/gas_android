package com.oldfeel.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by pyj on 15-12-19.
 */
public class BackButton extends ImageButton {

    public BackButton(Context context) {
        super(context);
        init();
    }

    public BackButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).onBackPressed();
            }
        });
    }
}
