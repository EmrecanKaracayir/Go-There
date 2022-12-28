package com.sep.gothere.component.dynamic.impl.layout

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.color.MaterialColors
import com.sep.gothere.R
import com.sep.gothere.component.dynamic.schema.AccentStylableView
import com.sep.gothere.helpers.color.Contraster
import com.sep.gothere.helpers.color.HSL
import com.sep.gothere.util.CommonColorUtils

class GTLayoutAccentHeader : ConstraintLayout, AccentStylableView {

    @Suppress("ConvertSecondaryConstructorToPrimary")
    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        applyDefaultStyle()
    }

    private fun applyDefaultStyle() {
        val gimmickAccentColors = Contraster.getAccentColors(
            HSL(MaterialColors.getColor(this, R.attr.GT_basicHcPrimary)),
            context
        )
        val gimmickAccentColorLC = gimmickAccentColors.accentColorLC
        val gimmickAccentGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(gimmickAccentColorLC, Color.TRANSPARENT)
        )
        background = gimmickAccentGradientDrawable
    }

    override fun initAccentStyle(accentColors: CommonColorUtils.Companion.AccentColors) {
        val accentGradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(accentColors.accentColorLC, Color.TRANSPARENT)
        )
        background = accentGradientDrawable
//        val transitionDrawables = arrayOf(background, accentGradientDrawable)
//        val mTransition = TransitionDrawable(transitionDrawables)
//        mTransition.isCrossFadeEnabled = true
//        background = mTransition
//        mTransition.startTransition(SHORT_ANIMATION_DURATION_MS)
    }

    override fun updateStyleState(styleState: AccentStylableView.StateStyle) {
        throw UnsupportedOperationException("This view's state shouldn't be changed!")
    }
}