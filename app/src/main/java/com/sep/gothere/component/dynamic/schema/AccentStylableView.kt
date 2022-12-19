package com.sep.gothere.component.dynamic.schema

import com.sep.gothere.util.CommonColorUtils

interface AccentStylableView {

    fun initAccentStyle(accentColors: CommonColorUtils.Companion.AccentColors)

    fun updateStyleState(styleState: StateStyle)

    enum class StateStyle {
        DEFAULT_STYLE_STATE,
        SUCCESS_STYLE_STATE,
        ERROR_STYLE_STATE
    }

}