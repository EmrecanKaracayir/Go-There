package com.sep.gothere.features.root_postlog.profile.branch_profile.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sep.gothere.R
import com.sep.gothere.base.BaseActivity
import com.sep.gothere.base.BaseFragment
import com.sep.gothere.databinding.FragmentProfileBinding
import com.sep.gothere.helpers.color.FeatureStylizer
import com.sep.gothere.helpers.color.HSL
import com.sep.gothere.navigation.NavigationInfoProvider
import com.sep.gothere.util.CustomFitsSystemUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile), NavigationInfoProvider {

    // THIS PAGE HAS RANDOMLY SELECTED STATIC ACCENT COLOR
    private val accentHSL = HSL(
        Color.rgb(
            (Math.random() * 200).toInt(),
            (Math.random() * 200).toInt(),
            (Math.random() * 255).toInt()
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        FragmentProfileBinding.bind(view).apply {
            val fs = FeatureStylizer(requireContext(), accentHSL)
            root.doOnLayout {
                lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        fs.findStylableComponentsAndApplyStyle(
                            root
                        )
                    }
                }
            }

            CustomFitsSystemUI().fitsSystemUI(
                exploreFragmentHeroLayoutFITSTOP, arrayListOf(
                    CustomFitsSystemUI.RequestedInset.TOP
                )
            )


            logOutButton.setOnClickListener {
                (activity as BaseActivity).loggedOut()
            }
        }
    }

    override fun getNavigationRoot() = NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG
    override fun getNavigationBranch() = NavigationInfoProvider.NavigationBranch.BRANCH_PROFILE
    override fun getNavigationTag() = NavigationInfoProvider.NavigationTag.TAG_PROFILE_FRAGMENT
    override fun isNavigationBranchOwner() = true
    override fun cacheOnBackPressed() = true
}