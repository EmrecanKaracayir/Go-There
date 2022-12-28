package com.sep.gothere.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CustomFitsSystemUI {

    enum class RequestedInset {
        START, TOP, END, BOTTOM
    }

    fun fitsSystemUI(view: View, requestedInsets: List<RequestedInset>) {
        val isLTR = ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR

        val paddingStart = view.paddingStart
        val paddingTop = view.paddingTop
        val paddingEnd = view.paddingEnd
        val paddingBottom = view.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insetTypes =
                WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.systemBars()
            val insets = windowInsets.getInsets(insetTypes)

            var updatedPaddingStart = paddingStart
            var updatedPaddingTop = paddingTop
            var updatedPaddingEnd = paddingEnd
            var updatedPaddingBottom = paddingBottom

            for (requestedInset in requestedInsets) {
                when (requestedInset) {
                    RequestedInset.START -> updatedPaddingStart += if (isLTR) insets.left else insets.right
                    RequestedInset.TOP -> updatedPaddingTop += insets.top
                    RequestedInset.END -> updatedPaddingEnd += if (isLTR) insets.right else insets.left
                    RequestedInset.BOTTOM -> updatedPaddingBottom += insets.bottom
                }.exhaustive
            }
            v.setPaddingRelative(updatedPaddingStart, updatedPaddingTop, updatedPaddingEnd, updatedPaddingBottom)
            WindowInsetsCompat.CONSUMED
        }
    }
}