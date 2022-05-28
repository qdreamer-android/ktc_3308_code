package com.pwong.uiframe.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import com.pwong.uiframe.R
import com.pwong.uiframe.utils.DensityUtil
import kotlin.math.min

class CircleBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var bgPaint: Paint? = null
    private var progressPaint: Paint? = null
    private var mRectF: RectF = RectF()
    private val anim by lazy { CircleBarAnim() }
    private var progressNum = 0
    private var maxNum = 100
    private var barWidth = 1F
    private var progressSweepAngle = 0F

    constructor(context: Context) : this(context, null)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleBar)
        val progressColor = typedArray.getColor(R.styleable.CircleBar_circleBarColor, Color.BLACK)
        val bgColor = typedArray.getColor(R.styleable.CircleBar_circleBackgroundColor, Color.WHITE)
        val dp10 = DensityUtil.dip2px(context, 10f)
        barWidth = typedArray.getDimension(R.styleable.CircleBar_circleBarWidth, dp10.toFloat())
        typedArray.recycle()
        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressPaint!!.style = Paint.Style.STROKE
        progressPaint!!.color = progressColor
        progressPaint!!.strokeWidth = barWidth
        progressPaint!!.strokeCap = Paint.Cap.ROUND
        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint!!.style = Paint.Style.STROKE
        bgPaint!!.color = bgColor
        bgPaint!!.strokeWidth = barWidth
        bgPaint!!.strokeCap = Paint.Cap.ROUND
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = measureSize(heightMeasureSpec)
        val width = measureSize(widthMeasureSpec)
        val min = width.coerceAtMost(height) // 获取View最短边的长度
        setMeasuredDimension(width, height)
        if (min >= barWidth * 2) {
            mRectF.set(barWidth / 2, barWidth / 2, width - barWidth / 2, height - barWidth / 2);
        }
    }

    private fun measureSize(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> min(0, specSize)
            else -> 0
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(mRectF, -90f, progressSweepAngle - 360, false, bgPaint!!)
        canvas.drawArc(mRectF, -90f, progressSweepAngle, false, progressPaint!!)
    }

    inner class CircleBarAnim : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation
        ) { //interpolatedTime从0渐变成1,到1时结束动画,持续时间由setDuration（time）方法设置
            super.applyTransformation(interpolatedTime, t)
            progressSweepAngle = interpolatedTime * 360 * progressNum / maxNum
            postInvalidate()
        }
    }

    fun setMaxNum(maxNum: Int) {
        this.maxNum = maxNum
    }

    fun setProgress(progress: Int) {
        clearAnimation()
        progressNum = progress
        progressSweepAngle = 360f * progress / maxNum
        postInvalidate()
    }

    fun setProgress(progress: Int, time: Long, listener: Animation.AnimationListener? = null) {
        progressNum = progress
        anim.duration = time
        anim.setAnimationListener(listener)
        startAnimation(anim)
    }
}