package com.feng.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.feng.library.R;

/**
 * Created by feng on 2017/9/13.
 */

public class StatusBarUtil {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColorKITKAT(Activity activity, int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int color = statusColor;
            ViewGroup contentView = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                contentView = (ViewGroup) activity.getWindow().getDecorView();
            } else {
                contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            }

            View statusBarView = getStatusBarColorKITKAT(activity);
            if (statusBarView == null) {
                int statusBarHeight = getStatusBarHeight(activity);
                statusBarView = new View(activity);
                statusBarView.setId(R.id.status_bar_color_kitkat);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
                statusBarView.setBackgroundColor(color);
                contentView.addView(statusBarView, lp);
            } else {
                statusBarView.setBackgroundColor(color);
            }
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static View getStatusBarColorKITKAT(Activity activity) {
        View view = null;
        if (activity != null) {
            view = activity.findViewById(R.id.status_bar_color_kitkat);
        }
        return view;
    }

}
