package com.cslong.kotlinapp.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircularView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    private var color: Int = Color.BLUE
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = color
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var radius = Math.min(width, height) / 2f //width和height是getWidth()和getHeight()
        canvas?.drawCircle(width / 2f, height / 2f, radius, paint)
    }


}