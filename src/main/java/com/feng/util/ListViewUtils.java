package com.feng.util;

import android.view.View;
import android.widget.ListView;

public class ListViewUtils {
	/**
	 * 无刷新重新绑定数据
	 * 
	 * @param listView
	 * @param runnable
	 */
	public static void bindDataNoRefresh(ListView listView, Runnable runnable) {
		int position = listView.getFirstVisiblePosition();
		View vv = listView.getChildAt(0);
		int tot = 0;
		if (vv != null) {
			tot = (int) vv.getScrollY();
		}
		runnable.run();
		listView.setSelectionFromTop(position, tot);
	}

}
