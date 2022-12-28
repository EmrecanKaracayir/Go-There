package com.sep.gothere.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.io.Serializable

fun Fragment.showSnackbar(
    message: String, duration: Int = Snackbar.LENGTH_LONG, view: View = requireView()
) {
    Snackbar.make(view, message, duration).show()
}

fun <T1, T2, T3, T4, T5, T6, T7, R> customCombine7(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
): Flow<R> = combine(
    combine(flow, flow2, flow3, ::Triple), combine(flow4, flow5, flow6, flow7, ::Quadruple)
) { t1, t2 ->
    transform(
        t1.first, t1.second, t1.third, t2.first, t2.second, t2.third, t2.fourth
    )
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> customCombine10(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R
): Flow<R> = combine(
    combine(flow, flow2, flow3, ::Triple), combine(flow4, flow5, flow6, flow7, ::Quadruple), combine(flow8, flow9, flow10, ::Triple),
) { t1, t2, t3 ->
    transform(
        t1.first, t1.second, t1.third, t2.first, t2.second, t2.third, t2.fourth, t3.first, t3.second, t3.third,
    )
}

data class Quadruple<out A, out B, out C, out D>(
    val first: A, val second: B, val third: C, val fourth: D
) : Serializable

val <T> T.exhaustive: T
    get() = this