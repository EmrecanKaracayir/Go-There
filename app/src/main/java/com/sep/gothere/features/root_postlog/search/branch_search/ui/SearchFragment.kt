package com.sep.gothere.features.root_postlog.search.branch_search.ui

import android.os.Bundle
import android.view.View
import com.sep.gothere.R
import com.sep.gothere.base.BaseFragment
import com.sep.gothere.databinding.FragmentSearchBinding
import com.sep.gothere.navigation.NavigationInfoProvider
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : BaseFragment(R.layout.fragment_search), NavigationInfoProvider {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchBinding.bind(view)
    }

    override fun getNavigationRoot() = NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG
    override fun getNavigationBranch() = NavigationInfoProvider.NavigationBranch.BRANCH_SEARCH
    override fun isNavigationBranchOwner() = true
    override fun cacheOnBackPressed() = true
}