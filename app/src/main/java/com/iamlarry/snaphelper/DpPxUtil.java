package com.iamlarry.snaphelper;

import android.content.Context;

public class DpPxUtil {
    private static float sDensity = 0f;// 根据GOOGLE IO文档中提到的，对density作缓存能起来省电的作用

    private static float getDensity(Context context) {
        if (sDensity == 0f)
            sDensity = context.getResources().getDisplayMetrics().density;
        return sDensity;
    }

    /**
     * dip 2 px
     */
    public static int dp2px(Context context, float dipValue) {
//        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics())+0.5f);
        return (int) (dipValue * getDensity(context) + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        return dp2px(context, dipValue);
    }

    /**
     * px 2 dip
     */
    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }

}
