package com.away0x.hi.ui.input

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.away0x.hi.ui.R

class InputItemLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var bottomLine: Line
    private var topLine: Line
    private val topPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bottomPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        dividerDrawable = ColorDrawable()
        showDividers = SHOW_DIVIDER_BEGINNING

        orientation = LinearLayout.HORIZONTAL

        val arr = context.obtainStyledAttributes(attrs, R.styleable.InputItemLayout)

        // 解析 title 资源属性
        val titleStyleId = arr.getResourceId(R.styleable.InputItemLayout_titleTextAppearance, 0)
        val title = arr.getString(R.styleable.InputItemLayout_title)
        parseTitleStyle(titleStyleId, title)

        // 解析右侧输入框资源属性
        val inputStyleId = arr.getResourceId(R.styleable.InputItemLayout_inputTextAppearance, 0)
        val hint = arr.getString(R.styleable.InputItemLayout_hint)
        val inputType = arr.getInteger(R.styleable.InputItemLayout_inputType, 0)
        parseInputStyle(inputStyleId, hint, inputType)

        // 解析上下分割线配置
        val topLineStyleId = arr.getResourceId(R.styleable.InputItemLayout_topLineAppearance, 0)
        val bottomLineStyleId = arr.getResourceId(R.styleable.InputItemLayout_bottomLineAppearance, 0)
        topLine = parseLineStyle(topLineStyleId)
        bottomLine = parseLineStyle(bottomLineStyleId)

        if (topLine.enable) {
            topPaint.color = topLine.color
            topPaint.style = Paint.Style.FILL_AND_STROKE
            topPaint.strokeWidth = topLine.height.toFloat()
        }

        if (bottomLine.enable) {
            bottomPaint.color = bottomLine.color
            bottomPaint.style = Paint.Style.FILL_AND_STROKE
            bottomPaint.strokeWidth = bottomLine.height.toFloat()
        }

        arr.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (topLine.enable) {
            canvas?.drawLine(
                topLine.leftMargin.toFloat(),
                0f,
                measuredWidth - topLine.rightMargin.toFloat(),
                0f,
                topPaint
            )
        }

        if (bottomLine.enable) {
            canvas?.drawLine(
                bottomLine.leftMargin.toFloat(),
                height - bottomLine.height.toFloat(),
                measuredWidth - bottomLine.rightMargin.toFloat(),
                height - bottomLine.height.toFloat(),
                bottomPaint
            )
        }
    }

    private fun parseTitleStyle(resId: Int, title: String?) {
        val arr = context.obtainStyledAttributes(resId, R.styleable.titleTextAppearance)
        val titleColor = arr.getColor(R.styleable.titleTextAppearance_titleColor, resources.getColor(R.color.color_565))
        val titleSize = arr.getDimensionPixelSize(R.styleable.titleTextAppearance_titleSize, applyUnit(TypedValue.COMPLEX_UNIT_SP, 15f))
        val minWidth = arr.getDimensionPixelOffset(R.styleable.titleTextAppearance_minWidth, 0)

        val titleView = TextView(context)
        titleView.textSize = titleSize.toFloat()
        titleView.setTextColor(titleColor)
        titleView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        titleView.minWidth = minWidth
        titleView.gravity = Gravity.LEFT and Gravity.CENTER
        titleView.text = title

        addView(titleView)
        arr.recycle()
    }

    private fun parseInputStyle(resId: Int, hint: String?, inputType: Int) {
        val arr = context.obtainStyledAttributes(resId, R.styleable.inputTextAppearance)
        val hintColor = arr.getColor(R.styleable.inputTextAppearance_hintColor, resources.getColor(R.color.color_d1d2))
        val inputColor = arr.getColor(R.styleable.inputTextAppearance_inputColor, resources.getColor(R.color.color_565))
        val textSize = arr.getDimensionPixelSize(R.styleable.inputTextAppearance_textSize, applyUnit(TypedValue.COMPLEX_UNIT_SP, 14f))

        val editText = EditText(context)
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        params.weight = 1f
        editText.layoutParams = params
        editText.hint = hint
        editText.setHintTextColor(hintColor)
        editText.setTextColor(inputColor)
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat()) // px 值
        editText.setBackgroundColor(Color.TRANSPARENT)
        editText.gravity = Gravity.LEFT and Gravity.CENTER

        when (inputType) {
            0 -> {
                editText.inputType = InputType.TYPE_TEXT_VARIATION_NORMAL
            }
            1 -> {
                editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            2 -> {
                editText.inputType = InputType.TYPE_NUMBER_VARIATION_NORMAL
            }
        }

        addView(editText)
        arr.recycle()
    }

    private fun parseLineStyle(resId: Int): Line {
        val line = Line()
        val arr = context.obtainStyledAttributes(resId, R.styleable.lineAppearance)
        line.color = arr.getColor(R.styleable.lineAppearance_color, resources.getColor(R.color.color_d1d2))
        line.height = arr.getDimensionPixelOffset(R.styleable.lineAppearance_height, 0)
        line.leftMargin = arr.getDimensionPixelOffset(R.styleable.lineAppearance_leftMargin, 0)
        line.rightMargin = arr.getDimensionPixelOffset(R.styleable.lineAppearance_rightMargin, 0)
        line.enable = arr.getBoolean(R.styleable.lineAppearance_enable, false)

        arr.recycle()
        return line
    }

    private fun applyUnit(unit: Int, value: Float): Int {
        return TypedValue.applyDimension(unit, value, resources.displayMetrics).toInt()
    }

    inner class Line {
        var color = 0
        var height = 0
        var leftMargin = 0
        var rightMargin = 0
        var enable = false
    }

}