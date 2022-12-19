package com.sep.gothere.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.core.view.postDelayed
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.color.MaterialColors
import com.sep.gothere.current.UserCredentials.Companion.DEBUG_USER_LOGGED_IN
import com.sep.gothere.databinding.ActivityBaseBinding
import com.sep.gothere.navigation.*
import com.sep.gothere.util.CustomFitsSystemUI
import com.sep.gothere.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBaseBinding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navbarOps(binding)

        CustomFitsSystemUI().fitsSystemUI(
            binding.navbarContainerFITSBOTTOM, arrayListOf(
                CustomFitsSystemUI.RequestedInset.BOTTOM
            )
        )

        if (savedInstanceState != null) {
            loadSession(supportFragmentManager)
            if (DEBUG_USER_LOGGED_IN) {
                updateNavbar(binding)
                bringNavbar()
            }
        } else {
            if (DEBUG_USER_LOGGED_IN) {
                loggedIn()
            } else {
                navigateToRoot(
                    supportFragmentManager,
                    NavigationInfoProvider.NavigationRoot.ROOT_PRELOG
                )
                binding.baseLayout.postDelayed({ loggedIn() }, 5000)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBackRequested()
            }
        })
    }

    private fun navbarOps(binding: ActivityBaseBinding) {
        binding.exploreNavButton.setOnClickListener {
            navigateToBranch(
                supportFragmentManager,
                NavigationInfoProvider.NavigationBranch.BRANCH_EXPLORE
            )
            updateNavbar(binding)
        }
        binding.searchNavButton.setOnClickListener {
            navigateToBranch(
                supportFragmentManager,
                NavigationInfoProvider.NavigationBranch.BRANCH_SEARCH
            )
            updateNavbar(binding)
        }
        binding.profileNavButton.setOnClickListener {
            navigateToBranch(
                supportFragmentManager,
                NavigationInfoProvider.NavigationBranch.BRANCH_PROFILE
            )
            updateNavbar(binding)
        }
        binding.businessNavButton.setOnClickListener {
            navigateToBranch(
                supportFragmentManager,
                NavigationInfoProvider.NavigationBranch.BRANCH_BUSINESS
            )
            updateNavbar(binding)
        }
    }

    private fun updateNavbar(binding: ActivityBaseBinding) {
        val currentBranch = getCurrentBranch(supportFragmentManager)
        binding.exploreNavButton.alpha = MaterialColors.ALPHA_MEDIUM
        binding.searchNavButton.alpha = MaterialColors.ALPHA_MEDIUM
        binding.profileNavButton.alpha = MaterialColors.ALPHA_MEDIUM
        binding.businessNavButton.alpha = MaterialColors.ALPHA_MEDIUM
        when (currentBranch) {
            NavigationInfoProvider.NavigationBranch.BRANCH_EXPLORE -> binding.exploreNavButton.alpha =
                MaterialColors.ALPHA_FULL
            NavigationInfoProvider.NavigationBranch.BRANCH_SEARCH -> binding.searchNavButton.alpha =
                MaterialColors.ALPHA_FULL
            NavigationInfoProvider.NavigationBranch.BRANCH_PROFILE -> binding.profileNavButton.alpha =
                MaterialColors.ALPHA_FULL
            NavigationInfoProvider.NavigationBranch.BRANCH_BUSINESS -> binding.businessNavButton.alpha =
                MaterialColors.ALPHA_FULL
            else -> throw IllegalArgumentException("Navbar shouldn't update with \"${currentBranch.name}\" branch!")
        }.exhaustive
    }

    fun loggedIn() {
        DEBUG_USER_LOGGED_IN = true
        navigateToRoot(supportFragmentManager, NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG)
        updateNavbar(binding)
        bringNavbar()
    }

    /*
    fun loggedOut() {
        navigateToRoot(supportFragmentManager, NavigationInfoProvider.NavigationRoot.ROOT_PRELOG)
        hideNavbar()
    }
    */

    private fun bringNavbar() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.baseLayout)
        constraintSet.clear(binding.navbarContainerFITSBOTTOM.id, ConstraintSet.TOP)
        constraintSet.connect(
            binding.navbarContainerFITSBOTTOM.id,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM
        )
        val transition = ChangeBounds()
        transition.interpolator = DecelerateInterpolator()
        transition.duration = 300
        TransitionManager.beginDelayedTransition(binding.baseLayout, transition)
        constraintSet.applyTo(binding.baseLayout)
    }

    /*
    private fun hideNavbar() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.baseLayout)
        constraintSet.clear(binding.navbarContainerFITSBOTTOM.id, ConstraintSet.BOTTOM)
        constraintSet.connect(
            binding.navbarContainerFITSBOTTOM.id,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM
        )
        val transition = ChangeBounds()
        transition.interpolator = DecelerateInterpolator()
        transition.duration = 300
        TransitionManager.beginDelayedTransition(binding.baseLayout, transition)
        constraintSet.applyTo(binding.baseLayout)
    }
     */

    fun navigateBackRequested() {
        navigateToPrevious(supportFragmentManager)
    }

    fun fragmentRequested(navigationTAG: NavigationTAG, animate: Boolean) {
        navigateToNext(supportFragmentManager, navigationTAG, animate)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}