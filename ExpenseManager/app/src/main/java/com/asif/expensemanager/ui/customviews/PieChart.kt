package com.asif.expensemanager.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.asif.expensemanager.R

class PieChart : View {
    var colorsValuesMap: Map<Int, Int>? = null
    private var arcSize = 8
    private var padding = 32
    private val paint: Paint
    private val arcRect = RectF()

    constructor(context: Context) : super(context) {
        paint = Paint()
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        paint = Paint()
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        paint = Paint()
        init(context, attrs)
    }

    private fun init(context: Context, set: AttributeSet?) {
        val density = context.resources.displayMetrics.density
        arcSize = (density * arcSize).toInt()
        if (set != null) {
            val typeArray = context.obtainStyledAttributes(set, R.styleable.PieChart, 0, 0)
            this.arcSize = typeArray.getDimensionPixelSize(R.styleable.PieChart_arcSize, arcSize)
            padding +=
                (paddingLeft + paddingRight + paddingTop + paddingBottom + paddingStart + paddingEnd) / 6
            typeArray.recycle()
        }
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val min = w.coerceAtMost(h)

        val diameter = min - padding
        val top = h / 2 - diameter / 2
        val left = w / 2 - diameter / 2
        arcRect.set(
            left.toFloat(),
            top.toFloat(),
            (left + diameter).toFloat(),
            (top + diameter).toFloat()
        )

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        if (!colorsValuesMap.isNullOrEmpty() && canvas != null) {
            var max = 0
            colorsValuesMap!!.values.forEach {
                max += it
            }

            paint.strokeWidth = arcSize.toFloat()
            var currentAngle = 0f
            var angle: Float
            colorsValuesMap!!.forEach { (color, value) ->
                paint.color = color
                angle = (value * 360) / max.toFloat()
                canvas.drawArc(arcRect, currentAngle - 0.5f, angle + 1, false, paint)
                currentAngle += angle
            }
        }
        super.onDraw(canvas)
    }
}