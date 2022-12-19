package com.sep.gothere.component.dynamic.impl.button

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.graphics.ColorUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.sep.gothere.R
import com.sep.gothere.component.dynamic.schema.AccentStylableView
import com.sep.gothere.util.CommonColorUtils

class GTButtonLabel : MaterialButton, AccentStylableView {

    @Suppress("ConvertSecondaryConstructorToPrimary")
    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        applyDefaultStyle()
    }

    //region STABLES
    // -----> Fill Color
    private val defEnabledFillColor =
        MaterialColors.getColor(this, R.attr.GT_basicTransparent, Color.RED)
    private val defDisabledFillColor =
        MaterialColors.getColor(this, R.attr.GT_basicTransparent, Color.RED)
    private val getDefFillColorStateList = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf()
        ),
        intArrayOf(
            defEnabledFillColor,
            defDisabledFillColor
        )
    )
    //endregion

    //region DYNAMICS
    // -----> Text Color
    private var defEnabledTextColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcPrimary, Color.RED)
    private var defDisabledTextColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcTertiary, Color.RED)
    private val getDefTextColorStateList: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf()
                ),
                intArrayOf(
                    defEnabledTextColor,
                    defDisabledTextColor
                )
            )
        }

    // -----> Icon Color
    private var defEnabledIconColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcPrimary, Color.RED)
    private var defDisabledIconColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcTertiary, Color.RED)
    private val getDefIconColorStateList: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf()
                ),
                intArrayOf(
                    defEnabledIconColor,
                    defDisabledIconColor
                )
            )
        }

    // -----> Ripple Color
    private var defRippleColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcTertiary, Color.RED)
    private val getDefRippleColorStateList: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_pressed),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_hovered),
                    intArrayOf(),
                ),
                intArrayOf(
                    defRippleColor,
                    defRippleColor,
                    defRippleColor,
                    defRippleColor
                )
            )
        }
    //endregion

    private fun applyDefaultStyle() {
        // -----> Text Color
        setTextColor(getDefTextColorStateList)
        // -----> Icon Color
        iconTint = getDefIconColorStateList
        // -----> Fill Color
        backgroundTintList = getDefFillColorStateList
        // -----> Ripple Color
        rippleColor = getDefRippleColorStateList
    }

    override fun initAccentStyle(accentColors: CommonColorUtils.Companion.AccentColors) {
        // -----> Text Color
        defEnabledTextColor = accentColors.accentColorHC
        defDisabledTextColor =
            ColorUtils.setAlphaComponent(
                accentColors.accentColorHC,
                Color.alpha(defDisabledTextColor)
            )
        // -----> Icon Color
        defEnabledIconColor = accentColors.accentColorHC
        defDisabledIconColor =
            ColorUtils.setAlphaComponent(
                accentColors.accentColorHC,
                Color.alpha(defDisabledIconColor)
            )
        // -----> Fill Color (STABLE)
        // -----> Ripple Color (Enabled State Color is always transparent)
        defRippleColor = ColorUtils.setAlphaComponent(
            accentColors.accentColorHC,
            Color.alpha(defRippleColor)
        )
        // INVALIDATE
        applyDefaultStyle()
    }

    override fun updateStyleState(styleState: AccentStylableView.StateStyle) {
        throw UnsupportedOperationException("This view's state shouldn't be changed from outside!")
    }
}