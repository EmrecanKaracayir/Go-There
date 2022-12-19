package com.sep.gothere.component.dynamic.impl.checkbox

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.graphics.ColorUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.color.MaterialColors
import com.sep.gothere.R
import com.sep.gothere.component.dynamic.schema.AccentStylableView
import com.sep.gothere.util.CommonColorUtils

class GTCheckBox : MaterialCheckBox, AccentStylableView {

    @Suppress("ConvertSecondaryConstructorToPrimary")
    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        applyDefaultStyle()
    }

    //region STABLE
    // -----> Fill
    private val getDefLabelTextColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcPrimary, Color.RED)
    //endregion

    //region DYNAMIC
    // -----> Fill
    private val defUncheckedFillColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcSecondary, Color.RED)
    private var defCheckedIndeterminateFillColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcPrimary, Color.RED)
    private val defErrorFillColor =
        MaterialColors.getColor(this, R.attr.GT_paletteRed, Color.RED)
    private val defDisabledFillColor =
        MaterialColors.getColor(this, R.attr.GT_basicLcTertiary, Color.RED)
    private val getDefFillColorStateList: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(com.google.android.material.R.attr.state_error),
                    intArrayOf(com.google.android.material.R.attr.state_indeterminate),
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(
                    defDisabledFillColor,
                    defErrorFillColor,
                    defCheckedIndeterminateFillColor,
                    defCheckedIndeterminateFillColor,
                    defUncheckedFillColor
                )
            )
        }
    //endregion

    private fun applyDefaultStyle() {
        // -----> Fill Color
        buttonTintList = getDefFillColorStateList
        // -----> Icon Color
        buttonIconTintList = MaterialColors.getColorStateList(
            context,
            R.attr.GT_basicHcPrimary,
            ColorStateList.valueOf(R.attr.GT_basicHcPrimary)
        )
        // -----> Label Color
        setTextColor(getDefLabelTextColor)
    }

    override fun initAccentStyle(accentColors: CommonColorUtils.Companion.AccentColors) {
        defCheckedIndeterminateFillColor = accentColors.accentColorLC
        applyDefaultStyle()
    }

    override fun updateStyleState(styleState: AccentStylableView.StateStyle) {
        throw UnsupportedOperationException("This view's state shouldn't be changed from outside!")
    }
}