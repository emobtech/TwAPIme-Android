package com.twapime.app.widget;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @author ernandes@gmail.com
 */
public class LoadingScrollListener implements OnScrollListener {
	/**
     * 
     */
	private int visibleThreshold;

	/**
     * 
     */
	private int currentPage;

	/**
     * 
     */
	private int previousTotal;

	/**
     * 
     */
	private boolean loading;

	/**
     * 
     */
	private Runnable task;

	/**
	 * @param visibleThreshold
	 * @param task
	 */
	public LoadingScrollListener(int visibleThreshold, Runnable task) {
		this.visibleThreshold = visibleThreshold;
		this.task = task;
		loading = true;
	}

	/**
	 * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
		int visibleItemCount, int totalItemCount) {
		if (loading) {
			if (totalItemCount > previousTotal) {
				loading = false;
				previousTotal = totalItemCount;
				currentPage++;
			}
		}
		if (!loading
				&& (totalItemCount - visibleItemCount)
					<= (firstVisibleItem + visibleThreshold)) {
			new Thread(task).start();
			loading = true;
		}
	}

	/**
	 * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}