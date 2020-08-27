package com.away0x.hi.ui.banner;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.away0x.hi.ui.banner.core.HiBannerAdapter;
import com.away0x.hi.ui.banner.core.IBindAdapter;
import com.away0x.hi.ui.banner.indicator.IHiIndicator;
import com.away0x.hi.ui.banner.model.HiBannerMo;

import java.util.List;

public interface IHiBanner {
    void setBannerData(@LayoutRes int layoutResId, @NonNull List<? extends HiBannerMo> models);

    void setBannerData(@NonNull List<? extends HiBannerMo> models);

    void setHiIndicator(IHiIndicator<?> hiIndicator);

    void setAutoPlay(boolean autoPlay);

    void setLoop(boolean loop);

    void setIntervalTime(int intervalTime);

    void setBindAdapter(IBindAdapter bindAdapter);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener);

    void setOnBannerClickListener(OnBannerClickListener onBannerClickListener);

    void setScrollDuration(int duration);

    interface OnBannerClickListener {
        void onBannerClick(@NonNull HiBannerAdapter.HiBannerViewHolder viewHolder, @NonNull HiBannerMo bannerMo, int position);
    }
}
