package com.cslong.kotlinapp.widget


import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import com.cslong.kotlinapp.R


class RoundeMenu : ViewGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(context, attributeSet)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(
        context,
        attributeSet,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attributeSet)
    }

    private val STATE_COLLAPSE = 0
    private val STATE_EXPAND = 1

    private val center = Point()
    private val mCenterPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRoundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //    private val outlineProvider: OvalOutline? = null
    private var mExpandAnimator = ValueAnimator.ofFloat(0f, 0f)
    private var mColorAnimator = ValueAnimator.ofFloat(0f, 0f)


    private var collapsedRadius = 0
    private var expandedRadius = 0
    private var mRoundColor = 0
    private var mCenterColor = 0


    private var expandProgress = 0.0f
    private var state: Int? = 0
    private var mDuration: Int? = 0


    private fun handleStyleable(context: Context, attrs: AttributeSet) {

        val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundelMenu)
        collapsedRadius =
            ta.getDimensionPixelSize(
                R.styleable.RoundelMenu_round_menu_collapsedRadius,
                dp2px(22.0.toFloat())
            )
        expandedRadius =
            ta.getDimensionPixelSize(
                R.styleable.RoundelMenu_round_menu_expandedRadius,
                dp2px(84.0.toFloat())
            )
        mRoundColor = ta.getColor(
            R.styleable.RoundelMenu_round_menu_roundColor,
            Color.parseColor("#ffffbb33")
        )
        mCenterColor = ta.getColor(
            R.styleable.RoundelMenu_round_menu_centerColor,
            Color.parseColor("#ffff8800")
        )
        mDuration = ta.getInteger(R.styleable.RoundelMenu_round_menu_duration, 400)

        ta.recycle()


    }

    private fun init(context: Context, attrs: AttributeSet) {
        handleStyleable(context, attrs)
        mCenterPaint.color = mCenterColor
        mCenterPaint.style = Paint.Style.FILL
        mRoundPaint.color = mRoundColor
        mRoundPaint.style = Paint.Style.FILL
        setWillNotDraw(false)

        state = STATE_COLLAPSE

        initAnim()
    }

    private fun initAnim() {

        mExpandAnimator = ValueAnimator.ofFloat(0f, 0f)
        mExpandAnimator.interpolator = OvershootInterpolator()
        mExpandAnimator.duration = mDuration!!.toLong()
        mExpandAnimator.addUpdateListener { animation ->

            expandProgress = animation.animatedValue as Float
            mRoundPaint.alpha = Math.min(255, (animation.animatedValue as Float * 255).toInt())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                invalidateOutline()
            }
            invalidate()
        }

        mColorAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), mRoundColor, mCenterColor)
        mColorAnimator.duration = mDuration!!.toLong()
        mColorAnimator.addUpdateListener { animation ->
            mCenterPaint.color =
                (animation.animatedValue as Int)
        }

    }

    fun collapse(animate: Boolean) {
        state = STATE_COLLAPSE
        for (i in 0 until childCount) {
            getChildAt(i).visibility = View.GONE
        }
        invalidate()
        if (animate) {
            startCollapseAnimation()
        }
    }


    fun expand(animate: Boolean) {
        state = STATE_EXPAND
        for (i in 0 until childCount) {
            getChildAt(i).visibility = View.VISIBLE
        }
        invalidate()
        if (animate) {
            startExpandAnimation()
        } else {
            for (i in 0 until childCount) {
                getChildAt(i).alpha = 1f
            }
        }
    }

    fun startExpandAnimation() {
        mExpandAnimator.setFloatValues(expandProgress.toFloat(), 1f)
        mExpandAnimator.start()
        mColorAnimator.setObjectValues(
            if (mColorAnimator.animatedValue == null) mRoundColor else mColorAnimator.animatedValue,
            mCenterColor
        )
        mColorAnimator.start()
        var delay: Int = 50
        for (i in 0 until childCount) {
            getChildAt(i).animate()
                .setStartDelay(delay.toLong())
                .setDuration(mDuration!!.toLong())
                .alphaBy(0f)
                .scaleXBy(0f)
                .scaleYBy(0f)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .start()
            delay += 50
        }
    }


    fun startCollapseAnimation() {
        mExpandAnimator.setFloatValues(expandProgress.toFloat(), 0f)
        mExpandAnimator.start()
        mColorAnimator.setObjectValues(
            if (mColorAnimator.animatedValue == null) mCenterColor else mColorAnimator.animatedValue,
            mRoundColor
        )
        mColorAnimator.start()
        var delay: Int = 50
        for (i in childCount - 1 downTo 0) {
            getChildAt(i).animate()
                .setStartDelay(delay.toLong())
                .setDuration(mDuration!!.toLong())
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .start()
            delay += 50
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0) {
            return
        }

        for (index in 1 until childCount) {
            val item = getChildAt(index)
            item.layout(l, t, r, b)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchPoint = Point()
        touchPoint[event!!.x.toInt()] = event.y.toInt()
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                //计算触摸点与中心点的距离
                val distance: Double =
                    getPointsDistance(
                        touchPoint,
                        center
                    )
                return if (state == STATE_EXPAND) { //展开状态下，如果点击区域与中心点的距离不处于子菜单区域，就收起菜单
                    if (distance > collapsedRadius + (expandedRadius - collapsedRadius) * expandProgress
                        || distance < collapsedRadius
                    ) {
                        collapse(true)
                        return true
                    }
                    //展开状态下，如果点击区域处于子菜单区域，则不消费事件
                    false
                } else { //收缩状态下，如果点击区域处于中心圆圈范围内，则展开菜单
                    if (distance < collapsedRadius) {
                        expand(true)
                        return true
                    }
                    //收缩状态下，如果点击区域不在中心圆圈范围内，则不消费事件
                    false
                }
            }
        }




        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = outlineProvider
        }
        val x: Int
        val y: Int
        x = w / 2
        y = h / 2
        center[x] = y

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (expandProgress > 0f) {
            var radius = collapsedRadius + (expandedRadius + collapsedRadius) * expandProgress
            canvas?.drawCircle(
                center.x.toFloat(),
                center.y.toFloat(),
                radius.toFloat(),
                mRoundPaint
            )
        }

        var radius = collapsedRadius + (collapsedRadius * .2f * expandProgress)
        canvas?.drawCircle(
            center.x.toFloat(),
            center.y.toFloat(), radius.toFloat(), mCenterPaint
        )

        val count = canvas!!.saveLayer(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            null,
            Canvas.ALL_SAVE_FLAG
        )
        canvas?.restoreToCount(count)
    }

    fun getPointsDistance(
        a: Point,
        b: Point
    ): Double {
        val dx = b.x - a.x
        val dy = b.y - a.y
        return Math.sqrt(dx * dx + dy * dy.toDouble())
    }


    private fun dp2px(dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal,
            context.resources.displayMetrics
        ).toInt()
    }

}