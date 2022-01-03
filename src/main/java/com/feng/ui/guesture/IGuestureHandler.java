package com.feng.ui.guesture;

import android.app.Activity;
import android.view.View;

/**
 * Created by feng on 2017/9/25.
 */
public interface IGuestureHandler {
    void onActivityCreate(Activity act);

    void onActivityPostCreate(Activity act);

    View findViewById(Activity act, int id);

    void enableGuesture(boolean enable);

    void connectStatusBar(View statusbar);
}
