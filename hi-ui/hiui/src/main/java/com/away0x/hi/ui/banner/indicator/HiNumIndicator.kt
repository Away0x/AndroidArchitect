package com.away0x.hi.ui.banner.indicator

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.away0x.hi.library.util.HiDisplayUtil

/** 数字指示器 */
class HiNumIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IHiIndicator<FrameLayout> {

    /** 指示点左右内间距 */
    private var mPointLeftRightPadding: Int = HiDisplayUtil.dp2px(10f, getContext().resources)

    /** 指示点上下内间距 */
    private var mPointTopBottomPadding: Int = HiDisplayUtil.dp2px(10f, getContext().resources)

    companion object {
        private const val VWC = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    override fun get(): FrameLayout {
        return this
    }

    override fun onInflate(count: Int) {
        removeAllViews()
        if (count <= 0) return

        val groupView = LinearLayout(context)

        val indexTv = TextView(context).apply {
            text = 1.toString()
            setTextColor(Color.WHITE)
        }
        val symbolTv = TextView(context).apply {
            text = " / "
            setTextColor(Color.WHITE)
        }
        val countTv = TextView(context).apply {
            text = count.toString()
            setTextColor(Color.WHITE)
        }

        groupView.run {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, mPointLeftRightPadding, mPointTopBottomPadding)

            addView(indexTv)
            addView(symbolTv)
            addView(countTv)
        }

        val groupViewParams = LayoutParams(VWC, VWC)
        groupViewParams.gravity = Gravity.END or Gravity.BOTTOM

        addView(groupView, groupViewParams)
    }

    override fun onPointChange(current: Int, count: Int) {
        val viewGroup = getChildAt(0) as ViewGroup
        val indexTv = viewGroup.getChildAt(0) as TextView
        val countTv = viewGroup.getChildAt(2) as TextView

        indexTv.text = (current + 1).toString()
        countTv.text = count.toString()
    }

}