package com.ss.profilepercentageview

import android.content.Context
import android.graphics.*

class ArcProgressPainterImp(
    private var color: Int,
    private var isDashEnable: Boolean,
    private var isRoundedCorner: Boolean,
    var max: Float,
    private var dashWidth: Float,
    private var arcBarWidth: Float,
    private var dashSpace: Float,
    private val blurMargin: Int,
    private val context: Context,
    startColor: Int,
    endColor: Int
) : ArcProgressPainter {
    private var circle: RectF? = null
    protected var paint: Paint? = null
    private val startAngle = 135f
    private val finishAngle = 272f
    private var width = 0
    private var height = 0
    private var plusAngle = 0f
    private var startColor = -0xd7909f
    private var endColor = -0xd12c4d

    private var mIsDashEnable = false
    private var mArcWidth = 0
    private var mDashWidth = 0
    private var mDashSpace = 0

    private fun initSize() {
        mDashWidth = DimensionUtils.getSizeInPixels(dashWidth, context)
        mArcWidth = DimensionUtils.getSizeInPixels(arcBarWidth, context)
        mDashSpace = DimensionUtils.getSizeInPixels(dashSpace, context)
    }

    private fun init() {
        initPainter()
    }

    private fun initPainter() {
        paint = Paint()
        paint!!.isAntiAlias = true
        paint!!.isAntiAlias = true
        paint!!.strokeWidth = mArcWidth.toFloat()
        paint!!.shader = sweepGradient()
        paint!!.style = Paint.Style.STROKE
        if(isDashEnable){
            paint!!.pathEffect =
                DashPathEffect(floatArrayOf(mDashWidth.toFloat(), mDashSpace.toFloat()), 0f)
        }else{
            if(isRoundedCorner){
                paint!!.strokeCap = Paint.Cap.ROUND
            }else{
                paint!!.strokeCap = Paint.Cap.BUTT
            }
        }
    }

    private fun initCircle() {
        val padding = mArcWidth / 2 + blurMargin
        circle = RectF()
        circle!![padding.toFloat(), padding.toFloat(), (width - padding).toFloat()] =
            (height - padding).toFloat()
    }

    override fun setDashWidth(value: Float){
        dashWidth = value
        initSize()
        initPainter()
    }
    override fun setArcWidth(value: Float){
        arcBarWidth = value
        initCircle()
        initSize()
        initPainter()
    }
    override fun setDashSpace(value: Float){
        dashSpace = value
        initSize()
        initPainter()
    }

    override fun setDashEnable(value: Boolean) {
        isDashEnable = value
        initPainter()
    }

    override fun setRounded(value: Boolean) {
        isRoundedCorner = value
        initPainter()
    }

    override fun draw(canvas: Canvas) {
        if(circle != null && paint != null){
            canvas.drawArc(circle!!, startAngle, plusAngle, false, paint!!)
        }
    }

    override fun setBackColor(color: Int) {
        this.color = color
        paint!!.color = color
    }

    override fun getBackColor(): Int {
        return color
    }

    override fun onSizeChanged(height: Int, width: Int) {
        this.width = width
        this.height = height
        initCircle()
    }

    override fun setValue(value: Int) {
        plusAngle = finishAngle * value / max
    }

    private fun sweepGradient(): SweepGradient {
        val sweepGradient: SweepGradient
        sweepGradient =
            SweepGradient(width * 0.5f, width * 0.5f, intArrayOf(startColor, endColor), null)
        return sweepGradient
    }

    init {
        this.mIsDashEnable = isDashEnable
        this.startColor = startColor
        this.endColor = endColor
        initSize()
        init()
    }
}