package com.feng.ref;

import java.lang.ref.SoftReference;
import java.util.concurrent.Callable;

public class RelaySoftReference<T> {
	
	private Callable<T> onGetResult;
	private SoftReference<T> _realObj;

	public RelaySoftReference(T arg, Callable<T> onGetResult) {
		_realObj = new SoftReference<T>(arg);
		this.onGetResult = onGetResult;
	}

	public T getObj() {
		T obj = _realObj.get();
		if (obj != null) {
			return obj;
		}
		try {
			obj = onGetResult.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		_realObj = new SoftReference<T>(obj);
		return obj;
	}
}
