package com.away0x.hi.ui.refresh;

public interface IHiRefresh {

    /**
     * 刷新时是否禁止滚动
     *
     * @param disableRefreshScroll 否禁止滚动
     */
    void setDisableRefreshScroll(boolean disableRefreshScroll);

    /**
     * 刷新完成
     */
    void refreshFinished();

    /**
     * 设置下拉刷新的监听器
     *
     * @param hiRefreshListener 刷新的监听器
     */
    void setRefreshListener(IHiRefresh.HiRefreshListener hiRefreshListener);

    /**
     * 设置下拉刷新的视图
     *
     * @param hiOverView 下拉刷新的视图
     */
    void setRefreshOverView(HiOverView hiOverView);

    /**
     * 下拉刷新监听器
     */
    interface HiRefreshListener {

        void onRefresh();

        boolean enableRefresh();
    }

}
