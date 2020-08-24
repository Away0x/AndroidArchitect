package com.away0x.as.proj.common.tab;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.away0x.hi.ui.tab.bottom.HiTabBottomInfo;

import java.util.List;

public class HiTabViewAdapter {

    private List<HiTabBottomInfo<?>> mInfoList;
    private Fragment mCurFragment;
    private FragmentManager mFragmentManager;

    public HiTabViewAdapter(FragmentManager fragmentManager, List<HiTabBottomInfo<?>> infoList) {
        this.mInfoList = infoList;
        this.mFragmentManager = fragmentManager;
    }

    /**
     * 实例化以及显示指定位置的 fragment
     * @param container
     * @param position
     */
    public void instantiateItem(View container, int position) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        // 创建下一个 fragment 时，要将当前的 fragment 隐藏
        if (mCurFragment != null) {
            fragmentTransaction.hide(mCurFragment);
        }

        String name = container.getId() + ":" + position;
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            fragmentTransaction.show(fragment);
        } else {
            fragment = getItem(position);
            if (!fragment.isAdded()) {
                fragmentTransaction.add(container.getId(), fragment, name);
            }
        }
        mCurFragment = fragment;
        fragmentTransaction.commitAllowingStateLoss();
    }

    public Fragment getItem(int position) {
        try {
            return mInfoList.get(position).fragment.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Fragment getmCurrentFragment() {
        return mCurFragment;
    }

    public int getCount() {
        return mInfoList == null ? 0 : mInfoList.size();
    }

}
