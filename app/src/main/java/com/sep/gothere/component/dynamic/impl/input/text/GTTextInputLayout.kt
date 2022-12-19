package com.sep.gothere.component.dynamic.impl.input.text

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.MaterialColors
import com.google.android.material.textfield.TextInputLayout
import com.sep.gothere.R
import com.sep.gothere.component.dynamic.schema.AccentStylableView
import com.sep.gothere.util.CommonColorUtils
import com.sep.gothere.util.exhaustive

class GTTextInputLayout : TextInputLayout, AccentStylableView {

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
    // -----> Helper Text
    private val defEnabledHoveredFocusedHelperTextColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcSecondary, Color.BLUE)
    private val defActivatedHelperTextColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcPrimary, Color.BLUE)
    private val defDisabledHelperTextColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcTertiary, Color.BLUE)
    private val getDefHelperTextColorStateList: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_activated),
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_hovered),
                    intArrayOf()
                ),
                intArrayOf(
                    defActivatedHelperTextColor,
                    defDisabledHelperTextColor,
                    defEnabledHoveredFocusedHelperTextColor,
                    defEnabledHoveredFocusedHelperTextColor,
                    defEnabledHoveredFocusedHelperTextColor,
                )
            )
        }

    // -----> All ERROR Colors
    private val getDefErrorColors =
        MaterialColors.getColorStateList(
            context, R.attr.GT_paletteRed,
            ColorStateList.valueOf(R.attr.GT_paletteRed)
        )
    private val getSucErrorColors =
        MaterialColors.getColorStateList(
            context, R.attr.GT_paletteGreen,
            ColorStateList.valueOf(R.attr.GT_paletteGreen)
        )
    //endregion

    //region DYNAMICS
    // -----> Hint Text
    private val defEnabledTextHintColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcSecondary, Color.BLUE)
    private var defFocusedTextHintColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcPrimary, Color.BLUE)
    private val defDisabledTextHintColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcTertiary, Color.BLUE)
    private val getDefTextHintColorStateList: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf()
                ),
                intArrayOf(
                    defDisabledTextHintColor,
                    defFocusedTextHintColor,
                    defEnabledTextHintColor
                )
            )
        }
    private val sucEnabledTextHintColor = ColorUtils.setAlphaComponent(
        MaterialColors.getColor(this, R.attr.GT_paletteGreen, Color.BLUE),
        Color.alpha(defEnabledTextHintColor)
    )
    private val sucFocusedTextHintColor =
        MaterialColors.getColor(this, R.attr.GT_paletteGreen, Color.BLUE)
    private val sucDisabledTextHintColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcTertiary, Color.BLUE)
    private val getSucTextHintColorStateList: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf()
                ),
                intArrayOf(
                    sucDisabledTextHintColor,
                    sucFocusedTextHintColor,
                    sucEnabledTextHintColor
                )
            )
        }

    // -----> Box Stroke
    private val defEnabledBoxStrokeColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcSecondary, Color.BLUE)
    private var defHoveredFocusedBoxStrokeColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcPrimary, Color.BLUE)
    private val defDisabledBoxStrokeColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcTertiary, Color.BLUE)
    private val getDefBoxStrokeColorStateList: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_hovered),
                    intArrayOf()
                ),
                intArrayOf(
                    defDisabledBoxStrokeColor,
                    defHoveredFocusedBoxStrokeColor,
                    defHoveredFocusedBoxStrokeColor,
                    defEnabledBoxStrokeColor
                )
            )
        }
    private val sucEnabledBoxStrokeColor =
        MaterialColors.getColor(this, R.attr.GT_paletteGreen, Color.BLUE)
    private var sucHoveredFocusedBoxStrokeColor =
        MaterialColors.getColor(this, R.attr.GT_paletteGreen, Color.BLUE)
    private val sucDisabledBoxStrokeColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcTertiary, Color.BLUE)
    private val getSucBoxStrokeColorStateList: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_hovered),
                    intArrayOf()
                ),
                intArrayOf(
                    sucDisabledBoxStrokeColor,
                    sucHoveredFocusedBoxStrokeColor,
                    sucHoveredFocusedBoxStrokeColor,
                    sucEnabledBoxStrokeColor
                )
            )
        }


    // -----> Icons (Leading, Trailing, Prefix, Suffix)
    private val defEnabledIconsColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcSecondary, Color.BLUE)
    private val defHoveredFocusedIconsColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcPrimary, Color.BLUE)
    private var defActivatedIconsColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcPrimary, Color.BLUE)
    private val defDisabledIconsColor =
        MaterialColors.getColor(this, R.attr.GT_basicHcTertiary, Color.BLUE)
    private val getDefIconsColorStateList: ColorStateList
        get() {
            return ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_activated),
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_hovered),
                    intArrayOf()
                ),
                intArrayOf(
                    defActivatedIconsColor,
                    defDisabledIconsColor,
                    defHoveredFocusedIconsColor,
                    defHoveredFocusedIconsColor,
                    defEnabledIconsColor
                )
            )
        }
    //endregion

    private fun applyDefaultStyle() {
        // -----> Hint Text When Filled
        hintTextColor = ColorStateList.valueOf(Color.YELLOW)
        // -----> Hint Text
        defaultHintTextColor = getDefTextHintColorStateList
        // -----> Box Stroke
        setBoxStrokeColorStateList(getDefBoxStrokeColorStateList)
        // -----> ERROR Box Stroke
        boxStrokeErrorColor = getDefErrorColors
        // -----> Leading Icon
        setStartIconTintList(getDefIconsColorStateList)
        // -----> Trailing Icon
        setEndIconTintList(getDefIconsColorStateList)
        // -----> ERROR Icon
        setErrorIconTintList(getDefErrorColors)
        // -----> Helper Text
        setHelperTextColor(getDefHelperTextColorStateList)
        // -----> ERROR Text
        setErrorTextColor(getDefErrorColors)
        // -----> Counter Overflow
        counterOverflowTextColor = getDefErrorColors
        // -----> Prefix Text
        setPrefixTextColor(getDefIconsColorStateList)
        // -----> Suffix Text
        setSuffixTextColor(getDefIconsColorStateList)
    }

    override fun initAccentStyle(accentColors: CommonColorUtils.Companion.AccentColors) {
        // -----> Hint Text
        defFocusedTextHintColor = accentColors.accentColorHC
        // -----> Box Stroke
        defHoveredFocusedBoxStrokeColor = accentColors.accentColorHC
        // -----> Icons (Leading, Trailing, Prefix, Suffix)
        defActivatedIconsColor = accentColors.accentColorHC
        // INVALIDATE
        applyDefaultStyle()
    }

    override fun updateStyleState(styleState: AccentStylableView.StateStyle) {
        when (styleState) {
            AccentStylableView.StateStyle.DEFAULT_STYLE_STATE, AccentStylableView.StateStyle.ERROR_STYLE_STATE -> {
                applyDefaultStyle()
                // HackFix for the expanded hint text not updating properly problem.
                isExpandedHintEnabled = false
                isExpandedHintEnabled = true
            }
            AccentStylableView.StateStyle.SUCCESS_STYLE_STATE -> {
                defaultHintTextColor = getSucTextHintColorStateList
                boxStrokeErrorColor = getSucErrorColors
                setBoxStrokeColorStateList(getSucBoxStrokeColorStateList)
                setErrorTextColor(getSucErrorColors)
                counterOverflowTextColor = getSucErrorColors
            }
        }.exhaustive
    }
}