package com.away0x.`as`.proj.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.away0x.`as`.proj.common.R

class EmptyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var title: TextView
    private var icon: TextView
    private var desc: TextView
    private var button: Button
    private var tips: IconFontTextView

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        LayoutInflater.from(context).inflate(R.layout.layout_empty_view, this, true)

        title = findViewById(R.id.empty_icon)
        icon = findViewById(R.id.empty_icon)
        desc = findViewById(R.id.empty_text)
        button = findViewById(R.id.empty_action)
        tips = findViewById(R.id.empty_tips)
    }

    /** 设置icon，需要在string.xml中定义 iconfont.ttf中的unicode码 */
    fun setIcon(@StringRes iconRes: Int) {
        icon.setText(iconRes)
    }

    fun setTitle(text: String) {
        title.text = text
        title.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
    }

    fun setDesc(text: String) {
        desc.text = text
        desc.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
    }

    @JvmOverloads
    fun setHelpAction(@StringRes actionId: Int = R.string.if_detail, listener: OnClickListener) {
        tips.setText(actionId)
        tips.setOnClickListener(listener)
        tips.visibility = if (actionId == -1) View.GONE else View.VISIBLE
    }


    fun setButton(text: String, listener: OnClickListener) {
        if (text.isEmpty()) {
            button.visibility = View.GONE
        } else {
            button.visibility = View.VISIBLE
            button.text = text
            button.setOnClickListener(listener)
        }
    }

}