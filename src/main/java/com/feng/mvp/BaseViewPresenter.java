package com.feng.mvp;

/**
 * Created by wj148202 on 2017/10/30.
 */

public class BaseViewPresenter<V extends BaseView> {
    
    protected V mView;

    public BaseViewPresenter(V view) {
        if (view == null) {
            throw new NullPointerException("View cannot be null!");
        }
        this.mView = view;
    }
}
