package com.feng.mvp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * 基础Fragment，生命周期关联了Presenter的相关生命周期
 *
 * @param <P> 相依赖的Presenter业务类
 */
public class BaseFragment<P extends BasePresenter> extends Fragment {

    public boolean onBackPress() {
        if (mPresenter != null) {
            return mPresenter.onBackPress();
        }
        return false;
    }

    protected P mPresenter;

    public void setPresenter(P presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPresenter != null) {
            mPresenter.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mPresenter != null) {
            mPresenter.onAttach(activity);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter != null) {
            mPresenter.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter != null) {
            mPresenter.onViewCreated(view, savedInstanceState);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mPresenter != null) {
            mPresenter.onActivityCreated(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPresenter != null) {
            mPresenter.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPresenter != null) {
            mPresenter.onStop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.onDestroyView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
            mPresenter.destroyUI();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mPresenter != null) {
            mPresenter.onDetach();
        }
    }

    /**
     * 获取当前fragment是否被显示
     *
     * @return
     */
    public boolean isShowing() {
        try {
            return getView() != null && getView().isShown();
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}
