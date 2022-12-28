package com.sep.gothere.features.root_postlog.explore.branch_explore.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sep.gothere.R
import com.sep.gothere.api.model.venue.get.request.GetVenueRequest
import com.sep.gothere.api.model.venue.get.response.GetVenueResponseData
import com.sep.gothere.base.BaseActivity
import com.sep.gothere.base.BaseFragment
import com.sep.gothere.data.NetworkVenueRepository
import com.sep.gothere.databinding.FragmentExploreBinding
import com.sep.gothere.features.root_postlog.explore.branch_explore.vm.ExploreViewModel
import com.sep.gothere.helpers.color.FeatureStylizer
import com.sep.gothere.helpers.color.HSL
import com.sep.gothere.helpers.color.Palette
import com.sep.gothere.navigation.NavigationInfoProvider
import com.sep.gothere.util.BUNDLE_ID_KEY
import com.sep.gothere.util.CustomFitsSystemUI
import com.sep.gothere.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ExploreFragment : BaseFragment(R.layout.fragment_explore), NavigationInfoProvider {

    @Inject
    lateinit var networkVenueRepository: NetworkVenueRepository

    private val viewModel: ExploreViewModel by viewModels()

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            if (viewModel.adapter == null) {
                fetchVenue()
            } else {
                viewModel.heroCardContent?.let { updateFirstWidget(binding, it) }
                recyclerViewFITSBOTTOM.adapter = viewModel.adapter
                recyclerViewFITSBOTTOM.layoutManager = LinearLayoutManager(requireContext())
            }

            exploreStateEventsHandler(this)

            CustomFitsSystemUI().fitsSystemUI(
                exploreFragmentHeroLayoutFITSTOP, arrayListOf(
                    CustomFitsSystemUI.RequestedInset.TOP
                )
            )

            CustomFitsSystemUI().fitsSystemUI(
                recyclerViewFITSBOTTOM, arrayListOf(
                    CustomFitsSystemUI.RequestedInset.BOTTOM
                )
            )

            val currentDate =
                DateFormat.getDateInstance(DateFormat.FULL).format(Calendar.getInstance().time)
                    .uppercase()

            dateLabel.text = currentDate

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                fetchVenue()
            }
        }
    }

    private fun fetchVenue() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getVenueVM(GetVenueRequest(take = 100, random = true))
            }
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun exploreStateEventsHandler(binding: FragmentExploreBinding) {
        binding.apply {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is ExploreViewModel.Event.VenueError -> {}
                            is ExploreViewModel.Event.VenueLoading -> {}
                            is ExploreViewModel.Event.VenueSuccessful -> {
                                viewModel.adapter!!.onVisitButtonClicked = {
                                    businessProfilePageRequested(it.venueID)
                                }
                                if (event.response.data != null) {
                                    updateFirstWidget(binding, event.response.data[0])
                                    heroVisitButton.setOnClickListener {
                                        businessProfilePageRequested(event.response.data[0].id)
                                    }
                                }

                                binding.recyclerViewFITSBOTTOM.adapter = viewModel.adapter
                                binding.recyclerViewFITSBOTTOM.layoutManager =
                                    LinearLayoutManager(requireContext())
                            }
                        }.exhaustive
                    }
                }
            }
        }
    }

    private fun updateFirstWidget(
        binding: FragmentExploreBinding,
        venue: GetVenueResponseData
    ) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loadProfileImage(venue.id)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loadCoverImage(venue.id)
            }
        }

        binding.heroVenueName.text = venue.name
        binding.heroVenueShortDesc.text = venue.shortDescription
        val venueUsernameFinal = "#" + venue.username
        binding.heroVenueUsername.text = venueUsernameFinal
    }

    private suspend fun loadProfileImage(venueID: Long) {
        withContext(Dispatchers.Main) {
            val profileImageByteArray: ByteArray = Base64.decode(
                networkVenueRepository.getVenueProfileImageRP(venueID).data,
                Base64.DEFAULT
            )

            Glide.with(requireContext())
                .asBitmap()
                .load(profileImageByteArray)
                .placeholder(R.drawable.photo_placeholder)
                .into(binding.heroVenueProfileImage)
        }
    }

    private suspend fun loadCoverImage(venueID: Long) {
        withContext(Dispatchers.Main) {
            val coverImageByteArray: ByteArray = Base64.decode(
                networkVenueRepository.getVenueCoverImageRP(venueID).data,
                Base64.DEFAULT
            )

            Glide.with(requireContext())
                .asBitmap()
                .load(coverImageByteArray)
                .override(1080).centerCrop()
                .placeholder(R.drawable.photo_placeholder)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap>?
                    ) {
                        binding.heroVenueCoverImage.setImageBitmap(resource)
                        calculateColors(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    private fun calculateColors(bitmap: Bitmap) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                Palette.from(bitmap)
                    .maximumColorCount(24)
                    .addFilter(Palette.KARACAYIR_FILTER)
                    .addTarget(Palette.KARACAYIR_TARGET).generate {
                        @ColorInt val dominantColor =
                            it.getColorForTarget(Palette.KARACAYIR_TARGET, Color.BLACK)
                        val fs = FeatureStylizer(requireContext(), HSL(dominantColor))
                        lifecycleScope.launch {
                            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                fs.findStylableComponentsAndApplyStyle(binding.root)
                            }
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
            NavigationInfoProvider.NavigationTag.TAG_BUSINESS_PROFILE_FRAGMENT_EXPLORE,
            true
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun getNavigationRoot() = NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG
    override fun getNavigationBranch() = NavigationInfoProvider.NavigationBranch.BRANCH_EXPLORE
    override fun getNavigationTag() = NavigationInfoProvider.NavigationTag.TAG_EXPLORE_FRAGMENT
    override fun isNavigationBranchOwner() = true
    override fun cacheOnBackPressed() = true
}