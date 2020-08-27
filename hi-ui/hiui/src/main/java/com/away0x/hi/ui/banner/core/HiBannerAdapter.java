package com.away0x.hi.ui.banner.core;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.away0x.hi.ui.banner.IHiBanner;
import com.away0x.hi.ui.banner.model.HiBannerMo;

import java.util.List;

/** HiViewPager的适配器，为页面填充数据 */
public class HiBannerAdapter extends PagerAdapter {

    private Context mContext;
    private SparseArray<HiBannerViewHolder> mCachedViews = new SparseArray<>(); // 缓存 view
    private IHiBanner.OnBannerClickListener mBannerClickListener;
    private IBindAdapter mBindAdapter;
    private List<? extends HiBannerMo> models;
    /** 是否开启自动轮播 */
    private boolean mAutoPlay = true;
    /** 非自动轮播状态下是否可以循环切换 */
    private boolean mLoop = false;
    private int mLayoutResId = -1;

    public HiBannerAdapter(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        // 无限轮播的关键点
        return mAutoPlay ? Integer.MAX_VALUE : (mLoop ? Integer.MAX_VALUE : getRealCount());
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) { return view == object; }

    /** 每次 item 创建都会添加到缓存 mCachedViews 中，所以重写这个方法，不调用 super.destroyItem，使 item 不会被销毁 */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {}

    @Override
    public int getItemPosition(@NonNull Object object) {
        // 让 item 每次都会刷新
        return POSITION_NONE;
    }

    /** 实例化 item */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // 得到真实的 position，方法传进来的可能是无限滚动时的 position，是不真实的
        int realPosition = position;
        if (getRealCount() > 0) {
            realPosition = position % getRealCount();
        }

        HiBannerViewHolder viewHolder = mCachedViews.get(realPosition);
        // 如果 rootView 已经添加了，则将其移除
        if (container.equals(viewHolder.rootView.getParent())) {
            container.removeView(viewHolder.rootView);
        }

        // 数据绑定
        onBind(viewHolder, models.get(realPosition), realPosition);

        // 防止重复添加异常的处理
        if (viewHolder.rootView.getParent() != null) {
            ((ViewGroup) viewHolder.rootView.getParent()).removeView(viewHolder.rootView);
        }

        // 添加 view
        container.addView(viewHolder.rootView);
        return viewHolder.rootView;
    }

    public void setBannerData(@NonNull List<? extends HiBannerMo> models) {
        this.models = models;
        initCachedView();
        notifyDataSetChanged();
    }
    public void setBindAdapter(IBindAdapter bindAdapter) { this.mBindAdapter = bindAdapter; }
    public void setOnBannerClickListener(IHiBanner.OnBannerClickListener OnBannerClickListener) { this.mBannerClickListener = OnBannerClickListener; }
    public void setLayoutResId(@LayoutRes int layoutResId) { this.mLayoutResId = layoutResId; }
    public void setAutoPlay(boolean autoPlay) { this.mAutoPlay = autoPlay; }
    public void setLoop(boolean loop) { this.mLoop = loop; }

    /** 获取 banner 数量 */
    public int getRealCount() {
        return models == null ? 0 : models.size();
    }

    /** 获取初次展示的 item 位置 */
    public int getFirstItem() {
        // Integer.MAX_VALUE / 2: 让用户无论向左滑还是向右滑，都有相同数量的 item 让他滑动
        return Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % getRealCount();
    }

    /** viewHodler 数据绑定 */
    protected void onBind(@NonNull final HiBannerViewHolder viewHolder, @NonNull final HiBannerMo bannerMo, final int position) {
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBannerClickListener != null) {
                    mBannerClickListener.onBannerClick(viewHolder, bannerMo, position);
                }
            }
        });

        // 让业务层进行数据的绑定
        if (mBindAdapter != null) {
            mBindAdapter.onBind(viewHolder, bannerMo, position);
        }
    }

    /** 初始化各个 view */
    private void initCachedView() {
        mCachedViews = new SparseArray<>();

        for (int i = 0; i < models.size(); i++) {
            HiBannerViewHolder viewHolder = new HiBannerViewHolder(createView(LayoutInflater.from(mContext), null));
            mCachedViews.put(i, viewHolder);
        }
    }

    private View createView(LayoutInflater layoutInflater, ViewGroup parent) {
        if (mLayoutResId == -1) {
            throw new IllegalArgumentException("you must be set setLayoutResId first");
        }
        return layoutInflater.inflate(mLayoutResId, parent, false);
    }

    public static class HiBannerViewHolder {

        private SparseArray<View> viewHolderSparseArr;
        View rootView;

        HiBannerViewHolder(View rootView) {
            this.rootView = rootView;
        }

        public View getRootView() {
            return rootView;
        }

        /** 方便外部查找 view，绑定数据 */
        public <V extends View> V findViewById(int id) {
            if (!(rootView instanceof ViewGroup)) {
                return (V) rootView;
            }

            if (this.viewHolderSparseArr == null) {
                this.viewHolderSparseArr = new SparseArray<>(1);
            }

            V childView = (V) viewHolderSparseArr.get(id); // 从缓存中查找 view
            if (childView == null) {
                childView = rootView.findViewById(id);
                this.viewHolderSparseArr.put(id, childView);
            }

            return childView;
        }

    }

}
