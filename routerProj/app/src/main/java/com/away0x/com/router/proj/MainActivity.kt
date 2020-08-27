package com.away0x.com.router.proj

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.away0x.com.router.proj.navigation.NavUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        // 路由器控制对象，它是路由跳转的唯一入口
        val navController = findNavController(R.id.nav_host_fragment)
        // 生成 NavGraph，设置到 navController 中
        NavUtil.builderNavGraph(this, nav_host_fragment.childFragmentManager, navController, R.id.nav_host_fragment)
        // 生成 bottom bar 底部导航
        NavUtil.builderBottomBar(navView)
        // 实现 tab 切换页面跳转
        navView.setOnNavigationItemSelectedListener {
            navController.navigate(it.itemId)
            true
        }
    }
}