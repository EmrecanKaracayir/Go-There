package com.sep.gothere.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import com.google.android.material.color.MaterialColors
import com.google.android.renderscript.Toolkit
import com.sep.gothere.R

class GTGlassSurface @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val overlayLuminosityBlendLayerColor =
        MaterialColors.getColor(this, R.attr.GT_colorLayoutBlurLuminosityLayer, Color.BLACK)
    private val luminosityBlendPaint = Paint()

    private val overlayColorBlendLayerColor =
        MaterialColors.getColor(this, R.attr.GT_colorLayoutBlendLayer, Color.BLACK)
    private val colorBlendPaint = Paint()


    private val mBlurRadius = 24
    private val mScaleFactor = 8

    private var mBlurredBitmap: Bitmap? = null
    private var mLastDrawnBitmap: Bitmap? = null
    private var mBlurringBitmap: Bitmap? = null
    private var mBlurringCanvas: Canvas? = null

    private var mTargetView = context.getActivity()?.window?.decorView!!

    private var mRectSrc = Rect()
    private var mRectDst = Rect()

    private var mDifferentRoot = false
    private var mIsRendering = false
    private var mRenderingCount = 0

    init {
        luminosityBlendPaint.color = overlayLuminosityBlendLayerColor
        luminosityBlendPaint.blendMode = BlendMode.LUMINOSITY
        luminosityBlendPaint.alpha = 184 // 72%

        colorBlendPaint.color = overlayColorBlendLayerColor
        colorBlendPaint.blendMode = BlendMode.COLOR
        colorBlendPaint.alpha = 102 // 40%
    }

    private val mBlurPreDrawListener = ViewTreeObserver.OnPreDrawListener {
        val targetLocation = IntArray(2)
        if (isShown && prepare()) {
            val redrawBitmap = mBlurredBitmap != mLastDrawnBitmap

            mTargetView.getLocationOnScreen(targetLocation)
            var x = -targetLocation[0]
            var y = -targetLocation[1]

            getLocationOnScreen(targetLocation)
            x += targetLocation[0]
            y += targetLocation[1]

            mBlurringBitmap!!.eraseColor(Color.TRANSPARENT)

            val saveC: Int = mBlurringCanvas!!.save()
            mIsRendering = true
            mRenderingCount++

            mBlurringCanvas!!.scale(1 / mScaleFactor.toFloat(), 1 / mScaleFactor.toFloat())
            mBlurringCanvas!!.translate(
                (-targetLocation[0]).toFloat(), -targetLocation[1].toFloat()
            )
            mTargetView.draw(mBlurringCanvas)

            mIsRendering = false
            mRenderingCount--
            mBlurringCanvas!!.restoreToCount(saveC)

            mBlurredBitmap = Toolkit.blur(mBlurringBitmap!!, mBlurRadius)

            if (redrawBitmap || mDifferentRoot) invalidate()
        }
        return@OnPreDrawListener true
    }

    private fun prepare(): Boolean {
        if (mBlurringBitmap == null) mBlurringBitmap = Bitmap.createBitmap(
            (measuredWidth / mScaleFactor), (measuredHeight / mScaleFactor), Bitmap.Config.ARGB_8888
        )
        if (mBlurringBitmap == null) return false
        if (mBlurringCanvas == null) mBlurringCanvas = Canvas(mBlurringBitmap!!)
        if (mBlurredBitmap == null) mBlurredBitmap = Bitmap.createBitmap(
            (measuredWidth / mScaleFactor), (measuredHeight / mScaleFactor), Bitmap.Config.ARGB_8888
        )
        return mBlurredBitmap != null
    }

    override fun draw(canvas: Canvas?) {
        if (!mIsRendering || mRenderingCount == 0) super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBlurredBitmap(canvas, mBlurredBitmap)
    }

    private fun drawBlurredBitmap(
        canvas: Canvas, blurredBitmap: Bitmap?
    ) {
        if (blurredBitmap != null) {
            mRectSrc.right = blurredBitmap.width
            mRectSrc.bottom = blurredBitmap.height
            mRectDst.right = width
            mRectDst.bottom = height
            canvas.drawBitmap(blurredBitmap, mRectSrc, mRectDst, null)
            mLastDrawnBitmap = Bitmap.createBitmap(blurredBitmap)
        }
        canvas.drawRect(mRectDst, luminosityBlendPaint)
        canvas.drawRect(mRectDst, colorBlendPaint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mTargetView.viewTreeObserver.addOnPreDrawListener(mBlurPreDrawListener)
        mDifferentRoot = mTargetView.rootView != rootView
        if (mDifferentRoot) {
            mTargetView.postInvalidate()
        }
    }

    override fun onDetachedFromWindow() {
        mTargetView.viewTreeObserver.removeOnPreDrawListener(mBlurPreDrawListener)
        releaseBitmap()
        super.onDetachedFromWindow()
    }

    private fun releaseBitmap() {
        if (mBlurringBitmap != null) {
            mBlurringBitmap!!.recycle()
            mBlurringBitmap = null
        }
        if (mBlurredBitmap != null) {
            mBlurredBitmap!!.recycle()
            mBlurredBitmap = null
        }
    }

    private tailrec fun Context.getActivity(): Activity? =
        this as? Activity ?: (this as? ContextWrapper)?.baseContext?.getActivity()
}