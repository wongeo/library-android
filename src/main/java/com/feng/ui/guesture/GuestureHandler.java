package com.feng.ui.guesture;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import java.lang.reflect.Method;

/**
 * 界面回退手势处理器
 */

public class GuestureHandler implements IGuestureHandler {
    private final static int EDGE_TRACING = GuestureLayout.EDGE_LEFT;       // 跟踪从左边缘向右滑手势

    private GuestureLayout mGuestureLayout;

    private Activity mActivity;


    @Override
    public void onActivityCreate(Activity activity) {
        if (activity != null) {
            mActivity = activity;
            activity.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            activity.getWindow().getDecorView().setBackgroundDrawable(null);
            mGuestureLayout = new GuestureLayout(activity.getApplication());
            mGuestureLayout.addGuestureListener(new GuestureLayout.GuestListener() {
                @Override
                public void onScrollStateChange(int state, float scrollPercent) {

                }

                @Override
                public void onEdgeTouch(int edgeFlag) {
                    convertActivityToTranslucent(mActivity);
                }

                @Override
                public void onScrollOverThreshold() {

                }
            });
            enableEdgeTracking(EDGE_TRACING);
        }
    }

    @Override
    public void onActivityPostCreate(Activity activity) {
        if (mGuestureLayout != null) {
            mGuestureLayout.attachToActivity(activity);
        }
    }

    @Override
    public View findViewById(Activity activity, int id) {
        if (mGuestureLayout != null) {
            return mGuestureLayout.findViewById(id);
        } else {
            return null;
        }
    }

    @Override
    public void enableGuesture(boolean enable) {
        GuestureLayout layout = getGuestureLayout();
        if (layout != null) {
            layout.setEnableGesture(enable);
        }
    }

    @Override
    public void connectStatusBar(View statusbar) {
        if (mGuestureLayout != null) {
            mGuestureLayout.setStatusBarView(statusbar);
        }
    }

    private void enableEdgeTracking(int edgeFlag) {
        GuestureLayout layout = getGuestureLayout();
        if (layout != null) {
            layout.setEdgeTrackingEnabled(edgeFlag);
        }
    }

    private GuestureLayout getGuestureLayout() {
        return mGuestureLayout;
    }

    /**
     * 针对4.4优化，将不透明的activity转换成透明的
     */
    public void convertActivityToTranslucent(Activity activity) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(activity, new Object[]{
                    null
            });
        } catch (Throwable t) {
        }
    }
}
