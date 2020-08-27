package com.away0x.hi.ui.app.demo.banner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.away0x.hi.ui.app.R
import com.away0x.hi.ui.banner.indicator.HiCircleIndicator
import com.away0x.hi.ui.banner.indicator.HiNumIndicator
import com.away0x.hi.ui.banner.indicator.IHiIndicator
import com.away0x.hi.ui.banner.model.HiBannerMo
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_hi_banner_demo.*

class HiBannerDemoActivity : AppCompatActivity() {

    private var urls = arrayOf(
        "https://www.devio.org/img/beauty_camera/beauty_camera1.jpg",
        "https://www.devio.org/img/beauty_camera/beauty_camera3.jpg",
        "https://www.devio.org/img/beauty_camera/beauty_camera4.jpg",
        "https://www.devio.org/img/beauty_camera/beauty_camera5.jpg",
        "https://www.devio.org/img/beauty_camera/beauty_camera2.jpg",
        "https://www.devio.org/img/beauty_camera/beauty_camera6.jpg",
        "https://www.devio.org/img/beauty_camera/beauty_camera7.jpg",
        "https://www.devio.org/img/beauty_camera/beauty_camera8.jpeg"
    )
    private var hiIndicator: IHiIndicator<*>? = null
    private var autoPlay: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_banner_demo)

        initView(HiCircleIndicator(this), false)

        auto_play.setOnCheckedChangeListener { _, isChecked ->
            autoPlay = isChecked
            initView(hiIndicator, autoPlay)
        }

        tv_switch.setOnClickListener {
            if (hiIndicator is HiCircleIndicator) {
                initView(HiNumIndicator(this), autoPlay)
            } else {
                initView(HiCircleIndicator(this), autoPlay)
            }
        }
    }

    private fun initView(hiIndicator: IHiIndicator<*>?, autoPlay: Boolean) {
        this.hiIndicator = hiIndicator

        val moList: MutableList<HiBannerMo> = ArrayList()
        for (i in 0..5) {
            val mo = BannerMo()
            mo.url = urls[i % urls.size]
            moList.add(mo)
        }

        banner.setHiIndicator(hiIndicator)
        banner.setAutoPlay(autoPlay)
        banner.setIntervalTime(2000)
        // 自定义布局
        banner.setBannerData(R.layout.banner_item_layout, moList)
        banner.setBindAdapter { viewHolder, mo, position ->
            val imageView: ImageView = viewHolder.findViewById(R.id.iv_image)
            Glide.with(this@HiBannerDemoActivity).load(mo.url).into(imageView)
            val titleView: TextView = viewHolder.findViewById(R.id.tv_title)
            titleView.text = mo.url
            Log.d("----position:", position.toString() + "url:" + mo.url)
        }
    }
}