package com.sep.gothere.features.root_postlog.search.leaf_business_profile_search.ui

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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sep.gothere.R
import com.sep.gothere.api.model.venue.get.request.GetVenueRequest
import com.sep.gothere.api.model.venue.get.response.GetVenueResponseData
import com.sep.gothere.base.BaseActivity
import com.sep.gothere.base.BaseFragment
import com.sep.gothere.data.NetworkVenueRepository
import com.sep.gothere.databinding.FragmentBusinessProfileCommonBinding
import com.sep.gothere.features.root_postlog.search.leaf_business_profile_search.vm.BusinessProfileSearchViewModel
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
import javax.inject.Inject


@AndroidEntryPoint
class BusinessProfileFragmentSearch : BaseFragment(R.layout.fragment_business_profile_common),
    NavigationInfoProvider {

    @Inject
    lateinit var networkVenueRepository: NetworkVenueRepository

    private val viewModel: BusinessProfileSearchViewModel by viewModels()

    private var _binding: FragmentBusinessProfileCommonBinding? = null
    private val binding get() = _binding!!

    private var venueID: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBusinessProfileCommonBinding.inflate(inflater, container, false)
        val bundle = arguments
        if (bundle != null) {
            venueID = bundle.getLong(BUNDLE_ID_KEY)
        } else throw IllegalArgumentException()
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.getVenueVM(GetVenueRequest(venueId = venueID))
                }
            }

            businessProfileUiStateEventsHandler()

            CustomFitsSystemUI().fitsSystemUI(
                businessProfileFragmentHeroLayoutFITSTOP, arrayListOf(
                    CustomFitsSystemUI.RequestedInset.TOP
                )
            )

            backButton.setOnClickListener {
                (activity as BaseActivity).navigateBackRequested()
            }
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun businessProfileUiStateEventsHandler() {
        binding.apply {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is BusinessProfileSearchViewModel.Event.VenueError -> {}
                            is BusinessProfileSearchViewModel.Event.VenueLoading -> {}
                            is BusinessProfileSearchViewModel.Event.VenueSuccessful -> {
                                if (event.response.data != null) updateFirstWidget(event.response.data[0]) else throw IllegalArgumentException()
                            }
                        }.exhaustive
                    }
                }
            }
        }
    }

    private fun updateFirstWidget(
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

        binding.venueName.text = venue.name
        val venueUsernameFinal = "#" + venue.username
        binding.venueUsername.text = venueUsernameFinal
        binding.venueShortDesc.text = venue.shortDescription
        binding.venueMainCommunication.text = venue.mail
        binding.venueFavCount.text = venue.favCount.toString()
        binding.venueBiography.text = venue.biography
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
                .into(binding.venueProfileImage)
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
                        binding.venueCoverImage.setImageBitmap(resource)
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun getNavigationRoot() = NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG
    override fun getNavigationBranch() = NavigationInfoProvider.NavigationBranch.BRANCH_SEARCH
    override fun getNavigationTag() = NavigationInfoProvider.NavigationTag.TAG_BUSINESS_PROFILE_FRAGMENT_EXPLORE

    override fun isNavigationBranchOwner() = false
    override fun cacheOnBackPressed() = false
}