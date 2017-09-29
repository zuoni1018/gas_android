package com.oldfeel;

import com.oldfeel.utils.StringUtil;

/**
 * Created by oldfeel on 16-11-16.
 */

public class Test {
    public static void main(String[] args) {
        System.out.println(StringUtil.getRandom(32));
        log(Double.valueOf(".1"));
    }

    public static void log(Object obj) {
        System.out.println(obj);
    }
}
