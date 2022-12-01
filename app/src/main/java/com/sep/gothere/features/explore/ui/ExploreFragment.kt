package com.sep.gothere.features.explore.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.sep.gothere.R
import com.sep.gothere.activity.BaseFragment
import com.sep.gothere.databinding.FragmentExploreBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExploreFragment : BaseFragment(R.layout.fragment_explore) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentExploreBinding.bind(view)
    }
}