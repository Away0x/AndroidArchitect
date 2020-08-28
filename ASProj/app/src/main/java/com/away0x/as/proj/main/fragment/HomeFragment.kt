package com.away0x.`as`.proj.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.away0x.`as`.proj.common.ui.component.HiBaseFragment
import com.away0x.`as`.proj.main.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : HiBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_btn.setOnClickListener {
            navigation("/profile/detail")
        }
        vip_btn.setOnClickListener {
            navigation("/profile/vip")
        }
        money_btn.setOnClickListener {
            navigation("/profile/authentication")
        }
        degrade_btn.setOnClickListener {
            navigation("/profile/unknow")
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home;
    }

    private fun navigation(path: String) {
        ARouter.getInstance().build(path).navigation()
    }

}