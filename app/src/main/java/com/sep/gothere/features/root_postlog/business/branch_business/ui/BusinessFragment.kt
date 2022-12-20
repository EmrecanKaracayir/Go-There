package com.sep.gothere.features.root_postlog.business.branch_business.ui

import android.os.Bundle
import android.view.View
import com.sep.gothere.R
import com.sep.gothere.base.BaseFragment
import com.sep.gothere.databinding.FragmentBusinessBinding
import com.sep.gothere.navigation.NavigationInfoProvider
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BusinessFragment : BaseFragment(R.layout.fragment_business), NavigationInfoProvider {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBusinessBinding.bind(view)
    }

    override fun getNavigationRoot() = NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG
    override fun getNavigationBranch() = NavigationInfoProvider.NavigationBranch.BRANCH_BUSINESS
    override fun getNavigationTag() = NavigationInfoProvider.NavigationTag.TAG_BUSINESS_FRAGMENT
    override fun isNavigationBranchOwner() = true
    override fun cacheOnBackPressed() = true
}