package com.sep.gothere.features.root_postlog.explore.branch_explore.ui

import android.os.Bundle
import android.view.View
import com.sep.gothere.R
import com.sep.gothere.base.BaseFragment
import com.sep.gothere.databinding.FragmentExploreBinding
import com.sep.gothere.navigation.NavigationInfoProvider
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExploreFragment : BaseFragment(R.layout.fragment_explore), NavigationInfoProvider {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentExploreBinding.bind(view)
    }

    override fun getNavigationRoot() = NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG
    override fun getNavigationBranch() = NavigationInfoProvider.NavigationBranch.BRANCH_EXPLORE
    override fun isNavigationBranchOwner() = true
    override fun cacheOnBackPressed() = true
}