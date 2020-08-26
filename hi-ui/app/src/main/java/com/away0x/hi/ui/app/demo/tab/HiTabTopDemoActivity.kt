package com.away0x.hi.ui.app.demo.tab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.away0x.hi.ui.app.R
import com.away0x.hi.ui.tab.top.HiTabTopInfo
import kotlinx.android.synthetic.main.activity_hi_tab_top_demo.*

class HiTabTopDemoActivity : AppCompatActivity() {

    val tabsStr = mutableListOf(
        "热门",
        "服装",
        "数码",
        "鞋子",
        "零食",
        "家电",
        "汽车",
        "百货",
        "家居",
        "装修",
        "运动"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_tab_top_demo)

        initTabTop()
    }

    private fun initTabTop() {
        val infoList = mutableListOf<HiTabTopInfo<Int>>()
        val defaultColor = resources.getColor(R.color.tabBottomDefaultColor)
        val tintColor = resources.getColor(R.color.tabBottomTintColor)

        tabsStr.forEach {
            infoList.add(HiTabTopInfo(it, defaultColor, tintColor))
        }

        tab_top_layout.inflateInfo(infoList.toList())
        tab_top_layout.defaultSelected(infoList[0])
        tab_top_layout.addTabSelectedChangeListener { index, prevInfo, nextInfo ->
            Toast.makeText(this, nextInfo.name, Toast.LENGTH_SHORT).run {
                setGravity(Gravity.CENTER, 0, 0)
                show()
            }
        }
    }
}