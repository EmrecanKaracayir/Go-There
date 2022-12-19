package com.sep.gothere.base

import android.view.animation.Animation
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.sep.gothere.R

open class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (nextAnim == R.anim.enter_from_right || nextAnim == R.anim.exit_to_right) view?.apply {
            ViewCompat.setTranslationZ(
                this, 100f
            )
        } else view?.apply {
            ViewCompat.setTranslationZ(this, 0f)
        }
        return super.onCreateAnimation(transit, enter, nextAnim)
    }
}