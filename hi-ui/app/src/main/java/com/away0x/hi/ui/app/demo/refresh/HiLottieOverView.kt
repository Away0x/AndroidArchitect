package com.away0x.hi.ui.app.demo.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.airbnb.lottie.LottieAnimationView
import com.away0x.hi.ui.app.R
import com.away0x.hi.ui.refresh.HiOverView

class HiLottieOverView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HiOverView(context, attrs, defStyleAttr) {

    private lateinit var pullAnimationView: LottieAnimationView

    override fun init() {
        LayoutInflater.from(context).inflate(R.layout.lottie_overview, this, true)
        pullAnimationView = findViewById(R.id.pull_animation)
        pullAnimationView.setAnimation("loading_wave.json");
    }

    override fun onRefresh() {
        pullAnimationView.speed = 2f;
        pullAnimationView.playAnimation();
    }

    override fun onFinish() {
        pullAnimationView.progress = 0f;
        pullAnimationView.cancelAnimation();
    }

    override fun onScroll(scrollY: Int, pullRefreshHeight: Int) {}

    override fun onOver() {}

    override fun onVisible() {}

}