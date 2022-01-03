package com.feng.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by feng on 2017/9/7.
 */

public class UIStyleCompat {

    /**
     * Drawable 着色
     *
     * @param context        上下文
     * @param res            资源id
     * @param colorStateList colorlist
     * @return drawable 被着
     */
    public static Drawable tintDrawable(@NonNull Context context, int res, ColorStateList colorStateList) {
        Drawable drawable = ContextCompat.getDrawable(context, res).mutate();
        int[][] states = new int[5][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_selected, android.R.attr.state_enabled};
        states[2] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[3] = new int[]{-android.R.attr.state_enabled};
        states[4] = new int[]{};
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(states[0], drawable);
        stateListDrawable.addState(states[1], drawable);
        stateListDrawable.addState(states[2], drawable);
        stateListDrawable.addState(states[3], drawable);
        stateListDrawable.addState(states[4], drawable);
        final Drawable.ConstantState constantState = stateListDrawable.getConstantState();
        drawable = DrawableCompat.wrap(constantState == null ? stateListDrawable : constantState.newDrawable().mutate()).mutate();
        DrawableCompat.setTintList(drawable, colorStateList);
        return drawable;
    }
}
