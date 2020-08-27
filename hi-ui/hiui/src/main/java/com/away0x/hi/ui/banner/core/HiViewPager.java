package com.away0x.hi.ui.banner.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

/**= 实现了自动翻页的ViewPager */
public class HiViewPager extends ViewPager {

    private int mIntervalTime;
    /** 是否开启自动轮播 */
    private boolean mAutoPlay = true;
    private boolean isLayout; // 是否 layout 过
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            next(); // 切换到下一个
            mHandler.postDelayed(this, mIntervalTime); // 延时一定时间执行下一次
        }
    };

    public HiViewPager(@NonNull Context context) {
        super(context);
    }

    /** 开启/关闭自动播放 */
    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
        if (!mAutoPlay) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    /**
     * 设置页面停留时间
     *
     * @param intervalTime 停留时间单位毫秒
     */
    public void setIntervalTime(int intervalTime) {
        this.mIntervalTime = intervalTime;
    }

    /**
     * 设置ViewPager的滚动速度
     *
     * @param duration page切换的时间长度
     */
    public void setScrollDuration(int duration) {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            scrollerField.set(this, new HiBannerScroller(getContext(), duration));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 启动 timer
    public void start() {
        mHandler.removeCallbacksAndMessages(null);
        if (mAutoPlay) {
            mHandler.postDelayed(mRunnable, mIntervalTime);
        }
    }

    // 停止 timer
    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
    }

    /** 用户拖动 item 时，暂停自动播放 */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                start();
                break;
            default:
                stop();
                break;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        isLayout = true;
    }

    /**
     * fix 使用 RecyclerView + ViewPager bug https://blog.csdn.net/u011002668/article/details/72884893
     *
     * 问题: 在 RecyclerView 中使用 ViewPager 时，会出现两个常见的 bug:
     *   1. RecyclerView 滚动上去，直至 ViewPager 看不见，再滚动下来，ViewPager 下一次切换没有动画
     *   2. 当 ViewPager 滚动到一半的时候，RecyclerView 滚动上去，再滚动下来，ViewPager 会卡在一半
     *
     * 原因:
     *   ViewPager 里有个私有变量 mFirstLayout，它是表示是不是第一次显示布局，如果是 true，则使用无动画的方式显示当前 item，
     *   如果是 false，则使用动画方式显示当前 item (详见 ViewPager#setCurrentItemInternal 方法源码)
     *   当 ViewPager 滚动上去后，因为 RecyclerView 的回收机制，ViewPager 会走 onDetachFromWindow，当再次滚动下来时，ViewPager 会
     *   走 onAttachedToWindow，而问题就出在 onAttachedToWindow。所以我们可以重写这个方法，fix 掉这个 bug
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (isLayout && getAdapter() != null && getAdapter().getCount() > 0) {
            try {
                // 以下代码，fix 了使用 RecyclerView + ViewPager 的 bug
                Field mScroller = ViewPager.class.getDeclaredField("mFirstLayout");
                mScroller.setAccessible(true);
                mScroller.set(this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        start();
    }

    /** fix 使用 RecyclerView + ViewPager bug */
    @Override
    protected void onDetachedFromWindow() {
        if (((Activity) getContext()).isFinishing()) {
            super.onDetachedFromWindow();
        }
        stop();
    }

    /**
     * 设置下一个要显示的 item，并返回 item 的 pos
     *
     * @return 下一个要显示 item 的 pos
     */
    private int next() {
        int nextPosition = -1;

        if (getAdapter() == null || getAdapter().getCount() <= 1) {
            stop();
            return nextPosition;
        }

        nextPosition = getCurrentItem() + 1;
        // 下一个索引大于 adapter 的 view的 最大数量时重新开始
        if (nextPosition >= getAdapter().getCount()) {
            nextPosition = ((HiBannerAdapter) getAdapter()).getFirstItem();
        }

        setCurrentItem(nextPosition, true);
        return nextPosition;
    }

}
