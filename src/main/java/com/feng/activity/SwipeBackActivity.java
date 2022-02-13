package com.feng.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.feng.ui.guesture.GuestureHandler;
import com.feng.ui.guesture.IGuestureHandler;

/**
 * Created by feng on 2017/9/25.
 */

public class SwipeBackActivity extends AppCompatActivity {
    protected IGuestureHandler mGuestureHandler;// 界面手势回退处理器


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isSupportGuesture()) {
            mGuestureHandler = new GuestureHandler();
            mGuestureHandler.onActivityCreate(this);
        }
    }

    private boolean isSupportGuesture() {
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mGuestureHandler != null) {
            mGuestureHandler.onActivityPostCreate(this);
        }
    }

    @Override
    public View findViewById(int id) {
        if (mGuestureHandler != null) {
            View view = super.findViewById(id);
            if (view == null) {
                return mGuestureHandler.findViewById(this, id);
            }
            return view;
        } else {
            return super.findViewById(id);
        }
    }

    /**
     * 设置是否开启手势回退，默认开启
     *
     * @param enable
     */
    protected void setGuestureEnable(boolean enable) {
        if (mGuestureHandler != null) {
            mGuestureHandler.enableGuesture(enable);
        }
    }
}
