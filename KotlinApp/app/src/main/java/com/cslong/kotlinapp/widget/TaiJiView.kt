package com.cslong.kotlinapp.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class TaiJiView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    private var whiteColor: Int = Color.WHITE
    private var mWhitePaint: Paint = Paint()
    private var blackColor: Int = Color.BLACK
    private var mBlackPaint: Paint = Paint()

    private var degrees = 0.0f

    init {
        mWhitePaint.color = whiteColor
        mWhitePaint.isAntiAlias = true
        mBlackPaint.color = blackColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val width = canvas?.width
        val height = canvas?.height

        var halfWidth = width?.div(2)?.toFloat()
        var halfHeight = height?.div(2)?.toFloat()

        if (halfWidth != null && halfHeight != null) {
            canvas?.translate(halfWidth, halfHeight)
        }

        canvas?.drawColor(Color.GRAY)
        canvas?.rotate(degrees)

        val radius = Math.min(width!!, height!!).div(2) - 100

        val rect = RectF(
            (-radius).toFloat(),
            (-radius).toFloat(),
            radius.toFloat(),
            radius.toFloat()
        )

        val nighth = -90f

        canvas.drawArc(rect, 90.toFloat(), 180.toFloat(), true, mBlackPaint)            //绘制黑色半圆
        canvas.drawArc(rect, nighth, 180.toFloat(), true, mWhitePaint)          //绘制白色半圆

        val smallRadius = (radius / 2).toFloat() //小圆半径为大圆的一半
        val resmallRadius = -smallRadius
        canvas.drawCircle(0f, resmallRadius, smallRadius.toFloat(), mBlackPaint)
        canvas.drawCircle(0f, smallRadius, smallRadius.toFloat(), mWhitePaint)

        canvas.drawCircle(0f, resmallRadius, smallRadius / 4, mWhitePaint)
        canvas.drawCircle(0f, smallRadius, smallRadius / 4, mBlackPaint)
    }

    public fun setRotate(degrees: Float) {
        this.degrees = degrees
        invalidate()
    }


}