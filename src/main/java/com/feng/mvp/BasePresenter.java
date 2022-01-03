package com.feng.mvp;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * <Pre>
 * mvp Presenter层基类
 * 使用{@link #getView()#mView} 时应当调用检查方法{@link #isViewAttached()}检查view是否还依附着
 * </Pre>
 *
 * @author adison
 *         created at 16/3/16 下午9:20
 */
public abstract class BasePresenter<V extends BaseFragment> {

    protected V mView;

    public BasePresenter(V view) {
        if (view == null) {
            throw new NullPointerException("View cannot be null!");
        }
        this.mView = view;
        mView.setPresenter(this);
    }

    /**
     * 销毁依附view
     */
    public final void destroyUI() {
        mView = null;
    }

    public V getView() {
        return mView;
    }

    /**
     * 该方法在 {@link Fragment#onSaveInstanceState(Bundle)}调用
     */
    public void onSaveInstanceState(Bundle outState) {
    }

    /**
     * 该方法在 {@link Fragment#onAttach(Activity)}调用
     *
     * @param activity The activity the fragment is attached to
     */
    public void onAttach(Activity activity) {
    }

    /**
     * 该方法在 {@link Fragment#onCreate(Bundle)}调用
     *
     * @param saved The bundle
     */
    public void onCreate(Bundle saved) {

    }

    /**
     * 该方法在 {@link Fragment#onViewCreated(View, Bundle)}调用
     *
     * @param view               The inflated view
     * @param savedInstanceState the bundle with the viewstate
     */
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }


    /**
     * 该方法在 {@link Fragment#onActivityCreated(Bundle)}调用
     *
     * @param savedInstanceState The saved bundle
     */
    public void onActivityCreated(Bundle savedInstanceState) {

    }

    /**
     * 该方法在 { {@link Fragment#onStart()}调用
     */
    public void onStart() {
    }

    /**
     * 该方法在 {@link Fragment#onResume()}调用
     */
    public void onResume() {
    }

    /**
     * 该方法在 {@link Fragment#onPause()}调用
     */
    public void onPause() {
    }

    /**
     * 该方法在  {@link Fragment#onStop()}调用
     */
    public void onStop() {
    }

    /**
     * 该方法在{@link Fragment#onDestroyView()}调用
     */
    public void onDestroyView() {
    }


    /**
     * 该方法在 {@link Fragment#onDestroy()}调用
     */
    public void onDestroy() {
    }


    /**
     * 该方法在{@link Fragment#onDetach()}调用
     */
    public void onDetach() {
    }

    public boolean onBackPress() {
        return false;
    }

    public boolean isViewAttached() {
        if (mView == null) {
            return false;
        } else {
            Fragment fragment = mView;
            return fragment.getActivity() != null;
        }
    }
}
