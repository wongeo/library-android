package com.feng.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class DensityUtils {
    /**
     * dp 转换为像素
     *
     * @param context 上下文
     * @param dp      dp 值
     * @return 像素值
     */
    public static int dpToPx(Context context, float dp) {
        Resources resources = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }
}
