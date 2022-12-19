package com.sep.gothere.helpers.color

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.sep.gothere.component.dynamic.schema.AccentStylableView
import com.sep.gothere.util.CommonColorUtils


class FeatureStylizer(
    private val context: Context,
    requestedColor: HSL
) {
    private val accentColors: CommonColorUtils.Companion.AccentColors

    init {
        accentColors = Contraster.getAccentColors(requestedColor, context)
    }

    fun findStylableComponentsAndApplyStyle(parent: ViewGroup) {
        for (i in 0 until parent.childCount) {
            val child: View? = parent.getChildAt(i)
            if (child != null) {
                when (child) {
                    is AccentStylableView -> child.initAccentStyle(accentColors)
                    is CircularProgressIndicator -> child.setIndicatorColor(accentColors.accentColorHC)
                    is LinearProgressIndicator -> child.setIndicatorColor(accentColors.accentColorHC)
                }
                if (child is ViewGroup) findStylableComponentsAndApplyStyle(child)
            }
        }
    }
}