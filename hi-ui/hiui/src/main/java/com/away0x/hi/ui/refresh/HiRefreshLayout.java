package com.away0x.hi.ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.away0x.hi.library.log.HiLog;

/**
 * 下拉刷新的视图容器 View
 */
public class HiRefreshLayout extends FrameLayout implements IHiRefresh {

    private static final String TAG = HiRefreshLayout.class.getSimpleName();
    private HiOverView.HiRefreshState mState;
    private GestureDetector mGestureDetector;
    private AutoScroller mAutoScroller;
    private IHiRefresh.HiRefreshListener mHiRefreshListener;
    protected HiOverView mHiOverView; // 下拉刷新的头部 view
    private int mLastY; // 最后下拉刷新 y 坐标
    // 刷新时是否禁止滚动
    private boolean disableRefreshScroll;

    public HiRefreshLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public HiRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HiRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), hiGestureDetector);
        mAutoScroller = new AutoScroller();
    }

    @Override
    public void setDisableRefreshScroll(boolean disableRefreshScroll) {
        this.disableRefreshScroll = disableRefreshScroll;
    }

    @Override
    public void refreshFinished() {
        final View head = getChildAt(0);
        HiLog.i(this.getClass().getSimpleName(), "refreshFinished head-bottom:" + head.getBottom());
        mHiOverView.onFinish();
        mHiOverView.setState(HiOverView.HiRefreshState.STATE_INIT);
        final int bottom = head.getBottom();
        // 如没在最初的位置，需要恢复到最初的位置
        if (bottom > 0) {
            recover(bottom);
        }
        mState = HiOverView.HiRefreshState.STATE_INIT;
    }

    @Override
    public void setRefreshListener(HiRefreshListener hiRefreshListener) {
        mHiRefreshListener = hiRefreshListener;
    }

    @Override
    public void setRefreshOverView(HiOverView hiOverView) {
        if (this.mHiOverView != null) {
            removeView(this.mHiOverView);
        }

        this.mHiOverView = hiOverView;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        // 添加到第 0 个位置
        addView(mHiOverView, 0, params);
    }

    /**
     * 事件分发处理
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mAutoScroller.isFinished()) {
            return false;
        }

        View head = getChildAt(0);
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL
                || ev.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK) { // 松开手
            if (head.getBottom() > 0) {
                if (mState != HiOverView.HiRefreshState.STATE_REFRESH) { // 非正在刷新
                    recover(head.getBottom());
                    return false;
                }
            }
            mLastY = 0;
        }

        boolean consumed = mGestureDetector.onTouchEvent(ev);
        HiLog.i(TAG, "gesture consumed：" + consumed);
        if ((consumed || (mState != HiOverView.HiRefreshState.STATE_INIT && mState != HiOverView.HiRefreshState.STATE_REFRESH)) && head.getBottom() != 0) {
            ev.setAction(MotionEvent.ACTION_CANCEL); // 让父类接受不到真实的事件
            return super.dispatchTouchEvent(ev);
        }

        return consumed || super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // 定义head和child的排列位置
        View head = getChildAt(0);
        View child = getChildAt(1);
        if (head != null && child != null) {
            HiLog.i(TAG, "onLayout head-height:" + head.getMeasuredHeight());
            int childTop = child.getTop();

            if (mState == HiOverView.HiRefreshState.STATE_REFRESH) {
                head.layout(0, mHiOverView.mPullRefreshHeight - head.getMeasuredHeight(), right, mHiOverView.mPullRefreshHeight); // 设置 left, top, right, bottom
                child.layout(0, mHiOverView.mPullRefreshHeight, right, mHiOverView.mPullRefreshHeight + child.getMeasuredHeight());
            } else {
                head.layout(0, childTop - head.getMeasuredHeight(), right, childTop);
                child.layout(0, childTop, right, childTop + child.getMeasuredHeight());
            }

            View other;
            for (int i = 2; i < getChildCount(); ++i) {
                other = getChildAt(i);
                other.layout(0, top, right, bottom);
            }
            HiLog.i(TAG, "onLayout head-bottom:" + head.getBottom());
        }
    }

    /**
     * 根据偏移量移动header与child
     *
     * @param offsetY 偏移量
     * @param nonAuto 是否非自动滚动触发
     * @return
     */
    private boolean moveDown(int offsetY, boolean nonAuto) {
        HiLog.i("111", "changeState:" + nonAuto);
        View head = getChildAt(0);
        View child = getChildAt(1);
        int childTop = child.getTop() + offsetY;
        HiLog.i("-----", "moveDown head-bottom:" + head.getBottom() + ",child.getTop():" + child.getTop() + ",offsetY:" + offsetY);

        if (childTop <= 0) { // 异常情况的补充
            HiLog.i(TAG, "childTop<=0,mState" + mState);
            offsetY = -child.getTop();
            // 移动head与child的位置，到原始位置
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);

            if (mState != HiOverView.HiRefreshState.STATE_REFRESH) {
                mState = HiOverView.HiRefreshState.STATE_INIT;
            }
        } else if (mState == HiOverView.HiRefreshState.STATE_REFRESH && childTop > mHiOverView.mPullRefreshHeight) {
            // 如果正在下拉刷新中，禁止继续下拉
            return false;
        } else if (childTop <= mHiOverView.mPullRefreshHeight) { // 还没超出设定的刷新距离
            if (mHiOverView.getState() != HiOverView.HiRefreshState.STATE_VISIBLE && nonAuto) { // 头部开始显示
                mHiOverView.onVisible();
                mHiOverView.setState(HiOverView.HiRefreshState.STATE_VISIBLE);
                mState = HiOverView.HiRefreshState.STATE_VISIBLE;
            }

            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);

            if (childTop == mHiOverView.mPullRefreshHeight && mState == HiOverView.HiRefreshState.STATE_OVER_RELEASE) {
                HiLog.i(TAG, "refresh，childTop：" + childTop);
                refresh();
            }
        } else {
            if (mHiOverView.getState() != HiOverView.HiRefreshState.STATE_OVER && nonAuto) {
                // 超出刷新位置
                mHiOverView.onOver();
                mHiOverView.setState(HiOverView.HiRefreshState.STATE_OVER);
            }

            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
        }

        if (mHiOverView != null) {
            mHiOverView.onScroll(head.getBottom(), mHiOverView.mPullRefreshHeight);
        }

        return true;
    }

    /** 下拉结束，恢复原样 */
    private void recover(int dis) {
        if (mHiRefreshListener != null && dis > mHiOverView.mPullRefreshHeight) {
            // 滚动到指定位置
            mAutoScroller.recover(dis - mHiOverView.mPullRefreshHeight);
            mState = HiOverView.HiRefreshState.STATE_OVER_RELEASE;
        } else {
            mAutoScroller.recover(dis);
        }
    }

    /** 刷新 */
    private void refresh() {
        if (mHiRefreshListener != null) {
            mState = HiOverView.HiRefreshState.STATE_REFRESH;
            mHiOverView.onRefresh();
            mHiOverView.setState(HiOverView.HiRefreshState.STATE_REFRESH);
            mHiRefreshListener.onRefresh();
        }
    }

    HiGestureDetector hiGestureDetector = new HiGestureDetector() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float disX, float disY) {
            // 横向滑动，或刷新被禁止则不处理
            if (Math.abs(disX) > Math.abs(disY) || mHiRefreshListener != null && !mHiRefreshListener.enableRefresh()) return false;
            // 刷新时是否禁止滑动
            if (disableRefreshScroll && mState == HiOverView.HiRefreshState.STATE_REFRESH) return true;

            View head = getChildAt(0);
            View child = HiScrollUtil.findScrollableChild(HiRefreshLayout.this);
            // 如果列表发生了滚动则不处理
            if (HiScrollUtil.childScrolled(child)) return false;

            // 没有刷新或没有达到可以刷新的距离，且头部已经划出或下拉
            if ((mState != HiOverView.HiRefreshState.STATE_REFRESH || head.getBottom() <= mHiOverView.mPullRefreshHeight) && (head.getBottom() > 0 || disY <= 0.0F)) {
                // 还在滑动中
                if (mState != HiOverView.HiRefreshState.STATE_OVER_RELEASE) {
                    int speed = (int) ((child.getTop() < mHiOverView.mPullRefreshHeight)
                            ? (mLastY / mHiOverView.minDamp)
                            : (mLastY / mHiOverView.minDamp));

                    // 如果是正在刷新状态，则不允许在滑动的时候改变状态
                    boolean b = moveDown(speed, true);
                    mLastY = (int) (-disY);
                    return b;
                }
            }

            return false;
        }
    };

    /**
     * 借助 Scroller 实现视图的自动滚动
     * https://juejin.im/post/5c7f4f0351882562ed516ab6
     */
    private class AutoScroller implements Runnable {

        private Scroller mScroller;
        private int mLastY;
        private boolean mIsFinished;

        AutoScroller() {
            mScroller = new Scroller(getContext(), new LinearInterpolator()); // 线性滚动
            mIsFinished = true;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) { // 滚动还未完成
                moveDown(mLastY - mScroller.getCurrY(), false);
                mLastY = mScroller.getCurrY(); // 记录 y，以便下次滚动
                post(this); // 继续执行 run 方法
            } else {
                removeCallbacks(this); // 移除任务
                mIsFinished = true;
            }
        }

        public void recover(int dis) {
            if (dis <= 0) return;

            removeCallbacks(this); // 新一轮滚多任务前，先将之前的滚动任务取消
            mLastY = 0;
            mIsFinished = false;
            mScroller.startScroll(0, 0, 0, dis, 300);
            post(this);
        }

        public boolean isFinished() {
            return mIsFinished;
        }

    }

}
