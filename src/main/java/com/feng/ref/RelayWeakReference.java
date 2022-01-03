package com.feng.ref;

import java.lang.ref.WeakReference;

/**
 * Created by feng on 2017/12/18.
 */

public class RelayWeakReference<T> {

    private Callable<T> mGetResult;
    private WeakReference<T> mRealObj;

    public RelayWeakReference(T arg, Callable<T> onGetResult) {
        mRealObj = new WeakReference<T>(arg);
        mGetResult = onGetResult;
    }

    public T getObj() {
        T obj = mRealObj.get();
        if (obj != null) {
            return obj;
        }
        obj = mGetResult.call();
        if (obj == null) {
            throw new IllegalStateException("mGetResult.call() is null");
        }
        mRealObj = new WeakReference<T>(obj);
        return obj;
    }

    public interface Callable<V> {
        V call();
    }
}
