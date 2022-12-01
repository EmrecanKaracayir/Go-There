package com.sep.gothere.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.sep.gothere.R
import com.sep.gothere.databinding.ActivityBaseBinding
import com.sep.gothere.features.explore.ui.ExploreFragment
import com.sep.gothere.features.signup.ui.SignUpFragment
import com.sep.gothere.features.welcome.ui.WelcomeFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBaseBinding

    private lateinit var welcomeFragment: WelcomeFragment
    private lateinit var signUpFragment: SignUpFragment
    private lateinit var exploreFragment: ExploreFragment

    private var fragmentStack: ArrayList<Fragment> = ArrayList()

    private val currentIndexInFragmentStack: Int
        get() {
            return fragmentStack.lastIndex
        }

    private fun selectFragmentForward(selectedFragment: Fragment) {
        val fragmentToDetach = fragmentStack.last()
        fragmentStack.add(selectedFragment)
        supportFragmentManager.beginTransaction().setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_right,
            R.anim.exit_to_left
        ).detach(fragmentToDetach).attach(fragmentStack.last()).setReorderingAllowed(true).commit()
    }

    private fun selectFragmentBackward() {
        supportFragmentManager.beginTransaction().setCustomAnimations(
            R.anim.enter_from_left,
            R.anim.exit_to_right,
            R.anim.enter_from_left,
            R.anim.exit_to_right,
        ).detach(fragmentStack.removeLast()).attach(fragmentStack.last()).setReorderingAllowed(true)
            .commit()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!resources.getBoolean(R.bool.isTablet)) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bringNavbar()

        if (savedInstanceState == null) {
            addInitialFragments()
            supportFragmentManager.beginTransaction().attach(fragmentStack.last())
                .setReorderingAllowed(true).commit()
        } else {
            findFragments()
            supportFragmentManager.beginTransaction().attach(fragmentStack.last())
                .setReorderingAllowed(true).commit()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentIndexInFragmentStack != 0) {
                    selectFragmentBackward()
                } else {
                    finish()
                }
            }
        })
    }

    private fun bringNavbar() {
        binding.baseLayout.postDelayed({
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.baseLayout)
            constraintSet.clear(binding.navbar.id, ConstraintSet.TOP)
            constraintSet.connect(
                binding.navbar.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
            val transition = ChangeBounds()
            transition.interpolator = DecelerateInterpolator()
            transition.duration = 300
            TransitionManager.beginDelayedTransition(binding.baseLayout, transition)
            constraintSet.applyTo(binding.baseLayout)
        }, 500)
    }

    private fun addInitialFragments() {
        welcomeFragment = WelcomeFragment()
        signUpFragment = SignUpFragment()
        exploreFragment = ExploreFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, welcomeFragment, FragmentTags.TAG_WELCOME_FRAGMENT.name)
            .add(R.id.fragment_container, signUpFragment, FragmentTags.TAG_SIGN_UP_FRAGMENT.name)
            .add(R.id.fragment_container, exploreFragment, FragmentTags.TAG_EXPLORE_FRAGMENT.name)
            .detach(welcomeFragment).detach(signUpFragment).detach(exploreFragment)
            .setReorderingAllowed(true).commit()

        fragmentStack.add(welcomeFragment)
    }

    private fun findFragments() {
        welcomeFragment =
            supportFragmentManager.findFragmentByTag(FragmentTags.TAG_WELCOME_FRAGMENT.name) as WelcomeFragment
        signUpFragment =
            supportFragmentManager.findFragmentByTag(FragmentTags.TAG_SIGN_UP_FRAGMENT.name) as SignUpFragment
        exploreFragment =
            supportFragmentManager.findFragmentByTag(FragmentTags.TAG_EXPLORE_FRAGMENT.name) as ExploreFragment

        for (savedFragment in savedFragmentStack) {
            when (savedFragment) {
                is WelcomeFragment -> fragmentStack.add(welcomeFragment)
                is SignUpFragment -> fragmentStack.add(signUpFragment)
                is ExploreFragment -> fragmentStack.add(exploreFragment)
            }
        }
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

    override fun onStop() {
        savedFragmentStack = fragmentStack
        super.onStop()
    }

    fun previousFragmentRequested() {
        selectFragmentBackward()
    }

    fun signUpFragmentRequested() {
        selectFragmentForward(signUpFragment)
    }

    fun loggedIn() {
        selectFragmentForward(exploreFragment)
        bringNavbar()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_INDEX, currentIndexInFragmentStack)
    }

    companion object {
        var savedFragmentStack: ArrayList<Fragment> = ArrayList()
        const val KEY_SELECTED_INDEX = "KEY_SELECTED_INDEX"

        enum class FragmentTags {
            TAG_WELCOME_FRAGMENT, TAG_SIGN_UP_FRAGMENT, TAG_EXPLORE_FRAGMENT
        }
    }
}