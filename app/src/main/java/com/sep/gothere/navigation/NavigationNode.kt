package com.sep.gothere.navigation

data class NavigationNode(
    val location: NavigationInfoProvider,
    var isActive: Boolean
)
