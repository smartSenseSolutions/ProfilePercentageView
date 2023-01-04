package com.ss.profilepercentageview

import android.animation.ValueAnimator
import androidx.appcompat.widget.AppCompatImageView
import kotlin.jvm.JvmOverloads
import android.os.Build
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import java.lang.Exception
import kotlin.math.pow

class ProfilePercentageView : AppCompatImageView {
    private val mDrawableRect = RectF()
    private val mBorderRect = RectF()
    private val mShaderMatrix = Matrix()
    private val mBitmapPaint = Paint()
    private val mBorderPaint = Paint()
    private val mCircleBackgroundPaint = Paint()
    private var mBorderColor = DEFAULT_BORDER_COLOR
    private var mBorderWidth = DEFAULT_BORDER_WIDTH
    private var mCircleBackgroundColor = DEFAULT_CIRCLE_BACKGROUND_COLOR
    private var mImageAlpha = DEFAULT_IMAGE_ALPHA
    private var mBitmap: Bitmap? = null
    private var mBitmapCanvas: Canvas? = null
    private var mDrawableRadius = 0f
    private var mBorderRadius = 0f
    private var mColorFilter: ColorFilter? = null
    private var mInitialized = false
    private var mRebuildShader = false
    private var mDrawableDirty = false
    private var mBorderOverlay = false
    private var mDisableCircularTransformation = false



    private var mImagePadding = DEFAULT_MIN_IMG_PADDING

    private var minValue = 0
    private var maxValue = 100
    private var currentValue = 0

    private val duration = 1000
    private val progressDelay: Long = 350
    private var progressLastValue = minValue.toInt()
    private var progressValueAnimator: ValueAnimator? = null
    private var interpolator: Interpolator = AccelerateDecelerateInterpolator()
    private var arcBackgroundPainter: ArcBackgroundPainter? = null
    private var arcProgressPainter: ArcProgressPainter? = null

    private var arcBackgroundColor = Color.parseColor("#E0E0E0")
    private var arcProgressColor = Color.parseColor("#2196F3")
    private var arcProgressStartColor = Color.parseColor("#094e35")
    private var arcProgressEndColor = Color.parseColor("#094e35")

    private var isRounded = true
    private var isDashEnable = false
    private var mDashWidth = DASH_WIDTH
    private var mArcWidth = ARC_WIDTH
    private var mDashSpace = DASH_SPACE
    private val margin = 10

    constructor(context: Context?) : super(context!!) {
        init()
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0) : super(
        context,
        attrs,
        defStyle
    ) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ProfilePercentageView, defStyle, 0)
        mBorderWidth = attributes.getDimension(
            R.styleable.ProfilePercentageView_ppv_imgBorderWidth,
            DEFAULT_BORDER_WIDTH
        )
        mBorderColor = attributes.getColor(R.styleable.ProfilePercentageView_ppv_imgBorderColor, DEFAULT_BORDER_COLOR)
        mBorderOverlay = attributes.getBoolean(R.styleable.ProfilePercentageView_ppv_imgBorderOverlay, DEFAULT_BORDER_OVERLAY)
        mCircleBackgroundColor = attributes.getColor(R.styleable.ProfilePercentageView_ppv_circleBackgroundColor, DEFAULT_CIRCLE_BACKGROUND_COLOR)

        arcBackgroundColor = attributes.getColor(R.styleable.ProfilePercentageView_ppv_arcBackgroundColor, arcBackgroundColor)
        arcProgressColor = attributes.getColor(R.styleable.ProfilePercentageView_ppv_arcProgressColor, arcProgressColor)
        arcProgressStartColor = attributes.getColor(R.styleable.ProfilePercentageView_ppv_arcProgressStartColor, arcProgressStartColor)
        arcProgressEndColor = attributes.getColor(R.styleable.ProfilePercentageView_ppv_arcProgressEndColor, arcProgressEndColor)
        maxValue = attributes.getInt(R.styleable.ProfilePercentageView_ppv_max, maxValue)
        currentValue = attributes.getInt(R.styleable.ProfilePercentageView_ppv_currentValue, currentValue)

        isRounded = attributes.getBoolean(R.styleable.ProfilePercentageView_ppv_isRounded, isRounded)
        isDashEnable = attributes.getBoolean(R.styleable.ProfilePercentageView_ppv_isDashEnable, isDashEnable)
        mDashWidth = attributes.getDimension(R.styleable.ProfilePercentageView_ppv_dashWidth, mDashWidth)
        mArcWidth = attributes.getDimension(R.styleable.ProfilePercentageView_ppv_arcWidth, mArcWidth)
        if(mArcWidth > DEFAULT_MAX_ARC_WIDTH){
            this.mArcWidth = DEFAULT_MAX_ARC_WIDTH
        }
        mDashSpace = attributes.getDimension(R.styleable.ProfilePercentageView_ppv_dashSpace, mDashSpace)

        mImagePadding = attributes.getDimension(R.styleable.ProfilePercentageView_ppv_imgPadding, mImagePadding)

        attributes.recycle()
        init()
    }

    private fun init() {
        mInitialized = true
        super.setScaleType(SCALE_TYPE)
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.isDither = true
        mBitmapPaint.isFilterBitmap = true
        mBitmapPaint.alpha = mImageAlpha
        mBitmapPaint.colorFilter = mColorFilter
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = mBorderColor
        mBorderPaint.strokeWidth = mBorderWidth.toFloat()
        mCircleBackgroundPaint.style = Paint.Style.FILL
        mCircleBackgroundPaint.isAntiAlias = true
        mCircleBackgroundPaint.color = mCircleBackgroundColor
        outlineProvider = OutlineProvider()

        initArcPainters()

        setValue(currentValue)
    }

    private fun initArcPainters(){
        val marginPixels = DimensionUtils.getSizeInPixels(margin.toFloat(), context)

        arcBackgroundPainter = ArcBackgroundPainterImp(arcBackgroundColor, isDashEnable, isRounded,
            mDashWidth, mArcWidth, mDashSpace, marginPixels, context)

        arcProgressPainter = ArcProgressPainterImp(arcProgressColor, isDashEnable, isRounded,
            maxValue.toFloat(), mDashWidth, mArcWidth, mDashSpace, marginPixels, context,
            arcProgressStartColor, arcProgressEndColor)

        initValueAnimator()
    }
    override fun setScaleType(scaleType: ScaleType) {
        require(scaleType == SCALE_TYPE) {
            String.format(
                "ScaleType %s not supported.",
                scaleType
            )
        }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        require(!adjustViewBounds) { "adjustViewBounds not supported." }
    }

    @SuppressLint("CanvasSize")
    override fun onDraw(canvas: Canvas) {
        arcBackgroundPainter?.draw(canvas)
        arcProgressPainter?.draw(canvas)

        if (mDisableCircularTransformation) {
            super.onDraw(canvas)
            return
        }
        if (mCircleBackgroundColor != Color.TRANSPARENT) {
            canvas.drawCircle(
                mDrawableRect.centerX(),
                mDrawableRect.centerY(),
                mDrawableRadius,
                mCircleBackgroundPaint
            )
        }
        if (mBitmap != null) {
            if (mDrawableDirty && mBitmapCanvas != null) {
                mDrawableDirty = false
                val drawable = drawable
                drawable.setBounds(0, 0, mBitmapCanvas!!.width, mBitmapCanvas!!.height)
                drawable.draw(mBitmapCanvas!!)
            }
            if (mRebuildShader) {
                mRebuildShader = false
                val bitmapShader =
                    BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                bitmapShader.setLocalMatrix(mShaderMatrix)
                mBitmapPaint.shader = bitmapShader
            }
            canvas.drawCircle(
                mDrawableRect.centerX(),
                mDrawableRect.centerY(),
                mDrawableRadius,
                mBitmapPaint
            )
        }
        if (mBorderWidth > 0) {
            canvas.drawCircle(
                mBorderRect.centerX(),
                mBorderRect.centerY(),
                mBorderRadius,
                mBorderPaint
            )
        }

        invalidate()
    }

    override fun invalidateDrawable(dr: Drawable) {
        mDrawableDirty = true
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        arcBackgroundPainter?.onSizeChanged(h, w)
        arcProgressPainter?.onSizeChanged(h, w)
        updateDimensions()
        invalidate()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        updateDimensions()
        invalidate()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        updateDimensions()
        invalidate()
    }

    var borderColor: Int
        get() = mBorderColor
        set(borderColor) {
            if (borderColor == mBorderColor) {
                return
            }
            mBorderColor = borderColor
            mBorderPaint.color = borderColor
            invalidate()
        }
    var circleBackgroundColor: Int
        get() = mCircleBackgroundColor
        set(circleBackgroundColor) {
            if (circleBackgroundColor == mCircleBackgroundColor) {
                return
            }
            mCircleBackgroundColor = circleBackgroundColor
            mCircleBackgroundPaint.color = circleBackgroundColor
            invalidate()
        }

    @Deprecated("Use {@link #setCircleBackgroundColor(int)} instead")
    fun setCircleBackgroundColorResource(@ColorRes circleBackgroundRes: Int) {
        circleBackgroundColor = context.resources.getColor(circleBackgroundRes)
    }

    var imagePadding: Float
        get() = mImagePadding
        set(imgPddng) {
            if (imgPddng == mImagePadding) {
                return
            }
            mImagePadding = imgPddng
            updateDimensions()
            invalidate()
        }

    var borderWidth: Float
        get() = mBorderWidth
        set(borderWidth) {
            if (borderWidth == mBorderWidth) {
                return
            }
            mBorderWidth = borderWidth
            mBorderPaint.strokeWidth = borderWidth.toFloat()
            updateDimensions()
            invalidate()
        }
    var isBorderOverlay: Boolean
        get() = mBorderOverlay
        set(borderOverlay) {
            if (borderOverlay == mBorderOverlay) {
                return
            }
            mBorderOverlay = borderOverlay
            updateDimensions()
            invalidate()
        }
    var isDisableCircularTransformation: Boolean
        get() = mDisableCircularTransformation
        set(disableCircularTransformation) {
            if (disableCircularTransformation == mDisableCircularTransformation) {
                return
            }
            mDisableCircularTransformation = disableCircularTransformation
            if (disableCircularTransformation) {
                mBitmap = null
                mBitmapCanvas = null
                mBitmapPaint.shader = null
            } else {
                initializeBitmap()
            }
            invalidate()
        }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        initializeBitmap()
        invalidate()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
        invalidate()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
        invalidate()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
        invalidate()
    }

    override fun setImageAlpha(alpha: Int) {
        var mAlpha = alpha
        mAlpha = mAlpha and 0xFF
        if (mAlpha == mImageAlpha) {
            return
        }
        mImageAlpha = mAlpha

        // This might be called during ImageView construction before
        // member initialization has finished on API level >= 16.
        if (mInitialized) {
            mBitmapPaint.alpha = mAlpha
            invalidate()
        }
    }

    override fun getImageAlpha(): Int {
        return mImageAlpha
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (cf === mColorFilter) {
            return
        }
        mColorFilter = cf

        // This might be called during ImageView construction before
        // member initialization has finished on API level <= 19.
        if (mInitialized) {
            mBitmapPaint.colorFilter = cf
            invalidate()
        }
    }

    override fun getColorFilter(): ColorFilter {
        return mColorFilter!!
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    BITMAP_CONFIG
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun initializeBitmap() {
        mBitmap = getBitmapFromDrawable(drawable)
        mBitmapCanvas = if (mBitmap != null && mBitmap!!.isMutable) {
            Canvas(mBitmap!!)
        } else {
            null
        }
        if (!mInitialized) {
            return
        }
        if (mBitmap != null) {
            updateShaderMatrix()
        } else {
            mBitmapPaint.shader = null
        }
    }

    private fun updateDimensions() {
        mBorderRect.set(calculateBounds())
        mBorderRadius = Math.min(
            (mBorderRect.height() - mBorderWidth) / 2.0f,
            (mBorderRect.width() - mBorderWidth) / 2.0f
        )
        mDrawableRect.set(mBorderRect)
        if (!mBorderOverlay && mBorderWidth > 0) {
            mDrawableRect.inset(mBorderWidth - 1.0f, mBorderWidth - 1.0f)
        }
        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f)
        updateShaderMatrix()
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val sideLength = Math.min(availableWidth, availableHeight)
        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f

        if(mImagePadding < 30){
            mImagePadding = DEFAULT_MIN_IMG_PADDING
        }else if(mImagePadding > 130){
            mImagePadding = DEFAULT_MAX_IMG_PADDING
        }
        val imgPadding = mImagePadding + mArcWidth

        return RectF(left+imgPadding, top+imgPadding, (left + sideLength - imgPadding), (top + sideLength - imgPadding))
    }

    private fun updateShaderMatrix() {
        if (mBitmap == null) {
            return
        }
        val scale: Float
        var dx = 0f
        var dy = 0f
        mShaderMatrix.set(null)
        val bitmapHeight = mBitmap!!.height
        val bitmapWidth = mBitmap!!.width
        if (bitmapWidth * mDrawableRect.height() > mDrawableRect.width() * bitmapHeight) {
            scale = mDrawableRect.height() / bitmapHeight.toFloat()
            dx = (mDrawableRect.width() - bitmapWidth * scale) * 0.5f
        } else {
            scale = mDrawableRect.width() / bitmapWidth.toFloat()
            dy = (mDrawableRect.height() - bitmapHeight * scale) * 0.5f
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(
            (dx + 0.5f).toInt() + mDrawableRect.left,
            (dy + 0.5f).toInt() + mDrawableRect.top
        )
        mRebuildShader = true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDisableCircularTransformation) {
            super.onTouchEvent(event)
        } else inTouchableArea(event.x, event.y) && super.onTouchEvent(event)
    }

    private fun inTouchableArea(x: Float, y: Float): Boolean {
        return if (mBorderRect.isEmpty) {
            true
        } else (x - mBorderRect.centerX()).toDouble().pow(2.0) + (y - mBorderRect.centerY()).toDouble()
            .pow(2.0) <= mBorderRadius.toDouble().pow(2.0)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private inner class OutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            if (mDisableCircularTransformation) {
                BACKGROUND.getOutline(view, outline)
            } else {
                val bounds = Rect()
                mBorderRect.roundOut(bounds)
                outline.setRoundRect(bounds, bounds.width() / 2.0f)
            }
        }
    }

    private inner class ProgressAnimatorListenerImp : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
            val value = valueAnimator.animatedValue as Float
            val intValue = value.toInt()
            updateValueProgress(intValue)
            progressLastValue = intValue
        }
    }

    private fun updateValueProgress(value: Int) {
        arcProgressPainter?.setValue(value)
    }

    fun setArcWidth(value: Float){
        if(value > DEFAULT_MAX_ARC_WIDTH){
            this.mArcWidth = DEFAULT_MAX_ARC_WIDTH
        }else{
            this.mArcWidth = value
        }

        arcBackgroundPainter?.setArcWidth(value)
        arcProgressPainter?.setArcWidth(value)
        updateDimensions()
        invalidate()
    }

    fun setDashEnable(value: Boolean){
        this.isDashEnable = value

        arcBackgroundPainter?.setDashEnable(value)
        arcProgressPainter?.setDashEnable(value)
        invalidate()
    }

    fun setRounded(value: Boolean){
        this.isRounded = value

        arcBackgroundPainter?.setRounded(value)
        arcProgressPainter?.setRounded(value)
        invalidate()
    }

    fun getCurrentValue(): Int{
        return currentValue
    }

    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
        if (currentValue in minValue..maxValue) {
            animateProgressValue()
        }
    }

    fun setValue(value: Int) {
        this.currentValue = value
        if (value in minValue..maxValue) {
            animateProgressValue()
        }
    }

    fun setValue(value: Int, animate: Boolean) {
        this.currentValue = value
        if (value in minValue..maxValue) {
            if (!animate) {
                updateValueProgress(value)
            } else {
                animateProgressValue()
            }
        }
    }

    private fun initValueAnimator() {
        progressValueAnimator = ValueAnimator()
        progressValueAnimator!!.interpolator = interpolator
        progressValueAnimator!!.addUpdateListener(ProgressAnimatorListenerImp())
    }

    private fun animateProgressValue() {
        if (progressValueAnimator != null) {
            progressValueAnimator!!.setFloatValues(progressLastValue.toFloat(), currentValue.toFloat())
            progressValueAnimator!!.duration = duration + progressDelay
            progressValueAnimator!!.start()
        }
    }

    fun setProgress(interpolator: Interpolator) {
        this.interpolator = interpolator
        if (progressValueAnimator != null) {
            progressValueAnimator!!.interpolator = interpolator
        }
    }

    companion object {
        private val SCALE_TYPE = ScaleType.CENTER_CROP
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private const val COLOR_DRAWABLE_DIMENSION = 2
        private const val DEFAULT_BORDER_WIDTH = 0f
        private const val DEFAULT_BORDER_COLOR = Color.BLACK
        private const val DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.TRANSPARENT
        private const val DEFAULT_IMAGE_ALPHA = 255
        private const val DEFAULT_BORDER_OVERLAY = false
        private const val DEFAULT_MIN_IMG_PADDING = 28f
        private const val DEFAULT_MAX_IMG_PADDING = 130f

        private const val DEFAULT_MIN_ARC_WIDTH = 12f
        private const val DEFAULT_MAX_ARC_WIDTH = 80f

        private const val DASH_WIDTH = 4f
        private const val ARC_WIDTH = DEFAULT_MIN_ARC_WIDTH
        private const val DASH_SPACE = 8f
    }
}