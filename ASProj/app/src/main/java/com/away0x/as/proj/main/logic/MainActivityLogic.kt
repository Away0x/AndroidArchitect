package com.away0x.`as`.proj.main.logic

import android.content.res.Resources
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import com.away0x.`as`.proj.common.tab.HiFragmentTabView
import com.away0x.`as`.proj.common.tab.HiTabViewAdapter
import com.away0x.`as`.proj.main.R
import com.away0x.`as`.proj.main.fragment.*
import com.away0x.hi.ui.tab.bottom.HiTabBottomInfo
import com.away0x.hi.ui.tab.bottom.HiTabBottomLayout

/**
 * 将 MainActivity 的一些逻辑内聚在这里，让 MainActivity 更加清爽
 */
class MainActivityLogic(private val activityProvider: ActivityProvider) {

    lateinit var fragmentTabView: HiFragmentTabView
    lateinit var hiTabBottomLayout: HiTabBottomLayout
    lateinit var infoList: MutableList<HiTabBottomInfo<Int>>
    var currentItemIndex: Int = -1

    private companion object {
        val SAVED_CURRENT_ID = "SAVED_CURRENT_ID"
    }

    init {
        initTabBottom()
    }

    private fun initTabBottom() {
        hiTabBottomLayout = activityProvider.findViewById(R.id.tab_bottom_layout)
        hiTabBottomLayout.setTabAlpha(0.85f)

        val defaultColor = activityProvider.getResources().getColor(R.color.tabBottomDefaultColor)
        val tintColor = activityProvider.getResources().getColor(R.color.tabBottomTintColor)
        val iconfont = "fonts/iconfont.ttf"

        val homeTabInfo = HiTabBottomInfo(
            "首页",
            iconfont,
            activityProvider.getString(R.string.if_home),
            null, defaultColor, tintColor)
        homeTabInfo.fragment = HomeFragment::class.java
        val favoriteTabInfo = HiTabBottomInfo(
            "收藏",
            iconfont,
            activityProvider.getString(R.string.if_favorite),
            null, defaultColor, tintColor)
        favoriteTabInfo.fragment = FavoiteFragment::class.java
        val catTabInfo = HiTabBottomInfo(
            "分类",
            iconfont,
            activityProvider.getString(R.string.if_category),
            null, defaultColor, tintColor)
        catTabInfo.fragment = CategoryFragment::class.java
        val recommendTabInfo = HiTabBottomInfo(
            "推荐",
            iconfont,
            activityProvider.getString(R.string.if_recommend),
            null, defaultColor, tintColor)
        recommendTabInfo.fragment = RecommendFragment::class.java
        val profileTabInfo = HiTabBottomInfo(
            "我的",
            iconfont,
            activityProvider.getString(R.string.if_profile),
            null, defaultColor, tintColor)
        profileTabInfo.fragment = ProfileFragment::class.java
        infoList = mutableListOf(homeTabInfo, favoriteTabInfo, catTabInfo, recommendTabInfo, profileTabInfo)

        hiTabBottomLayout.inflateInfo(infoList.toList())
        initFragmentTabView()

        hiTabBottomLayout.addTabSelectedChangeListener { index, _, _ ->
            fragmentTabView.currentItem = index
        }
        hiTabBottomLayout.defaultSelected(homeTabInfo)
    }

    private fun initFragmentTabView() {
        val adapter = HiTabViewAdapter(activityProvider.getSupportFragmentManager(), infoList.toList())
        fragmentTabView = activityProvider.findViewById(R.id.fragment_tab_view)
        fragmentTabView.adapter = adapter
    }

    interface ActivityProvider {
        /** 下面这些方法都是 AppCompatActivity 有的 */
        fun <T : View> findViewById(@IdRes id: Int): T
        fun getResources(): Resources
        fun getSupportFragmentManager(): FragmentManager
        fun getString(@StringRes resId: Int): String
    }
}