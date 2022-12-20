package com.sep.gothere.features.root_postlog.profile.branch_profile.ui

import android.os.Bundle
import android.view.View
import com.sep.gothere.R
import com.sep.gothere.base.BaseFragment
import com.sep.gothere.databinding.FragmentProfileBinding
import com.sep.gothere.navigation.NavigationInfoProvider
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile), NavigationInfoProvider {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileBinding.bind(view)
    }

    override fun getNavigationRoot() = NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG
    override fun getNavigationBranch() = NavigationInfoProvider.NavigationBranch.BRANCH_PROFILE
    override fun getNavigationTag() = NavigationInfoProvider.NavigationTag.TAG_PROFILE_FRAGMENT
    override fun isNavigationBranchOwner() = true
    override fun cacheOnBackPressed() = true
}