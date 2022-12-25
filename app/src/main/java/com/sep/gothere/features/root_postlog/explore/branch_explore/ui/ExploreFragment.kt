package com.sep.gothere.features.root_postlog.explore.branch_explore.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.doOnLayout
import androidx.lifecycle.lifecycleScope
import com.sep.gothere.R
import com.sep.gothere.base.BaseFragment
import com.sep.gothere.databinding.FragmentExploreBinding
import com.sep.gothere.helpers.color.Contraster
import com.sep.gothere.helpers.color.FeatureStylizer
import com.sep.gothere.helpers.color.HSL
import com.sep.gothere.helpers.color.Palette
import com.sep.gothere.navigation.NavigationInfoProvider
import com.sep.gothere.util.CustomFitsSystemUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*


@AndroidEntryPoint
class ExploreFragment : BaseFragment(R.layout.fragment_explore), NavigationInfoProvider {

    private val accentHSL = HSL(
        Color.rgb(
            (Math.random() * 200).toInt(),
            (Math.random() * 200).toInt(),
            (Math.random() * 255).toInt()
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentDate =
            DateFormat.getDateInstance(DateFormat.FULL).format(Calendar.getInstance().time)
                .uppercase()
        FragmentExploreBinding.bind(view).apply {
            lifecycleScope.launch {
                Palette.from(heroImage.drawable.toBitmap()).maximumColorCount(24).addFilter(Palette.KARACAYIR_FILTER)
                    .addTarget(Palette.KARACAYIR_TARGET).generate {
                        @ColorInt val dominantColor = it.getColorForTarget(Palette.KARACAYIR_TARGET, Color.BLACK)
                        val fs = FeatureStylizer(requireContext(), HSL(dominantColor))
                        root.doOnLayout { fs.findStylableComponentsAndApplyStyle(root) }
                    }
            }

            CustomFitsSystemUI().fitsSystemUI(
                exploreFragmentHeroLayoutFITSTOP, arrayListOf(
                    CustomFitsSystemUI.RequestedInset.TOP
                )
            )

            CustomFitsSystemUI().fitsSystemUI(
                contentLayoutFITSBOTTOM, arrayListOf(
                    CustomFitsSystemUI.RequestedInset.BOTTOM
                )
            )

            dateLabel.text = currentDate
        }
    }

    override fun getNavigationRoot() = NavigationInfoProvider.NavigationRoot.ROOT_POSTLOG
    override fun getNavigationBranch() = NavigationInfoProvider.NavigationBranch.BRANCH_EXPLORE
    override fun getNavigationTag() = NavigationInfoProvider.NavigationTag.TAG_EXPLORE_FRAGMENT
    override fun isNavigationBranchOwner() = true
    override fun cacheOnBackPressed() = true
}