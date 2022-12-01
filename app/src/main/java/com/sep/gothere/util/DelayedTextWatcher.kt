package com.sep.gothere.util

import android.text.Editable
import android.text.TextWatcher
import java.util.*

class DelayedTextWatcher(private val afterTextChangedLambda: (text: Editable?) -> Unit = {}) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    private var timer = Timer()
    private val delay: Long = 1000 // Milliseconds

    override fun afterTextChanged(s: Editable?) {
        timer.cancel()
        timer = Timer()
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    afterTextChangedLambda(s)
                }

            }, delay
        )
    }
}