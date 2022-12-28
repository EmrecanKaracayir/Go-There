package com.sep.gothere.features.root_postlog.search.branch_search.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sep.gothere.R
import com.sep.gothere.api.model.venue.get.request.GetVenueRequest
import com.sep.gothere.base.BaseActivity
import com.sep.gothere.base.BaseFragment
import com.sep.gothere.databinding.FragmentSearchBinding
import com.sep.gothere.features.root_postlog.search.branch_search.vm.SearchViewModel
import com.sep.gothere.helpers.color.FeatureStylizer
import com.sep.gothere.helpers.color.HSL
import com.sep.gothere.navigation.NavigationInfoProvider
import com.sep.gothere.util.BUNDLE_ID_KEY
import com.sep.gothere.util.CustomFitsSystemUI
import com.sep.gothere.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : BaseFragment(R.layout.fragment_search), NavigationInfoProvider {

    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val accentHSL: HSL
        get() {
            return HSL(
                Color.rgb(
                    (Math.random() * 200).toInt(),
                    (Math.random() * 200).toInt(),
                    (Math.random() * 255).toInt()
                )
            )
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            if (viewModel.adapter == null) {
                this@SearchFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.getVenueVM(GetVenueRequest(random = true, take = 50))
                    }
                }
            }

            CustomFitsSystemUI().fitsSystemUI(
                searchFragmentHeroLayoutFITSTOP, arrayListOf(
                    CustomFitsSystemUI.RequestedInset.TOP
                )
            )

            CustomFitsSystemUI().fitsSystemUI(
                recyclerViewFITSBOTTOM, arrayListOf(
                    CustomFitsSystemUI.RequestedInset.BOTTOM
                )
            )

            searchStateEventsHandler()

            searchFieldEditText.addTextChangedListener {
                this@SearchFragment.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.getVenueVM(GetVenueRequest(search = it.toString()))
                    }
                }
            }
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun searchStateEventsHandler() {
        binding.apply {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is SearchViewModel.Event.VenueError -> {
                                searchFieldPB.visibility = View.GONE
                            }
                            is SearchViewModel.Event.VenueLoading -> {
                                searchFieldPB.visibility = View.VISIBLE
                            }
                            is SearchViewModel.Event.VenueSuccessful -> {

                                viewModel.adapter!!.onVisitButtonClicked = {
                                    businessProfilePageRequested(it.venueID)
                                }

                                searchFieldPB.visibility = View.GONE
                                recyclerViewFITSBOTTOM.adapter = viewModel.adapter
                                recyclerViewFITSBOTTOM.layoutManager =
                                    LinearLayoutManager(requireContext())

                                val accentColors = accentHSL

                                recyclerViewFITSBOTTOM.doOnNextLayout {
                                    val fs = FeatureStylizer(requireContext(), accentColors)
                                    lifecycleScope.launch {
                                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                            fs.findStylableComponentsAndApplyStyle(binding.root)
                                        }
                                    }
                                }
                            }
                        }.exhaustive
                    }
                }
            }
        }
    }

    private fun businessProfilePageRequested(id: Long) {
        val bundle = Bundle()
        bundle.putLong(BUNDLE_ID_KEY, id)
        (activity as BaseActivity).fragmentWithBundleRequested(
            bundle,
            NavigationInfoProvider.NavigationTag.TAG_BUSINESS_PROFILE_FRAGMENT_SEARCH,
            true
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun getNavigationRoot() = NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG
    override fun getNavigationBranch() = NavigationInfoProvider.NavigationBranch.BRANCH_SEARCH
    override fun getNavigationTag() = NavigationInfoProvider.NavigationTag.TAG_SEARCH_FRAGMENT
    override fun isNavigationBranchOwner() = true
    override fun cacheOnBackPressed() = true
}