package com.sep.gothere.features.root_postlog.explore.branch_explore.model

import android.graphics.Bitmap

data class WidgetBasicVisit(
    val venueCoverImage: Bitmap,
    val venueLogoImage: Bitmap,
    val venueName: String,
    val venueShortDesc: String,
    val venueID: String
)
